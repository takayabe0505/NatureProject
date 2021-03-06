package ParalysisRate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.LonLat;


public class MainFlow {

	public static Integer max_id_count = 500000; //TODO change numbers 
	public static Integer min = 5;

	public static Double  bin = 15d;
	public static String  homepath = "/home/t-tyabe/NatureExp/";
	public static String  respath  = "/home/t-tyabe/NatureExp/results0418/";
	public static String  dislog = "/home/t-tyabe/NatureExp/DisasterAlertData_shutoken.csv";
	public static File    holidays = new File(homepath+"holidays.csv");
	public static File    shapedir = new File("");

	public static void main(String args[]) throws IOException, ParseException{
		File homepath_root = new File(homepath); homepath_root.mkdir();
		File respath_file  = new File(respath);  respath_file.mkdir();

		File results_day_ids_points = new File(respath+"day_id_points.csv");
		File dates_of_disaster = new File(homepath+"dates_of_disaster_snow.csv");
		BufferedReader br = new BufferedReader(new FileReader(dates_of_disaster));
		String line = null;

		while((line=br.readLine())!=null){
			String[] tokens = line.split(",");
			String disaster_date = tokens[0];
			String level = tokens[1];
			entireflow(disaster_date,level,results_day_ids_points); // line = disaster_date = YYYYMMDD
		}
		br.close();

		//		entireflow("20150417","4"); //FOR test only. 


	}

	protected static final SimpleDateFormat YMD = new SimpleDateFormat("yyyyMMdd");//change time format

	public static void entireflow(String disaster_date,String level,File results_day_ids_points) throws IOException, ParseException{ // <-- for each disaster day

		//		File out = new File("c:/users/yabetaka/desktop/testresults_10mins_typhoon_km.csv"); //day, time, flowamount
		File out = new File(respath+disaster_date+"_"+level+"_results.csv"); //day, code, time, flow **code=DD,ND,OD

		HashMap<String, HashMap<Integer, Double>> result = new HashMap<String, HashMap<Integer, Double>>();

		HashSet<String> exp_dates = DateGetter.getTargetDates(disaster_date, dislog, holidays);
		exp_dates.add(disaster_date);
		exp_dates.add(DateGetter.nextday_str(disaster_date));
		System.out.println("days for exp are; "+exp_dates);

		runforday(out,result,disaster_date,exp_dates,results_day_ids_points);
		System.out.println("done "+disaster_date);

		File out1 = new File(respath+disaster_date+"_forplot1day.csv");
		CreateOutPutFile.modify_1day(out, out1);

		File out2 = new File(respath+disaster_date+"_forplot2days.csv");
		CreateOutPutFile.modify_2days(out, out2);

	}

	public static void runforday(File out, HashMap<String, HashMap<Integer, Double>> result, String day, 
			HashSet<String> datesforexp, File results_day_ids_points) throws IOException, ParseException{	

		BufferedWriter bw = new BufferedWriter(new FileWriter(out, true));
		BufferedWriter bw_idpoints = new BufferedWriter(new FileWriter(results_day_ids_points, true));

		int count_normaldays = 1;

		for(String d : datesforexp){
			String code = SmallMethods.code_of_day(d, day);
			String code_2 = SmallMethods.getCode2(code, count_normaldays);

			if(code.equals("OD")){
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

				HashMap<String, TreeMap<Integer,LonLat>> map = new HashMap<String, TreeMap<Integer,LonLat>>();

				//				Date d_date = YMD.parse(d);
				//				if(d_date.before(YMD.parse("20151101"))){
				map = GPSLogdataIntoMap.intomap7(in,shapedir, max_id_count, bin, min, 1);
				//					if(map.keySet().size()<=15000){
				//						System.out.println("couldn't get 500000 ids so trying again...");
				//						map = GPSLogdataIntoMap.intomap7(in, max_id_count, bin, min, 1);
				//					}
				//				}
				//				else{
				//					map = GPSLogdataIntoMap.intomap6(in, max_id_count, bin, min);
				//				}

				Integer totalpoints = 0;
				for(String id : map.keySet()){
					totalpoints = totalpoints + map.get(id).size();
				}
				bw_idpoints.write(d+","+map.keySet().size()+","+String.valueOf(totalpoints));
				bw_idpoints.newLine();

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
						bw_each.write(d+","+String.valueOf(time)+",0");
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
		bw_idpoints.close();
	}


}
