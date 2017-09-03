package sho.cmis.fs.internal;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmisFileStore extends FileStore
{
	private static final Logger LOG = LoggerFactory.getLogger(CmisFileStore.class.getName());

	private static final long SPACE = 10000000000000l;

	@Override
	public String name()
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FileStore name");
		return null;
	}

	@Override
	public String type()
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FileStore type");
		return null;
	}

	@Override
	public boolean isReadOnly()
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FileStore isReadOnly");
		return false;
	}

	@Override
	public long getTotalSpace() throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FileStore getTotalSpace");
		return SPACE;
	}

	@Override
	public long getUsableSpace() throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FileStore getUsableSpace");
		return SPACE;
	}

	@Override
	public long getUnallocatedSpace() throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FileStore getUnallocatedSpace");
		return SPACE;
	}

	@Override
	public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type)
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FileStore supportsFileAttributeView1");
		return false;
	}

	@Override
	public boolean supportsFileAttributeView(String name)
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FileStore supportsFileAttributeView2");
		return false;
	}

	@Override
	public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type)
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FileStore getFileStoreAttributeView");
		return null;
	}

	@Override
	public Object getAttribute(String attribute) throws IOException
	{
		// TODO Auto-generated method stub
		LOG.trace("IN FileStore getAttribute");
		return null;
	}

}
