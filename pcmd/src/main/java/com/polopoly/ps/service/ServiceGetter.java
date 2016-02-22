package com.polopoly.ps.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Retrieves {@link Service}s. These are objects whose implementation we do not
 * wish to know about at compile time and therefore fetched from a
 * {@link ServiceLoader} definition of the class fetched. We use this among
 * other things to distinguish between Polopoly and Pojo factories in the
 * various kinds of tests but also generally to avoid having Polopoly
 * dependencies or to switch between implementations that are different in
 * development from production (for com.polopoly.ps Comyan).
 */
public class ServiceGetter {
	public static class ServicesBackup {
		private HashMap<Class<?>, Object> serviceByClass;

		private ServicesBackup() {
			this.serviceByClass = new HashMap<Class<?>, Object>(
					ServiceGetter.serviceByClass);
		}

		public void restore() {
			ServiceGetter.serviceByClass.clear();
			ServiceGetter.serviceByClass.putAll(serviceByClass);
		}
	}

	private static final Logger LOGGER = Logger.getLogger(ServiceGetter.class
			.getName());

	private static Map<Class<?>, Object> serviceByClass = new HashMap<Class<?>, Object>();

	public static <S extends Service> S getRawService(Class<S> serviceClass)
			throws NoSuchServiceException {
		return loadServiceInstance(serviceClass, false);
	}

	public static <S extends Service> S getService(Class<S> serviceClass)
			throws NoSuchServiceException {
		try {
			S cachedResult = getServiceFromCache(serviceClass);
			if (cachedResult != null) {
				return cachedResult;
			}
			S serviceInstance = loadServiceInstance(serviceClass, true);
			return serviceInstance;
		} catch (NoSuchServiceException e) {
			throw e;
		} catch (Throwable t) {
			throw new NoSuchServiceException("While loading service "
					+ serviceClass.getName() + ": " + t.getMessage(), t);
		}
	}

	private static <S extends Service> S loadServiceInstance(
			Class<S> serviceClass, boolean wrap) throws NoSuchServiceException {
		ServiceLoader<S> services = ServiceLoader.load(serviceClass);
		Iterator<S> serviceIterator = services.iterator();
		final S serviceInstance;
		if (wrap) {
			serviceInstance = getWrappedService(serviceClass, serviceIterator);
		} else {
			serviceInstance = loadRawService(serviceClass, serviceIterator);
		}
		LOGGER.log(Level.FINE, "Using " + serviceClass.getSimpleName()
				+ " implementation: " + serviceInstance.getClass().getName());
		putServiceInCache(serviceClass, serviceInstance);
		return serviceInstance;
	}

	@SuppressWarnings("unchecked")
	public static <S extends Service> S wrap(S service, Class<S> serviceClass) {
		ServiceLoader<S> services = ServiceLoader.load(serviceClass);
		List<ServiceWrapper<S>> wrappers = getSortedWrappers(services);

		S last = service;

		for (ServiceWrapper<S> wrapper : wrappers) {
			wrapper.setDelegate(last);

			last = (S) wrapper;
		}

		return last;
	}

	@SuppressWarnings("unchecked")
	private static <S extends Service> List<ServiceWrapper<S>> getSortedWrappers(
			ServiceLoader<S> services) {
		List<ServiceWrapper<S>> wrappers = new ArrayList<ServiceWrapper<S>>();
		Iterator<S> serviceIterator = services.iterator();
		while (serviceIterator.hasNext()) {
			S potentialWrapper = serviceIterator.next();
			if (potentialWrapper instanceof ServiceWrapper<?>) {
				wrappers.add((ServiceWrapper<S>) potentialWrapper);
			}
		}

		Collections.sort(wrappers, new Comparator<ServiceWrapper<S>>() {
			public int compare(ServiceWrapper<S> wrapper1,
					ServiceWrapper<S> wrapper2) {
				ServiceWrapperRelativeOrder relativeOrder = wrapper1
						.getWrapperOrder(wrapper2);
				if (relativeOrder == ServiceWrapperRelativeOrder.I_DONT_CARE) {
					return 0;
				} else if (relativeOrder == ServiceWrapperRelativeOrder.USE_SORT_INDEX) {
					return Integer
							.valueOf(wrapper2.getWrapperIndex())
							.compareTo(
									Integer.valueOf(wrapper1.getWrapperIndex()));
				} else {
					throw new IllegalStateException(
							"Unrecognized ServiceWrapperRelativeOrder: "
									+ relativeOrder);
				}
			}
		});
		return wrappers;
	}

	private static <S extends Service> S getWrappedService(
			Class<S> serviceClass, Iterator<S> serviceIterator)
			throws NoSuchServiceException {
		S rawService = loadRawService(serviceClass, serviceIterator);
		return wrap(rawService, serviceClass);
	}

	private static <S extends Service> S loadRawService(Class<S> serviceClass,
			Iterator<S> serviceIterator) throws NoSuchServiceException {
		List<S> services = new ArrayList<S>();
		while (serviceIterator.hasNext()) {
			S service = serviceIterator.next();
			if (!(service instanceof ServiceWrapper<?>)) {
				services.add(service);
			}
		}
		if (services.isEmpty()) {
			throwNoServiceException(serviceClass);
		}
		S rawService = services.get(0);
		if (services.size() > 1) {
			LOGGER.log(Level.WARNING, "There are multiple implementations for "
					+ serviceClass.getName() + ": at least "
					+ services.get(1).getClass().getName() + " and "
					+ rawService.getClass().getName() + ".");
		}
		return rawService;
	}

	private static <S extends Service> void throwNoServiceException(
			Class<S> serviceClass) throws NoSuchServiceException {
		throw new NoSuchServiceException(
				"There was no implementation for "
						+ serviceClass.getName()
						+ ". There must be a dependency with a service declaration for it (it's in a file called META-INF/services/"
						+ serviceClass.getName() + " in the classpath.");
	}

	@SuppressWarnings("unchecked")
	private static <S extends Service, T extends S> S putServiceInCache(
			Class<S> serviceClass, T firstService) {
		synchronized (serviceByClass) {
			return (S) serviceByClass.put(serviceClass, firstService);
		}
	}

	@SuppressWarnings("unchecked")
	private static <S extends Service> S getServiceFromCache(
			Class<? extends S> serviceClass) {
		synchronized (serviceByClass) {
			Object cachedResult = serviceByClass.get(serviceClass);

			return (S) cachedResult;
		}
	}

	public static ServicesBackup backup() {
		return new ServicesBackup();
	}

	public static <S extends Service, T extends S> S overrideService(
			Class<S> serviceClass, T realService) {
		return putServiceInCache(serviceClass, realService);
	}
}
