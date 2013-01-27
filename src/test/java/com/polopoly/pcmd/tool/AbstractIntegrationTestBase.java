package com.polopoly.pcmd.tool;

import org.junit.runner.RunWith;

import com.polopoly.ps.testbase.TestApplication;
import com.polopoly.ps.testbase.TestBaseJUnitRunner;
import com.polopoly.ps.testbase.TestContentImporter;
import com.polopoly.ps.testbase.annotations.ImportTestContent;
import com.polopoly.ps.testbase.annotations.InjectTestApplication;
import com.polopoly.ps.testbase.annotations.InjectTestContentImporter;

@ImportTestContent
@RunWith(TestBaseJUnitRunner.class)
public abstract class AbstractIntegrationTestBase {

    @InjectTestApplication(applicationComponents = { SolrSearchClientApplicationComponentFactory.class })
    protected TestApplication testApplication;

    @InjectTestContentImporter
    protected TestContentImporter testContentImporter;

}
