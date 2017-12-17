package sho.drive.cmis.mountpoint.internal;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

import sho.drive.api.MountPoint;

@Component(name = CmisMountPoint.COMPONENT_NAME, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = CmisMountPointCfg.class)
public class CmisMountPoint implements MountPoint
{
	static final String COMPONENT_NAME = "sho.drive.cmis.mountpoint";

	@Override
	public Path getMountPath()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileSystem getFileSystem()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
