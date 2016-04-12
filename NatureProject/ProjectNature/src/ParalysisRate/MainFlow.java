package ParalysisRate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.LonLat;


public class MainFlow {

	public static Integer max_id_count = 10; //TODO change numbers 

	public static Double  bin = 15d;
	public static String  homepath = "/home/t-tyabe/NatureExp/";
	public static String  respath  = "/home/t-tyabe/NatureExp/results/";
	public static String  dislog = "/home/t-tyabe/NatureExp/DisasterAlertData_shutoken.csv";
	public static File    holidays = new File(homepath+"holidays.csv");

	public static void main(String args[]) throws IOException, ParseException{
		File homepath_root = new File(homepath); homepath_root.mkdir();

		File dates_of_disaster = new File(homepath+"dates_of_disaster.csv");
		BufferedReader br = new BufferedReader(new FileReader(dates_of_disaster));
		String line = null;

		while((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			String disaster_date = tokens[0];
			String level = tokens[1];
			entireflow(disaster_date,level); // line = disaster_date = YYYYMMDD
		}
		br.close();

	}

	public static void entireflow(String disaster_date,String level) throws IOException, ParseException{ // <-- for each disaster day

		//		File out = new File("c:/users/yabetaka/desktop/testresults_10mins_typhoon_km.csv"); //day, time, flowamount
		File out = new File(respath+disaster_date+"_"+level+"_results.csv"); //day, code, time, flow **code=DD,ND,OD

		HashMap<String, HashMap<Integer, Double>> result = new HashMap<String, HashMap<Integer, Double>>();

		HashSet<String> exp_dates = DateGetter.getTargetDates(disaster_date, dislog, holidays);
		exp_dates.add(disaster_date);
		exp_dates.add(DateGetter.nextday(disaster_date));
		System.out.println("days for exp are; "+exp_dates);

		runforday(out,result,disaster_date,exp_dates);
		System.out.println("done "+disaster_date);

		File out1 = new File(respath+disaster_date+"_forplot1day.csv");
		CreateOutPutFile.modify_1day(out, out1);

		File out2 = new File(respath+disaster_date+"_forplot2days.csv");
		CreateOutPutFile.modify_2days(out, out2);

	}

	public static void runforday(File out, HashMap<String, HashMap<Integer, Double>> result, String day, HashSet<String> datesforexp) throws IOException, ParseException{	

		BufferedWriter bw = new BufferedWriter(new FileWriter(out, true));

		int count_normaldays = 1;
		for(String d : datesforexp){
			String code = SmallMethods.code_of_day(d, day);
			String code_2 = code;
			if(code.equals("OD")){
				code_2="OD"+String.valueOf(count_normaldays);
				count_normaldays++;
			}

			if(!(new File(respath+d+".csv").exists())){

				File out_eachday = new File(respath+d+".csv");
				BufferedWriter bw_each = new BufferedWriter(new FileWriter(out_eachday));

				//disaster_date = YYYYMMDD
				SmallMethods.extractfromcommand(d); System.out.println("#done uncompressing ");

				HashMap<Integer, Double> resforday = new HashMap<Integer, Double>();
				File in = new File("/home/t-tyabe/Data/grid/0/tmp/ktsubouc/gps_"+d+".csv");
				//File in = new File("c:/users/yabetaka/desktop/data/snowGPS/Data/Tokyo-Snow_13/1421134801_13/13_2013"+month+String.valueOf(i)+".csv");

				HashMap<String, TreeMap<Integer,LonLat>> map = intomap(in);
				System.out.println("done putting id and logs into map "+map.size());

				for(String id : map.keySet()){
					TreeMap<Integer, Double> id_velocity = CalculatePRate.calculate(map.get(id));

					for(Integer time : id_velocity.keySet()){
						if(resforday.containsKey(time)){
							Double newvalue = resforday.get(time) + id_velocity.get(time); //convertkm 
							resforday.put(time, newvalue);
						}				
						else{
							resforday.put(time, id_velocity.get(time));
						}
					}	
				}
				result.put(d, resforday);
				System.out.println("done "+d);

				for(int time = 0; time<1440/bin; time++){
					if(result.get(d).containsKey(time)){
						bw.write(d+","+code+","+String.valueOf(time)+","+String.valueOf(result.get(d).get(time))+","+code_2);
						bw_each.write(d+","+String.valueOf(time)+","+String.valueOf(result.get(d).get(time)));
					}
					else{
						bw.write(d+","+code+","+String.valueOf(time)+","+","+code_2);
						bw_each.write(d+","+String.valueOf(time)+",");
					}
					bw.newLine();				
					bw_each.newLine();
				}
				bw_each.close();
				System.out.println("#calculated and written out "+d+" for first time.");
				in.delete();
			}
			else{
				BufferedReader br_already = new BufferedReader(new FileReader(new File(respath+d+".csv")));
				String line_already = null;
				while((line_already=br_already.readLine())!=null){
					String[] tokens = line_already.split(",");
					String day_already = tokens[0];
					String t_already = tokens[1];
					String res_already = tokens[2];
					bw.write(day_already+",OD,"+t_already+","+res_already+","+code_2);
					bw.newLine();
				}
				br_already.close();
				System.out.println("#read and written out "+d+" from past results.");
			}
		}
		bw.close();
	}

	public static HashMap<String, TreeMap<Integer,LonLat>> intomap(File in) throws IOException{
		
		// read GPS log file
		HashSet<String> IDs_insidearea = new HashSet<String>();
		HashMap<String,Integer> tempmap = new HashMap<String,Integer>();
		
		BufferedReader br1 = new BufferedReader(new FileReader(in));
		File out_points = new File(respath+"points_inside.csv");
		BufferedWriter bw  = new BufferedWriter(new FileWriter(out_points));
		String line1 = null;
		while((line1=br1.readLine())!=null){
			String[] tokens = line1.split("\t"); 
			if(tokens.length==7){
				String id_br1 = tokens[0];
				if(!id_br1.equals("null")){
					if(tokens[4].length()>=18){
						Double lon = Double.parseDouble(tokens[3]);
						Double lat = Double.parseDouble(tokens[2]);
						if(SmallMethods.AreaOverlap(new LonLat(lon,lat)).equals("yes")){
							bw.write(String.valueOf(lon)+","+String.valueOf(lat));
							bw.newLine();
							if(tempmap.containsKey(id_br1)){
								Integer newval = tempmap.get(id_br1)+1;
								tempmap.put(id_br1, newval);
								if(tempmap.get(id_br1)==3){ //TODO change minumum points 
									IDs_insidearea.add(id_br1);
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
		bw.close();

		HashMap<String, TreeMap<Integer,LonLat>> map = new HashMap<String, TreeMap<Integer,LonLat>>(); //id, time, location
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=br.readLine()) != null){
			String[] tokens = line.split("\t");
			if(tokens.length==7){
				String id = tokens[0];
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

}
