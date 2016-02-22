package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.UnversionedTool;
import com.polopoly.testbase.ImportTestContent;
import com.polopoly.util.client.PolopolyContext;

@ImportTestContent
public class UnversionedToolIT extends AbstractIntegrationTestBase {

    private PolopolyContext context;
    private StringBuffer out;

    @Inject
    private PolicyCMServer cmServer;

    @Before
    public void setup() {
        context = new PolopolyContext(cmServer);
        out = new StringBuffer();
        System.setOut(new PrintStream(new StringBufferOutputStream(out)));

    }

    @Test
    public void unversionedExternalIDTest() throws FatalToolException, ArgumentException {

        List<String> args = new ArrayList<String>();
        args.add(UnversionedToolIT.class.getName() + ".article");

        DefaultArguments arguments = new DefaultArguments("UnversionedTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new UnversionedTool(), context, arguments);
        assertTrue(out.toString().trim().matches("\\d{1}.\\d{3}$"));
    }

    @Test
    public void unversionedContentIDTest() throws FatalToolException, ArgumentException {
        VersionedContentId versionContentId;
        String articleContentId = "";
        try {
            versionContentId =
                cmServer
                    .findContentIdByExternalId(new ExternalContentId(UnversionedToolIT.class.getName() + ".article"));
            articleContentId = versionContentId.getContentIdString();
        } catch (CMException e) {
            e.printStackTrace();
        }

        List<String> args = new ArrayList<String>();
        args.add(articleContentId);

        DefaultArguments arguments = new DefaultArguments("UnversionedTool", new HashMap<String, List<String>>(), args);
        arguments.setContext(context);

        Main.execute(new UnversionedTool(), context, arguments);
        assertTrue(out.toString().trim().matches("\\d{1}.\\d{3}$"));
    }

}
