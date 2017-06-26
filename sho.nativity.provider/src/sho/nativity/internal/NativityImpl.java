package sho.nativity.internal;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.nativity.control.NativityControl;
import com.liferay.nativity.control.NativityControlUtil;
import com.liferay.nativity.modules.fileicon.FileIconControl;
import com.liferay.nativity.modules.fileicon.FileIconControlCallback;
import com.liferay.nativity.modules.fileicon.FileIconControlUtil;

/**
 *
 */
@Component(name = NativityImpl.COMPONENT_NAME, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = Config.class)
public class NativityImpl
{
	final NativityControl nativityControl = NativityControlUtil.getNativityControl();

	static final String COMPONENT_NAME = "sho.nativity";

	private static final Logger LOG = LoggerFactory.getLogger(NativityImpl.class.getName());

	@Reference
	private FileIconControlCallback fileIconCB;

	@Activate
	private void activate(Config config)
	{
		LOG.trace("Activating component: {} ...", COMPONENT_NAME);
		setupNativity(config);
		LOG.debug("Activated component: {}", COMPONENT_NAME);
	}

	private void setupNativity(final Config config)
	{
		final String[] filterFolders = config.filter_folders().split(",");
		for (int i = 0; i < filterFolders.length; i++)
		{
			filterFolders[i] = filterFolders[i].trim();
		}

		nativityControl.connect();
		nativityControl.setFilterFolders(filterFolders);

		FileIconControl fileIconControl = FileIconControlUtil.getFileIconControl(nativityControl, this.fileIconCB);
		fileIconControl.enableFileIcons();

	}

}
