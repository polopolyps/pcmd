package com.polopoly.ps.contentimporter.hotdeploy.xml.parser.cache;

import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseCallback;
import com.polopoly.ps.contentimporter.hotdeploy.xml.parser.ParseContext;

public interface SingleCallMemento {

    void replay(ParseContext context, SingleCallMemento memento,
            ParseCallback parseCallback);

}
