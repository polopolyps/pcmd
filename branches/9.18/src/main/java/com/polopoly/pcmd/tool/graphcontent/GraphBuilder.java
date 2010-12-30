package com.polopoly.pcmd.tool.graphcontent;

import java.util.Iterator;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.server.ServerNames;
import com.polopoly.pcmd.tool.graphcontent.filter.AggregateContentFilter;
import com.polopoly.pcmd.tool.graphcontent.filter.ContentFilter;
import com.polopoly.pcmd.tool.graphcontent.filter.ExcludeMajorsContentFilter;
import com.polopoly.pcmd.tool.graphcontent.filter.MajorsContentFilter;
import com.polopoly.pcmd.tool.graphcontent.filter.PrefixContentFilter;
import com.polopoly.pcmd.tool.graphcontent.model.ContentGraph;
import com.polopoly.pcmd.tool.graphcontent.model.ContentNode;
import com.polopoly.pcmd.tool.graphcontent.model.Reference;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.exception.ContentGetException;
import com.polopoly.util.policy.Util;

public class GraphBuilder {
    private static final int MAJOR_RMD = 13;

    private PolopolyContext context;
    private GraphContentParameters parameters;
    private ContentGraph graph;

    private ContentFilter seedFilter;
    private ContentFilter graphFilter;

    public GraphBuilder(PolopolyContext context, GraphContentParameters parameters, ContentGraph graph) {
        this.context = context;
        this.parameters = parameters;
        this.graph = graph;

        seedFilter = setUpSeedFilters();
        graphFilter = setUpGraphFilters();
    }

    public void fillWith(Iterator<ContentId> contentIds) {
        try {
            graphContent(filter(contentIds, seedFilter));
        } catch (Exception e) {
            throw new RuntimeException("Could not generate content graph model", e);
        }
    }
    
    private Iterator<ContentUtil> filter(Iterator<ContentId> contentIds, ContentFilter filter) {
        return new FilteredContentIterator(context, filter, contentIds);
    }
    
    private void graphContent(Iterator<ContentUtil> contentIter) throws Exception {
        System.err.println("Fetching content for graph...");

        while (contentIter.hasNext()) {
            try {
                ContentUtil content = contentIter.next();
                traverseForward(content, null, null, parameters.getDepth());
                traverseBackward(content, null, null, parameters.getDepth());
            } catch (ContentGetException e) {
                e.printStackTrace();
            }
        }
    }

    private Reference traverseForward(ContentUtil content, ContentId parent, ContentId rmdId, int level) throws ContentGetException {
        ContentId contentId = content.getContentId().getContentId();
        Reference ref = null;
        if (!graph.hasNodeFor(contentId)) {
            ContentNode node = graph.addNodeAndAutoTag(content);
            if (parent == null) {
                node.tag("origin");
            } else {
                node.tag("downstream");
            }
        } else if (parent == null) {
            graph.getNodeFor(contentId).tag("origin");
        }
        
        if (parent != null) {
            ref = graph.addReference(parent, contentId, rmdId);
        }

        if (level > 0) {
            // Have we reached our depth? We still want to add references to any content
            // that we already have in the graph.
            boolean handleOnlyAlreadyAdded = (level == 1);
            
            for (String group : content.getContentReferenceGroupNames()) {
                for (String name : content.getContentReferenceNames(group)) {
                    ContentIdUtil refId = content.getContentReference(group, name);
                    
                    Reference newRef = handleForwardReference(contentId, refId, null, level, handleOnlyAlreadyAdded);
                    
                    if (newRef != null) {
                        if (group.equals(ServerNames.CONTENT_ATTRG_SYSTEM) && name.equals(ServerNames.CONTENT_ATTR_INPUT_TEMPLATEID)) {
                            newRef.tag("inputTemplate");
                        }
                    }
                }
            }
        }

        return ref;
    }

