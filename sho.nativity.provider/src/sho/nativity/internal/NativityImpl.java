package sho.nativity.internal;

import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.nativity.control.NativityControl;
import com.liferay.nativity.control.NativityControlUtil;
import com.liferay.nativity.modules.fileicon.FileIconControlCallback;

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

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	private AtomicReference<FileIconControlCallback> fileIconCBs = new AtomicReference<>();

	@Activate
	void activate(Config config)
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

	}

}
