package sho.cmis.drive.nativity.icon.internal;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.nativity.modules.fileicon.FileIconControlCallback;

@Component(name = CmisDriveFileIcon.COMPONENT_NAME)
public class CmisDriveFileIcon implements FileIconControlCallback
{
	static final String COMPONENT_NAME = "sho.cmis.drive.nativity.icon";

	private static final Logger LOG = LoggerFactory.getLogger(CmisDriveFileIcon.class.getName());

	@Override
	public int getIconForFile(String path)
	{
		return 0;
	}

}
