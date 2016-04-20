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
//		SmallMethods.extractfromcommand2(yyyymmdd); System.out.println("#done uncompressing ");

//		HashMap<Integer, Double> resforday = new HashMap<Integer, Double>();
		File in = new File("/home/t-tyabe/Data/grid/0/tmp/hadoop-ktsubouc/data_"+yyyymmdd+".csv");
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		int count = 0;
		int count2 = 0;
		while((line=br.readLine())!=null){
			String[] tokens = line.split("\t");
			String id = tokens[0];
			if(id.length()>0){
				count++;
//				System.out.println(id);
			}
			count2++;
		}
		System.out.println("======");
		System.out.println("======");
		System.out.println(count+" out of "+count2+" lines have first column");
		br.close();
	}
	
	public static void main(String args[]) throws IOException{
		File home = new File(homepath); home.mkdir();
		File res = new File(respath); res.mkdir();
		uncompress_getIDs("20160418");
	}
	
}
