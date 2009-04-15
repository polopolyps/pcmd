package com.polopoly.pcmd.tool;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.pcmd.field.AbstractContentIdField;
import com.polopoly.pcmd.util.ContentIdToPolicyIterator;
import com.polopoly.pcmd.util.PolicyModification;
import com.polopoly.pcmd.util.PolicyModificationException;
import com.polopoly.pcmd.util.PolicyUtil;

public class GroovyTool implements Tool<GroovyParameters> {
    private boolean doBreak = false;

    public GroovyParameters createParameters() {
        return new GroovyParameters();
    }

    public void execute(final PolopolyContext context, final GroovyParameters parameters) {
        final Binding binding = new Binding();
        final GroovyShell shell = new GroovyShell(getClass().getClassLoader(), binding);

        binding.setVariable("cmServer", context.getPolicyCMServer());
        binding.setVariable("userServer", context.getUserServer());
        binding.setVariable("context", context);
        binding.setVariable("binding", binding);

        shell.evaluate("com.polopoly.cm.policy.Policy.metaClass.propertyMissing = { String field -> " +
                    "childPolicy = delegate.getChildPolicy(field); " +
        		"if (childPolicy instanceof com.polopoly.cm.app.policy.SingleValued) {" +
        		"  return childPolicy.value" +
        		"}" +
        		"else if (childPolicy instanceof com.polopoly.cm.client.ContentListAware) {" +
        		"  return childPolicy.contentList" +
        		"}" +
        		"else {" +
        		"  return childPolicy" +
        		"}}");

        shell.evaluate("com.polopoly.cm.policy.Policy.metaClass.propertyMissing = { String field, value -> " +
                "childPolicy = delegate.getChildPolicy(field); " +
                "childPolicy.setValue(value)" +
                "}");

        shell.evaluate("com.polopoly.cm.collections.ContentList.metaClass.getAt = { Number index -> " +
                " delegate.getEntry(index).referredContentId " +
                "}");

        shell.evaluate("com.polopoly.cm.collections.ContentListRead.metaClass.iterator = { -> " +
                "return new com.polopoly.pcmd.util.ContentListIterator(delegate) }");

        ContentIdToPolicyIterator it =
            new ContentIdToPolicyIterator(context, parameters.getContentIds(), parameters.isStopOnException());

        while (it.hasNext()) {
            Policy policy = it.next();

            if (parameters.isModify()) {
                try {
                    new PolicyUtil(policy).modify(new PolicyModification<Policy>() {
                        public void modify(Policy newVersion) throws CMException {
                            execute(newVersion, shell, binding, context, parameters);
                        }}, Policy.class);
                } catch (PolicyModificationException e) {
                    String errorString = "While running Groovy code for " + policy.getContentId().getContentId().getContentIdString() + ": " + e;

                    if (parameters.isStopOnException()) {
                        throw new CMRuntimeException(errorString, e);
                    }
                    else {
                        System.err.println(errorString);
                    }
                }
            }
            else {
                execute(policy, shell, binding, context, parameters);
            }

            if (doBreak) {
                break;
            }
        }

        it.printInfo(System.err);
    }

    private void execute(Policy policy, GroovyShell shell, Binding binding,
            PolopolyContext context, GroovyParameters parameters) {
        binding.setVariable("policy", policy);

        Object value = shell.evaluate(parameters.getGroovy());

        if (value instanceof ContentId) {
            value = AbstractContentIdField.get((ContentId) value, context);
        }

        System.out.println(AbstractContentIdField.get(policy.getContentId(), context) + ": " + value);

        if (value instanceof Boolean && !((Boolean) value).booleanValue()) {
            doBreak = true;
        }
    }

    public String getHelp() {
        return "Executes the specified Groovy statements for each content ID specified. " +
            "Variables are: policy - the current policy, cmServer - the PolicyCMServer, userServer - the UserServer.";
    }

}
