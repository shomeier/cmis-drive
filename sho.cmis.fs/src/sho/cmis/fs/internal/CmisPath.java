package sho.cmis.fs.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmisPath implements Path
{
	private static final Logger LOG = LoggerFactory.getLogger(CmisPath.class.getName());

	private CmisFileSystem cmisFs;
	private FileableCmisObject cmisObject;
	private String path;

	private ArrayList<Path> childrenCache;

	CmisPath(CmisFileSystem cmisFs, String path, CmisObject cmisObject)
	{
		this.cmisFs = cmisFs;
		this.path = path;
		this.cmisObject = (FileableCmisObject) cmisObject;
	}

	@Override
	public FileSystem getFileSystem()
	{
		LOG.trace("CmisPath: getFileSystem");
		return this.cmisFs;
	}

	@Override
	public boolean isAbsolute()
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: isAbsolute");
		return false;
	}

	@Override
	public Path getRoot()
	{
		LOG.trace("CmisPath: getRoot");
		return cmisFs.getPath("/");
	}

	@Override
	public Path getFileName()
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: getFileName");
		return this;
	}

	@Override
	public Path getParent()
	{
		LOG.trace("CmisPath: getParent");

		CmisCache cmisCache = cmisFs.getCmisCache();
		return cmisCache.getParentCmisPath(path);
	}

	@Override
	public int getNameCount()
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: getNameCount");
		return 0;
	}

	@Override
	public Path getName(int index)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: getName");
		return null;
	}

	@Override
	public Path subpath(int beginIndex, int endIndex)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: subpath");
		return null;
	}

	@Override
	public boolean startsWith(Path other)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: startsWith1");
		return false;
	}

	@Override
	public boolean startsWith(String other)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: startsWith2");
		return false;
	}

	@Override
	public boolean endsWith(Path other)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: endsWith1");
		return false;
	}

	@Override
	public boolean endsWith(String other)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: endsWith2");
		return false;
	}

	@Override
	public Path normalize()
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: normalize");
		return null;
	}

	@Override
	public Path resolve(Path other)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: resolve1");
		return null;
	}

	@Override
	public Path resolve(String other)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: resolve2");
		return null;
	}

	@Override
	public Path resolveSibling(Path other)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: resolveSibling1");
		return null;
	}

	@Override
	public Path resolveSibling(String other)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: resolveSibling2");
		return null;
	}

	@Override
	public Path relativize(Path other)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: relativize");
		return null;
	}

	@Override
	public URI toUri()
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: toUri");
		return null;
	}

	@Override
	public Path toAbsolutePath()
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: toAbsolutePath");
		return null;
	}

	@Override
	public Path toRealPath(LinkOption... options) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: toRealPath");
		return null;
	}

	@Override
	public File toFile()
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: toFile");
		return null;
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: register1");
		return null;
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: register2");
		return null;
	}

	@Override
	public Iterator<Path> iterator()
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: iterator");
		return null;
	}

	@Override
	public int compareTo(Path other)
	{
		// TODO Auto-generated method stub
		LOG.trace("CmisPath: compareTo");
		return 0;
	}

	public String getName()
	{
		return Util.getName(this.path);
	}

	public FileableCmisObject getCmisObject()
	{
		return cmisObject;
	}

	public Iterator<Path> getChildren()
	{
		LOG.trace("CmisPath: getChildren()");
		CmisCache cmisCache = this.cmisFs.getCmisCache();
		return cmisCache.getChildren(this.path).values().iterator();
	}

	public String toString()
	{
		return cmisObject.getName();
	}
}
