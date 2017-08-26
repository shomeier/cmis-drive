package sho.cmis.drive;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.nativity.control.NativityControl;
import com.liferay.nativity.control.NativityControlUtil;
import com.liferay.nativity.listeners.SocketCloseListener;
import com.liferay.nativity.listeners.SocketOpenListener;
import com.liferay.nativity.modules.contextmenu.ContextMenuControl;
import com.liferay.nativity.modules.contextmenu.ContextMenuControlCallback;
import com.liferay.nativity.modules.contextmenu.ContextMenuControlUtil;
import com.liferay.nativity.modules.contextmenu.model.ContextMenuAction;
import com.liferay.nativity.modules.contextmenu.model.ContextMenuItem;
import com.liferay.nativity.modules.fileicon.FileIconControl;
import com.liferay.nativity.modules.fileicon.FileIconControlCallback;
import com.liferay.nativity.modules.fileicon.FileIconControlUtil;

import co.paralleluniverse.javafs.JavaFS;
import sho.cmis.fs.CmisConfig;
import sho.cmis.fs.CmisFS;

@Component(name = CmisDrive.COMPONENT_NAME, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = CmisDriveConfig.class)
public class CmisDrive
{
	private static final Logger LOG = LoggerFactory.getLogger(CmisDrive.class.getName());

	private final BundleContext bc = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	private final static String USE_SERVER = "APLN";
	// private final static String USE_SERVER = "ALF";

	private static final String ALFRESCO_URL =
		"https://cmis.alfresco.com/alfresco/api/-default-/public/cmis/versions/1.1/browser";
	private static final String APLN_URL = "http://localhost:8083/cmisBrowser";

	static final String COMPONENT_NAME = "sho.cmis.drive";

	private static final String OVERLAY_OMN_ICON = "/Volumes/Shorty_JetDrive_1/tmp/omnIcon.icns";
	// private static final String OVERLAY_CMIS_ICON = "./img/cmis-wb-icon.png.icns";
	private static final String OVERLAY_CMIS_ICON = System.getProperty("icon");

	// private static final String MOUNT_POINT = "/Volumes/Shorty_JetDrive_1/tmp/drive_mountpoint";
	private static final String MOUNT_POINT = "/Volumes/Shorty_JetDrive_1/mount";
	// private static final String CMIS_MOUNT_POINT_URI = "cmis:///Volumes/Shorty_JetDrive_1/tmp/drive_mountpoint";
	private static final String CMIS_URI = "cmis:///";

	private static final boolean READONLY = true;

	@Reference
	SessionFactory sessionFactory;

	Session cmisSession;

