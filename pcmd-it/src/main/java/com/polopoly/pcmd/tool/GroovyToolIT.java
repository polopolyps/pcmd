package com.polopoly.pcmd.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.geronimo.mail.util.StringBufferOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.GroovyTool;
import com.polopoly.testnext.base.ImportTestContent;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.client.PolopolyContext;


public class GroovyToolIT extends AbstractIntegrationTestBase {

    private PolopolyContext context;
    private StringBuffer out;

    @Inject
    private PolicyCMServer cmServer;

    @Inject
    private UserServer userServer;

    @Before
    public void setup() throws ArgumentException {
        context = new PolopolyContext(userServer, cmServer);
        out = new StringBuffer();
        System.setOut(new PrintStream(new StringBufferOutputStream(out)));
    }

    private String getAuthor(String id) throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("println policy.author");
        args.add(id);

        Map<String, List<String>> options = new HashMap<String, List<String>>();

        DefaultArguments arguments = new DefaultArguments("GroovyTool", options, args);
        arguments.setContext(context);

        Main.execute(new GroovyTool(), context, arguments);

        return out.toString();
    }

    @Test
    public void createTest() throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("policy.setComponent(\"name\", \"value\", \"First Groovy Article\"); "
                 + "policy.setComponent(\"author\", \"value\", \"The Groovy Editor\")");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("create", Arrays.asList("1"));
        options.put("inputtemplate", Arrays.asList("pcmd.StandardArticle"));

        DefaultArguments arguments = new DefaultArguments("GroovyTool", options, args);
        arguments.setContext(context);

        Main.execute(new GroovyTool(), context, arguments);

        // gets the contentId string in <major=1>.<minor>.<version> format
        String newArticle = out.toString();
        Pattern pattern = Pattern.compile("(1\\.)+(\\d)+(\\.)+(\\d)+");
        Matcher matcher = pattern.matcher(newArticle);

        if (matcher.find()) {
            String contentId = matcher.group(0);
            System.err.println("Newly created article: " + contentId);
            String author = getAuthor(contentId);
            assertTrue(author.contains("The Groovy Editor"));
        } else {
            assertFalse("This should not happen. One should be able to retrieve the contentId from newly created article",
                        true);
        }
    }

    @Test
    public void createQuietTest() throws ArgumentException {
        List<String> args = new ArrayList<String>();
        args.add("policy.setComponent(\"name\", \"A second Groovy Article\");"
                 + "policy.setComponent(\"author\",\"The Groovy Editor\")");
        
        //new StandardArticlePolicy().setComponent(name, value);

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("create", Arrays.asList("1"));
        options.put("inputtemplate", Arrays.asList("pcmd.StandardArticle"));
        options.put("quiet", Arrays.asList("true"));

        DefaultArguments arguments = new DefaultArguments("GroovyTool", options, args);
        arguments.setContext(context);

        Main.execute(new GroovyTool(), context, arguments);

        assertTrue(out.toString().isEmpty());
    }

    @ImportTestContent(files = { "com.polopoly.pcmd.tool.GroovyToolIT.xml" })
    @Test
    public void modifyTest() throws ArgumentException {
        System.err.println("Original Author is: " + getAuthor(GroovyToolIT.class.getName() + ".article"));

        List<String> args = new ArrayList<String>();
        args.add("policy.setComponent(\"author\", \"value\", \"The Grooviest Editor ever\")");
        args.add(GroovyToolIT.class.getName() + ".article");

        Map<String, List<String>> options = new HashMap<String, List<String>>();
        options.put("modify", Arrays.asList("true"));

        DefaultArguments arguments = new DefaultArguments("GroovyTool", options, args);
        arguments.setContext(context);

        Main.execute(new GroovyTool(), context, arguments);

        String modifiedArticle = getAuthor(GroovyToolIT.class.getName() + ".article");
        assertTrue("was" + modifiedArticle,modifiedArticle.contains("The Grooviest Editor ever"));
    }

}
