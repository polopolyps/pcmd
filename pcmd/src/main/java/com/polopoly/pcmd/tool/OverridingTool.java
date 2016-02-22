package com.polopoly.pcmd.tool;

import com.polopoly.ps.pcmd.util.ToolRetriever;

/**
 * If you want to override an existing tool with the same name, implement this
 * interface.
 */
public interface OverridingTool {
	/**
	 * Will be called by the {@link ToolRetriever} when there are multiple tools
	 * with the same name. Should return true if this tool should be used
	 * instead of the specified tool.
	 */
	boolean isOverriderOf(Tool<?> tool);
}
