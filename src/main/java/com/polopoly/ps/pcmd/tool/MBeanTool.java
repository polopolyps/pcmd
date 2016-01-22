package com.polopoly.ps.pcmd.tool;

import java.net.MalformedURLException;
import java.util.List;

import javax.management.ObjectName;

import org.jolokia.client.J4pClient;
import org.jolokia.client.request.J4pExecRequest;
import org.jolokia.client.request.J4pExecResponse;
import org.jolokia.client.request.J4pListRequest;
import org.jolokia.client.request.J4pListResponse;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pReadResponse;
import org.jolokia.client.request.J4pSearchRequest;
import org.jolokia.client.request.J4pSearchResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.base.Strings;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.ps.pcmd.FatalToolException;
import com.polopoly.util.client.PolopolyContext;

public class MBeanTool implements Tool<MBeanParameters> {

    private J4pClient client;

    @Override
    public void execute(final PolopolyContext context, final MBeanParameters parameters) throws FatalToolException {

        try {
            initFromServiceUrl(parameters.getServiceUrl());

            final String mbeanName = Strings.emptyToNull(parameters.getMBeanName());
            final String attributeName = Strings.emptyToNull(parameters.getAttributeName());
            final String methodName = Strings.emptyToNull(parameters.getMethodName());
            final List<String> methodArgs = parameters.getMethodArgs();

            switch (parameters.getOperation()) {
                case LIST:
                    doList(mbeanName);
                    break;

                case SEARCH:
                    doSearch(mbeanName);
                    break;

                case ATTR:
                    doAttribute(mbeanName, attributeName);
                    break;

                case INVOKE:
                    doInvoke(mbeanName, methodName, methodArgs);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof javax.management.RuntimeOperationsException) {
                javax.management.RuntimeOperationsException r = (javax.management.RuntimeOperationsException)e;
                r.getCause().printStackTrace();
                r.getTargetException().printStackTrace();
            }
            throw new FatalToolException(e);
        }
    }

    private void doInvoke(final String mbeanName, final String methodName, final List<String> methodArgs) throws Exception {
        System.out.println("mbean: " + new ObjectName(mbeanName).toString());
        System.out.println("operation: " + methodName);
        for (final String arg : methodArgs) {
            System.out.println("arg: " + arg);
        }
        J4pExecRequest request = new J4pExecRequest(new ObjectName(mbeanName), methodName, methodArgs.toArray());
        J4pExecResponse response = client.execute(request);
        if (response != null) {
            final JSONObject object = response.asJSONObject();
            dump(object, "");
        }
    }

    private void doAttribute(final String mbeanName, final String attributeName) throws Exception {
        J4pReadRequest request = new J4pReadRequest(new ObjectName(mbeanName), attributeName);
        J4pReadResponse response = client.execute(request);
        if (response != null) {
            final JSONObject object = response.asJSONObject();
            dump(object, "");
        }
    }

    private void doSearch(final String mbeanExpression) throws Exception {
        J4pSearchRequest request = new J4pSearchRequest(mbeanExpression);
        J4pSearchResponse response = client.execute(request);
        if (response != null) {
            final JSONObject object = response.asJSONObject();
            dump(object, "");
        }
    }

    private void doList(final String mbeanName) throws Exception {
        if (mbeanName == null) {
            J4pListRequest request = new J4pListRequest("/");
            J4pListResponse response = client.execute(request);
            if (response != null) {
                final JSONObject object = response.asJSONObject();
                dump(object, "");
            }
        } else {
            J4pListRequest request = new J4pListRequest(new ObjectName(mbeanName));
            J4pListResponse response = client.execute(request);
            if (response != null) {
                final JSONObject object = response.asJSONObject();
                dump(object, "");
            }
        }
    }

    private void dump(final JSONObject object, final String space) {
        if (object != null) {
            for (final Object key : object.keySet()) {
                System.out.print(space + key.toString());
                final Object o = object.get(key);
                dumpValue(o, space);
            }
        }
    }

    private void dumpValue(final Object object, final String space) {
        if (object instanceof JSONObject) {
            System.out.println();
            dump((JSONObject) object, space + "  ");
        } else if (object instanceof JSONArray) {
            System.out.println();
            final JSONArray array = (JSONArray) object;
            for (int idx = 0; idx < array.size(); idx++) {
                final Object oa = array.get(idx);
                System.out.print(space);
                dumpValue(oa, space);
            }
        } else {
            if (object != null) {
                System.out.println(" " + object.toString());
            } else {
                System.out.println();
            }
        }
    }

    private void initFromServiceUrl(final String serviceUrl) throws MalformedURLException {
        client = new J4pClient(serviceUrl);
    }

    @Override
    public MBeanParameters createParameters() {
        return new MBeanParameters();
    }

    @Override
    public String getHelp() {
        return "Use to list MBeans, list and  get MBean attributes and invoke commands.";
    }

}
