package Kumamoto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GetDiffFile {

	public static String  homepath = "C:/Users/yabetaka/Desktop/KumamotoProject/";
	public static String  res_path = "C:/Users/yabetaka/Desktop/KumamotoProject/Visu_ready_heatmap_1/";

	//TODO : change these parameters
	public static String  exp_path = homepath+"mesh_22_token1/";
	public static String  meshsize = "4";
	public static String  hour     = "22";
//	public static double  k        = 2d;
	//TODO : change these parameters

	public static void main(String args[]) throws IOException{
		File home = new File(homepath); home.mkdir();
		File res = new File(res_path); res.mkdir();

		ArrayList<String> list_normal = new ArrayList<String>();
		list_normal.add("20151215");
		list_normal.add("20151216");
		list_normal.add("20151217");
		list_normal.add("20151218");
		list_normal.add("20160127");
		list_normal.add("20160128");
		list_normal.add("20160130");
		list_normal.add("20160129");
		File normal_avg = new File(res_path+"normal_average.csv");
		NormalizePop.normalize_getaverage(list_normal,normal_avg,exp_path,hour,meshsize);
		System.out.println("got normal avg file");

		ArrayList<String> list_disaster = new ArrayList<String>();
		list_disaster.add("20160414");
		list_disaster.add("20160415");
		list_disaster.add("20160416");
		list_disaster.add("20160417");
		list_disaster.add("20160418");
		for(String d : list_disaster){
			File thatday = new File(exp_path+"kumamoto_"+d+"_"+hour+"_mesh"+meshsize+".csv");
			File out     = new File(res_path+"kumamoto_"+d+"_"+hour+"_mesh"+meshsize+"_forplot.csv");
			double magfac = NormalizePop.getMagFac(thatday);
			calculatediff(normal_avg,thatday,out,magfac);
			System.out.println("got visualization file for "+d);
		}

	}

	public static void calculatediff(File normal, File thatday, File out, double magfac) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		HashMap<String,String> map = normal_intomap(normal);
		BufferedReader br = new BufferedReader(new FileReader(thatday));
		String line = br.readLine();
		while((line=br.readLine())!=null){
			String[] tokens = line.split("\t");
			String meshcode = tokens[0];
			String pop      = tokens[1];
			String wkt      = tokens[2];
			if(map.containsKey(meshcode)){
				Double normal_avg = Double.parseDouble(map.get(meshcode).split("\t")[1]);
				Double normal_dev = Double.parseDouble(map.get(meshcode).split("\t")[2]);
				Double thisday_val = Double.parseDouble(tokens[1])*magfac;
				String code = null;
				if(Math.abs(thisday_val-normal_avg)>normal_dev*2d){
					if((thisday_val-normal_avg)>=0){
						code = "2";
					}
					else if((thisday_val-normal_avg)<=0){
						code = "-2";
					}
				}
				else if(Math.abs(thisday_val-normal_avg)>normal_dev*1d){
					if((thisday_val-normal_avg)>=0){
						code = "1";
					}
					else if((thisday_val-normal_avg)<=0){
						code = "-1";
					}
				}
				else{
					code = "0";
				}
				bw.write(meshcode+"\t"+pop+"\t"+code+"\t"+wkt);
				bw.newLine();
			}
		}
		br.close();
		bw.close();
	}

	public static HashMap<String,String> normal_intomap(File normal) throws IOException{
		HashMap<String,String> map = new HashMap<String,String>();
		BufferedReader br = new BufferedReader(new FileReader(normal));
		String line = null;
		while((line=br.readLine())!=null){
			String[] tokens = line.split("\t");
			map.put(tokens[0], line);
		}
		br.close();
		return map;
	}
}
