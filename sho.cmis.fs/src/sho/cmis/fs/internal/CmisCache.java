package sho.cmis.fs.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

public class CmisCache
{
	private final Session session;

	private CmisFileSystem cmisFs;

	private CmisPath rootFolder;

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
		if (!childrenCache.containsKey(path))
		{
			CmisObject folder = session.getObjectByPath(path);
			if (folder instanceof Folder)
			{
				ItemIterable<CmisObject> children = ( (Folder) folder ).getChildren();
				Map<String, Path> childrenMap = new HashMap<>();
				for (CmisObject cmisObject : children)
				{
					// String cmisPath = path;
					// if (!path.equals("/"))
					// {
					// cmisPath = cmisPath + "/";
					// }
					String cmisPath = path + cmisObject.getName();
					System.out.println("Putting in cache: " + cmisPath);
					childrenMap.put(cmisPath, new CmisPath(cmisFs, cmisPath, cmisObject));
				}
				childrenCache.put(path, childrenMap);
			}
		}
		return childrenCache.get(path);
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
		System.out.println("RetVal for path = " + path + ": " + retVal);
		return retVal;
	}

	Path getCmisPath(Path path)
	{
		return getCmisPath(getFullPath(path));
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

	SeekableByteChannel getContent(Path path) throws IOException
	{
		System.out.println("In getContent for path: " + path);
		// if (getFullPath(path).endsWith("sample2.jpg"))
		// {
		CmisPath cmisPath = (CmisPath) getCmisPath(path);
		FileableCmisObject cmisObject = cmisPath.getCmisObject();
		System.out.println("In getContent for ID: " + cmisObject.getId());

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
		// }

		return null;
	}

	private String getFullPath(Path path)
	{
		List<String> paths = ( (CmisPath) path ).getCmisObject().getPaths();
		System.out.println("Full path: " + paths.get(0));
		return paths.get(0);
	}
}
