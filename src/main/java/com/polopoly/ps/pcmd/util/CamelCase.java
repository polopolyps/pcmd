package com.polopoly.ps.pcmd.util;

public class CamelCase {
	private char lowerCaseWordSeparator;

	public CamelCase() {
		this('-');
	}
	
	public CamelCase(char lowerCaseWordSeparator) {
		this.lowerCaseWordSeparator = lowerCaseWordSeparator;
	}
	
	public String toCamelCase(String tool) {
		if (tool.length() == 0) {
			return "";
		}

		StringBuffer result = new StringBuffer(tool.length());

		boolean nextUppercase = true;

		for (int i = 0; i < tool.length(); i++) {
			char ch = tool.charAt(i);

			if (ch == lowerCaseWordSeparator) {
				nextUppercase = true;
				continue;
			}

			if (nextUppercase) {
				result.append(Character.toUpperCase(ch));
				nextUppercase = false;
			} else {
				result.append(ch);
			}
		}

		return result.toString();
	}

	public String fromCamelCase(String name) {
		if (name.length() == 0) {
			return "";
		}

		StringBuffer result = new StringBuffer(name.length());

		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);

			if (Character.isUpperCase(ch)) {
				if (result.length() > 0) {
					result.append(lowerCaseWordSeparator);
				}
				result.append(Character.toLowerCase(ch));
			} else {
				result.append(ch);
			}
		}

		return result.toString();
	}

}
