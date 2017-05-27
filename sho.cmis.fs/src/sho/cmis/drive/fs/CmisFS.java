package sho.cmis.drive.fs;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Map;

public class CmisFS
{
	public static FileSystem newFileSystem(URI uri, Map<String, String> config)
	{

		// ImmutableMap<String, ?> env = ImmutableMap.of(CONFIG_KEY, config);
		try
		{
			// Using FileSystems.newFileSystem so that we use the same FileSystemProvider that users will
			// get if they use FileSystems (or other methods like Paths.get(URI)) directly, if possible.
			// We pass in the ClassLoader that loaded this class to ensure that JimfsFileSystemProvider
			// will be found, though if that ClassLoader isn't the system ClassLoader, a new
			// JimfsFileSystemProvider will be created each time.
			return FileSystems.newFileSystem(uri, config, null);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}
}
