package com.polopoly.ps.pcmd.parser;

public class TimeStampParser implements Parser<Long> {

	@Override
	public Long parse(String string) throws ParseException {
		try {
			return Long.parseLong(string);
		} catch (NumberFormatException e) {
			throw new ParseException(this, string,
					"Expected a long number denoting a timestamp");
		}
	}

	@Override
	public String getHelp() {
		return "A timestamp as a long (denoting ms since 1970).";
	}

}
