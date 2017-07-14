package sho.cmis.drive.nativity.menu.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.Session;
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

	// TODO: Get from some central config (ConfigAdmin ?)
	private static final String MOUNT_POINT = "/Volumes/Shorty_JetDrive_1/mount";

	@Reference
	Session cmisSession;

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
			ContextMenuItem renditionContextMenuItem = createRenditionContextmenuItem(paths);
			ContextMenuItem versionContextMenuItem = createVersionContextmenuItem(paths);

			contextMenuItems.add(renditionContextMenuItem);
			contextMenuItems.add(versionContextMenuItem);

		}
		return contextMenuItems;
	}

	private ContextMenuItem createVersionContextmenuItem(String paths[])
	{
		ContextMenuItem contextMenuItem = new ContextMenuItem("Versions");

		ContextMenuAction contextMenuAction = new ContextMenuAction()
		{
			@Override
			public void onSelection(String[] paths)
			{
				for (String path : paths)
				{
					System.out.println("Absolute Path: " + path);
					System.out.println("CMIS Path: " + getCmisPath(path));
				}
			}
		};

		// only react on single selection
		if (paths.length == 1)
		{
			String cmisPath = getCmisPath(paths[0]);
			CmisObject cmisObject = cmisSession.getObjectByPath(cmisPath);
			System.out.println("CMISObject: " + cmisObject);
			if (cmisObject instanceof Document)
			{
				Document cmisDocument = (Document) cmisObject;
				List<Document> versions = cmisDocument.getAllVersions();
				System.out.println("Number of versions: " + versions.size());
				for (Document document : versions)
				{
					ContextMenuItem cmi = getContextMenuItem(document);
					// contextMenuItem.addSeparator();
					contextMenuItem.addContextMenuItem(cmi);
				}
			}
		}

		contextMenuItem.setContextMenuAction(contextMenuAction);
		contextMenuItem.setIconPath(OVERLAY_CMIS_ICON);
		contextMenuItem.setIconId("1");
		contextMenuItem.setHelpText("HelpText");
		contextMenuItem.setEnabled(true);

		return contextMenuItem;
	}

	private ContextMenuItem createRenditionContextmenuItem(String paths[])
	{
		ContextMenuItem contextMenuItem = new ContextMenuItem("Renditions");

		ContextMenuAction contextMenuAction = new ContextMenuAction()
		{
			@Override
			public void onSelection(String[] paths)
			{
				for (String path : paths)
				{
					System.out.println("Absolute Path: " + path);
					System.out.println("CMIS Path: " + getCmisPath(path));
				}
			}
		};

		// only react on single selection
		if (paths.length == 1)
		{
			String cmisPath = getCmisPath(paths[0]);
			CmisObject cmisObject = cmisSession.getObjectByPath(cmisPath);
			System.out.println("CMISObject: " + cmisObject);
			List<Rendition> renditions = cmisObject.getRenditions();
			if (renditions != null)
			{
				System.out.println("Number of renditions: " + renditions.size());
				for (Rendition rendition : renditions)
				{
					ContextMenuItem cmi = getContextMenuItem(rendition);
					// contextMenuItem.addSeparator();
					contextMenuItem.addContextMenuItem(cmi);
				}
			}
		}

		contextMenuItem.setContextMenuAction(contextMenuAction);
		contextMenuItem.setIconPath(OVERLAY_CMIS_ICON);
		contextMenuItem.setIconId("1");
		contextMenuItem.setHelpText("HelpText");
		contextMenuItem.setEnabled(true);

		return contextMenuItem;
	}

	private ContextMenuItem getContextMenuItem(Rendition rendition)
	{
		String title = rendition.getTitle();
		ContextMenuItem contextMenuItem = new ContextMenuItem(title);
		contextMenuItem.setIconPath(OVERLAY_CMIS_ICON);
		return contextMenuItem;
	}

	private ContextMenuItem getContextMenuItem(Document version)
	{
		String title = version.getVersionLabel();
		ContextMenuItem contextMenuItem = new ContextMenuItem(title);
		contextMenuItem.setIconPath(OVERLAY_CMIS_ICON);
		return contextMenuItem;
	}

	private String getCmisPath(String absolutePath)
	{
		return absolutePath.substring(MOUNT_POINT.length());
	}

}
