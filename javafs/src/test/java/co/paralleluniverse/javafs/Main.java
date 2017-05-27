package co.paralleluniverse.javafs;

import java.nio.file.FileSystem;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.common.jimfs.Jimfs;

public class Main
{
	public static void main(final String... args) throws Exception
	{
		try
		{
			if (args.length < 1 || args.length > 3)
				throw new IllegalArgumentException();

			int i = 0;
			boolean readonly = false;
			if ("-r".equals(args[i]))
			{
				readonly = true;
				i++;
			}
			final String mountPoint = args[i++];
			final FileSystem fs = i >= args.length ? Jimfs.newFileSystem() : ZipFS.newZipFileSystem(Paths.get(args[i++]));

			System.out.println("========================");
			System.out.println("Mounting filesystem " + fs + " at " + mountPoint + ( readonly ? " READONLY" : "" ));
			System.out.println("========================");

			Map<String, String> options = new HashMap<>();
			// options.put("fsname", fs.getClass().getSimpleName() + "@" + System.currentTimeMillis());
			options.put("fsname", "OMN");
			options.put("volname", "Online Media Net");
			options.put("volicon", "/Users/SHomeier/Documents/omnIcon.icns");

			JavaFS.mount(fs, Paths.get(mountPoint), readonly, true, options);
			// JavaFS.unmount(Paths.get(mountPoint));
			Thread.sleep(Long.MAX_VALUE);
		}
		catch (IllegalArgumentException e)
		{
			System.err.println("Usage: JavaFS [-r] <mountpoint> [<zipfile>]");
			System.exit(1);
		}
	}
}
