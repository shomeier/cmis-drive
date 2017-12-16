package sho.cmis.client.session.internal;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.ChangeEvent;
import org.apache.chemistry.opencmis.client.api.ChangeEvents;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.QueryStatement;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.BulkUpdateObjectIdAndChangeToken;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.spi.CmisBinding;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(name = CmisSession.COMPONENT_NAME, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = CmisSessionCfg.class)
public class CmisSession implements org.apache.chemistry.opencmis.client.api.Session
{
	private static final Logger LOG = LoggerFactory.getLogger(CmisSession.class.getName());

	static final String COMPONENT_NAME = "sho.cmis.client.session";

	@Reference
	SessionFactory sessionFactory;

	private Session session;

	@Activate
	public void activate(final CmisSessionCfg config) throws Exception
	{
		LOG.info("Activating component: {} ...", COMPONENT_NAME);

		Map<String, String> envConfig = new HashMap<>();
		envConfig.put(SessionParameter.BINDING_TYPE, "browser");
		envConfig.put(SessionParameter.BROWSER_URL, config.url());
		envConfig.put(SessionParameter.USER, config.user());
		envConfig.put(SessionParameter.PASSWORD, config.password());
		if (config.repository_id() != null)
			envConfig.put(SessionParameter.REPOSITORY_ID, config.repository_id());
		envConfig.put(SessionParameter.CACHE_PATH_OMIT, "false");

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

		if (config.repository_id() != null)
			session = sessionFactory.createSession(envConfig);
		else
			session = sessionFactory.getRepositories(envConfig).get(0).createSession();
		session.setDefaultContext(opCtx);

		// inject our session into object factory so that all calls come in here ...
		session.getObjectFactory().initialize(this, envConfig);

		// register CMIS Session as OSGi Service
		// ServiceRegistration<Session> serviceRegistration = bc.registerService(Session.class, session, null);

		LOG.debug("Activated component: {}", COMPONENT_NAME);
	}

	@Override
	public void clear()
	{
		session.clear();
	}

	@Override
	public CmisBinding getBinding()
	{
		return session.getBinding();
	}

	@Override
	public Map<String, String> getSessionParameters()
	{
		return session.getSessionParameters();
	}

	@Override
	public OperationContext getDefaultContext()
	{
		return session.getDefaultContext();
	}

	@Override
	public void setDefaultContext(OperationContext context)
	{
		session.setDefaultContext(context);
	}

	@Override
	public OperationContext createOperationContext()
	{
		return session.createOperationContext();
	}

	@Override
	public OperationContext createOperationContext(Set<String> filter, boolean includeAcls, boolean includeAllowableActions,
		boolean includePolicies, IncludeRelationships includeRelationships, Set<String> renditionFilter,
		boolean includePathSegments, String orderBy, boolean cacheEnabled, int maxItemsPerPage)
	{
		return session.createOperationContext(filter, includeAcls, includeAllowableActions, includePolicies, includeRelationships,
			renditionFilter, includePathSegments, orderBy, cacheEnabled, maxItemsPerPage);
	}

	@Override
	public ObjectId createObjectId(String id)
	{
		return session.createObjectId(id);
	}

	@Override
	public Locale getLocale()
	{
		return session.getLocale();
	}

	@Override
	public RepositoryInfo getRepositoryInfo()
	{
		return session.getRepositoryInfo();
	}

	@Override
	public ObjectFactory getObjectFactory()
	{
		return session.getObjectFactory();
	}

	@Override
	public ObjectType getTypeDefinition(String typeId)
	{
		return session.getTypeDefinition(typeId);
	}

	@Override
	public ObjectType getTypeDefinition(String typeId, boolean useCache)
	{
		return session.getTypeDefinition(typeId, useCache);
	}

	@Override
	public ItemIterable<ObjectType> getTypeChildren(String typeId, boolean includePropertyDefinitions)
	{
		return session.getTypeChildren(typeId, includePropertyDefinitions);
	}

	@Override
	public List<Tree<ObjectType>> getTypeDescendants(String typeId, int depth, boolean includePropertyDefinitions)
	{
		return session.getTypeDescendants(typeId, depth, includePropertyDefinitions);
	}

	@Override
	public ObjectType createType(TypeDefinition type)
	{
		return session.createType(type);
	}

	@Override
	public ObjectType updateType(TypeDefinition type)
	{
		return session.updateType(type);
	}

	@Override
	public void deleteType(String typeId)
	{
		session.deleteType(typeId);
	}

	@Override
	public Folder getRootFolder()
	{
		return session.getRootFolder();
	}

	@Override
	public Folder getRootFolder(OperationContext context)
	{
		return session.getRootFolder(context);
	}

	@Override
	public ItemIterable<Document> getCheckedOutDocs()
	{
		return session.getCheckedOutDocs();
	}

	@Override
	public ItemIterable<Document> getCheckedOutDocs(OperationContext context)
	{
		return session.getCheckedOutDocs(context);
	}

