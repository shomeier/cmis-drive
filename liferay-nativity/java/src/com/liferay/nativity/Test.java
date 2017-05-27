package com.liferay.nativity;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.nativity.control.NativityControl;
import com.liferay.nativity.control.NativityControlUtil;
import com.liferay.nativity.modules.contextmenu.ContextMenuControlCallback;
import com.liferay.nativity.modules.contextmenu.ContextMenuControlUtil;
import com.liferay.nativity.modules.contextmenu.model.ContextMenuAction;
import com.liferay.nativity.modules.contextmenu.model.ContextMenuItem;
import com.liferay.nativity.modules.fileicon.FileIconControl;
import com.liferay.nativity.modules.fileicon.FileIconControlCallback;
import com.liferay.nativity.modules.fileicon.FileIconControlUtil;
import com.liferay.nativity.util.OSDetector;

public class Test
{
	private static final Logger LOG = LoggerFactory.getLogger(Test.class.getName());

	public static void main(String[] attr)
	{
		nativity();
	}

	private static void nativity()
	{
		String testFolder = "/Users/SHomeier/Documents/TestSync";

		try
		{
			NativityControl nativityControl = NativityControlUtil.getNativityControl();

			nativityControl.connect();

			// Setting filter folders is required for Mac's Finder Sync plugin
			nativityControl.setFilterFolder(testFolder);

			/* File Icons */

			int testIconId = 1;

			// FileIconControlCallback used by Windows and Mac
			FileIconControlCallback fileIconControlCallback = new FileIconControlCallback()
			{
				@Override
				public int getIconForFile(String path)
				{
					// return testIconId;
					return 1;
				}
			};

			FileIconControl fileIconControl = FileIconControlUtil.getFileIconControl(nativityControl, fileIconControlCallback);

			fileIconControl.enableFileIcons();

			String testFilePath = testFolder + "/TestSync.pdf";

			if (OSDetector.isWindows())
			{
				// This id is determined when building the DLL
				testIconId = 1;
			}
			else if (OSDetector.isMinimumAppleVersion(OSDetector.MAC_YOSEMITE_10_10))
			{
				// Used by Mac Finder Sync. This unique id can be set at runtime.
				testIconId = 1;

				fileIconControl.registerIconWithId("/Users/SHomeier/Documents/apln_icon_center.icns", "test label",
					Integer.toString(testIconId));
			}
			else if (false)
			{
				// Used by Mac Injector and Linux
				testIconId = fileIconControl.registerIcon("/Users/liferay/Desktop/testIcon.icns");
			}

			// FileIconControl.setFileIcon() method only used by Linux
			fileIconControl.setFileIcon(testFilePath, testIconId);

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

					List<ContextMenuItem> contextMenuItems = new ArrayList<ContextMenuItem>()
					{
					};
					contextMenuItems.add(contextMenuItem);

					// Mac Finder Sync will only show the parent level of context menus
					return contextMenuItems;
				}
			};

			ContextMenuControlUtil.getContextMenuControl(nativityControl, contextMenuControlCallback);
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e);
			LOG.error("Error: ", e);
		}
	}
}
