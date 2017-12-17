package sho.drive.api;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * <p>
 * This is an example of an interface that is expected to be implemented by Consumers of the API; for example this interface may
 * define a listener or a callback. Adding methods to this interface is a MAJOR change, because ALL clients are affected.
 * </p>
 * @see ConsumerType
 * @since 1.0
 */
@ConsumerType
public interface MountPoint
{
	Path getMountPath();

	FileSystem getFileSystem();
}
