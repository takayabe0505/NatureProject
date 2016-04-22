package Kumamoto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import jp.ac.ut.csis.pflow.geom.Mesh;

public class selectEmptyEvac {

	public static void main(String[] args) throws IOException{
		File in  = new File("c:/users/yabetaka/desktop/KumamotoProject/ksj.kumamoto_evacuation_facility_h24/ksj.kumamoto_evacuation_facility_full_h24.tsv");
		File out = new File("c:/users/yabetaka/desktop/KumamotoProject/ksj.kumamoto_evacuation_facility_h24/ksj.kumamoto_evacuation_facility_full_h24_notworking.csv");
		selectPOI(in,out);
	}
	
	public static void selectPOI(File POI, File out) throws IOException{
		File disasterday = new File("c:/users/yabetaka/desktop/KumamotoProject/Visu_ready2/kumamoto_20160418_22_mesh4_forplot.csv");
		HashMap<String,Integer> res = getdisasterday(disasterday);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		BufferedReader br = new BufferedReader(new FileReader(POI));
		String line = br.readLine();
		while((line=br.readLine())!=null){
			System.out.println(line);
			String[] tokens = line.split("\t");
			Double lon = Double.parseDouble(tokens[0]);
			Double lat = Double.parseDouble(tokens[1]);
			Mesh mesh = new Mesh(4,lon,lat);
			String mc = mesh.getCode();
			if(!res.containsKey(mc)){
				bw.write(line);
				bw.newLine();
			}
			else{
				if(res.get(mc).equals("0")){
					bw.write(line);
					bw.newLine();
				}
			}
		}
		br.close();
		bw.close();
	}
	
	public static HashMap<String,Integer> getdisasterday(File thatday) throws IOException{
		HashMap<String,Integer> res = new HashMap<String,Integer>();
		BufferedReader br = new BufferedReader(new FileReader(thatday));
		String line = br.readLine();
		while((line=br.readLine())!=null){
			String[] tokens = line.split("\t");
			String meshcode = tokens[0];
			Integer pop      = Integer.valueOf(tokens[1]);
			res.put(meshcode, pop);
		}
		br.close();
		return res;
	}
	
}
