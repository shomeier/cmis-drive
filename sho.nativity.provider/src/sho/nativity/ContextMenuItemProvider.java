package sho.nativity;

import java.util.List;

import org.osgi.annotation.versioning.ConsumerType;

import com.liferay.nativity.modules.contextmenu.model.ContextMenuItem;

@ConsumerType
public interface ContextMenuItemProvider
{
	List<ContextMenuItem> getContextMenuItems(String[] paths);
}
