package de.nx42.maps4cim;


import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.beust.jcommander.ParameterException;

import de.nx42.maps4cim.util.gis.Coordinate;

public class LauncherTest {

	@Test
	public void testParseFile() {
		try {
			String[] args = new String[]{ "-c", "file.xml" };
			Launcher l = new Launcher();
			l.parse(args);
			assertEquals(new File("file.xml"), l.config);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testParseDouble() {
		try {
			String[] args = new String[]{ "-e", "8.33" };
			Launcher l = new Launcher();
			l.parse(args);
			assertEquals(8.33, l.extent, 0.0000001);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testParseCoordinate() {
		try {
			String[] args = new String[]{ "-ce", "48.5,11.5" };
			Launcher l = new Launcher();
			l.parse(args);
			assertEquals(new Coordinate(48.5, 11.5), l.center);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testParseFileFail() {
		try {
			String[] args = new String[]{ "-c", "file:?.xml" };
			Launcher l = new Launcher();
			l.parse(args);
			// hint: does work on some OS...
//			fail("Should have thrown Exception!");
		} catch(ParameterException e) {
			// great :)
		} catch(Exception e) {
			// unexpected :(
			fail(e.getMessage());
		}
	}

	@Test
	public void testParseDoubleFail() {
		try {
			String[] args = new String[]{ "-e", "eight.33" };
			Launcher l = new Launcher();
			l.parse(args);
			fail("Should have thrown Exception!");
		} catch(ParameterException e) {
			// great :)
		} catch(Exception e) {
			// unexpected :(
			fail(e.getMessage());
		}
	}

	@Test
	public void testParseCoordinateFail() {
		try {
			String[] args = new String[]{ "-ce", "48.5,", "11.5" };
			Launcher l = new Launcher();
			l.parse(args);
			fail("Should have thrown Exception!");
		} catch(ParameterException e) {
			// great :)
		} catch(Exception e) {
			// unexpected :(
			fail(e.getMessage());
		}
	}

//	@Test
//	public void testPrintHelp() {
//		try {
//			System.out.println("Help arg:");
//			String[] args = new String[]{ "-h" };
//			Launcher l = new Launcher();
//			l.parse(args);
//		} catch(Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//	}

//	@Test
//	public void testPrintHelp2() {
//		try {
//			System.out.println("No arg:");
//			String[] args = new String[]{ };
//			Launcher l = new Launcher();
//			l.parse(args);
//		} catch(Exception e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
//	}

}
