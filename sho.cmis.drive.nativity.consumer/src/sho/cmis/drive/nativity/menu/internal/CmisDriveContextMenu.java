package sho.cmis.drive.nativity.menu.internal;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.nativity.modules.contextmenu.model.ContextMenuAction;
import com.liferay.nativity.modules.contextmenu.model.ContextMenuItem;

import sho.nativity.ContextMenuItemProvider;

@Component(name = CmisDriveContextMenu.COMPONENT_NAME)
public class CmisDriveContextMenu implements ContextMenuItemProvider
{
	static final String COMPONENT_NAME = "sho.cmis.drive.nativity.menu";

	private static final Logger LOG = LoggerFactory.getLogger(CmisDriveContextMenu.class.getName());

	private static final String OVERLAY_CMIS_ICON = System.getProperty("icon");

	@Activate
	private void activate()
	{
		LOG.debug("Activated component: {}", COMPONENT_NAME);
	}

	@Override
	public List<ContextMenuItem> getContextMenuItems(String[] paths)
	{
		ContextMenuItem contextMenuItem = new ContextMenuItem("CmisDrive MenuProvider Test");

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

		List<ContextMenuItem> contextMenuItems = new ArrayList<ContextMenuItem>();
		contextMenuItems.add(contextMenuItem);

		return contextMenuItems;
	}

}
