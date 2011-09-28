package com.polopoly.ps.pcmd.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import com.polopoly.pcmd.tool.OverridingTool;
import com.polopoly.pcmd.tool.Tool;
import com.polopoly.util.CheckedCast;
import com.polopoly.util.CheckedClassCastException;

public class ToolRetriever {
	public static class NoSuchToolException extends Exception {
		public NoSuchToolException(String message) {
			super(message);
		}
	}

	private static List<String> TOOLS_PACKAGES = new ArrayList<String>();

	static {
		TOOLS_PACKAGES.add("com.polopoly.ps.pcmd.tool");
		TOOLS_PACKAGES.add("com.polopoly.pcmd.tool");
	}

	public static Tool<?> getTool(String toolShortName)
			throws NoSuchToolException {
		String toolClassName = toCamelCase(toolShortName) + "Tool";

		Tool<?> tool = null;

		try {
			for (String packageName : TOOLS_PACKAGES) {
				try {
					Tool<?> potentialTool = CheckedCast.cast(
							Class.forName(packageName + "." + toolClassName)
									.newInstance(), Tool.class);

					warnMultipleTools(toolShortName, tool, potentialTool);

					tool = potentialTool;
				} catch (ClassNotFoundException e) {
					// try next package;
				}
			}

			for (Tool<?> potentialTool : getAllTools()) {
				if (toolClassName.equals(potentialTool.getClass()
						.getSimpleName())) {
					warnMultipleTools(toolShortName, tool, potentialTool);

					tool = potentialTool;
				}
			}

			if (tool == null) {
				throw new NoSuchToolException(
						"Tool \""
								+ toolShortName
								+ "\" not found. Looked for implementation in a class called "
								+ toolClassName + " in packages "
								+ TOOLS_PACKAGES + ".");
			}
		} catch (CheckedClassCastException e) {
			throw new NoSuchToolException("The tool " + toolClassName
					+ " did not implement the Tool interface.");
		} catch (InstantiationException e) {
			throw new NoSuchToolException("The tool " + toolClassName
					+ " could not be instantiated: " + e);
		} catch (IllegalAccessException e) {
			throw new NoSuchToolException("The tool " + toolClassName
					+ " could not be instantiated: " + e);
		} catch (NoClassDefFoundError e) {
			throw new NoSuchToolException("The tool " + toolClassName
					+ " could not be instantiated: " + e);
		}

		return tool;
	}

	private static Tool<?> warnMultipleTools(String toolShortName,
			Tool<?> tool, Tool<?> potentialTool) {
		if (tool == null || tool.equals(potentialTool)) {
			return tool;
		}

		if (tool instanceof OverridingTool
				&& ((OverridingTool) tool).isOverriderOf(potentialTool)) {
			// clear case. no warning.
			return tool;
		}

		if (potentialTool instanceof OverridingTool
				&& ((OverridingTool) potentialTool).isOverriderOf(tool)) {
			// clear case. no warning.
			return potentialTool;
		}

		Tool<?> result = potentialTool;

		if (potentialTool instanceof OverridingTool) {
			result = potentialTool;
		}

		if (tool instanceof OverridingTool) {
			result = tool;
		}

		System.out
				.println("There are multiple tools with the name "
						+ toolShortName
						+ ": "
						+ potentialTool.getClass().getName()
						+ " and "
						+ tool.getClass().getName()
						+ " and none of them implemented OverridingTool and indicated that they are an overriding tool. Using "
						+ result.getClass().getName());

		return result;
	}

	public static List<Tool<?>> getAllTools() {
		List<Tool<?>> tools = new ArrayList<Tool<?>>();

		try {
			System.err.println("Available tools: ");
			@SuppressWarnings("rawtypes")
			ServiceLoader<Tool> toolLoader = ServiceLoader.load(Tool.class);

			for (Tool<?> tool : toolLoader) {
				tools.add(tool);
			}

			Collections.sort(tools, new Comparator<Tool<?>>() {
				@Override
				public int compare(Tool<?> t1, Tool<?> t2) {
					return t1.getClass().getName()
							.compareTo(t2.getClass().getName());
				}
			});
		} catch (NoClassDefFoundError e) {
			System.err
					.println("You need JDK 1.6+ to retrieve information on available tools.");
		}

		return tools;
	}

	private static String toCamelCase(String tool) {
		if (tool.length() == 0) {
			return "";
		}

		StringBuffer result = new StringBuffer(tool.length());

		boolean nextUppercase = true;

		for (int i = 0; i < tool.length(); i++) {
			char ch = tool.charAt(i);

			if (ch == '-') {
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

	private static String fromCamelCase(String name) {
		if (name.length() == 0) {
			return "";
		}

		StringBuffer result = new StringBuffer(name.length());

		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);

			if (Character.isUpperCase(ch)) {
				if (result.length() > 0) {
					result.append('-');
				}
				result.append(Character.toLowerCase(ch));
			} else {
				result.append(ch);
			}
		}

		return result.toString();
	}

	public static String getToolName(Class<?> toolClass) {
		String name = toolClass.getSimpleName();

		if (name.endsWith("Tool")) {
			name = name.substring(0, name.length() - 4);
		}

		return fromCamelCase(name);
	}

	public static void addToolsPackage(String packageName) {
		TOOLS_PACKAGES.add(packageName);
	}

	public static void clearToolsPackages() {
		TOOLS_PACKAGES.clear();
	}
}