    private Reference handleForwardReference(ContentId fromId, ContentId refId, ContentId rmdId, int level, boolean handleOnlyAlreadyAdded)
        throws ContentGetException {
        
        // We handle reference metadata differently if it's not to be rendered
        if (!parameters.isRenderRMD() && refId.getMajor() == MAJOR_RMD) {
            ContentUtil rmdContent = context.getContent(refId);
            
            ContentIdUtil realRefId = rmdContent.getContentReference("polopoly.ReferenceMetaData", "referredId");
            
            if (realRefId != null) {
                Reference realRef = handleForwardReference(fromId, realRefId, refId, level, handleOnlyAlreadyAdded);
                if (realRef != null) {
                    realRef.tag("rmd");
                    realRef.tag("rmdId:" + refId.getContentId());
                    realRef.tag("rmdInputTemplate:" + rmdContent.getInputTemplate().getExternalIdString());
                    return realRef;
                }
            }
            
        } else {
            if (!handleOnlyAlreadyAdded && graphFilter.accepts(refId) || graph.hasNodeFor(refId)) {
                ContentUtil refContent = context.getContent(refId);
                if (graphFilter.accepts(refContent)) {
                    return traverseForward(refContent, fromId, rmdId, level - 1);
                }
            }
        }
        
        return null;
    }

    private Reference traverseBackward(ContentUtil content, ContentId parent, ContentId rmdId, int level) throws CMException {
        ContentId contentId = content.getContentId().getContentId();
        Reference ref = null;
        if (!graph.hasNodeFor(contentId)) {
            ContentNode node = graph.addNodeAndAutoTag(content);
            if (parent == null) {
                node.tag("origin");
            } else {
                node.tag("upstream");
            }
        } else if (parent == null) {
            graph.getNodeFor(contentId).tag("origin");
        }
        
        if (parent != null) {
            ref = graph.addReference(contentId, parent, rmdId);
        }

        if (level > 0) {
            // Have we reached our depth? We still want to add references to any content
            // that we already have in the graph.
            boolean handleOnlyAlreadyAdded = (level == 1);
            
            ContentId[] referers = content.getReferringContentIds(VersionedContentId.LATEST_COMMITTED_VERSION, null, null);
            for (ContentId refId : referers) {
                Reference newRef = handleBackwardReference(contentId, refId, null, level, handleOnlyAlreadyAdded);
                
                if (newRef != null) {
                    if (contentId.equalsIgnoreVersion(context.getContent(refId).getInputTemplateId())) {
                        newRef.tag("inputTemplate");
                    }
                }
            }
        }

        return ref;
    }

    private Reference handleBackwardReference(ContentId toId, ContentId refId, ContentId rmdId, int level, boolean handleOnlyAlreadyAdded)
        throws CMException {
        
        // We handle reference metadata differently if it's not to be rendered
        if (!parameters.isRenderRMD() && refId.getMajor() == MAJOR_RMD) {
            ContentUtil rmdContent = context.getContent(refId);
            
            ContentId[] realRefIds = rmdContent.getReferringContentIds(VersionedContentId.LATEST_COMMITTED_VERSION, null, null);
            if (realRefIds.length > 0) {
                ContentIdUtil realRefId = Util.util(realRefIds[0], context);
                
                Reference realRef = handleBackwardReference(toId, realRefId, refId, level, handleOnlyAlreadyAdded);
                if (realRef != null) {
                    realRef.tag("rmd");
                    realRef.tag("rmdId:" + refId.getContentId());
                    realRef.tag("rmdInputTemplate:" + rmdContent.getInputTemplate().getExternalIdString());
                    return realRef;
                }
            }
        } else {
            if (!handleOnlyAlreadyAdded && graphFilter.accepts(refId) || graph.hasNodeFor(refId)) {
                ContentUtil refContent = context.getContent(refId);
                if (graphFilter.accepts(refContent)) {
                    return traverseBackward(refContent, toId, rmdId, level - 1);
                }
            }
        }
        
        return null;
    }

    private ContentFilter setUpSeedFilters() {
        AggregateContentFilter contentFilter = new AggregateContentFilter();
        if (!parameters.isRenderRMD()) {
            contentFilter.add(new ExcludeMajorsContentFilter(13));
        }
        if (parameters.isExcludePolopoly()) {
            contentFilter.add(new PrefixContentFilter("p.", true));
        }
        if (parameters.isExcludeGreenFieldTimes()) {
            contentFilter.add(new PrefixContentFilter("example.", true));
        }
        return contentFilter;
    }

    private AggregateContentFilter setUpGraphFilters() {
        AggregateContentFilter contentFilter = new AggregateContentFilter();
        if (parameters.isExcludePolopoly()) {
            contentFilter.add(new PrefixContentFilter("p.", true));
        }
        if (parameters.isExcludeGreenFieldTimes()) {
            contentFilter.add(new PrefixContentFilter("example.", true));
        }
        if (parameters.getFilterMajors() != null) {
            contentFilter.add(new MajorsContentFilter(parameters.getFilterMajors()));
        }
        return contentFilter;
    }
}
