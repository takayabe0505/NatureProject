package ParalysisRate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class GPSLogdataIntoMap {

	public static int tokenscheck(File in) throws IOException{
		BufferedReader br1 = new BufferedReader(new FileReader(in));
		String line1 = null;
		int count = 0;
		int num = 0;
		while((line1=br1.readLine())!=null){
			String[] tokens = line1.split("\t"); 
			num = tokens.length;
			if(count==10000){
				break;
			}
		}
		br1.close();
		return num;
	}

	public static HashMap<String, TreeMap<Integer,LonLat>> intomap7(File in, File shapedir, Integer max_id_count, Double bin, int min, int id_token) throws IOException{ //until 2015-10-31 ... ID ID ... or null ID ... 7 tokens

		int count = 0;
		int countall = 0;
		// read GPS log file
		HashSet<String> IDs_insidearea = new HashSet<String>();
		HashMap<String,Integer> tempmap = new HashMap<String,Integer>();

		BufferedReader br1 = new BufferedReader(new FileReader(in));
		//		File out_points = new File(respath+"points_inside.csv");
		//		BufferedWriter bw  = new BufferedWriter(new FileWriter(out_points));
		String line1 = null;
		while((line1=br1.readLine())!=null){
			countall++;
			String[] tokens = line1.split("\t"); 
			if(tokens.length==7){
				String id_br1 = tokens[id_token];
				if(!id_br1.equals("null")){
					if(tokens[4].length()>=18){
						Double lon = Double.parseDouble(tokens[3]);
						Double lat = Double.parseDouble(tokens[2]);
						if(SmallMethods.AreaOverlap(new LonLat(lon,lat),shapedir).equals("yes")){
							count++;
							if(count%10000==0){
								System.out.println("#got: "+count+" points out of "+countall);
							}
							//							bw.write(id_br1+","+String.valueOf(lon)+","+String.valueOf(lat));
							//							bw.newLine();
							if(tempmap.containsKey(id_br1)){
								Integer newval = tempmap.get(id_br1)+1;
								tempmap.put(id_br1, newval);
								if(tempmap.get(id_br1)==min){ //TODO change minumum points 
									IDs_insidearea.add(id_br1);
									//									System.out.println(id_br1); //TODO delete this 
								}
							}
							else{
								tempmap.put(id_br1, 1);
							}
						}
						if(IDs_insidearea.size()==max_id_count){
							System.out.println("successfully got "+max_id_count+" ids.");
							break;
						}
					}
				}
			}
		}
		br1.close();
		//		bw.close();

		HashMap<String, TreeMap<Integer,LonLat>> map = new HashMap<String, TreeMap<Integer,LonLat>>(); //id, time, location
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=br.readLine()) != null){
			String[] tokens = line.split("\t");
			if(tokens.length==7){
				String id = tokens[id_token];
				//			String date = tokens[1].split(" ")[0];
				if(!id.equals("null")){
					if(tokens[4].length()>=18){
						String Y_time = SmallMethods.convertYtime(tokens[4]); //yyyymmdd hh:mm:ss
						Integer time = SmallMethods.convertintomins(Y_time.split(" ")[1], bin);
						Double lon = Double.parseDouble(tokens[3]);
						Double lat = Double.parseDouble(tokens[2]);
						LonLat p = new LonLat(lon,lat);
						if(IDs_insidearea.contains(id)){
							if(map.keySet().contains(id)){
								map.get(id).put(time, p);
							}
							else{
								TreeMap<Integer,LonLat> temp = new TreeMap<Integer,LonLat>();
								temp.put(time, p);
								map.put(id, temp);
							}
						}
					}
				}
			}
			//			else{
			//				System.out.println(line);
			//			}
		}
		br.close();
		return map;
	}

	public static HashMap<String, TreeMap<Integer,LonLat>> intomap6(File in, File shapedir, Integer max_id_count, Double bin, int min) throws IOException{ //after 11/1

		// read GPS log file
		HashSet<String> IDs_insidearea = new HashSet<String>();
		HashMap<String,Integer> tempmap = new HashMap<String,Integer>();

		BufferedReader br1 = new BufferedReader(new FileReader(in));
		//	File out_points = new File(respath+"points_inside.csv");
		//	BufferedWriter bw  = new BufferedWriter(new FileWriter(out_points));
		String line1 = null;
		while((line1=br1.readLine())!=null){
			String[] tokens = line1.split("\t"); 
			if(tokens.length==7){
				String id_br1 = tokens[0];
				if(!id_br1.equals("null")){
					if(tokens[3].length()>=18){
						Double lon = Double.parseDouble(tokens[2]);
						Double lat = Double.parseDouble(tokens[1]);
						if(SmallMethods.AreaOverlap(new LonLat(lon,lat), shapedir).equals("yes")){
							//						bw.write(id_br1+","+String.valueOf(lon)+","+String.valueOf(lat));
							//						bw.newLine();
							if(tempmap.containsKey(id_br1)){
								Integer newval = tempmap.get(id_br1)+1;
								tempmap.put(id_br1, newval);
								if(tempmap.get(id_br1)==min){ //TODO change minumum points 
									IDs_insidearea.add(id_br1);
									//								System.out.println(id_br1); //TODO delete this 
								}
							}
							else{
								tempmap.put(id_br1, 1);
							}
						}
						if(IDs_insidearea.size()==max_id_count){
							System.out.println("successfully got "+max_id_count+" ids.");
							break;
						}
					}
				}
			}
		}
		br1.close();
		//	bw.close();

		HashMap<String, TreeMap<Integer,LonLat>> map = new HashMap<String, TreeMap<Integer,LonLat>>(); //id, time, location
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=br.readLine()) != null){
			String[] tokens = line.split("\t");
			if(tokens.length==7){
				String id = tokens[0];
				//			String date = tokens[1].split(" ")[0];
				if(!id.equals("null")){
					if(tokens[3].length()>=18){
						String Y_time = SmallMethods.convertYtime(tokens[3]); //yyyymmdd hh:mm:ss
						Integer time = SmallMethods.convertintomins(Y_time.split(" ")[1], bin);
						Double lon = Double.parseDouble(tokens[2]);
						Double lat = Double.parseDouble(tokens[1]);
						LonLat p = new LonLat(lon,lat);
						if(IDs_insidearea.contains(id)){
							if(map.keySet().contains(id)){
								map.get(id).put(time, p);
							}
							else{
								TreeMap<Integer,LonLat> temp = new TreeMap<Integer,LonLat>();
								temp.put(time, p);
								map.put(id, temp);
							}
						}
					}
				}
			}
			//			else{
			//				System.out.println(line);
			//			}
		}
		br.close();
		return map;
	}

}
