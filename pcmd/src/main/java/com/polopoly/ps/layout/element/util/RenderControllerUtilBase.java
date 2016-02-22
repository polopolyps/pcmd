package com.polopoly.ps.layout.element.util;

import com.polopoly.render.CacheInfo;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.mvc.RenderControllerBase;

public class RenderControllerUtilBase extends RenderControllerBase {

    @Override
    public void populateModelAfterCacheKey(RenderRequest request, TopModel m,
            CacheInfo cacheInfo, ControllerContext context) {
        populateModelAfterCacheKey(new ControllerUtil(request, m, context));
    }

    protected void populateModelAfterCacheKey(ControllerUtil controllerUtil) {
    }

    @Override
    public void populateModelBeforeCacheKey(RenderRequest request, TopModel m,
            ControllerContext context) {
        populateModelBeforeCacheKey(new ControllerUtil(request, m, context));
    }

    protected void populateModelBeforeCacheKey(ControllerUtil controllerUtil) {
    }

}
