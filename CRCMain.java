
// Name: Tyler Gauntlett
// NID: ty340586
// Course: CIS3360-16Spring 0R02
// Date: 3/30/2016

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class CRCMain {

	public static String CRCPOLY = "10000010110001001";

	public static void load() throws URISyntaxException, IOException {

		Scanner sc = new Scanner(System.in);

		System.out.println("Enter the name of the file you want to check:");

		boolean quit = false;
		String fileChoiceString;

		File f = null;

		// Check if the file is inside the src directory. Run a loop until a
		// valid file is entered.
		do {

			try {

				fileChoiceString = sc.next();

				URL openKeyFile = CRCMain.class.getResource(fileChoiceString);
				f = new File(openKeyFile.toURI());

				if (f.exists()) {
					quit = true;
				}
			} catch (Exception e) {

				System.out.println("Please enter a file located in the src directory.");

			}

		} while (!quit);

		Scanner fileChoice = new Scanner(f);
		int runCount = 0;

		// Statement to keep track of the current input so it doesn't have to be
		// continuously scanned.
		String currentFileInput = null;
		StringBuilder sb;
		quit = false;

		do {

			System.out.println("-------------Menu-------------");
			System.out.println("1. Calculate CRC");
			System.out.println("2. Verify CRC");
			System.out.println("3. Exit");
			System.out.println("Choose from the above menu: ");

			// Take in user menu selection.
			String menuItem = sc.next();

			// Switch statement to handle menu.
			switch (menuItem) {
			case "1":

				sb = new StringBuilder();

				// If it is the first time this is running,
				// scan the input in with a scanner.
				if (runCount == 0) {
					while (fileChoice.hasNextLine()) {
						sb.append(fileChoice.nextLine());
					}

					currentFileInput = sb.toString();
				}
				// If this isn't the first run, there is already a
				// computed value from the previous run. Use that
				// instead.
				else {
					sb.append(currentFileInput);
				}

				// Increase count after the program has ran 1 time.
				runCount++;

				// Return the current input.
				currentFileInput = CalculateCRC(fileChoice, f, sb);
				break;

			case "2":

				sb = new StringBuilder();

				// If it is the first time this is running,
				// scan the input in with a scanner.
				if (runCount == 0) {
					while (fileChoice.hasNextLine()) {
						sb.append(fileChoice.nextLine());
					}

					currentFileInput = sb.toString();
				}
				// If this isn't the first run, there is already a
				// computed value from the previous run. Use that
				// instead.
				else {
					sb.append(currentFileInput);
				}

				// Increase count after the program has ran 1 time.
				runCount++;

				// Run the verify CRC.
				VerifyCRC(fileChoice, f, sb);
				break;

			case "3":
				System.out.println("You've chosen Exit.");
				quit = true;
				break;

			default:
				System.out.println("Invalid choice. Please enter a number 1-3 inclusively.");

			}
		} while (!quit);

		sc.close();
		fileChoice.close();

	}

	// Converts hex to binary.
	static String hexToBin(String s) {
		return new BigInteger(s, 16).toString(2);
	}

	// Converts binary to hex.
	static String binToHex(String binaryStr) {
		int bin = Integer.parseInt(binaryStr, 2);
		String hexStr = Integer.toString(bin, 16);

		return hexStr;
	}

	// XOR bits from one string together with another string.
	static StringBuilder xOrBits(StringBuilder original) {

		for (int k = 0; k < CRCPOLY.length(); k++) {
			if (original.charAt(k) == CRCPOLY.charAt(k)) {
				original.replace(k, k + 1, "0");
			} else
				original.replace(k, k + 1, "1");
		}

		return original;
	}

	public static String CalculateCRC(Scanner fileChoice, File f, StringBuilder sb) throws URISyntaxException {

		String hexInputString = sb.toString();

		String binaryInputString = hexToBin(hexInputString);

		System.out.println("The input file (hex): " + hexInputString);

		System.out.println("The input file (bin):");

		// Loop to print binary input bits in sets of 4 with 32 bits to a line.
		int spaciningCounter = binaryInputString.length();
		for (int i = 0; i < binaryInputString.length(); i++) {

			// Handles spaces.
			if (spaciningCounter % 4 == 0 && i != 0 && (binaryInputString.length() - spaciningCounter) % 32 != 0)
				System.out.print(" ");

			// Prints char.
			System.out.print(binaryInputString.charAt(i));
			spaciningCounter--;

			// Math to calculate when to put a new line.
			if ((binaryInputString.length() - spaciningCounter) % 32 == 0)
				System.out.println();
		}

		System.out.println();

		System.out.print("The polynomial that was used (binary bit string): ");

		// Print CRC polynomial in groups of 4.
		spaciningCounter = CRCPOLY.length();
		for (int i = 0; i < CRCPOLY.length(); i++) {
			if (spaciningCounter % 4 == 0 && i != 0)
				System.out.print(" ");

			System.out.print(CRCPOLY.charAt(i));
			spaciningCounter--;
		}
		// New line for print purposes.
		System.out.println();

		System.out.println("We will append " + (CRCPOLY.length() - 1) + " zeros at the end of the binary input.\n");

		System.out.println("The binary string answer at each XOR step of CRC calculation:");

		// Clear string builder before reusing.
		sb.delete(0, sb.length());

		sb.append(binaryInputString);

		// Add the number of 0's on the end base on input.
		for (int i = 0; i < CRCPOLY.length() - 1; i++) {
			sb.append('0');
		}

		// Print unaltered binary input in sets of 4.
		spaciningCounter = sb.toString().length();
		for (int i = 0; i < sb.toString().length(); i++) {
			if (spaciningCounter % 4 == 0 && i != 0)
				System.out.print(" ");

			System.out.print(sb.toString().charAt(i));
			spaciningCounter--;
		}

		System.out.println();

		// Loop that generates and prints the xor bits.
		int skipCount = 0;
		for (int j = 0; j < sb.length() - CRCPOLY.length() + skipCount; j++) {

			try {

				// Check if the number has leading zeros. If so, skip them
				// and keep track of how many were skipped.
				while (sb.charAt(j) == '0') {
					j++;
					skipCount++;
				}

				// Create variables.
				StringBuilder temp = new StringBuilder();
				String modifiedSB = null;

				// Break off CRCPOLY.length() amount of bits from the binary
				// input before XOR'ing.
				modifiedSB = sb.substring(j, j + CRCPOLY.length());

				// Add the string to an empty string builder.
				temp.append(modifiedSB);

				// Run it through the XOR function.
				temp = xOrBits(temp);

				// Replace the master string builder with the slave
				// derived from the output of the XOR function.
				sb.replace(j, j + CRCPOLY.length(), temp.toString());

				// Print in subsections of 4 bits together.
				spaciningCounter = sb.toString().length();
				for (int i = 0; i < sb.toString().length(); i++) {
					if (spaciningCounter % 4 == 0 && i != 0)
						System.out.print(" ");

					System.out.print(sb.toString().charAt(i));
					spaciningCounter--;

				}

				System.out.println();

			} catch (Exception e) {
				// Run the algorithm until arrayOutOfBounds. Catch this error
				// and break from loop.
				break;
			}

		}

		// Create a string that is size 0 to highest order exponent starting
		// in the LSB.
		String CRCBinString = sb.substring(sb.length() - CRCPOLY.length() + 1, sb.length());

		String CRCHexString = binToHex(CRCBinString).toUpperCase();

		System.out.print("Thus, the CRC is ");

		// Prints with proper spacing.
		spaciningCounter = CRCBinString.length();
		for (int i = 0; i < CRCBinString.length(); i++) {
			if (spaciningCounter % 4 == 0 && i != 0)
				System.out.print(" ");

			System.out.print(CRCBinString.charAt(i));
			spaciningCounter--;
		}

		System.out.println(" (bin) = " + CRCHexString + " (hex)");

		System.out.println("CRC has been appended to the end of the input file.");

		System.out.println("Reading input file again: ");

		// Write to file enclosed in the try block below.
		try {

			// Transfer the src extension to the existing file name.
			File tempFile = new File("src/", f.getName());

			// Write to the src file. ACTIVATE IF USING ECLIPSE
			FileWriter fileWritter = new FileWriter(tempFile.getAbsolutePath(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(CRCHexString);
			bufferWritter.flush();
			bufferWritter.close();
			fileWritter.close();

			// Write to the bin file.
			fileWritter = new FileWriter(f.getAbsolutePath(), true);
			bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(CRCHexString);
			bufferWritter.flush();
			bufferWritter.close();
			fileWritter.close();
			
			
		

		} catch (IOException e) {

			try{
				// Write to the bin file.
				FileWriter fileWritter = new FileWriter(f.getAbsolutePath(), true);
				BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
				bufferWritter.write(CRCHexString);
				bufferWritter.flush();
				bufferWritter.close();
				fileWritter.close();
				
			}
			catch(IOException ex){
				
				ex.printStackTrace();
			}
			
			

		}

		StringBuilder tempSB = new StringBuilder();

		// Read from new file enclosed in the try block below.
		try {

			// Create a new temp scanner to read the file.

			Scanner tempScanner = new Scanner(f);

			while (tempScanner.hasNext()) {
				tempSB.append(tempScanner.next());
			}

			tempScanner.close();

			System.out.println(tempSB.toString());

			sb.delete(0, sb.length());

			sb.append(tempSB.toString());

			System.out.println("Closing input file.");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return tempSB.toString();

	}

	public static void VerifyCRC(Scanner fileChoice, File f, StringBuilder sb) {

		String hexInputString = sb.toString();

		String binaryInputString = hexToBin(hexInputString);

		System.out.println("The input file (hex): " + hexInputString);

		System.out.println("The input file (bin):");

		// Loop to print binary input bits in sets of 4 with 32 bits to a line.
		int spaciningCounter = binaryInputString.length();
		for (int i = 0; i < binaryInputString.length(); i++) {

			// Handles spaces.
			if (spaciningCounter % 4 == 0 && i != 0 && (binaryInputString.length() - spaciningCounter) % 32 != 0)
				System.out.print(" ");

			// Prints char.
			System.out.print(binaryInputString.charAt(i));
			spaciningCounter--;

			// Math to calculate when to put a new line.
			if ((binaryInputString.length() - spaciningCounter) % 32 == 0)
				System.out.println();
		}

		// Print used for formatting.
		System.out.println();

		System.out.print("The polynomial that was used (binary bit string): ");

		// Print CRC polynomial in groups of 4.
		spaciningCounter = CRCPOLY.length();
		for (int i = 0; i < CRCPOLY.length(); i++) {
			if (spaciningCounter % 4 == 0 && i != 0)
				System.out.print(" ");

			System.out.print(CRCPOLY.charAt(i));
			spaciningCounter--;
		}
		// Print statement for formatting.
		System.out.println();

		// Calculate the CRC based on the size of the CRC polynomial.
		String CRCHexString = hexInputString.substring(hexInputString.length() - ((CRCPOLY.length() - 1) / 4),
				hexInputString.length());

		System.out.println("The " + (CRCPOLY.length() - 1) + "-bit CRC at the end of the file: " + CRCHexString + "\n");

		// Clear string builder before reusing.
		sb.delete(0, sb.length());

		sb.append(binaryInputString);

		System.out.println("The binary string answer at each XOR step of CRC verification:");

		// Loop that generates and prints the xor bits.
		int skipCount = 0;
		for (int j = 0; j < sb.length() - CRCPOLY.length() + skipCount; j++) {

			try {

				// Check if the number has leading zeros. If so, skip them
				// and keep track of how many were skipped.
				while (sb.charAt(j) == '0') {
					j++;
					skipCount++;
				}

				// Create variables.
				StringBuilder temp = new StringBuilder();
				String modifiedSB = null;

				// Break off CRCPOLY.length() amount of bits from the binary
				// input before XOR'ing.
				modifiedSB = sb.substring(j, j + CRCPOLY.length());

				// Add the string to an empty string builder.
				temp.append(modifiedSB);

				// Run it through the XOR function.
				temp = xOrBits(temp);

				// Replace the master string builder with the slave
				// derived from the output of the XOR function.
				sb.replace(j, j + CRCPOLY.length(), temp.toString());

				// Print in subsections of 4 bits together.
				spaciningCounter = sb.toString().length();
				for (int i = 0; i < sb.toString().length(); i++) {
					if (spaciningCounter % 4 == 0 && i != 0)
						System.out.print(" ");

					System.out.print(sb.toString().charAt(i));
					spaciningCounter--;
				}

				System.out.println();

			} catch (Exception e) {
				// Run the algorithm until arrayOutOfBounds. Catch this error
				// and break from loop.
				break;
			}

		}

		boolean result = true;

		// Scan through output for non zero values.
		for (int i = 0; i < sb.length(); i++) {
			if (sb.toString().charAt(i) != '0') {
				result = false;
				break;
			}
		}

		// Check if there were any non zeros found above.
		System.out.print("Did the CRC check pass? (Yes or No): ");

		if (result)
			System.out.print("Yes\n");
		else
			System.out.print("No\n");

	}

	public static void main(String[] args) {

		try {
			load();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
}
