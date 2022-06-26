package it.unimib.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unimib.model.RepairTarget;
import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

public final class SbflRankMergingUtilFlacoco {
	
	private static final String OUTPUT_FILENAME = "except_ranking.csv";
	
	public SbflRankMergingUtilFlacoco() {}
	
	public static void merge(List<RepairTarget> repairTargetList, String sbflRankFile, String output) {
		
		CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build(); // custom separator
		
		CSVReader reader;
		
		try {
			reader = new CSVReaderBuilder(
			      new FileReader(sbflRankFile))
			      .withCSVParser(csvParser)   // custom CSV parser
			      .build();
			
			List<String[]> content = reader.readAll();
			
			List<String[]> filteredContent = new ArrayList<String[]>();
			
			for (int i = 0; i < content.size(); i++) {
				if (content.get(i) != null) {
					if (Double.valueOf(content.get(i)[2]) > 0) {
						filteredContent.add(new String[]{content.get(i)[0]+":"+content.get(i)[1], content.get(i)[2]});
					} else {
						break;
					}
				}
			}
			
			if (repairTargetList != null) {
				for (int i = 0; i < repairTargetList.size(); i++) {
					for (int j = 0; j < filteredContent.size(); j++) {
						if (filteredContent.get(j) != null) {
							String sbflClassName = StringUtils.substringBefore(filteredContent.get(j)[0], ":");
							sbflClassName = StringUtils.substringBefore(sbflClassName, "$");
							if (repairTargetList.get(i).getSuspiciousLocation().getClassName().equals(sbflClassName)) {
								String sbflLine = StringUtils.substringAfter(filteredContent.get(j)[0], ":");
								if (repairTargetList.get(i).getSuspiciousLocation().getLineNumber() == Integer.valueOf(sbflLine)) {
									filteredContent.set(j, null);
									break;
								}
							}
						} 
					}
				}
				
				
				File directory = new File(output);
			    if (! directory.exists()){
			        directory.mkdirs();
			    }
			    
			    CSVWriter writer = new CSVWriter(new FileWriter(output + File.separator + OUTPUT_FILENAME, false),
					    ';',
					    CSVWriter.NO_QUOTE_CHARACTER,
					    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					    CSVWriter.RFC4180_LINE_END);
			    
			    List<String[]> finalOutput = new ArrayList<String[]>();
			    finalOutput.add(new String[] {"name", "suspiciousness_value"});
			    
			    for (int i = 0; i < repairTargetList.size(); i++) {
			    	finalOutput.add(new String[]{repairTargetList.get(i).toStringWithoutSuspiciousnessScore(), String.valueOf(repairTargetList.get(i).getSuspiciousnessScore())});
			    }
				
				finalOutput.addAll(filteredContent);
				
				writer.writeAll(finalOutput);
				writer.close();
			} else {
				File directory = new File(output);
			    if (! directory.exists()){
			        directory.mkdirs();
			    }
			    
			    CSVWriter writer = new CSVWriter(new FileWriter(output + File.separator + OUTPUT_FILENAME, false),
					    ';',
					    CSVWriter.NO_QUOTE_CHARACTER,
					    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
					    CSVWriter.RFC4180_LINE_END);
			    
			    List<String[]> finalOutput = new ArrayList<String[]>();
			    finalOutput.add(new String[] {"name", "suspiciousness_value"});
			    
			    finalOutput.addAll(filteredContent);
				
				writer.writeAll(finalOutput);
				writer.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CsvException e) {
			e.printStackTrace();
		}
	} 
}
