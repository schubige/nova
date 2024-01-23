package org.corebounce.nova.tools;

import java.io.File;
import java.io.IOException;

public class Convert3DD3RAW {

	@SuppressWarnings("nls")
	public static void main(String[] args) throws IOException {
		File src = new File(args[0]);
		if(src.isDirectory()) {
			for(File f : src.listFiles())
				if(f.isFile() && f.getName().endsWith(".3dd")) {
					new Reader3DD(f, Integer.parseInt(args[1]), Integer.parseInt(args[2]), 10, true);
				}
		} else {
			new Reader3DD(src, Integer.parseInt(args[1]), Integer.parseInt(args[2]), 10, true);
		}
	}
}
