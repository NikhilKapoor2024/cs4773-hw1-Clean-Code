package misc;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Scanner;

import org.junit.BeforeClass;
import org.junit.Test;

import misc.EmployeeReport;

public class TestEmployeeReport {
	private static String expectedFromData1;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		StringBuffer fileContents = new StringBuffer();
		Scanner fileInput = new Scanner(new File("expected1.txt"));
		while(fileInput.hasNextLine())
			fileContents.append(fileInput.nextLine() + "\n");
		expectedFromData1 = fileContents.toString();
		fileInput.close();
	}

	@Test
	public void testFileData1() {
		assertEquals(expectedFromData1, EmployeeReport.generateReportFromFile("data1.txt"));
	}

	@Test
	public void testFileData2() {
		assertEquals(null, EmployeeReport.generateReportFromFile("data2.txt"));
	}

	@Test
	public void testFileData3() {
		assertEquals(null, EmployeeReport.generateReportFromFile("data3.txt"));
	}
}