	@Override
	public CmisObject getObject(ObjectId objectId)
	{
		return session.getObject(objectId);
	}

	@Override
	public CmisObject getObject(ObjectId objectId, OperationContext context)
	{
		return session.getObject(objectId, context);
	}

	@Override
	public CmisObject getObject(String objectId)
	{
		return session.getObject(objectId);
	}

	@Override
	public CmisObject getObject(String objectId, OperationContext context)
	{
		return session.getObject(objectId, context);
	}

	@Override
	public CmisObject getObjectByPath(String path)
	{
		return session.getObjectByPath(path);
	}

	@Override
	public CmisObject getObjectByPath(String path, OperationContext context)
	{
		return session.getObjectByPath(path, context);
	}

	@Override
	public CmisObject getObjectByPath(String parentPath, String name)
	{
		return session.getObjectByPath(parentPath, name);
	}

	@Override
	public CmisObject getObjectByPath(String parentPath, String name, OperationContext context)
	{
		return session.getObjectByPath(parentPath, name, context);
	}

	@Override
	public Document getLatestDocumentVersion(ObjectId objectId)
	{
		return session.getLatestDocumentVersion(objectId);
	}

	@Override
	public Document getLatestDocumentVersion(ObjectId objectId, OperationContext context)
	{
		return session.getLatestDocumentVersion(objectId, context);
	}

	@Override
	public Document getLatestDocumentVersion(ObjectId objectId, boolean major, OperationContext context)
	{
		return session.getLatestDocumentVersion(objectId, major, context);
	}

	@Override
	public Document getLatestDocumentVersion(String objectId)
	{
		return session.getLatestDocumentVersion(objectId);
	}

	@Override
	public Document getLatestDocumentVersion(String objectId, OperationContext context)
	{
		return session.getLatestDocumentVersion(objectId, context);
	}

	@Override
	public Document getLatestDocumentVersion(String objectId, boolean major, OperationContext context)
	{
		return session.getLatestDocumentVersion(objectId, major, context);
	}

	@Override
	public boolean exists(ObjectId objectId)
	{
		return session.exists(objectId);
	}

	@Override
	public boolean exists(String objectId)
	{
		return session.exists(objectId);
	}

	@Override
	public boolean existsPath(String path)
	{
		return session.existsPath(path);
	}

	@Override
	public boolean existsPath(String parentPath, String name)
	{
		return session.existsPath(parentPath, name);
	}

	@Override
	public void removeObjectFromCache(ObjectId objectId)
	{
		session.removeObjectFromCache(objectId);
	}

	@Override
	public void removeObjectFromCache(String objectId)
	{
		session.removeObjectFromCache(objectId);
	}

	@Override
	public ItemIterable<QueryResult> query(String statement, boolean searchAllVersions)
	{
		return session.query(statement, searchAllVersions);
	}

	@Override
	public ItemIterable<QueryResult> query(String statement, boolean searchAllVersions, OperationContext context)
	{
		return session.query(statement, searchAllVersions, context);
	}

	@Override
	public ItemIterable<CmisObject> queryObjects(String typeId, String where, boolean searchAllVersions, OperationContext context)
	{
		return session.queryObjects(typeId, where, searchAllVersions, context);
	}

	@Override
	public QueryStatement createQueryStatement(String statement)
	{
		return session.createQueryStatement(statement);
	}

	@Override
	public QueryStatement createQueryStatement(Collection<String> selectPropertyIds, Map<String, String> fromTypes,
		String whereClause, List<String> orderByPropertyIds)
	{
		return session.createQueryStatement(selectPropertyIds, fromTypes, whereClause, orderByPropertyIds);
	}

	@Override
	public ChangeEvents getContentChanges(String changeLogToken, boolean includeProperties, long maxNumItems)
	{
		return session.getContentChanges(changeLogToken, includeProperties, maxNumItems);
	}

	@Override
	public ChangeEvents getContentChanges(String changeLogToken, boolean includeProperties, long maxNumItems,
		OperationContext context)
	{
		return session.getContentChanges(changeLogToken, includeProperties, maxNumItems, context);
	}

	@Override
	public ItemIterable<ChangeEvent> getContentChanges(String changeLogToken, boolean includeProperties)
	{
		return session.getContentChanges(changeLogToken, includeProperties);
	}

	@Override
	public ItemIterable<ChangeEvent> getContentChanges(String changeLogToken, boolean includeProperties, OperationContext context)
	{
		return session.getContentChanges(changeLogToken, includeProperties, context);
	}

	@Override
	public String getLatestChangeLogToken()
	{
		return session.getLatestChangeLogToken();
	}

	@Override
	public ObjectId createDocument(Map<String, ?> properties, ObjectId folderId, ContentStream contentStream,
		VersioningState versioningState, List<Policy> policies, List<Ace> addAces, List<Ace> removeAces)
	{
		return session.createDocument(properties, folderId, contentStream, versioningState, policies, addAces, removeAces);
	}

