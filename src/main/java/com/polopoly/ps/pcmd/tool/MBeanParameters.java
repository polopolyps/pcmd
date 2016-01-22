package com.polopoly.ps.pcmd.tool;

import java.util.List;

import com.google.common.collect.Lists;
import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.util.client.PolopolyContext;

public class MBeanParameters implements Parameters {

    private static final String SERVICE_URL = "serviceurl";
    private static final String MBEAN_NAME = "mbean";
    private static final String METHOD_NAME = "method";
    private static final String ATTRIBUTE_NAME = "attr";

    private static final String DEFAULT_SERVICE_URL = "http://localhost:8080/solr-indexer/jolokia";

    private OPERATION operation = OPERATION.LIST;
    private String serviceUrl;
    private String mbeanName;
    private String methodName;
    private String attributeName;
    private List<String> methodArgs = Lists.newArrayList();

    @Override
    public void parseParameters(final Arguments args, final PolopolyContext context) throws ArgumentException {

        final String arg = args.getArgument(0);

        if (arg.equals("list")) {
            setOperation(OPERATION.LIST);
        } else if (arg.equals("search")) {
            setOperation(OPERATION.SEARCH);
        } else if (arg.equals("attr")) {
            setOperation(OPERATION.ATTR);
        } else if (arg.equals("inv")) {
            setOperation(OPERATION.INVOKE);
        } else {
            throw new ArgumentException("\"" + arg + "\" is not a valid command.");
        }

        if (getOperation() != OPERATION.INVOKE) {

            if (args.getArgumentCount() > 1) {
                throw new ArgumentException("There must be only one argument.");
            }

        } else {

            for (int i = 1; i < args.getArgumentCount(); i++) {
                methodArgs.add(args.getArgument(i));
            }

        }

        setServiceUrl(args.getOptionString(SERVICE_URL, DEFAULT_SERVICE_URL));
        setMBeanName(args.getOptionString(MBEAN_NAME, null));
        setMethodName(args.getOptionString(METHOD_NAME, null));
        setAttributeName(args.getOptionString(ATTRIBUTE_NAME, null));

        if (getServiceUrl() == null) {
            throw new ArgumentException("Missing " + SERVICE_URL + " parameter.");
        }

        if (getOperation().equals(OPERATION.ATTR)) {
            if (getMBeanName() == null) {
                throw new ArgumentException("Missing " + MBEAN_NAME + " parameter.");
            }
            if (getAttributeName() == null) {
                throw new ArgumentException("Missing " + ATTRIBUTE_NAME + " parameter.");
            }
        }

        if (getOperation().equals(OPERATION.INVOKE)) {
            if (getMBeanName() == null) {
                throw new ArgumentException("Missing " + MBEAN_NAME + " parameter.");
            }
            if (getMethodName() == null) {
                throw new ArgumentException("Missing " + METHOD_NAME + " parameter.");
            }
        }
    }

    @Override
    public void getHelp(final ParameterHelp help) {
        help.setArguments(null, "The command list|search|attr|inv and the arguments for invoke commands");
        help.addOption(SERVICE_URL, null, "The service url (default is http://localhost:8080/solr-indexer/jolokia)");
        help.addOption(MBEAN_NAME, null, "The mbean name");
        help.addOption(METHOD_NAME, null, "The method name");
        help.addOption(ATTRIBUTE_NAME, null, "The attribute name");
    }

    public OPERATION getOperation() {
        return operation;
    }

    public void setOperation(final OPERATION operation) {
        this.operation = operation;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(final String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getMBeanName() {
        return mbeanName;
    }

    public void setMBeanName(final String mbeanName) {
        this.mbeanName = mbeanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(final String attributeName) {
        this.attributeName = attributeName;
    }

    public List<String> getMethodArgs() {
        return methodArgs;
    }

    public enum OPERATION {

        LIST,

        SEARCH,

        ATTR,

        INVOKE

    }
}
