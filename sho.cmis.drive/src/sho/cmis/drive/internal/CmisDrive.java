package sho.cmis.drive.internal;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.Session;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
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

@Component(name = CmisDrive.COMPONENT_NAME, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = CmisDriveCfg.class)
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

	private static final boolean READONLY = false;

	@Reference
	FileSystemProvider fsp;

	@Reference
	Session cmisSession;

	@Activate
	public void activate(final CmisDriveCfg config) throws Exception
	{
		LOG.info("Activating component: {} ...", COMPONENT_NAME);
		modified(config);
		LOG.debug("Activated component: {} ...", COMPONENT_NAME);
	}

	@Modified
	public void modified(final CmisDriveCfg config) throws Exception
	{
		LOG.info("Modifying component: {} ...", COMPONENT_NAME);

		ServiceLoader<FileSystemProvider> load = java.util.ServiceLoader.load(FileSystemProvider.class);
		for (FileSystemProvider fileSystemProvider : load)
		{
			LOG.info("ServiceLoader FSP: " + fileSystemProvider.getScheme());
		}

		for (FileSystemProvider fsr : FileSystemProvider.installedProviders())
		{
			LOG.info("FSP: " + fsr.getScheme());
		}
		javafs(config.mount_path());
		LOG.debug("Modified component: {}", COMPONENT_NAME);
	}

	@Deactivate
	public void deactivate(final CmisDriveCfg config) throws Exception
	{
		LOG.debug("Deactiving component: {} ...", COMPONENT_NAME);
		JavaFS.unmount(Paths.get(config.mount_path()));
		LOG.debug("Deactivated component: {} ...", COMPONENT_NAME);
	}

	private void javafs(String mountpath) throws Exception
	{
		Map<String, Object> fsConfig = new HashMap<>();
		fsConfig.put("CmisSession", cmisSession);

		FileSystem fs = fsp.newFileSystem(new URI(CMIS_URI), fsConfig);
		// FileSystem fs = CmisFS.newFileSystem(new URI(CMIS_URI), fsConfig);
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
		JavaFS.mount(fs, Paths.get(mountpath), READONLY, true, options);
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