	@Override
	public ObjectId createDocument(Map<String, ?> properties, ObjectId folderId, ContentStream contentStream,
		VersioningState versioningState)
	{
		return session.createDocument(properties, folderId, contentStream, versioningState);
	}

	@Override
	public ObjectId createDocumentFromSource(ObjectId source, Map<String, ?> properties, ObjectId folderId,
		VersioningState versioningState, List<Policy> policies, List<Ace> addAces, List<Ace> removeAces)
	{
		return session.createDocumentFromSource(source, properties, folderId, versioningState, policies, addAces, removeAces);
	}

	@Override
	public ObjectId createDocumentFromSource(ObjectId source, Map<String, ?> properties, ObjectId folderId,
		VersioningState versioningState)
	{
		return session.createDocumentFromSource(source, properties, folderId, versioningState);
	}

	@Override
	public ObjectId createFolder(Map<String, ?> properties, ObjectId folderId, List<Policy> policies, List<Ace> addAces,
		List<Ace> removeAces)
	{
		return session.createFolder(properties, folderId, policies, addAces, removeAces);
	}

	@Override
	public ObjectId createFolder(Map<String, ?> properties, ObjectId folderId)
	{
		return session.createFolder(properties, folderId);
	}

	@Override
	public ObjectId createPolicy(Map<String, ?> properties, ObjectId folderId, List<Policy> policies, List<Ace> addAces,
		List<Ace> removeAces)
	{
		return session.createPolicy(properties, folderId, policies, addAces, removeAces);
	}

	@Override
	public ObjectId createPolicy(Map<String, ?> properties, ObjectId folderId)
	{
		return session.createPolicy(properties, folderId);
	}

	@Override
	public ObjectId createItem(Map<String, ?> properties, ObjectId folderId, List<Policy> policies, List<Ace> addAces,
		List<Ace> removeAces)
	{
		return session.createItem(properties, folderId, policies, addAces, removeAces);
	}

	@Override
	public ObjectId createItem(Map<String, ?> properties, ObjectId folderId)
	{
		return session.createItem(properties, folderId);
	}

	@Override
	public ObjectId createRelationship(Map<String, ?> properties, List<Policy> policies, List<Ace> addAces, List<Ace> removeAces)
	{
		return session.createRelationship(properties, policies, addAces, removeAces);
	}

	@Override
	public ObjectId createRelationship(Map<String, ?> properties)
	{
		return session.createRelationship(properties);
	}

	@Override
	public ItemIterable<Relationship> getRelationships(ObjectId objectId, boolean includeSubRelationshipTypes,
		RelationshipDirection relationshipDirection, ObjectType type, OperationContext context)
	{
		return session.getRelationships(objectId, includeSubRelationshipTypes, relationshipDirection, type, context);
	}

	@Override
	public List<BulkUpdateObjectIdAndChangeToken> bulkUpdateProperties(List<CmisObject> objects, Map<String, ?> properties,
		List<String> addSecondaryTypeIds, List<String> removeSecondaryTypeIds)
	{
		return session.bulkUpdateProperties(objects, properties, addSecondaryTypeIds, removeSecondaryTypeIds);
	}

	@Override
	public void delete(ObjectId objectId)
	{
		session.delete(objectId);
	}

	@Override
	public void delete(ObjectId objectId, boolean allVersions)
	{
		session.delete(objectId, allVersions);
	}

	@Override
	public List<String> deleteTree(ObjectId folderId, boolean allVersions, UnfileObject unfile, boolean continueOnFailure)
	{
		return session.deleteTree(folderId, allVersions, unfile, continueOnFailure);
	}

	@Override
	public ContentStream getContentStream(ObjectId docId)
	{
		return session.getContentStream(docId);
	}

	@Override
	public ContentStream getContentStream(ObjectId docId, String streamId, BigInteger offset, BigInteger length)
	{
		return session.getContentStream(docId, streamId, offset, length);
	}

	@Override
	public Acl getAcl(ObjectId objectId, boolean onlyBasicPermissions)
	{
		return session.getAcl(objectId, onlyBasicPermissions);
	}

	@Override
	public Acl applyAcl(ObjectId objectId, List<Ace> addAces, List<Ace> removeAces, AclPropagation aclPropagation)
	{
		return session.applyAcl(objectId, addAces, removeAces, aclPropagation);
	}

	@Override
	public Acl setAcl(ObjectId objectId, List<Ace> aces)
	{
		return session.setAcl(objectId, aces);
	}

	@Override
	public void applyPolicy(ObjectId objectId, ObjectId... policyIds)
	{
		session.applyPolicy(objectId, policyIds);
	}

	@Override
	public void removePolicy(ObjectId objectId, ObjectId... policyIds)
	{
		session.removePolicy(objectId, policyIds);
	}
}
