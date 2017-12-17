package sho.cmis.client.session.factory.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(name = CmisSessionFactory.COMPONENT_NAME, immediate = true)
public class CmisSessionFactory implements org.apache.chemistry.opencmis.client.api.SessionFactory
{
	private static final Logger LOG = LoggerFactory.getLogger(CmisSessionFactory.class.getName());

	private final BundleContext bc = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	static final String COMPONENT_NAME = "sho.cmis.client.session.factory";

	// private Session session;

	@Activate
	public void activate(Map<String, ?> properties) throws Exception
	{
		LOG.info("Activating component: {} ...", COMPONENT_NAME);

		for (String key : properties.keySet())
		{
			System.out.println("Key: " + key + ", Value: " + properties.get(key));
		}
		// Map<String, String> envConfig = new HashMap<>();
		// envConfig.put(SessionParameter.BINDING_TYPE, "browser");
		// envConfig.put(SessionParameter.BROWSER_URL, config.url());
		// envConfig.put(SessionParameter.USER, config.user());
		// envConfig.put(SessionParameter.PASSWORD, config.password());
		// if (config.repository_id() != null)
		// envConfig.put(SessionParameter.REPOSITORY_ID, config.repository_id());
		// envConfig.put(SessionParameter.CACHE_PATH_OMIT, "false");

		OperationContext opCtx = new OperationContextImpl();
		Set<String> filter = new HashSet<>();
		filter.add("cmis:baseTypeId");
		filter.add("cmis:objectId");
		filter.add("cmis:objectTypeId");
		filter.add("cmis:name");
		filter.add("cmis:contentStreamLength");
		filter.add("cmis:contentStreamFileName");
		filter.add("cmis:versionLabel");
		filter.add("cmis:versionSeriesId");
		// opCtx.setFilter(filter);
		opCtx.setIncludePathSegments(true);

		// if (config.repository_id() != null)
		// session = sessionFactory.createSession(envConfig);
		// else
		// session = sessionFactory.getRepositories(envConfig).get(0).createSession();
		// session.setDefaultContext(opCtx);
		//
		// // inject our session into object factory so that all calls come in here ...
		// session.getObjectFactory().initialize(this, envConfig);

		// CmisSession session = new CmisSession(envConfig, null, null, null, null);

		// register CMIS Session as OSGi Service
		// ServiceRegistration<Session> serviceRegistration = bc.registerService(Session.class, session, null);

		LOG.debug("Activated component: {}", COMPONENT_NAME);
	}

	@Override
	public Session createSession(Map<String, String> parameters)
	{
		SessionWrapper session = new SessionWrapper(parameters, null, null, null, null);
		session.connect();

		return session;
	}

	@Override
	public List<Repository> getRepositories(Map<String, String> parameters)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
