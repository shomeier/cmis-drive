package sho.nativity.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = NativityConfig.CONFIG_NAME, description = "Configuration for the Liferay Nativity Component")
@interface NativityConfig
{
	static final String CONFIG_NAME = "Liferay Nativity Config";

	@AttributeDefinition(required = false)
	String filter_folders();
}
