package Kumamoto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class POI {

	public static void main(String[] args) throws IOException{
		File in  = new File("c:/users/yabetaka/desktop/KumamotoProject/ksj.kumamoto_evacuation_facility_h24/ksj.kumamoto_evacuation_facility_full_h24.tsv");
		File out = new File("c:/users/yabetaka/desktop/KumamotoProject/ksj.kumamoto_evacuation_facility_h24/itiji_only.csv");
		selectPOI(in,out);
	}
	
	public static void selectPOI(File POI, File out) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		BufferedReader br = new BufferedReader(new FileReader(POI));
		String line = br.readLine();
		while((line=br.readLine())!=null){
			System.out.println(line);
			String[] tokens = line.split("\t");
			String type = tokens[5];
			if(type.contains("itiji")){
				bw.write(line);
				bw.newLine();
			}
		}
		br.close();
		bw.close();
	}
	
}
