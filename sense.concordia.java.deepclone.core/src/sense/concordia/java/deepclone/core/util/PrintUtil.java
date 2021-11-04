package sense.concordia.java.deepclone.core.util;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class PrintUtil {
	public static CSVPrinter createCSVPrinter(String fileName, String... header) throws IOException {
		return new CSVPrinter(new FileWriter(fileName, true), CSVFormat.EXCEL.withHeader(header));
	}
	
}