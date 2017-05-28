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

import org.apache.chemistry.opencmis.client.api.Session;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class CmisFileSystem extends FileSystem
{
	private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	private final CmisFileSystemProvider provider;
	private final URI uri;
	private final Session session;

	CmisFileSystem(CmisFileSystemProvider provider, URI uri, Session session)
	{
		this.provider = provider;
		this.uri = uri;
		this.session = session;
		// ServiceTracker<Object, Object> serviceTracker = new ServiceTracker<>(context, SessionFactory.class.getName(), null);
		// serviceTracker.open();
		// SessionFactory sessionFactory = ( (SessionFactory) serviceTracker.getService() );
	}

	@Override
	public FileSystemProvider provider()
	{
		System.out.println("IN FS provider!!!");
		return this.provider;
	}

	@Override
	public void close() throws IOException
	{
		System.out.println("IN FS close!!!");
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOpen()
	{
		// TODO Auto-generated method stub
		System.out.println("IN FS isOpen!!!");
		return false;
	}

	@Override
	public boolean isReadOnly()
	{
		// TODO Auto-generated method stub
		System.out.println("IN FS isReadOnly!!!");
		return false;
	}

	@Override
	public String getSeparator()
	{
		System.out.println("IN FS getSeparator!!!");
		return "/";
	}

	@Override
	public Iterable<Path> getRootDirectories()
	{
		// TODO Auto-generated method stub
		System.out.println("IN FS getRootDirectories!!!");

		ArrayList<Path> pathArr = new ArrayList<>();
		pathArr.add(getRootPath());
		return pathArr;
	}

	@Override
	public Iterable<FileStore> getFileStores()
	{
		// TODO Auto-generated method stub
		System.out.println("IN FS getFileStores!!!");
		List<FileStore> list = new LinkedList<FileStore>();
		list.add(new CmisFileStore());
		return list;
	}

	@Override
	public Set<String> supportedFileAttributeViews()
	{
		// TODO Auto-generated method stub
		System.out.println("IN FS supportedFileAttributeViews!!!");
		return null;
	}

	@Override
	public Path getPath(String first, String... more)
	{
		// TODO Auto-generated method stub
		System.out.println("IN FS getPath with first: " + first);

		if (first.equals("/"))
			return getRootPath();
		else
			return new CmisPath(this, session.getObjectByPath(first));
	}

	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern)
	{
		System.out.println("IN FS getPathMatcher!!!");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService()
	{
		// TODO Auto-generated method stub
		System.out.println("IN FS getUserPrincipalLookupService!!!");
		return null;
	}

	@Override
	public WatchService newWatchService() throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FS newWatchService!!!");
		return null;
	}

	private CmisPath getRootPath()
	{
		return new CmisPath(this, session.getRootFolder());
	}

}
