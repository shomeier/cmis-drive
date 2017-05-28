package sho.cmis.fs.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator
{
	// ServiceTracker for PreferencesServices
	// private ServiceTracker serviceTracker;
	// BundleContext
	private BundleContext bc;
	// Registration of Echo service
	// private ServiceRegistration registration;

	// activation
	public void start(BundleContext context) throws Exception
	{
		bc = context;
		// init and start ServiceTracker to track PreferencesService
		// serviceTracker = new ServiceTracker(context, PreferencesService.class.getName(), new Customizer());
		// serviceTracker.open();
	}

	// deactivation
	public void stop(BundleContext context) throws Exception
	{
		// stop ServiceTracker to track PreferencesService
		// serviceTracker.close();
		// serviceTracker = null;
	}

	// customizer that handles tracked service registration/modification/unregistration events
	private class Customizer implements ServiceTrackerCustomizer
	{
		public Object addingService(ServiceReference reference)
		{
			System.out.println("PreferencesService is linked");
			// register Echo service
			Dictionary<String, String> props = new Hashtable<String, String>();
			// props.put(ECHO_TYPE_PROP, "BundleActivator");
			// registration = bc.registerService(Echo.class.getName(), Activator.this, props);

			return bc.getService(reference);
		}

		public void modifiedService(ServiceReference reference, Object service)
		{
		}

		public void removedService(ServiceReference reference, Object service)
		{
			// unregister Echo service
			// registration.unregister();
			System.out.println("PreferencesService is unlinked");
		}
	}
}
