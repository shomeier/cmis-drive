package sho.cmis.client.session.factory.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(name = CmisSessionFactory.COMPONENT_NAME, immediate = true)
public class CmisSessionFactory implements org.apache.chemistry.opencmis.client.api.SessionFactory
{
	private static final Logger LOG = LoggerFactory.getLogger(CmisSessionFactory.class.getName());

	static final String COMPONENT_NAME = "sho.cmis.client.session.factory";

	@Activate
	public void activate(Map<String, ?> properties) throws Exception
	{
		LOG.debug("Activated component: {}", COMPONENT_NAME);
	}

	@Override
	public Session createSession(Map<String, String> parameters)
	{
		CmisSessionWrapper session = new CmisSessionWrapper(parameters, null, null, null, null);
		session.connect();

		OperationContext opCtx = session.getDefaultContext();
		Set<String> filter = new HashSet<>();
		filter.add("cmis:baseTypeId");
		filter.add("cmis:objectId");
		filter.add("cmis:objectTypeId");
		filter.add("cmis:name");
		filter.add("cmis:contentStreamLength");
		filter.add("cmis:contentStreamFileName");
		filter.add("cmis:versionLabel");
		filter.add("cmis:versionSeriesId");
		opCtx.setFilter(filter);
		opCtx.setIncludePathSegments(true);
		session.setDefaultContext(opCtx);

		return session;
	}

	@Override
	public List<Repository> getRepositories(Map<String, String> parameters)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
