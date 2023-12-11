package misc;

import java.io.*;
import java.util.*;

/* This class containes variables and methods that will interact with a given file of a certain format. */
public class EmployeeReport {
	private static String [] firstNames, lastNames, employTypes;
	private static int [] ages, yearsHired;
	private static double [] payments;
	private static int ageSum, numCommissions, numHourlys, numSalaries;
	private static double sumCommissions, sumHourlys, sumSalaries;
	private static double averageAge, averageCommission, averageHourly, averageSalary;
	private static StringBuffer printString = new StringBuffer();
	private static Scanner fileScanner = null;
	
	// This method generates a report of the number of employees as well as averages and 
	// a count of employees that have shared first and last names.
	public static String generateReportFromFile(String inputFile) {
		
		fileScanner = createScannerObject(fileScanner, inputFile);
		
		arrayInitializer(fileScanner);
		fileScanner.close();

		fileScanner = createScannerObject(fileScanner, inputFile);

		int parseAndFillResult = parseThroughLineAndFillArrays(fileScanner);
		if (parseAndFillResult == 1 || parseAndFillResult == -1) { // 1 = failed to populate arrays, -1 = no records found
			System.err.println("Error with parsing and filling arrays.");
			return null;
		}

		printRowsOfEmployees(printString);
		averagesCalculationsAndPrinting(printString);

		commonNamesFinder("First", firstNames, printString);
		commonNamesFinder("Last", lastNames, printString);

		fileScanner.close(); // close the file
		return printString.toString();
	}

	private static Scanner createScannerObject(Scanner newScanner, String fileName) {
		try {
			newScanner = new Scanner(new File(fileName));
			return newScanner;
		}
		catch (FileNotFoundException error) {
			System.err.println(error.getMessage());
			return null;
		}
	}

	private static void arrayInitializer(Scanner scannerObj) {
		int lineCount = 0;

		while (scannerObj.hasNextLine()) {
			String currentLine = scannerObj.nextLine();
			if (currentLine.length() > 0)
				lineCount++;
		}

		firstNames = new String[lineCount];
		lastNames = new String[lineCount];
		ages = new int[lineCount];
		employTypes = new String[lineCount];
		payments = new double[lineCount];
		yearsHired = new int[lineCount];
	}

	private static int parseThroughLineAndFillArrays(Scanner scannerObj) {
		int linesInFile = 0;

		while (scannerObj.hasNextLine()) {
			String line = scannerObj.nextLine();
			if (line.length() > 0) {
				String [] words = line.split(",");

				int index = employeeSorter(words, linesInFile);
				
				firstNames[index] = words[0];
				lastNames[index] = words[1];
				employTypes[index] = words[3];
				try {
					ages[index] = Integer.parseInt(words[2]);
					payments[index] = Double.parseDouble(words[4]);
					yearsHired[index] = Integer.parseInt(words[5]);
				} catch (Exception e) { 
					System.err.println(e.getMessage());
					scannerObj.close();
					return 1;
				}

				linesInFile++;
			}
		}

		if (linesInFile == 0) {
			System.err.println("No records were found in file.");
			scannerObj.close();
			return -1;
		}
		
		return 0;
	}
	
	private static int employeeSorter(String[] words, int linesInFile) {
		int index = 0;

		for (; index < lastNames.length; index++) {
			if (lastNames[index] == null)
				break;
			
			if (lastNames[index].compareTo(words[1]) > 0) {
				for (int i = linesInFile; i > index; i--) {
					firstNames[i] = firstNames[i - 1];
					lastNames[i] = lastNames[i - 1];
					ages[i] = ages[i - 1];
					employTypes[i] = employTypes[i - 1];
					payments[i] = payments[i - 1];
					yearsHired[i] = yearsHired[i - 1];
				}
				break;
			}
		}

		return index;
	}

	private static StringBuffer printRowsOfEmployees(StringBuffer sbObj) {
		sbObj.append(String.format("# of people imported: %d\n", firstNames.length));

		sbObj.append(String.format("\n%-30s %s %s %-12s %13s\n", "Person Name", "Age", "Hired", "Emp. Type", "Pay"));
		for(int i = 0; i < 30; i++)
			sbObj.append(String.format("-"));
		sbObj.append(String.format(" ---"));
		sbObj.append(String.format(" ----- "));
		for(int i = 0; i < 12; i++)
			sbObj.append(String.format("-"));
		sbObj.append(String.format(" "));
		for(int i = 0; i < 13; i++)
			sbObj.append(String.format("-"));
		sbObj.append(String.format("\n"));

		for(int i = 0; i < firstNames.length; i++) {
			sbObj.append(String.format("%-30s %-3d %-5d %-12s $%12.2f\n", firstNames[i] + " " + lastNames[i], ages[i]
						, yearsHired[i], employTypes[i], payments[i]));
		}

		return sbObj;
	}

	private static StringBuffer averagesCalculationsAndPrinting(StringBuffer sbObj) {

		for(int i = 0; i < firstNames.length; i++) {
			ageSum += ages[i];
			if(employTypes[i].equals("Commission")) {
				sumCommissions += payments[i];
				numCommissions++;
			} else if(employTypes[i].equals("Hourly")) {
				sumHourlys += payments[i];
				numHourlys++;
			} else if(employTypes[i].equals("Salary")) {
				sumSalaries += payments[i];
				numSalaries++;
			}
		}

		averageAge = (float) ageSum / firstNames.length;
		sbObj.append(String.format("\nAverage age:         %12.1f\n", averageAge));
		averageCommission = sumCommissions / numCommissions;
		sbObj.append(String.format("Average commission:  $%12.2f\n", averageCommission));
		averageHourly = sumHourlys / numHourlys;
		sbObj.append(String.format("Average hourly wage: $%12.2f\n", averageHourly));
		averageSalary = sumSalaries / numSalaries;
		sbObj.append(String.format("Average salary:      $%12.2f\n", averageSalary));

		return sbObj;
	}

	private static void commonNamesFinder(String namePart, String [] names, StringBuffer sb) {
		HashMap<String, Integer> nameHashMap = new HashMap<String, Integer>();
		int numNames = 0;
		int numMatches = 0;

		for (int i = 0; i < names.length; i++) {
			if (nameHashMap.containsKey(names[i])) {
				nameHashMap.put(names[i], nameHashMap.get(names[i]) + 1);
				numNames++;
			} else {
				nameHashMap.put(names[i], 1);
			}
		}

		sb.append(String.format("\n%s names with more than one person sharing it:\n", namePart));
		if (numNames > 0) {
			Set<String> set = nameHashMap.keySet();
			for (String str : set) {
				if (nameHashMap.get(str) > 1) {
					numMatches = nameHashMap.get(str);
					sb.append(String.format("%s, # people with this name: %d\n", str, numMatches));
				}
			}
		} else {
			sb.append(String.format("All %s names are unique", namePart));
		}
	}
}