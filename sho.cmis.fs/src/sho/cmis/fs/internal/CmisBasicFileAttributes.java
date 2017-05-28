package sho.cmis.fs.internal;

import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashSet;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;

public class CmisBasicFileAttributes implements PosixFileAttributes
{
	CmisPath cmisPath;

	public CmisBasicFileAttributes(CmisPath cmisPath)
	{
		this.cmisPath = cmisPath;
	}

	@Override
	public FileTime lastModifiedTime()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileTime lastAccessTime()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileTime creationTime()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRegularFile()
	{
		// TODO Auto-generated method stub
		if (cmisPath.getCmisObject() instanceof Document)
			return true;
		return false;
	}

	@Override
	public boolean isDirectory()
	{
		// TODO Auto-generated method stub
		if (cmisPath.getCmisObject() instanceof Folder)
			return true;
		return false;
	}

	@Override
	public boolean isSymbolicLink()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOther()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object fileKey()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserPrincipal owner()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GroupPrincipal group()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<PosixFilePermission> permissions()
	{
		Set perms = new HashSet();
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OTHERS_READ);
		return perms;
	}

}
