package sho.cmis.client.session.factory.internal;

import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.bindings.cache.TypeDefinitionCache;
import org.apache.chemistry.opencmis.client.runtime.cache.Cache;
import org.apache.chemistry.opencmis.commons.spi.AuthenticationProvider;

public class SessionWrapper extends org.apache.chemistry.opencmis.client.runtime.SessionImpl
{
	private static final long serialVersionUID = 1L;

	public SessionWrapper(Map<String, String> parameters, ObjectFactory objectFactory, AuthenticationProvider authenticationProvider,
		Cache cache, TypeDefinitionCache typeDefCache)
	{
		super(parameters, objectFactory, authenticationProvider, cache, typeDefCache);
	}
}
