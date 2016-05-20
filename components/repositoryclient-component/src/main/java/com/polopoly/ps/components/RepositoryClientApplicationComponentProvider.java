package com.polopoly.ps.components;

import com.atex.onecms.content.RepositoryClient;
import com.polopoly.application.Application;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.CmClientBase;
import com.polopoly.ps.pcmd.ApplicationComponentProvider;

public class RepositoryClientApplicationComponentProvider implements ApplicationComponentProvider {

	@Override
	public void add(Application appication) throws IllegalApplicationStateException {
		CmClient cmClient =  (CmClient) appication.getApplicationComponent(CmClientBase.DEFAULT_COMPOUND_NAME);
		RepositoryClient repositoryClient = new RepositoryClient(cmClient);
		appication.addApplicationComponent(repositoryClient);
	}

}
