package com.polopoly.ps.pcmd.ant;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Parameter;

import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.ps.pcmd.Main;
import com.polopoly.ps.pcmd.PcmdPolopolyClient;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.DefaultArguments;
import com.polopoly.ps.pcmd.tool.HelpParameters;
import com.polopoly.ps.pcmd.tool.HelpTool;
import com.polopoly.ps.pcmd.tool.RequiresIndexServer;
import com.polopoly.ps.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.util.ToolRetriever;
import com.polopoly.ps.pcmd.util.ToolRetriever.NoSuchToolException;
import com.polopoly.util.client.ClientFromArgumentsConfigurator;
import com.polopoly.util.client.ConnectException;
import com.polopoly.util.client.PolopolyContext;

public class PcmdTask extends Task {
    private List<Parameter> parameters = new ArrayList<Parameter>();

    private String toolName;

    private static PolopolyContext context;

    @Override
    public void execute() throws BuildException {
        try {
            executeAndThrowException();
        } catch (FatalToolException e) {
            throw new BuildException(e);
        } catch (NoSuchToolException e) {
            throw new BuildException(e);
        } catch (ArgumentException e) {
            System.err.println("Invalid parameters: " + e.getMessage());

            if (toolName != null) {
                HelpParameters helpParameters = new HelpParameters();
                helpParameters.setTool(toolName);
                new HelpTool().execute(context, helpParameters);
            }

            throw new BuildException(e);
        } catch (ConnectException e) {
            throw new BuildException(e);
        } catch (CMRuntimeException e) {
            if (e.getCause() instanceof Exception) {
                throw new BuildException(e.getCause());
            }

            throw new BuildException(e);
        }
    }

    private void executeAndThrowException() throws BuildException,
            NoSuchToolException, ArgumentException, ConnectException,
            FatalToolException {
        if (toolName == null) {
            throw new BuildException(
                    "Tool parameter to PCMD task must be specified. Run \"pcmd help\" for a list of tools.");
        }

        System.out.println("pcmd " + toolName + ":");

        Tool<?> tool = ToolRetriever.getTool(toolName);

        PcmdPolopolyClient client = new PcmdPolopolyClient();
        client.setAttachStatisticsService(false);
        client.setAttachSearchService(tool instanceof RequiresIndexServer);

        DefaultArguments arguments = new ParameterArgumentParser().parse(
                toolName, parameters);

        if (context == null) {
            new ClientFromArgumentsConfigurator(client, arguments).configure();
            context = client.connect();
        }

        arguments.setContext(context);

        Main.execute(tool, context, arguments);
    }

    public void setTool(String tool) {
        this.toolName = tool;
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }
}
