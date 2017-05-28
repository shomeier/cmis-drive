package sho.cmis.fs.internal;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;

public class CmisDirectoryStream implements DirectoryStream<Path>
{
	private CmisPath cmisPath;

	CmisDirectoryStream(CmisPath cmisPath, Filter<? super Path> filter)
	{
		this.cmisPath = cmisPath;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void close() throws IOException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<Path> iterator()
	{
		// TODO Auto-generated method stub
		return cmisPath.getChildren();
	}

}