	@Activate
	public void activate(final CmisDriveConfig config)
	{
		LOG.info("Activating component: {} ...", COMPONENT_NAME);

		ServiceLoader<FileSystemProvider> load = java.util.ServiceLoader.load(FileSystemProvider.class);
		for (FileSystemProvider fileSystemProvider : load)
		{
			LOG.info("ServiceLoader FSP: " + fileSystemProvider.getScheme());
		}

		for (FileSystemProvider fsr : FileSystemProvider.installedProviders())
		{
			LOG.info("FSP: " + fsr.getScheme());
		}

		Map<String, String> envConfig = new HashMap<>();
		envConfig.put(CmisConfig.BINDING_TYPE, "browser");
		envConfig.put(CmisConfig.BROWSER_URL, config.url());
		envConfig.put(CmisConfig.USER, config.user());
		envConfig.put(CmisConfig.PASSWORD, config.password());
		if (config.repository_id() != null)
			envConfig.put(CmisConfig.REPOSITORY_ID, config.repository_id());
		envConfig.put(CmisConfig.CACHE_PATH_OMIT, "false");

		OperationContext opCtx = new OperationContextImpl();
		Set<String> filter = new HashSet<>();
		filter.add("cmis:baseTypeId");
		filter.add("cmis:objectId");
		filter.add("cmis:objectTypeId");
		filter.add("cmis:name");
		filter.add("cmis:contentStreamLength");
		filter.add("cmis:contentStreamFileName");
		filter.add("cmis:versionLabel");
		filter.add("cmis:versionSeriesId");
		// opCtx.setFilter(filter);
		opCtx.setIncludePathSegments(true);
		if (config.repository_id() != null)
			cmisSession = sessionFactory.createSession(envConfig);
		else
			cmisSession = sessionFactory.getRepositories(envConfig).get(0).createSession();
		cmisSession.setDefaultContext(opCtx);
		// register CMIS Session as OSGi Service
		ServiceRegistration<Session> serviceRegistration = bc.registerService(Session.class, cmisSession, null);

		// we need to wait for the CMIS Filesystem Bundle to be started
		Dictionary<String, Object> ht = new Hashtable<String, Object>();
		ht.put(EventConstants.EVENT_TOPIC, "org/osgi/framework/BundleEvent/STARTED");
		bc.registerService(EventHandler.class.getName(), new EventHandler()
		{
			@Override
			public void handleEvent(Event event)
			{
				System.out.println("handleEvent: " + event);
				if (event.containsProperty("bundle.symbolicName"))
				{
					if (event.getProperty("bundle.symbolicName").equals("sho.cmis.fs"))
					{
						try
						{
							javafs();
							// TODO: Handle Nativity over own OSGi Bundle
							// nativity();
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}, ht);
		LOG.debug("Activated component: {}", COMPONENT_NAME);
	}

	private void javafs() throws Exception
	{
		Map<String, Object> fsConfig = new HashMap<>();
		fsConfig.put("CmisSession", cmisSession);

		FileSystem fs = CmisFS.newFileSystem(new URI(CMIS_URI), fsConfig);
		// FileSystem fs = Jimfs.newFileSystem();
		LOG.info("FS separator: " + fs.getSeparator());

		Map<String, String> options = new HashMap<>();
		// options.put("fsname", fs.getClass().getSimpleName() + "@" + System.currentTimeMillis());
		options.put("nobrowse", null);
		options.put("noappledouble", null);
		options.put("fsname", "CMIS");
		options.put("volname", "Alfresco CMIS Server");
		options.put("volicon", OVERLAY_CMIS_ICON);

		LOG.info("Mounting FileSystem ...");
		JavaFS.mount(fs, Paths.get(MOUNT_POINT), READONLY, false, options);
		LOG.info("... mounted FileSystem!");
	}

	private void nativity()
	{
		// final String testFolder = "/Volumes/Shorty_JetDrive_1/tmp";
		final String testFolder = MOUNT_POINT;

		try
		{
			final NativityControl nativityControl = NativityControlUtil.getNativityControl();

			nativityControl.connect();

			nativityControl.addSocketOpenListener(new SocketOpenListener()
			{

				@Override
				public void onSocketOpen()
				{
					System.out.println("Socket opened !!!!");

					// nativityControl.addFavoritesPath(testFolder);

					// Setting filter folders is required for Mac's Finder Sync plugin
					nativityControl.setFilterFolder(testFolder);

					final int testIconId = 98786;

					// FileIconControlCallback used by Windows and Mac
					FileIconControlCallback fileIconControlCallback = new FileIconControlCallback()
					{
						@Override
						public int getIconForFile(String path)
						{
							// return testIconId;
							System.out.println("111111.....Get ICon For File: " + path);
							return testIconId;
						}
					};

					FileIconControl fileIconControl =
						FileIconControlUtil.getFileIconControl(nativityControl, fileIconControlCallback);

					fileIconControl.enableFileIcons();

					// String testFilePath = testFolder + "/squirrel.zip";

					// if (OSDetector.isWindows())
					// {
					// // This id is determined when building the DLL
					// testIconId = 1;
					// }
					// else if (OSDetector.isMinimumAppleVersion(OSDetector.MAC_YOSEMITE_10_10))
					// {
					// Used by Mac Finder Sync. This unique id can be set at runtime.
					// testIconId = 1;

					fileIconControl.registerIconWithId(OVERLAY_CMIS_ICON, "omn", Integer.toString(testIconId));
					// }
					// else if (false)
					// {
					// // Used by Mac Injector and Linux
					// testIconId = fileIconControl.registerIcon("/Users/liferay/Desktop/testIcon.icns");
					// }
				}
			});

			nativityControl.addSocketCloseListener(new SocketCloseListener()
			{

				@Override
				public void onSocketClose()
				{
					// TODO Auto-generated method stub
					System.out.println("Socket Closed .....");
				}
			});

			nativityControl.addFavoritesPath(testFolder);

			// Setting filter folders is required for Mac's Finder Sync plugin
			nativityControl.setFilterFolder(testFolder);

			// final int testIconId = 98786;
			//
			// // FileIconControlCallback used by Windows and Mac
			// FileIconControlCallback fileIconControlCallback = new FileIconControlCallback()
			// {
			// @Override
			// public int getIconForFile(String path)
			// {
			// // return testIconId;
			// System.out.println("Get ICon For File: " + path);
			// return testIconId;
			// }
			// };
			//
			// FileIconControl fileIconControl = FileIconControlUtil.getFileIconControl(nativityControl, fileIconControlCallback);
			//
			// fileIconControl.enableFileIcons();

			// String testFilePath = testFolder + "/squirrel.zip";

			// if (OSDetector.isWindows())
			// {
			// // This id is determined when building the DLL
			// testIconId = 1;
			// }
			// else if (OSDetector.isMinimumAppleVersion(OSDetector.MAC_YOSEMITE_10_10))
			// {
			// Used by Mac Finder Sync. This unique id can be set at runtime.
			// testIconId = 1;

			// fileIconControl.registerIconWithId(OVERLAY_CMIS_ICON, "omn", Integer.toString(testIconId));
			// }
			// else if (false)
			// {
			// // Used by Mac Injector and Linux
			// testIconId = fileIconControl.registerIcon("/Users/liferay/Desktop/testIcon.icns");
			// }

			// FileIconControl.setFileIcon() method only used by Linux
			// fileIconControl.setFileIcon(testFilePath, testIconId);

			/* Context Menus */

			ContextMenuControlCallback contextMenuControlCallback = new ContextMenuControlCallback()
			{
				@Override
				public List<ContextMenuItem> getContextMenuItems(String[] paths)
				{
					ContextMenuItem contextMenuItem = new ContextMenuItem("CmisDrive Menu Test");

					ContextMenuAction contextMenuAction = new ContextMenuAction()
					{
						@Override
						public void onSelection(String[] paths)
						{
							for (String path : paths)
							{
								System.out.print(path + ", ");
							}

							System.out.println("selected");
						}
					};

					contextMenuItem.setContextMenuAction(contextMenuAction);
					contextMenuItem.setIconPath(OVERLAY_CMIS_ICON);

					List<ContextMenuItem> contextMenuItems = new ArrayList<ContextMenuItem>()
					{
					};
					contextMenuItems.add(contextMenuItem);

					// Mac Finder Sync will only show the parent level of context menus
					return contextMenuItems;
				}
			};

			ContextMenuControl contextMenuControl =
				ContextMenuControlUtil.getContextMenuControl(nativityControl, contextMenuControlCallback);

			Set<String> allObservedFolders = nativityControl.getAllObservedFolders();
			for (String folder : allObservedFolders)
			{
				System.out.println("OF: " + folder);
			}
			if (allObservedFolders.isEmpty())
				System.out.println("OF is EMPTY!!!!");
		}
		catch (

		Exception e)
		{
			System.out.println("Error: " + e);
			LOG.error("Error: ", e);
		}
	}

}
