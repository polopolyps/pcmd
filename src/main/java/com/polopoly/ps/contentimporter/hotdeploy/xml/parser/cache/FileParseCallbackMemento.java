package com.polopoly.ps.contentimporter.hotdeploy.xml.parser.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.contentimporter.hotdeploy.client.Major;
import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.util.SingleObjectHolder;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ContentListWrapperAwareParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.LayoutAwareParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseContext;

public class FileParseCallbackMemento extends SingleObjectHolder<List<SingleCallMemento>>
    implements ParseCallback, ContentListWrapperAwareParseCallback, LayoutAwareParseCallback
{
    private static final Logger logger = Logger.getLogger(FileParseCallbackMemento.class.getName());

    private DeploymentFile file;
    private List<SingleCallMemento> mementos;

    public FileParseCallbackMemento(DeploymentFile file)
    {
        super(new ArrayList<SingleCallMemento>());
        mementos = heldObject;
        this.file = file;
    }

    public void classReferenceFound(DeploymentFile foundInFile, String klass)
    {
        createMemento(foundInFile, new ClassReferenceMemento(klass));
    }

    public void contentFound(ParseContext context, String externalId, Major major, String inputTemplate)
    {
        createMemento(context.getFile(), new ContentMemento(externalId, major, inputTemplate));
    }

    public void contentReferenceFound(ParseContext context, Major major, String externalId)
    {
        createMemento(context.getFile(), new ContentReferenceMemento(major, externalId));
    }

    private void createMemento(DeploymentFile foundInFile, SingleCallMemento memento)
    {
        if (!foundInFile.equals(file)) {
            logger.log(Level.WARNING, "Attempt to log mementos both from " + foundInFile + " and " + file + " in same memento.");
        }
        else {
            mementos.add(memento);
        }
    }

    public void replay(ParseCallback parseCallback)
    {
        ParseContext context = new ParseContext(file);

        for (SingleCallMemento memento : mementos) {
            memento.replay(context, memento, parseCallback);
        }
    }

    public List<SingleCallMemento> getMementos()
    {
        return mementos;
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer(100);

        for (SingleCallMemento memento : mementos) {
            if (result.length() > 0) {
                result.append(", ");
            }

            result.append(memento.toString());
        }

        return result.toString();
    }

    public void contentListWrapperFound(ParseContext context, String externalId, String contentListWrapperClass)
    {
        createMemento(context.getFile(), new ContentListWrapperMemento(externalId, contentListWrapperClass));
    }

    public void layoutFound(ParseContext context, String externalId, String layoutClass)
    {
        createMemento(context.getFile(), new LayoutMemento(externalId, layoutClass));
    }
}
