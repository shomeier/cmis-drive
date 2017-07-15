package sho.cmis.fs.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmisCache
{
	private static final Logger LOG = LoggerFactory.getLogger(CmisCache.class.getName());

	private final Session session;

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private CmisFileSystem cmisFs;

	private CmisPath rootFolder;

	// key: path (e.g. '/' for root folder)
	// value: a map with
	// key: path (e.g. '/mySubfolder')
	// value: CmisPath Object
	private Map<String, Map<String, Path>> childrenCache;

	CmisCache(Session session)
	{
		this.session = session;
		this.childrenCache = new HashMap<>();
	}

	void setFilesystem(CmisFileSystem cmisFs)
	{
		this.cmisFs = cmisFs;
	}

	Map<String, Path> getChildren(String path)
	{
		lock.writeLock().lock();
		Map<String, Path> retVal = null;
		try
		{
			boolean cacheHit = childrenCache.containsKey(path);
			if (!cacheHit)
			{
				CmisObject folder = session.getObjectByPath(path);
				if (folder instanceof Folder)
				{
					ItemIterable<CmisObject> children = ( (Folder) folder ).getChildren();
					Map<String, Path> childrenMap = new HashMap<>();
					for (CmisObject cmisObject : children)
					{
						String cmisPath = path + cmisObject.getName();
						childrenMap.put(cmisPath, new CmisPath(cmisFs, cmisPath, cmisObject));
					}
					childrenCache.put(path, childrenMap);
				}
			}
			retVal = childrenCache.get(path);
			return retVal;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	Path getCmisPath(String path)
	{
		if (path.endsWith(".DS_Store") || ( path.endsWith(".hidden") ))
			return null;

		if (path.equals("/"))
			return getRootCmisPath();

		String parent = Util.getParentPath(path);
		Map<String, Path> children = getChildren(parent);
		Path retVal = children.get(path);
		return retVal;
	}

	Path getCmisPath(CmisPath path)
	{
		String fullPath = getFullPath(path);
		Path cmisPath = getCmisPath(fullPath);
		return cmisPath;
	}

	Path getParentCmisPath(String path)
	{
		if (path.equals("/"))
			return null;

		String parent = Util.getParentPath(path);
		return getCmisPath(parent);
	}

	CmisPath getRootCmisPath()
	{
		if (rootFolder == null)
			rootFolder = new CmisPath(cmisFs, "/", session.getRootFolder());
		return rootFolder;
	}

	SeekableByteChannel getContent(CmisPath path) throws IOException
	{
		CmisPath cmisPath = (CmisPath) getCmisPath(path);
		FileableCmisObject cmisObject = cmisPath.getCmisObject();

		lock.writeLock().lock();
		try
		{
			if (cmisObject instanceof Document)
			{
				Path target = Paths.get("/Volumes/Shorty_JetDrive_1/drive_tmp/" + cmisObject.getId());
				if (!Files.exists(target))
				{
					ContentStream contentStream = ( (Document) cmisObject ).getContentStream();
					InputStream stream = contentStream.getStream();
					Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
				}
				SeekableByteChannel byteChannel = Files.newByteChannel(target);
				return byteChannel;
			}

			return null;
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	private String getFullPath(CmisPath path)
	{
		// CmisFileableObject.getPaths() should not be called since it always results in a getParent remote call for Documents
		// for Folder we can avoid another remote call by Including PathsSegments in OpCtx
		// List<String> paths = ( (CmisPath) path ).getCmisObject().getPaths();
		// return paths.get(0);
		return path.getFullPath();
	}
}
