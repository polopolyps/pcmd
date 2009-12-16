package com.polopoly.pcmd.tool;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;

import com.polopoly.pcmd.argument.ArgumentException;
import com.polopoly.pcmd.argument.Arguments;
import com.polopoly.pcmd.argument.NotProvidedException;
import com.polopoly.pcmd.argument.ParameterHelp;
import com.polopoly.pcmd.parser.IntegerParser;
import com.polopoly.util.client.PolopolyContext;

public class LuceneParameters extends LuceneParametersBase {
    private static final String BATCH_SIZE = "batchsize";
    private int batchSize = LuceneTool.DEFAULT_BATCH_SIZE;

    private Query query;

    @Override
    public void getHelp(ParameterHelp help) {
        super.getHelp(help);
        help.setArguments(null, "A sequence of search queries for different fields in the form <field1>:<query2> [<field2>:<query2>]*.");
        help.addOption(BATCH_SIZE, new IntegerParser(), "The number of content IDs to request in one batch while searching. Defaults to " + LuceneTool.DEFAULT_BATCH_SIZE + ".");
    }

    @Override
    public void parseParameters(Arguments args, PolopolyContext context)
            throws ArgumentException {
        super.parseParameters(args, context);

        BooleanQuery booleanQuery = new BooleanQuery();

        if (args.getArgumentCount() == 0) {
            throw new ArgumentException("There were no arguments (possibly only options). The search query should be specified as arguments.");
        }

        for (int i = 0; i < args.getArgumentCount(); i++) {
            String arg = args.getArgument(i);

            int j = arg.indexOf(':');

            if (j == -1) {
                throw new ArgumentException("The arguments must be <field name>:<query expression>. The argument \"" + arg + "\" did not match this.");
            }

            String field = arg.substring(0, j);
            String queryString = arg.substring(j+1);

            try {
                booleanQuery.add(new BooleanClause(
                        new QueryParser(field, new KeywordAnalyzer()).parse(queryString),
                        Occur.MUST));
            } catch (org.apache.lucene.queryParser.ParseException e) {
                throw new ArgumentException("Lucene's KeywordAnalyzer could not analyzer \"" + queryString + "\": " + e.getMessage());
            }
        }

        query = booleanQuery;

        try {
            setBatchSize(args.getOption(BATCH_SIZE, new IntegerParser()));
        } catch (NotProvidedException e) {
        }
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getBatchSize() {
        return batchSize;
    }
}
