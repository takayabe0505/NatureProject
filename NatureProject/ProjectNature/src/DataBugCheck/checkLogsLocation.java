package DataBugCheck;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import ParalysisRate.SmallMethods;

public class checkLogsLocation {

	public static void main(String[] args) throws IOException{
		
		String d = "20150624";
		SmallMethods.extractfromcommand(d); System.out.println("#done uncompressing ");
		File in = new File("/home/t-tyabe/Data/grid/0/tmp/ktsubouc/gps_"+d+".csv");
		File out = new File("/home/t-tyabe/Data/grid/0/tmp/ktsubouc/gps_"+d+"_selected100000.csv");
		BufferedReader br = new BufferedReader(new FileReader(in));
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line = null;
		while((line=br.readLine())!=null){
			Double rand = Math.random();
			if(rand<0.0001){
				String[] tokens = line.split("\t");
				String lat = tokens[2];
				String lon = tokens[3];
				bw.write(lon+","+lat);
				bw.newLine();
			}
		}
		br.close();
		bw.close();
	}
	
}
