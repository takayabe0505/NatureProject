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

	public static Integer max_id_count = 10000;

	public static Double  bin = 15d;
	public static String  homepath = "/home/t-tyabe/NatureExp/";
	public static String  dislog = "/home/t-tyabe/NatureExp/DisasterAlertData_shutoken.csv";

	public static void main(String args[]) throws IOException, ParseException{
		File homepath_root = new File(homepath); homepath_root.mkdir();

		File dates_of_disaster = new File(homepath+"dates_of_disaster.csv");
		BufferedReader br = new BufferedReader(new FileReader(dates_of_disaster));
		String line = null;

		HashSet<String> calculated_days = new HashSet<String>();

		while((line=br.readLine())!=null){
			entireflow(line, calculated_days); // line = disaster_date = YYYYMMDD
		}
		br.close();

	}

	public static void entireflow(String disaster_date, HashSet<String> calculated_days) throws IOException, ParseException{ // <-- for each disaster day

		//		File out = new File("c:/users/yabetaka/desktop/testresults_10mins_typhoon_km.csv"); //day, time, flowamount
		File out = new File(homepath+disaster_date+".csv"); //day, code, time, flow **code=DD,ND,OD

		HashMap<String, HashMap<Integer, Double>> result = new HashMap<String, HashMap<Integer, Double>>();

		HashSet<String> exp_dates = DateGetter.getTargetDates(disaster_date, dislog);
		exp_dates.add(disaster_date);
		exp_dates.add(DateGetter.nextday(disaster_date));

		runforday(out,result,disaster_date,exp_dates,calculated_days);
		System.out.println("done "+disaster_date);

		File out1 = new File(homepath+disaster_date+"_forplot1day.csv");
		CreateOutPutFile.modify_1day(out, out1);

		File out2 = new File(homepath+disaster_date+"_forplot2days.csv");
		CreateOutPutFile.modify_2days(out, out2);

	}

	public static void runforday(File out, HashMap<String, HashMap<Integer, Double>> result, String day, HashSet<String> datesforexp,
			HashSet<String> calculated_days) throws IOException, ParseException{	

		BufferedWriter bw = new BufferedWriter(new FileWriter(out, true));

		for(String d : datesforexp){

			if(!calculated_days.contains(d)){
				
				File out_eachday = new File(homepath+d+".csv");
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
						bw.write(d+","+SmallMethods.code_of_day(d, day)+","+String.valueOf(time)+","+String.valueOf(result.get(d).get(time)));
						bw_each.write(d+","+String.valueOf(time)+","+String.valueOf(result.get(d).get(time)));
					}
					else{
						bw.write(d+","+SmallMethods.code_of_day(d, day)+","+String.valueOf(time)+",");
						bw_each.write(d+","+String.valueOf(time)+",");
					}
					bw.newLine();				
					bw_each.newLine();
				}
				calculated_days.add(d);
				bw_each.close();
				System.out.println("#calculated and written out "+d+" for first time.");
			}
			else{
				BufferedReader br_already = new BufferedReader(new FileReader(new File(homepath+d+".csv")));
				String line_already = null;
				while((line_already=br_already.readLine())!=null){
					String[] tokens = line_already.split(",");
					String day_already = tokens[0];
					String t_already = tokens[1];
					String res_already = tokens[2];
					bw.write(day_already+","+SmallMethods.code_of_day(d,day)+","+t_already+","+res_already);
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
		BufferedReader br1 = new BufferedReader(new FileReader(in));
		String line1 = null;
		while((line1=br1.readLine())!=null){
			String id_br1 = line1.split("\t")[0];
			Double lon = Double.parseDouble(line1.split(",")[3]);
			Double lat = Double.parseDouble(line1.split(",")[2]);
			if(SmallMethods.AreaOverlap(new LonLat(lon,lat)).equals("yes")){
				IDs_insidearea.add(id_br1);
				//System.out.println("number of ids: "+IDs_insidearea.size());
			}
			if(IDs_insidearea.size()==max_id_count){
				System.out.println("successfully got "+max_id_count+" ids.");
				break;
			}
		}
		br1.close();

		HashMap<String, TreeMap<Integer,LonLat>> map = new HashMap<String, TreeMap<Integer,LonLat>>(); //id, time, location
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=br.readLine()) != null){
			String[] tokens = line.split("\t");
			String id = tokens[0];
			//			String date = tokens[1].split(" ")[0];
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
		br.close();
		return map;
	}

}