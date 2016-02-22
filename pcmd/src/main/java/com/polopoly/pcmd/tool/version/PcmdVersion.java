package com.polopoly.pcmd.tool.version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PcmdVersion {

	private static String VERSION_RESOURCE = "META-INF/pcmdVersion.txt";
	public static String CODE_STRING = null;
	private static Logger LOG = Logger.getLogger(PcmdVersion.class.getName());
	public static PcmdVersion CODE = null;

	static {
		ClassLoader cl = PcmdVersion.class.getClassLoader();
		try {
			CODE = readCurrentVersion(cl);
		} catch (Throwable t) {
			String m = "Could not init class.";
			if (cl instanceof URLClassLoader) {
				m += " Classpath was: " + Arrays.asList(((URLClassLoader) cl).getURLs());
			}
			LOG.log(Level.SEVERE, m, t);
		}
	}

	public PcmdVersion(String version) {

	}

	static PcmdVersion readCurrentVersion(ClassLoader classLoader) throws IOException {
		InputStream is = classLoader.getResourceAsStream(VERSION_RESOURCE);
		if (is == null) {
			throw new IOException("Could not find resource '" + VERSION_RESOURCE + "'.");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		CODE_STRING = br.readLine();
		br.close();

		if (CODE_STRING == null) {
			throw new IOException("Could not parse the string, " + "somehow empty string property set");
		}

		return new PcmdVersion(CODE_STRING);
	}
	
	@Override
	public String toString() {
		return CODE_STRING;
	}

}
