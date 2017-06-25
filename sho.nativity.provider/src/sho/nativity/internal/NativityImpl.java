package sho.nativity.internal;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.nativity.control.NativityControl;
import com.liferay.nativity.control.NativityControlUtil;

/**
 *
 */
@Component(name = NativityImpl.COMPONENT_NAME, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = NativityConfig.class)
public class NativityImpl
{
	final NativityControl nativityControl = NativityControlUtil.getNativityControl();

	static final String COMPONENT_NAME = "sho.nativity";

	private static final Logger LOG = LoggerFactory.getLogger(NativityImpl.class.getName());

	@Activate
	void activate(NativityConfig config)
	{
		LOG.trace("Activating component: {} ...", COMPONENT_NAME);
		setupNativity(config);
		LOG.debug("Activated component: {}", COMPONENT_NAME);
	}

	private void setupNativity(final NativityConfig config)
	{
		final String[] filterFolders = config.filter_folders().split(",");
		for (int i = 0; i < filterFolders.length; i++)
		{
			filterFolders[i] = filterFolders[i].trim();
		}

		nativityControl.connect();
		nativityControl.setFilterFolders(filterFolders);
	}

}
