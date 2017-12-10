package sho.cmis.drive.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = CmisDriveCfg.CONFIG_NAME, description = "Configuration for CMIS Drive")
@interface CmisDriveCfg
{
	static final String CONFIG_NAME = "CMIS Drive Config";

	@AttributeDefinition(description = "The absolute path of where to mount the CMIS repository to.")
	String mount_path();
}
