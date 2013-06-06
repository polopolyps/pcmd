package com.polopoly.ps.contentimporter.hotdeploy.xml.parser.cache;

import java.util.HashMap;
import java.util.Map;

import com.polopoly.ps.contentimporter.hotdeploy.file.DeploymentFile;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.DeploymentFileParser;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseCallback;

public class ParsedFilesCache {
    private DeploymentFileParser parser;
    private Map<DeploymentFile, FileParseCallbackMemento> mementoByFile =
        new HashMap<DeploymentFile, FileParseCallbackMemento>();

    public ParsedFilesCache(DeploymentFileParser parser) {
        this.parser = parser;
    }

    public void parse(DeploymentFile file,
            ParseCallback parseCallback) {
        FileParseCallbackMemento cachedMemento = mementoByFile.get(file);

        if (cachedMemento != null) {
            cachedMemento.replay(parseCallback);
        }
        else {
            FileParseCallbackMemento newMemento = getFileMemento(file);

            parser.parse(file, new ParseCallbackMultiplexer(parseCallback, newMemento));
        }
    }

    private FileParseCallbackMemento getFileMemento(DeploymentFile file) {
        FileParseCallbackMemento memento = mementoByFile.get(file);

        if (memento == null) {
            memento = new FileParseCallbackMemento(file);
            mementoByFile.put(file, memento);
        }

        return memento;
    }

}
