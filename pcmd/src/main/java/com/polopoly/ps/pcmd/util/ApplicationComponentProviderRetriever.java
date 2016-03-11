package com.polopoly.ps.pcmd.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.polopoly.ps.pcmd.ApplicationComponentProvider;
import com.polopoly.ps.pcmd.util.ToolRetriever.NoSuchToolException;

public class ApplicationComponentProviderRetriever {

	public static List<ApplicationComponentProvider> getApplicationComponentProviders()
			throws NoSuchToolException {
		ServiceLoader<ApplicationComponentProvider> applicationComponentProviderServiceLoader = ServiceLoader
				.load(ApplicationComponentProvider.class);

		List<ApplicationComponentProvider> arrayList = new ArrayList<ApplicationComponentProvider>();
		for (ApplicationComponentProvider applicationComponentProvider : applicationComponentProviderServiceLoader) {
			arrayList.add(applicationComponentProvider);
		}

		return arrayList;

	}
}
