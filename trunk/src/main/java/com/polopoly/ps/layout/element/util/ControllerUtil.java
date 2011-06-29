package com.polopoly.ps.layout.element.util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.polopoly.application.Application;
import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.cm.servlet.RequestPreparator;
import com.polopoly.cm.servlet.URLBuilder;
import com.polopoly.model.ModelPathUtil;
import com.polopoly.model.ModelStoreInBean;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.model.context.PageScope;
import com.polopoly.siteengine.model.context.SiteScope;
import com.polopoly.siteengine.model.request.ContentPath;
import com.polopoly.user.server.UserServer;
import com.polopoly.util.CheckedCast;
import com.polopoly.util.CheckedClassCastException;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.policy.Util;

public class ControllerUtil {
    private static final Logger logger = Logger.getLogger(ControllerUtil.class
            .getName());

    private RenderRequest request;

    private TopModel m;

    private ControllerContext context;

    private PolopolyContext polopolyContext;

    public ControllerUtil(RenderRequest request, TopModel m,
            ControllerContext context) {
        this.request = request;
        this.m = m;
        this.context = context;
    }

    public PolopolyContext getPolopolyContext() {
        if (polopolyContext == null) {
            Application application = context.getApplication();

            if (application != null) {
                polopolyContext = new PolopolyContext(application);
            } else {
                throw new CMRuntimeException(
                        "Could not get the application from controller context.");
            }
        }

        return polopolyContext;
    }

    public UserServer getUserServer() {
        return getPolopolyContext().getUserServer();
    }

    public PolicyCMServer getPolicyCMServer() {
        return getPolopolyContext().getPolicyCMServer();
    }

    public String createUrl(List<ContentId> newContentIdPath) {
        URLBuilder urlBuilder = RequestPreparator
                .getURLBuilder((HttpServletRequest) request);

        if (urlBuilder == null) {
            logger.log(Level.WARNING,
                    "Found no URL builder in request. Could not create URL for path "
                            + newContentIdPath + ".");
            return "";
        }

        try {
            return urlBuilder.createUrl(newContentIdPath,
                    (HttpServletRequest) request);
        } catch (CMException e) {
            logger.log(Level.WARNING, "Could not create URL for path "
                    + newContentIdPath + " using URL builder of type "
                    + urlBuilder.getClass().getName() + ": " + e.getMessage(),
                    e);

            return "";
        }
    }

    public <T> T getStack(String variable, Class<T> klass)
            throws ModelGetException {
        try {
            Object bean = ModelPathUtil.getBean(m.getStack(), variable);

            if (bean == null) {
                throw new ModelVariableNullException("Variable stack/"
                        + variable + " was null.");
            }

            return CheckedCast.cast(bean, klass);
        } catch (CheckedClassCastException e) {
            throw new ModelGetException("While getting variable stack/"
                    + variable + ": " + e.getMessage());
        }
    }

    public <T> T getLocal(String variable, Class<T> klass)
            throws ModelGetException {
        try {
            return CheckedCast.cast(ModelPathUtil.getBean(m.getLocal(),
                    variable), klass);
        } catch (CheckedClassCastException e) {
            throw new ModelGetException("While getting variable local/"
                    + variable + ": " + e.getMessage());
        }
    }

    public void setLocal(String variable, Object o) {
        ModelPathUtil.set(m.getLocal(), variable, o);
    }

    public void setStack(String variable, Object o) {
        ModelPathUtil.set(m.getStack(), variable, o);
    }

    public TopModel getModel() {
        return m;
    }

    /**
     * Returns the policy of the object (e.g. an element or an article) the
     * controller is defined on.
     */
    public <T> T getPolicy(Class<T> policyClass)
            throws InvalidControllerPolicyException {
        try {
            return CheckedCast.cast(getPolicy(), policyClass,
                    "The controller's policy");
        } catch (CheckedClassCastException e) {
            throw new InvalidControllerPolicyException(e);
        }
    }

    public Policy getPolicy() {
        return (Policy) context.getContentModel().getAttribute(
                ModelStoreInBean.BEAN_ATTRIBUTE_NAME);
    }

    public <T> T getPage(Class<T> pageClass) throws NoCurrentPageException {
        try {
            PageScope page = m.getContext().getPage();

            if (page == null) {
                throw new NoCurrentPageException(
                        "No page available in model for "
                                + request.getRequestURI() + ".");
            }

            return CheckedCast.cast(page.getBean(), pageClass, "Current page");
        } catch (CheckedClassCastException e) {
            throw new NoCurrentPageException(e);
        }
    }

    public <T> T getSite(Class<T> siteClass) {
        try {
            SiteScope site = m.getContext().getSite();

            if (site == null) {
                throw new CMRuntimeException("No site available in model for "
                        + request.getRequestURI() + ".");
            }

            return CheckedCast.cast(site.getBean(), siteClass, "Current site");
        } catch (CheckedClassCastException e) {
            throw new CMRuntimeException(e);
        }
    }

    public HttpServletRequest getRequest() {
        return (HttpServletRequest) request;
    }

    public ContentPath getContentPath() {
        return m.getContext().getPage().getContentPath();
    }

    public Policy getArticle() throws NoCurrentArticleException {
        return getArticle(Policy.class);
    }

    public <T> T getArticle(Class<T> articleClass)
            throws NoCurrentArticleException {
        PageScope page = m.getContext().getPage();

        ContentPath contentPath;

        if (page == null) {
            contentPath = m.getRequest().getOriginalContentPath();
        } else {
            contentPath = page.getPathAfterPage();
        }

        for (int i = contentPath.size() - 1; i >= 0; i--) {
            ContentId contentId = (ContentId) contentPath.get(i);

            if (contentId.getMajor() == 1) {
                try {
                    return getPolopolyContext().getPolicy(contentId,
                            articleClass);
                } catch (PolicyGetException e) {
                    throw new NoCurrentArticleException(e);
                }
            }
        }

        throw new NoCurrentArticleException(
                "There was no article in the content path ("
                        + toString(contentPath) + ").");
    }

    private String toString(ContentPath contentPath) {
        String result = "";

        for (int i = contentPath.size() - 1; i >= 0; i--) {
            result += ((ContentId) contentPath.get(i)).getContentIdString();

            if (i > 0) {
                result += ", ";
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return request.getRequestURI() + " (" + Util.util(getPolicy()) + ")";
    }
}
