package sho.cmis.drive.nativity.icon.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = Config.CONFIG_NAME, description = "Configuration for the Liferay Nativity FileIconControl Component")
@interface Config
{
	static final String CONFIG_NAME = "Liferay Nativity FileIconControl";

	@AttributeDefinition()
	String file_icons();
}
