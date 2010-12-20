package com.polopoly.pcmd.tool;

import static com.polopoly.util.policy.Util.util;

import java.util.List;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policymvc.PolicyCMServerModelDomain;
import com.polopoly.cm.policymvc.PolicyModelDomain;
import com.polopoly.model.Model;
import com.polopoly.pcmd.argument.ContentIdListParameters;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;
import com.polopoly.util.content.ContentUtil;

public class ModelInspectTool implements Tool<ContentIdListParameters> {

    public ContentIdListParameters createParameters() {
        return new ContentIdListParameters();
    }

    public void execute(PolopolyContext context, ContentIdListParameters parameters) {
        ContentIdToContentIterator it = new ContentIdToContentIterator(context,
                parameters.getContentIds(), parameters.isStopOnException());

        while (it.hasNext()) {
            ContentRead content = it.next();

            ContentUtil contentUtil = util(content, context.getPolicyCMServer());

            PolicyModelDomain modelDomain = new PolicyCMServerModelDomain(context.getPolicyCMServer(), "Pcmd model domain");
                
            try {
                Model model = modelDomain.getModel(contentUtil.getContentId());
                
                printModel("", model);

            } catch (CMException e) {
                if (parameters.isStopOnException()) {
                    throw new CMRuntimeException(e);
                } else {
                    System.err.println(content.getContentId().getContentIdString() + ": " + e);
                }
            }
        }

        it.printInfo(System.err);
    }

    private void printModel(String parentPath, Object attribute) {
    	if (attribute instanceof Model) {
    		Model model = (Model) attribute;
    		System.out.print("[" + model.getModelType().getName() + "]");
    		System.out.println(" #" + attribute.getClass().getSimpleName() + "#");
        	for (String attributeName : model.getAttributeNames()) {
        		System.out.print(parentPath + attributeName + "=");
        		printModel(attributeName + ".", model.getAttribute(attributeName));
        	}
    	} else if (attribute instanceof List<?>) {
    		for (Model model : (List<Model>)attribute) {
				System.out.println("hej");
			}
    	} else {
    		// Print attribute value
    		System.out.print(attribute);
    		// TODO: Should this be checked before recursion instead?
    		if (attribute != null) {
    			// Print attribute class/type
    			System.out.print(" [" + attribute.getClass().getName() + "]");
    		}
    		System.out.println();
    	}
	}
    

	public String getHelp() {
        return "Prints the content model ($content...) of the specified objects.";
    }
}
