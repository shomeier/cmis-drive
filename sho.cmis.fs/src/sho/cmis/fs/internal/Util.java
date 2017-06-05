package sho.cmis.fs.internal;

public final class Util
{
	public static String getParentPath(String path)
	{
		int endIndex = path.lastIndexOf("/");
		String parent = path.substring(0, endIndex + 1);
		return parent;
	}

	public static String getName(String path)
	{
		int endIndex = path.lastIndexOf("/");
		String name = path.substring(endIndex + 1, path.length());
		return name;
	}
}
