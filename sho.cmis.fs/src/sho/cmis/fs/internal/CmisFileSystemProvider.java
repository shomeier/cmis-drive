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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

public class CmisFileSystemProvider extends FileSystemProvider
{
	private final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

	private SessionFactory sessionFactory;

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

	@Override
	public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException
	{

		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		BundleContext bundleContext = bundle.getBundleContext();
		// TODO Auto-generated method stub
		System.out.println("IN FSP newFileSystem(URI uri, Map<String, ?> env)!!!");
		ServiceTracker<Object, Object> serviceTracker = new ServiceTracker<>(context, SessionFactory.class.getName(), null);
		serviceTracker.open();
		SessionFactory sessionFactory = ( (SessionFactory) serviceTracker.getService() );
		Session session = sessionFactory.createSession(null);

		CmisFileSystem cmisFs = new CmisFileSystem(this, uri, session);
		this.filesystems.put(uri, cmisFs);
		return cmisFs;
	}

	@Override
	public FileSystem getFileSystem(URI uri)
	{
		System.out.println("IN FSP getFileSystem(URI uri)!!!");
		FileSystem cmisFs = this.filesystems.get(uri);
		return cmisFs;
	}

	@Override
	public Path getPath(URI uri)
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP getPath!!!");
		return Paths.get(uri);
		// return null;
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
		throws IOException
	{
		System.out.println("IN FSP newByteChannel!!!");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP newDirectoryStream!!!");
		return null;
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP createDirectory!!!");

	}

	@Override
	public void delete(Path path) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP Odelete!!!");

	}

	@Override
	public void copy(Path source, Path target, CopyOption... options) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP delete!!!");

	}

	@Override
	public void move(Path source, Path target, CopyOption... options) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP move!!!");

	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP isSameFile!!!");
		return false;
	}

	@Override
	public boolean isHidden(Path path) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP isHidden!!!");
		return false;
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP getFileStore!!!");
		return null;
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP checkAccess!!!");

	}

	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options)
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP getFileAttributeView!!!");
		return null;
	}

	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP readAttributes1 with path: " + path);
		return Files.readAttributes(path, type, options);
		// return null;
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FSP readAttributes2!!!");
		return null;
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException
	{
		System.out.println("IN FSP setAttribute!!!");
		// TODO Auto-generated method stub

	}

}
