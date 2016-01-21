package com.polopoly.ps.pcmd.tool;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.polopoly.cm.ContentId;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.CommandLineArgumentParser;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.util.client.PolopolyContext;

/**
 * Unit test for {@link ReindexParameters}.
 *
 * @author mnova
 */
@RunWith(MockitoJUnitRunner.class)
public class ReindexParametersTest {

    @Mock
    PolopolyContext context;

    @Test
    public void testDefaultParameters() throws ArgumentException {

        final ReindexParameters params = prepareParameters(new String[0]);

        Assert.assertEquals(ReindexParameters.DEFAULT_SERVICE_URL, params.getServiceUrl());
        Assert.assertEquals(ReindexParameters.DEFAULT_INDEX_NAME, params.getIndexName());

    }

    @Test
    public void testServiceUrl() throws ArgumentException {

        final String url = "http://p-cmserver:8200/solr-indexer";
        final String[] args = {
            "--serviceurl=" + url
        };

        final ReindexParameters params = prepareParameters(args);

        Assert.assertEquals(url, params.getServiceUrl());
        Assert.assertEquals(ReindexParameters.DEFAULT_INDEX_NAME, params.getIndexName());

    }

    @Test
    public void testServiceUrlWithSlash() throws ArgumentException {

        final String url = "http://p-cmserver:8200/solr-indexer";
        final String[] args = {
                "--serviceurl=" + url + "/"
        };

        final ReindexParameters params = prepareParameters(args);

        Assert.assertEquals(url, params.getServiceUrl());
        Assert.assertEquals(ReindexParameters.DEFAULT_INDEX_NAME, params.getIndexName());

    }

    @Test
    public void testEmptyServiceUrl() throws ArgumentException {

        final String[] args = {
                "--serviceurl="
        };

        final ReindexParameters params = prepareParameters(args);

        Assert.assertEquals(ReindexParameters.DEFAULT_SERVICE_URL, params.getServiceUrl());
        Assert.assertEquals(ReindexParameters.DEFAULT_INDEX_NAME, params.getIndexName());

    }

    @Test
    public void testIndexName() throws ArgumentException {

        final String url = "http://p-cmserver:8200/solr-indexer";
        final String indexName = "public";
        final String[] args = {
                "--serviceurl=" + url,
                "--index=" + indexName
        };

        final ReindexParameters params = prepareParameters(args);

        Assert.assertEquals(url, params.getServiceUrl());
        Assert.assertEquals(indexName, params.getIndexName());

    }

    @Test
    public void testSingleContentId() throws ArgumentException {

        final String[] args = {
                "reindex",
                "2.100"
        };

        final ReindexParameters params = prepareParameters(args);

        final Iterator<ContentId> iterator = params.getContentIds();
        Assert.assertNotNull(iterator);
        Assert.assertTrue(iterator.hasNext());

        final ContentId contentId = iterator.next();
        Assert.assertNotNull(contentId);
        Assert.assertEquals(2, contentId.getMajor());
        Assert.assertEquals(100, contentId.getMinor());

    }

    @Test
    public void testMaxIds() throws ArgumentException {

        final String[] args = {
                "--max=127"
        };

        final ReindexParameters params = prepareParameters(args);

        Assert.assertEquals(ReindexParameters.DEFAULT_SERVICE_URL, params.getServiceUrl());
        Assert.assertEquals(ReindexParameters.DEFAULT_INDEX_NAME, params.getIndexName());
        Assert.assertEquals(127, params.getMaxContentIds());

    }

    @Test(expected = ArgumentException.class)
    public void testInvalidMaxIds() throws ArgumentException {

        final String[] args = {
                "--max=asdasds"
        };

        final ReindexParameters params = prepareParameters(args);

    }

    @Test
    public void testReindexAllTrue() throws ArgumentException {

        final String[] args = {
                "--reindexall=true"
        };

        final ReindexParameters params = prepareParameters(args);

        Assert.assertEquals(ReindexParameters.DEFAULT_SERVICE_URL, params.getServiceUrl());
        Assert.assertEquals(ReindexParameters.DEFAULT_INDEX_NAME, params.getIndexName());
        Assert.assertEquals(true, params.isReindexAll());

    }

    @Test
    public void testReindexAllFalse() throws ArgumentException {

        final String[] args = {
                "--reindexall=false"
        };

        final ReindexParameters params = prepareParameters(args);

        Assert.assertEquals(ReindexParameters.DEFAULT_SERVICE_URL, params.getServiceUrl());
        Assert.assertEquals(ReindexParameters.DEFAULT_INDEX_NAME, params.getIndexName());
        Assert.assertEquals(false, params.isReindexAll());

    }

    @Test
    public void testReindexAllWrong() throws ArgumentException {

        final String[] args = {
                "--reindexall=asdlkasjd"
        };

        final ReindexParameters params = prepareParameters(args);

        Assert.assertEquals(ReindexParameters.DEFAULT_SERVICE_URL, params.getServiceUrl());
        Assert.assertEquals(ReindexParameters.DEFAULT_INDEX_NAME, params.getIndexName());
        Assert.assertEquals(false, params.isReindexAll());

    }

    private ReindexParameters prepareParameters(final String[] args)
            throws ArgumentException {

        return prepareParameters(new ReindexParameters(), args);

    }

    private <T extends Parameters> T prepareParameters(final T parameters, final String[] args)
            throws ArgumentException {

        return prepareParameters(parameters, context, args);

    }

    private <T extends Parameters> T prepareParameters(final T parameters, final PolopolyContext context, final String[] args)
            throws ArgumentException {

        final DefaultArguments arguments = new CommandLineArgumentParser().parse(args);
        parameters.parseParameters(arguments, context);
        return parameters;

    }

}