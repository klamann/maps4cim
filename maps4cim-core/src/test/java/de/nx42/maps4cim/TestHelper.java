package de.nx42.maps4cim;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.common.io.Resources;

public class TestHelper {

	public static File getTestFolder() {
		URL url = Resources.getResource("testdir");
		File f;
		try {
		  f = new File(url.toURI());
		} catch(URISyntaxException e) {
		  f = new File(url.getPath());
		}
		return f.getParentFile();
	}

}
