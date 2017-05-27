package sho.cmis.drive;

import java.nio.file.FileSystem;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.nativity.control.MessageListener;
import com.liferay.nativity.control.NativityControl;
import com.liferay.nativity.control.NativityControlUtil;
import com.liferay.nativity.control.NativityMessage;
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

@Component(immediate = true)
public class CmisDrive
{
	private static final Logger LOG = LoggerFactory.getLogger(CmisDrive.class.getName());

	private static final String COMPONENT_NAME = "sho.cmis.drive";

	private static final String OVERLAY_ICON = "/Volumes/Shorty_JetDrive_1/tmp/omnIcon.icns";

	private static final String MOUNT_POINT = "/Volumes/Shorty_JetDrive_1/tmp/drive_mountpoint";

	private static final boolean READONLY = true;

	@Activate
	public void activate()
	{
		LOG.info("Activating component: {} ...", COMPONENT_NAME);
		try
		{
			javafs();
		}
		catch (Exception ioe)
		{
			LOG.error("Error in javafs: ", ioe);
			ioe.printStackTrace();
		}
		nativity();
		LOG.debug("Activated component: {}", COMPONENT_NAME);
	}

	private void javafs() throws Exception
	{
		FileSystem fs = null;
		Map<String, String> options = new HashMap<>();
		// options.put("fsname", fs.getClass().getSimpleName() + "@" + System.currentTimeMillis());
		options.put("fsname", "OMN");
		options.put("volname", "Online Media Net");
		options.put("volicon", OVERLAY_ICON);

		LOG.info("Mounting FileSystem ...");
		JavaFS.mount(fs, Paths.get(MOUNT_POINT), READONLY, true, options);
		LOG.info("... mounted FileSystem!");
	}

	private void nativity()
	{
		// final String testFolder = "/Volumes/Shorty_JetDrive_1/tmp";
		final String testFolder = MOUNT_POINT;

		try
		{
			final NativityControl nativityControl = NativityControlUtil.getNativityControl();

			nativityControl.addSocketOpenListener(new SocketOpenListener()
			{

				@Override
				public void onSocketOpen()
				{
					// TODO Auto-generated method stub
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
							System.out.println("Get ICon For File: " + path);
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

					fileIconControl.registerIconWithId(OVERLAY_ICON, "omn", Integer.toString(testIconId));
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

			nativityControl.connect();

			nativityControl.addFavoritesPath(testFolder);

			// Setting filter folders is required for Mac's Finder Sync plugin
			nativityControl.setFilterFolder(testFolder);

			/* File Icons */

			nativityControl.registerMessageListener(new MessageListener(testFolder)
			{

				@Override
				public NativityMessage onMessage(NativityMessage nativityMessage)
				{
					System.out.println("Received Message: " + nativityMessage);
					return null;
				}
			});

			final int testIconId = 98786;

			// FileIconControlCallback used by Windows and Mac
			FileIconControlCallback fileIconControlCallback = new FileIconControlCallback()
			{
				@Override
				public int getIconForFile(String path)
				{
					// return testIconId;
					System.out.println("Get ICon For File: " + path);
					return testIconId;
				}
			};

			FileIconControl fileIconControl = FileIconControlUtil.getFileIconControl(nativityControl, fileIconControlCallback);

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

			fileIconControl.registerIconWithId(OVERLAY_ICON, "omn", Integer.toString(testIconId));
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
					ContextMenuItem contextMenuItem = new ContextMenuItem("Nativity Test");

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
					contextMenuItem.setIconPath(OVERLAY_ICON);

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
		catch (Exception e)
		{
			System.out.println("Error: " + e);
			LOG.error("Error: ", e);
		}
	}

}
