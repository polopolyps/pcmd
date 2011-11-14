package com.polopoly.util;

public final class OsUtils {
	
	private static String OS;
	
	static{
		OS = System.getProperty("os.name");
	}
	private OsUtils(){
		//
	}
	
	public static boolean isWindowsOS(){
		return OS.startsWith("Windows");
	}
}
