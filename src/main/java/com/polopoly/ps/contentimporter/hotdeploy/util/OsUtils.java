package com.polopoly.ps.contentimporter.hotdeploy.util;

public final class OsUtils {
	private static String OS;

	static{
		OS = System.getProperty("os.name");
	}

	public static boolean isWindowsOS(){
		return OS.startsWith("Windows");
	}
}
