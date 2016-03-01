package com.polopoly.ps.pcmd.text;

public class ComponentValueParser {

	public String escape(String value) {
		return value.replaceAll("\\:", "\\\\:");
	}

}
