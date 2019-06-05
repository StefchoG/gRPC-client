package org.tues.stefchog.UI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RequestsHistoryReader {
	private String fileName;
	
	public RequestsHistoryReader(String fileName) {
		super();
		this.fileName = fileName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public List<String> readRequestFile() {
		File file = new File(fileName);
		Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		sc.useDelimiter("delimiter");
		
		List <String> result = new ArrayList();
		result.add(sc.next());
		while(sc.hasNext()) {
			result.add(sc.next());
		}
		sc.close();
		
		System.out.println(result);
		return 	result.subList(result.size()-6 , result.size() );
	}
	
	
	
}
