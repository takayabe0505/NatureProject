package Kumamoto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;
import jp.ac.ut.csis.pflow.obs.aggre.MeshTrafficVolume;
import ParalysisRate.SmallMethods;

public class CheckIDs {

	static File shapedir = new File("/home/t-tyabe/KumamotoSHP");
	static GeometryChecker gchecker = new GeometryChecker(shapedir);

	public static String  homepath = "/home/t-tyabe/Kumamoto/";
	public static String  respath  = "/home/t-tyabe/Kumamoto/results0420_3/";

	public static void uncompress_run(String yyyymmdd) throws IOException{
		SmallMethods.extractfromcommand2(yyyymmdd); System.out.println("#done uncompressing ");

		File in = new File("/home/t-tyabe/Data/grid/0/tmp/hadoop-ktsubouc/data_"+yyyymmdd+".csv");

		//		for(int i=0; i<=23; i++){
		int i = 20;
		File out = new File(respath+"/kumamoto_"+yyyymmdd+"_"+String.format("%02d", i)+".csv");
		writeout_byhour(in, out, i);
		
		File out_mesh3 = new File(respath+"/kumamoto_"+yyyymmdd+"_"+String.format("%02d", i)+"_mesh3.csv");
		aggregate(out,out_mesh3,3);
		File out_mesh4 = new File(respath+"/kumamoto_"+yyyymmdd+"_"+String.format("%02d", i)+"_mesh4.csv");
		aggregate(out,out_mesh4,4);
		File out_mesh5 = new File(respath+"/kumamoto_"+yyyymmdd+"_"+String.format("%02d", i)+"_mesh5.csv");
		aggregate(out,out_mesh5,5);
		//		}

		in.delete();
	}

	public static void writeout_byhour(File in, File out, int hour) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(in));
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line = null;
		HashSet<String> id_already = new HashSet<String>();
		int count = 0;
		int count2 = 0;
//		int count1 = 0;
		while((line=br.readLine())!=null){
			String[] tokens = line.split("\t");
			String id = tokens[1];
			if(id.length()>0){
				if(tokens.length==7){
					Integer h = Integer.valueOf(tokens[4].split("T")[1].split(":")[0]);
					if(Math.abs(h-hour)<=2){
						if(!id_already.contains(id)){
							List<String> zonecodeList = gchecker.listOverlaps("JCODE",Double.parseDouble(tokens[3]),Double.parseDouble(tokens[2]));
							if(!zonecodeList.isEmpty()){
								bw.write(line);
								bw.newLine();
								count++;
							}
							id_already.add(id);
						}
					}
				}
			}
			count2++;
		}
		System.out.println(count+" out of "+count2+" lines have first column; ended "+in.toString());
		System.out.println("======");
		br.close();
		bw.close();
	}

	public static void aggregate(File in, File out, int mesh_level){
		// create instance ////////////////////////////////
		MeshTrafficVolume volume = new MeshTrafficVolume(mesh_level);	 // mesh level=5

		try{
			BufferedReader br = new BufferedReader(new FileReader(in));
			String line = null;
			int i = 1;
			while( (line = br.readLine()) != null ) {
				String[] tokens = line.split("\t");
				//				String pid = tokens[0];
				double lon = Double.parseDouble(tokens[3]);
				double lat = Double.parseDouble(tokens[2]);
				LonLat pos = new LonLat(lon, lat);

				volume.aggregate(String.valueOf(i),0,pos,1,1);
				i++;
				if (i % 10000 == 0){
					System.out.println(i);
				}
			}
			br.close();
		}

		catch(FileNotFoundException e) {
			System.out.println("File not found:");
		}
		catch(IOException e) {
			System.out.println(e);
		}

		// file export ////////////////////////////////////
		volume.export(out);
	}

	public static void main(String args[]) throws IOException{
		File home = new File(homepath); home.mkdir();
		File res = new File(respath); res.mkdir();

		ArrayList<String> list = new ArrayList<String>();
		list.add("20151215");
		list.add("20151216");
		list.add("20151217");
		list.add("20151218");
		list.add("20160127");
		list.add("20160128");
		list.add("20160130");
		list.add("20160129");
		list.add("20160414");
		list.add("20160415");
		list.add("20160416");
		list.add("20160417");
		list.add("20160418");

		for(String d : list){
			uncompress_run(d);
		}
	}

}
