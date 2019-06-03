package org.tues.stefchog.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class RequestsHistoryReader {
	private String fileName;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public RequestsHistoryReader(String fileName) {
		super();
		this.fileName = fileName;
	}
	
	public String readRequestFile() {
		File file = new File(fileName);
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sc.useDelimiter("delimiter");
		
		String result = sc.next();
		System.out.println(result);
		while(sc.hasNext()) {
			result =sc.next();
			System.out.println(result);
		}
		sc.close();
		return result;
	}
	
	
	
}
