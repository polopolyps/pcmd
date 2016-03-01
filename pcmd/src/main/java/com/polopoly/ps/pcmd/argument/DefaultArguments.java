package com.polopoly.ps.pcmd.argument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.ps.pcmd.parser.BooleanParser;
import com.polopoly.ps.pcmd.parser.ContentIdParser;
import com.polopoly.ps.pcmd.parser.ParseException;
import com.polopoly.ps.pcmd.parser.Parser;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.FetchingIterator;

public class DefaultArguments implements Arguments {
	private Map<String, List<String>> options = new HashMap<String, List<String>>();
	private Set<String> unusedParameters = new HashSet<String>();
	private List<String> arguments = new ArrayList<String>();
	private PolopolyContext context;
	private String toolName;

	public DefaultArguments(String toolName, Map<String, List<String>> options,
			List<String> arguments) throws ArgumentException {
		this.toolName = toolName;
		this.options = options;
		this.arguments = arguments;

		unusedParameters.addAll(options.keySet());

		for (int i = 0; i < arguments.size(); i++) {
			unusedParameters.add(getUnusedArgumentString(i));
		}
	}
	
	
	

	private String getUnusedArgumentString(int i) {
		return "argument " + (i + 1);
	}

	public void setContext(PolopolyContext context) {
		this.context = context;
	}

	public boolean getFlag(String option, boolean defaultValue)
			throws ParseException {
		List<String> optionValues = options.get(option);

		if (optionValues == null) {
			return defaultValue;
		}

		usedOption(option);

		if (optionValues.size() > 1) {
			System.out.println("Only one value for option \"" + option
					+ "\" was expected. " + "Only the value \""
					+ optionValues.get(0) + "\" will be used.");
		}

		return new BooleanParser().parse(optionValues.get(0));
	}

	public Collection<ContentId> getArgumentContentIds(int firstContentIdIdx,
			boolean stopOnException) throws ArgumentException {
		if (arguments.size() <= firstContentIdIdx) {
			throw new NotProvidedException(
					"Expected a list of content IDs as arguments.");
		}

		Collection<ContentId> contentIds = new ArrayList<ContentId>();

		Parser<ContentId> parser;

		if (context != null) {
			parser = new ContentIdParser(context);
		} else {
			parser = new ContentIdParser();
		}

		for (int i = firstContentIdIdx; i < arguments.size(); i++) {
			try {
				usedArgument(i);
				contentIds.add(parser.parse(arguments.get(i)));
			} catch (ArgumentException e) {
				if (stopOnException) {
					throw e;
				} else {
					System.err.println(e.getMessage());
				}
			}
		}

		return contentIds;
	}

	public <T> T getOption(String name, Parser<T> parser, String defaultString)
			throws ParseException {
		try {
			String optionString;
			try {
				optionString = getOptionString(name);
			} catch (NotProvidedException e) {
				optionString = defaultString;
			}

			return parser.parse(optionString);
		} catch (ParseException e) {
			e.setField(name);

			throw e;
		}
	}

	public <T> T getOption(String name, Parser<T> parser)
			throws ArgumentException {
		try {
			return parser.parse(getOptionString(name));
		} catch (ParseException e) {
			e.setField(name);

			throw e;
		}
	}
	
//	public List<String> getOptions() {
//		ArrayList<String> optionsList = new ArrayList<String>();
//		Set<Entry<String, List<String>>> entrySet = options.entrySet();
//		 for (Entry<String, List<String>> entry : entrySet) {
//			 entry.getKey();
//			 optionsList.add(entry.getKey());
//		}
//		 
//		 return optionsList;
//	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getOptions(String name, Parser<T> parser)
			throws ArgumentException {
		try {
			List<String> optionStrings = getOptionStrings(name);

			List<T> result = new ArrayList<T>();

			for (String optionString : optionStrings) {
				result.add(parser.parse(optionString));
			}

			return result;
		} catch (ParseException e) {
			e.setField(name);

			throw e;
		}
	}

	public List<String> getOptionStrings(String name)
			throws NotProvidedException {
		List<String> optionValues = options.get(name);

		if (optionValues == null) {
			throw new NotProvidedException(name);
		}

		usedOption(name);

		return optionValues;
	}

	public void setOptionString(String name, String value) {
		List<String> optionValues = options.get(name);

		if (optionValues == null) {
			optionValues = new ArrayList<String>();
			options.put(name, optionValues);
		} else {
			optionValues.clear();
		}

		optionValues.add(value);
	}

	public String getOptionString(String name) throws NotProvidedException {
		List<String> optionStrings = getOptionStrings(name);

		if (optionStrings.size() > 1) {
			System.out.println("Only one value for option \"" + name
					+ "\" was expected. " + "Only the value \""
					+ optionStrings.get(0) + "\" will be used.");
		}

		return optionStrings.get(0);
	}

	public String getOptionString(String name, String defaultValue) {
		try {
			return getOptionString(name);
		} catch (NotProvidedException e) {
			return defaultValue;
		}
	}
	

	private Iterator<ContentId> stdInContentIdIterator = new FetchingIterator<ContentId>() {
		private BufferedReader reader = new BufferedReader(
				new InputStreamReader(System.in));
		private boolean first = true;

		@Override
		protected ContentId fetch() {
			if (first) {
				System.err
						.println("Reading content IDs from standard input...");
				first = false;
			}

			try {
				String readLine = reader.readLine();

				if (readLine == null) {
					return null;
				}

				if (readLine.trim().length() == 0) {
					return fetch();
				}

				return new ContentIdParser(context).parse(readLine);
			} catch (IOException e) {
				System.err.println(e.toString());

				return null;
			} catch (ParseException e) {
				try {
					if (getFlag(ContentIdListParameters.STOPONEXCEPTION, true)) {
						throw new CMRuntimeException(e);
					} else {
						return fetch();
					}
				} catch (ParseException e1) {
					throw new CMRuntimeException(e);
				}
			}
		}
	};
	
	
	

	public Iterator<ContentId> getStdInContentIds() {
		return stdInContentIdIterator;
	}

	public String getArgument(int i) throws NotProvidedException {
		try {
			usedArgument(i);

			return arguments.get(i);
		} catch (IndexOutOfBoundsException e) {
			throw new NotProvidedException("Argument " + (i + 1));
		}
	}

	public <T> T getArgument(int i, Parser<T> parser) throws ArgumentException {
		usedArgument(i);

		return parser.parse(getArgument(i));
	}

	private void usedArgument(int i) {
		unusedParameters.remove(getUnusedArgumentString(i));
	}

	private void usedOption(String name) {
		unusedParameters.remove(name);
	}

	public int getArgumentCount() {
		return arguments.size();
	}

	public String getToolName() throws NotProvidedException {
		if (toolName == null) {
			throw new NotProvidedException(
					"Expected tool name as first argument.");
		}

		return toolName;
	}

	public Set<String> getUnusedParameters() {
		return unusedParameters;
	}




	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DefaultArguments [options=");
		builder.append(options);
		builder.append(", unusedParameters=");
		builder.append(unusedParameters);
		builder.append(", arguments=");
		builder.append(arguments);
		builder.append(", context=");
		builder.append(context);
		builder.append(", toolName=");
		builder.append(toolName);
		builder.append(", stdInContentIdIterator=");
		builder.append(stdInContentIdIterator);
		builder.append("]");
		return builder.toString();
	}

	
}
