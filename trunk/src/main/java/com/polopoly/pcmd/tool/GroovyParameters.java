package com.polopoly.pcmd.tool;

import com.polopoly.cm.ContentId;
import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.parser.ContentIdParser;
import com.polopoly.pcmd.parser.IntegerParser;
import com.polopoly.util.client.PolopolyContext;

public class GroovyParameters extends ListParameters {
    private static final String MODIFY = "modify";
    private static final String QUIET = "quiet";
    private static final String CREATE = "create";
    private static final String INPUT_TEMPLATE = "inputtemplate";
    private String groovy = "";
    private boolean modify;
    private boolean quiet;
    private int create = 0;
    private ContentId inputTemplate;

    @Override
    public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
        super.parseParameters(args, context);

        setGroovy(args.getArgument(0));
        setModify(args.getFlag(MODIFY, false));
        setQuiet(args.getFlag(QUIET, false));

        String createString = args.getOptionString(CREATE, null);

        if ("true".equals(createString)) {
            setCreate(1);
        }
        else if (createString != null) {
            setCreate(new IntegerParser().parse(createString));
        }

        if (getCreate() > 0) {
            setInputTemplate(args.getOption(INPUT_TEMPLATE, new ContentIdParser(context)));
        }
    }

    @Override
    public void getHelp(ParameterHelp help) {
        help.setArguments(null, "The Groovy code to execute.");

        super.getHelp(help);

        help.addOption(MODIFY, null, "Flag indicating whether to, for each object, first create a new version, then execute the Groovy code, then commit the new version. Defaults to false.");
        help.addOption(QUIET, null, "Whether to suppress printint of the content IDs and the result of evaluating the Groovy statements. Defaults to false.");
        help.addOption(CREATE, new IntegerParser(), "Whether to create new instances on which to run the Groovy statements before committing them. May be set to a number to indicate that multiple instances should be created.");
        help.addOption(INPUT_TEMPLATE, new ContentIdParser(), "If " + CREATE + " is set; the input template of the objects to create.");
    }

    @Override
    protected int getFirstContentIdIndex() {
        return 1;
    }

    public String getGroovy() {
        return groovy;
    }

    public void setGroovy(String groovy) {
        this.groovy = groovy;
    }

    public void setModify(boolean modify) {
        this.modify = modify;
    }

    public boolean isModify() {
        return modify;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setCreate(int create) {
        this.create = create;
    }

    public int getCreate() {
        return create;
    }

    public void setInputTemplate(ContentId inputTemplate) {
        this.inputTemplate = inputTemplate;
    }

    public ContentId getInputTemplate() {
        return inputTemplate;
    }
}
