package com.polopoly.ps.pcmd.tool;

import java.io.File;

import com.polopoly.ps.pcmd.argument.ArgumentException;
import com.polopoly.ps.pcmd.argument.Arguments;
import com.polopoly.ps.pcmd.argument.ParameterHelp;
import com.polopoly.ps.pcmd.argument.Parameters;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.ps.pcmd.parser.ExistingFileParser;
import com.polopoly.util.client.PolopolyContext;

public class RegularJstackReportParameters implements Parameters {

	private File file;
	private boolean printStackTrace;

	@Override
	public void parseParameters(Arguments args, PolopolyContext context) throws ArgumentException {
		file = args.getArgument(0, new ExistingFileParser());
		printStackTrace = args.getFlag("printstacktrace", false);
	}

	public File getFile() {
		return file;
	}

	@Override
	public void getHelp(ParameterHelp help) {
		help.setArguments(new ExistingFileParser(), "A file containing a series of jstack outputs.");
		help.addOption("printstacktrace", new BooleanParser(), "Whether to print the stacktrace or just one line.");
	}

	public boolean isPrintStackTrace() {
		return printStackTrace;
	}

}
