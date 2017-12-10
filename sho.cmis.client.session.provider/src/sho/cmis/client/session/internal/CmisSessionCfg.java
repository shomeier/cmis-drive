package sho.cmis.client.session.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

@ObjectClassDefinition(name = CmisSessionCfg.CONFIG_NAME, description = "Configuration for CMIS Session")
@interface CmisSessionCfg
{
	static final String CONFIG_NAME = "CMIS Session Config";

	@AttributeDefinition(description = "CMIS URL")
	String url();

	@AttributeDefinition(description = "CMIS Binding", options = { @Option(label = "AtomPub", value = "AtomPub"),
		@Option(label = "Browser", value = "Browser"), @Option(label = "WebService", value = "WebService"),
		@Option(label = "Local", value = "Local"), })
	String binding();

	@AttributeDefinition(description = "Username")
	String user();

	// TODO: Decrypt/Encrypt
	@AttributeDefinition(description = "Password")
	String password();

	@AttributeDefinition(description = "The ID of the CMIS repository to mount. If not specified the first repository is used.", required = false)
	String repository_id();

	@AttributeDefinition(description = "The absolute path of where to mount the CMIS repository to.")
	String mount_path();
}
