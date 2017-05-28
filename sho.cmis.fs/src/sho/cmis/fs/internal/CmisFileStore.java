package sho.cmis.fs.internal;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

public class CmisFileStore extends FileStore
{

	@Override
	public String name()
	{
		// TODO Auto-generated method stub
		System.out.println("IN FileStore name");
		return null;
	}

	@Override
	public String type()
	{
		// TODO Auto-generated method stub
		System.out.println("IN FileStore type");
		return null;
	}

	@Override
	public boolean isReadOnly()
	{
		// TODO Auto-generated method stub
		System.out.println("IN FileStore isReadOnly");
		return false;
	}

	@Override
	public long getTotalSpace() throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FileStore getTotalSpace");
		return 0;
	}

	@Override
	public long getUsableSpace() throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FileStore getUsableSpace");
		return 0;
	}

	@Override
	public long getUnallocatedSpace() throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FileStore getUnallocatedSpace");
		return 0;
	}

	@Override
	public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type)
	{
		// TODO Auto-generated method stub
		System.out.println("IN FileStore supportsFileAttributeView1");
		return false;
	}

	@Override
	public boolean supportsFileAttributeView(String name)
	{
		// TODO Auto-generated method stub
		System.out.println("IN FileStore supportsFileAttributeView2");
		return false;
	}

	@Override
	public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type)
	{
		// TODO Auto-generated method stub
		System.out.println("IN FileStore getFileStoreAttributeView");
		return null;
	}

	@Override
	public Object getAttribute(String attribute) throws IOException
	{
		// TODO Auto-generated method stub
		System.out.println("IN FileStore getAttribute");
		return null;
	}

}
