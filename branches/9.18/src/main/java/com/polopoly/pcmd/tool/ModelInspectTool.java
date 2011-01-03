package com.polopoly.pcmd.tool;

import static com.polopoly.util.policy.Util.util;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.client.ContentRead;
import com.polopoly.cm.policymvc.PolicyCMServerModelDomain;
import com.polopoly.cm.policymvc.PolicyModelDomain;
import com.polopoly.model.Model;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.ContentIdToContentIterator;
import com.polopoly.util.content.ContentUtil;

public class ModelInspectTool implements Tool<ModelInspectParameters> {

    private int maxDepth = 4;
	private int modelDepth;


	public ModelInspectParameters createParameters() {
        return new ModelInspectParameters();
    }

    public void execute(PolopolyContext context, ModelInspectParameters parameters) {
        ContentIdToContentIterator it = new ContentIdToContentIterator(context,
                parameters.getContentIds(), parameters.isStopOnException());

        this.maxDepth = parameters.getDepth();
        
        while (it.hasNext()) {
            ContentRead content = it.next();

            ContentUtil contentUtil = util(content, context.getPolicyCMServer());

            PolicyModelDomain modelDomain = new PolicyCMServerModelDomain(context.getPolicyCMServer(), "Pcmd model domain");
                
            try {
                Model model = modelDomain.getModel(contentUtil.getContentId());
                
                modelDepth = 0; 
                
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

    @SuppressWarnings("unchecked")
	private void printModel(String parentPath, Object attribute) {
    	if (attribute instanceof Model) {
    		Model model = (Model) attribute;
    		if ( model.getModelType() != null) {
    		    System.out.print("[T:" + model.getModelType().getName() + "]");
    		}
    		System.out.println();
    		//System.out.println(" #" + attribute.getClass().getSimpleName() + "#");
    		if (modelDepth >= maxDepth) {
    			return;
    		}
        	for (String attributeName : model.getAttributeNames()) {
        		System.out.print(parentPath + attributeName + "=");
        		modelDepth++;
        		printModel(parentPath + attributeName + ".", model.getAttribute(attributeName));
        		modelDepth--;
        	}
    	} else if (attribute instanceof Iterable<?>) {
    	    Iterable<Model> modelList = (Iterable<Model>) attribute;
    	    System.out.print("[C:" + modelList.getClass().getName() + "]");
    	    int i = 0;
    		for (Object entry : modelList) {
    			modelDepth++;
    		    printModel(parentPath + "[" + i + "]" + ".", entry);
    		    modelDepth--;
    		    i++;
    		}
    	} else if (attribute instanceof String) {
    		// Print attribute value
    		System.out.println("\"" + attribute + "\"");
    	} else {
    		// Print attribute value
    		System.out.print(attribute);
    		// TODO: Should this be checked before recursion instead?
    		if (attribute != null) {
    			// Print attribute class/type
    			System.out.print(" [C:" + attribute.getClass().getName() + "]");
    		}
    		System.out.println();
    	}
	}
    

	public String getHelp() {
        return "Prints the content model ($content...) of the specified objects.";
    }
}
