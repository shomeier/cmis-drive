package sho.cmis.fs.internal;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmisFileSystemProvider extends FileSystemProvider
{
	private static final Logger LOG = LoggerFactory.getLogger(CmisFileSystem.class.getName());

	private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	// private SessionFactory sessionFactory;

	private Map<URI, FileSystem> filesystems = new HashMap<>();

	public CmisFileSystemProvider()
	{
		// default ctor
	}

	@Override
	public String getScheme()
	{
		return "cmis";
	}

	protected String uriToPath(URI uri)
	{
		String scheme = uri.getScheme();
		if (( scheme == null ) || !scheme.equalsIgnoreCase(getScheme()))
		{
			throw new IllegalArgumentException("URI scheme is not '" + getScheme() + "'");
		}
		// only support legacy JAR URL syntax jar:{uri}!/{entry} for now
		String spec = uri.getRawSchemeSpecificPart();
		// int sep = spec.indexOf("!/");
		// if (sep != -1)
		// spec = spec.substring(0, sep);
		return spec;
		// return Paths.get(new URI(spec)).toAbsolutePath();
	}

	@Override
	public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException
	{
		LOG.trace("IN FSP newFileSystem(URI uri, Map<String, ?> env)!!!");
		// ServiceTracker<Object, Object> serviceTracker = new ServiceTracker<>(context, SessionFactory.class.getName(), null);
		// serviceTracker.open();
		// SessionFactory sessionFactory = ( (SessionFactory) serviceTracker.getService() );
		// // Session session = sessionFactory.createSession((Map<String, String>) env);
		// Session session = null;
		//
		// // if repository id is set we can directly create the session ...
		// if (env.containsKey(CmisConfig.REPOSITORY_ID))
		// {
		// session = sessionFactory.createSession((Map<String, String>) env);
		//
		// // otherwise we just take the first repository ...
		// }
		// else
		// {
		// List<Repository> repositories = sessionFactory.getRepositories((Map<String, String>) env);
		// session = repositories.get(0).createSession();
		//
		// }
		Session session = (Session) env.get("CmisSession");

		OperationContext opCtx = new OperationContextImpl();
		opCtx.setCacheEnabled(true);
		session.setDefaultContext(opCtx);

		CmisCache cmisCache = new CmisCache(session);
		CmisFileSystem cmisFs = new CmisFileSystem(this, uri, cmisCache);
		// TODO: This weird!
		cmisCache.setFilesystem(cmisFs);

		this.filesystems.put(uri, cmisFs);
		return cmisFs;
	}

	@Override
	public FileSystem getFileSystem(URI uri)
	{
		LOG.trace("IN FSP getFileSystem(URI uri)!!!");
		FileSystem cmisFs = this.filesystems.get(uri);
		return cmisFs;
	}

	@Override
	public Path getPath(URI uri)
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP getPath!!!");
		// return Paths.get(uri);
		return null;
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
		throws IOException
	{
		LOG.trace("IN FSP newByteChannel with path: " + path);

		for (OpenOption option : options)
		{
			LOG.trace("Option: " + option);
		}
		for (FileAttribute<?> fileAttribute : attrs)
		{

			LOG.trace("attrs: " + fileAttribute);
		}

		return ( (CmisFileSystem) path.getFileSystem() ).getCmisCache().getContent(path);
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP newDirectoryStream!!!");
		return new CmisDirectoryStream((CmisPath) dir, filter);
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP createDirectory!!!");

	}

	@Override
	public void delete(Path path) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP Odelete!!!");

	}

	@Override
	public void copy(Path source, Path target, CopyOption... options) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP delete!!!");

	}

	@Override
	public void move(Path source, Path target, CopyOption... options) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP move!!!");

	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP isSameFile!!!");
		return false;
	}

	@Override
	public boolean isHidden(Path path) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP isHidden!!!");
		return false;
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP getFileStore!!!");
		return null;
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP checkAccess!!!");

	}

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options)
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP getFileAttributeView!!!");
		return null;
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP readAttributes1 with path: " + ( (CmisPath) path ).getName());
		LOG.trace("222 N FSP readAttributes1 with path: " + path);
		return (A) new CmisBasicFileAttributes((CmisPath) path);
		// return Files.readAttributes(path, type, options);
		// return null;
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FSP readAttributes2!!!");
		return null;
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException
	{
		LOG.trace("IN FSP setAttribute!!!");
		// TODO Auto-generated method stub

	}

}
