package Kumamoto;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.ac.ut.csis.pflow.geom.Mesh;

public class NormalizePop {

	public static void normalize_getaverage(ArrayList<String> normal_days, File normal_avg, String exp_path,
			String hour, String meshsize) throws IOException{

		HashMap<String,ArrayList<Double>> resmap = new HashMap<String, ArrayList<Double>>();
		BufferedWriter bw = new BufferedWriter(new FileWriter(normal_avg));

		for(String d : normal_days){
			File in = new File(exp_path+"kumamoto_"+d+"_"+hour+"_mesh"+meshsize+".csv");
			double magfac = getMagFac(in);
			System.out.println(d+", magfac: "+String.valueOf(magfac));

			BufferedReader br = new BufferedReader(new FileReader(in));
			String line = br.readLine();
			while((line=br.readLine())!=null){
				String[] tokens = line.split("\t");
				String mc = tokens[0];
				Double p = Double.parseDouble(tokens[1])*magfac;
				if(resmap.containsKey(mc)){
					resmap.get(mc).add(p);
				}
				else{
					ArrayList<Double> temp = new ArrayList<Double>();
					temp.add(p);
					resmap.put(mc, temp);
				}
			}
			br.close();
		}

		for(String meshcode : resmap.keySet()){
			String avg      = String.valueOf(getavg(resmap.get(meshcode)));
			String std_dev  = String.valueOf(getstddev(resmap.get(meshcode)));
			Mesh   mesh     = new Mesh(meshcode);
			Rectangle2D.Double rect = mesh.getRect();
			String wkt      = String.format("POLYGON((%f %f,%f %f,%f %f,%f %f,%f %f))",	rect.getMinX(),rect.getMinY(),
					rect.getMinX(),rect.getMaxY(),
					rect.getMaxX(),rect.getMaxY(),
					rect.getMaxX(),rect.getMinY(),
					rect.getMinX(),rect.getMinY());		

			bw.write(meshcode+"\t"+avg+"\t"+std_dev+"\t"+wkt);
			bw.newLine();
		}
		bw.close();
	}

//	public static String  homepath = "C:/Users/yabetaka/Desktop/KumamotoProject/";
//	static File shapedir = new File(homepath+"Kumamoto_aso_SHP");
//	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static double getMagFac(File in) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = br.readLine();
		ArrayList<Double> list = new ArrayList<Double>();
		while((line=br.readLine())!=null){
			String[] tokens = line.split("\t");
//			String meshcode = tokens[0];
//			Mesh   mesh     = new Mesh(meshcode);
//			LonLat center = mesh.getCenter();
//			List<String> zonecodeList = gchecker.listOverlaps("JCODE",center.getLon(),center.getLat());
//			if(!zonecodeList.isEmpty()){
				Double pop = Double.parseDouble(tokens[1]);
				list.add(pop);
//			}
		}
		Double sum = 0d;
		for(Double p : list){
			sum = sum + p;
		}	
		Double magfac = 10000000d/sum; //Å@ëçåv1000ñúêlÇ…ìùàÍÇ∑ÇÈ
		br.close();
		return magfac;
	}

	public static double getavg(ArrayList<Double> temp){
		Double sum = 0d;
		for(Double d :temp){
			sum = sum + d;
		}
		Double avg = sum/(double)temp.size();
		return avg;
	}

	public static double getstddev(ArrayList<Double> temp){
		Double avg = getavg(temp);

		Double sum = 0d;
		for(Double d :temp){
			sum = sum + Math.pow(d-avg, 2);
		}
		Double bunsan = sum/(double)temp.size();
		Double std_dev = Math.pow(bunsan, 0.5d);
		return std_dev;
	}

}
