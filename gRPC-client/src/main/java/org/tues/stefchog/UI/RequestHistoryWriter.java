package org.tues.stefchog.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class RequestHistoryWriter {
//	private List<String> names = new ArrayList();
//	private List<String> values = new ArrayList();
	private String fileName;
	
	public RequestHistoryWriter(String fileName) {
		super();
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void writeRequestFIle(List<String> names, List<String> values) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new FileOutputStream(new File(fileName),true));
			writer.println(names);
	    	writer.println(values);
	    	writer.println("delimiter");
	    	writer.close();
	    	System.out.println("request saved to file");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
