package sho.cmis.fs.internal;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmisFileSystem extends FileSystem
{
	private static final Logger LOG = LoggerFactory.getLogger(CmisFileSystem.class.getName());

	private final String ROOT_PATH = "/";

	private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	private final CmisFileSystemProvider provider;
	private final URI uri;
	private final CmisCache cache;

	// private Map<String, CmisPath> cache = new HashMap<>();

	CmisFileSystem(CmisFileSystemProvider provider, URI uri, CmisCache cache)
	{
		this.provider = provider;
		this.uri = uri;
		this.cache = cache;
		// ServiceTracker<Object, Object> serviceTracker = new ServiceTracker<>(context, SessionFactory.class.getName(), null);
		// serviceTracker.open();
		// SessionFactory sessionFactory = ( (SessionFactory) serviceTracker.getService() );
	}

	@Override
	public FileSystemProvider provider()
	{
		LOG.trace("IN FS provider!!!");
		return this.provider;
	}

	public CmisCache getCache()
	{
		return cache;
	}

	@Override
	public void close() throws IOException
	{
		LOG.trace("IN FS close!!!");
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOpen()
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FS isOpen!!!");
		return false;
	}

	@Override
	public boolean isReadOnly()
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FS isReadOnly!!!");
		return false;
	}

	@Override
	public String getSeparator()
	{
		LOG.trace("IN FS getSeparator!!!");
		return "/";
	}

	@Override
	public Iterable<Path> getRootDirectories()
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FS getRootDirectories!!!");

		ArrayList<Path> pathArr = new ArrayList<>();
		pathArr.add(getRootPath());
		return pathArr;
	}

	@Override
	public Iterable<FileStore> getFileStores()
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FS getFileStores!!!");
		List<FileStore> list = new LinkedList<FileStore>();
		list.add(new CmisFileStore());
		return list;
	}

	@Override
	public Set<String> supportedFileAttributeViews()
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FS supportedFileAttributeViews!!!");
		return null;
	}

	@Override
	public Path getPath(String first, String... more)
	{
		// TODO Auto-generated method stub
		LOG.debug("IN FS getPath with first: " + first);
		return cache.getCmisPath(first);
	}

	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern)
	{
		LOG.trace("IN FS getPathMatcher!!!");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService()
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FS getUserPrincipalLookupService!!!");
		return null;
	}

	@Override
	public WatchService newWatchService() throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FS newWatchService!!!");
		return null;
	}

	public CmisCache getCmisCache()
	{
		return this.cache;
	}

	private CmisPath getRootPath()
	{
		return cache.getRootCmisPath();
	}

}
