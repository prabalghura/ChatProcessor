package com.smartapp.nlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TrainingDataExtractor {

	public static void main(String[] args){
		// TODO Auto-generated method stub
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader("/home/rajendrag/Downloads/sentences.csv"));
			FileWriter fw = new FileWriter(new File("/home/rajendrag/Desktop/sentences.csv"),true);
			bw = new BufferedWriter(fw);
			String line = "";
			line = br.readLine();
			int countq = 0, countd=0;
			while(line != null) {
				String[] values = line.split("\t");
				if(values[1].equals("eng")) {
					if(values[2].contains("?")) {
						String output = values[2].replace("\"", "");
						bw.write("QUERY " + output);
						bw.newLine();
						countq++;
					}else {
						if(countd<countq) {
							String output = values[2].replace("\"", "");
							bw.write("DECLARATIVE " + output);
							bw.newLine();
							countd++;
						}
					}
				}
				line = br.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
