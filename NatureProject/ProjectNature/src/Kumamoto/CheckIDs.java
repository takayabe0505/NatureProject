package Kumamoto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import ParalysisRate.SmallMethods;

public class CheckIDs {

	public static String  homepath = "/home/t-tyabe/Kumamoto/";
	public static String  respath  = "/home/t-tyabe/Kumamoto/results0420/";

	public static void uncompress_getIDs(String yyyymmdd) throws IOException{
		SmallMethods.extractfromcommand(yyyymmdd); System.out.println("#done uncompressing ");

//		HashMap<Integer, Double> resforday = new HashMap<Integer, Double>();
		File in = new File("/home/t-tyabe/Data/grid/0/tmp/ktsubouc/gps_"+yyyymmdd+".csv");
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		int count = 0;
		while((line=br.readLine())!=null){
			String[] tokens = line.split("\t");
			String id = tokens[0];
			if(id.length()>0){
				count++;
				System.out.println(id);
			}
		}
		System.out.println("======");
		System.out.println("======");
		System.out.println(count+" lines have first column");
		br.close();
	}
}
