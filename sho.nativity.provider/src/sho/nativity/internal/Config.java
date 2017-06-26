package sho.nativity.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = Config.CONFIG_NAME, description = "Configuration for the Liferay Nativity Component")
@interface Config
{
	static final String CONFIG_NAME = "Liferay Nativity Config";

	@AttributeDefinition(required = false)
	String filter_folders();
}
