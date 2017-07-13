package sho.nativity.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.nativity.control.NativityControl;
import com.liferay.nativity.control.NativityControlUtil;
import com.liferay.nativity.listeners.SocketOpenListener;
import com.liferay.nativity.modules.contextmenu.ContextMenuControl;
import com.liferay.nativity.modules.contextmenu.ContextMenuControlCallback;
import com.liferay.nativity.modules.contextmenu.ContextMenuControlUtil;
import com.liferay.nativity.modules.contextmenu.model.ContextMenuItem;
import com.liferay.nativity.modules.fileicon.FileIconControl;
import com.liferay.nativity.modules.fileicon.FileIconControlCallback;
import com.liferay.nativity.modules.fileicon.FileIconControlUtil;

import sho.nativity.ContextMenuItemProvider;

/**
 *
 */
@Component(name = NativityImpl.COMPONENT_NAME, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = Config.class)
public class NativityImpl
{
	final NativityControl nativityControl = NativityControlUtil.getNativityControl();

	static final String COMPONENT_NAME = "sho.nativity";

	private static final String OVERLAY_CMIS_ICON = System.getProperty("icon");

	private static final Logger LOG = LoggerFactory.getLogger(NativityImpl.class.getName());

	@Reference
	private FileIconControlCallback fileIconCB;

	private CopyOnWriteArrayList<ContextMenuItemProvider> contextMenuItemProviders =
		new CopyOnWriteArrayList<ContextMenuItemProvider>();

	@Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC)
	void addContextMenuItemProvider(ContextMenuItemProvider contextMenuItemProvider)
	{
		contextMenuItemProviders.add(contextMenuItemProvider);
	}

	void removeContextMenuItemProvider(ContextMenuItemProvider contextMenuItemProvider)
	{
		contextMenuItemProviders.remove(contextMenuItemProvider);
	}

	@Activate
	private void activate(Config config)
	{
		LOG.trace("Activating component: {} ...", COMPONENT_NAME);
		modified(config);
		LOG.debug("Activated component: {}", COMPONENT_NAME);
	}

	@Modified
	public void modified(Config config)
	{
		LOG.debug("Modifying component: {} ...", COMPONENT_NAME);
		setupNativity(config);
		LOG.debug("Modified component: {}", COMPONENT_NAME);
	}

	@Deactivate
	public void deactivate()
	{
		LOG.debug("Deactivating component: {} ...", COMPONENT_NAME);
		nativityControl.disconnect();
		LOG.debug("Deactivated component: {}", COMPONENT_NAME);
	}

	private void setupNativity(final Config config)
	{
		final String[] filterFolders = config.filter_folders().split(",");
		for (int i = 0; i < filterFolders.length; i++)
		{
			filterFolders[i] = filterFolders[i].trim();
		}

		nativityControl.connect();

		nativityControl.addSocketOpenListener(new SocketOpenListener()
		{

			@Override
			public void onSocketOpen()
			{
				LOG.debug("Nativity - Socket Opened");
				nativityControl.setFilterFolders(filterFolders);
				FileIconControl fileIconControl = FileIconControlUtil.getFileIconControl(nativityControl, fileIconCB);
				fileIconControl.registerIconWithId(OVERLAY_CMIS_ICON, "label", "1");
				fileIconControl.enableFileIcons();

				ContextMenuControlCallback contextMenuControlCB = new ContextMenuControlCallback()
				{
					@Override
					public List<ContextMenuItem> getContextMenuItems(String[] paths)
					{

						List<ContextMenuItem> contextMenuItems = new ArrayList<ContextMenuItem>();

						for (ContextMenuItemProvider contextMenuItemProvider : contextMenuItemProviders)
						{
							contextMenuItems.addAll(contextMenuItemProvider.getContextMenuItems(paths));
						}

						return contextMenuItems;
					}
				};

				ContextMenuControl contextMenuControl =
					ContextMenuControlUtil.getContextMenuControl(nativityControl, contextMenuControlCB);
				contextMenuControl.registerIcon(OVERLAY_CMIS_ICON, "1");
			}
		});
	}

}
