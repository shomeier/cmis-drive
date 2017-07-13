package sho.cmis.drive.nativity.menu.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
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

	@Reference
	SessionFactory sessionFactory;

	@Activate
	private void activate()
	{
		LOG.debug("Activated component: {}", COMPONENT_NAME);
	}

	@Modified
	public void modified()
	{
		LOG.debug("Modified component: {}", COMPONENT_NAME);
	}

	@Deactivate
	public void deactivate()
	{
		LOG.debug("Deactivated component: {}", COMPONENT_NAME);
	}

	@Override
	public List<ContextMenuItem> getContextMenuItems(String[] paths)
	{
		List<ContextMenuItem> contextMenuItems = new ArrayList<ContextMenuItem>();
		if (( paths != null ) && ( paths.length > 0 ))
		{
			ContextMenuItem contextMenuItem = new ContextMenuItem("Renditions");

			ContextMenuAction contextMenuAction = new ContextMenuAction()
			{
				@Override
				public void onSelection(String[] paths)
				{
					for (String path : paths)
					{
						System.out.print(path + ", ");
					}

					System.out.println("selected on path: " + paths[0]);
				}
			};

			contextMenuItem.setContextMenuAction(contextMenuAction);
			contextMenuItem.setIconPath(OVERLAY_CMIS_ICON);
			contextMenuItem.setIconId("1");
			contextMenuItem.setHelpText("HelpText");
			contextMenuItem.setEnabled(true);

			contextMenuItems.add(contextMenuItem);

		}
		return contextMenuItems;
	}

}
