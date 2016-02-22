package com.polopoly.ps.pcmd.parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.polopoly.ps.pcmd.parser.ParseException;
import com.polopoly.ps.pcmd.parser.Parser;

public class DateParser implements Parser<Date> {
	private static final String FORMAT_STRING = "yyyyMMddHHmmss";
	private static final DateFormat FORMAT = new SimpleDateFormat(FORMAT_STRING);

	@Override
	public String getHelp() {
		return "A date of the format " + FORMAT_STRING;
	}

	@Override
	public Date parse(String string) throws ParseException {
		try {
			return FORMAT.parse(string);
		} catch (java.text.ParseException e) {
			throw new ParseException(this, string, e);
		}
	}

	public String format(Date date) {
		return FORMAT.format(date);
	}

}
