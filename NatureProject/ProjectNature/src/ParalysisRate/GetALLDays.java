package ParalysisRate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.LonLat;


public class GetALLDays {
	
	// for calculating flow rate for all days. we use 2nd ID for all IDs. 

	public static Integer max_id_count = 200000; //TODO change numbers 
	public static Integer min = 5;

	public static Double  bin = 15d;
	public static String  homepath = "/home/t-tyabe/NatureExp/";
	public static String  respath  = "/home/t-tyabe/NatureExp/Kanazawa_results0415/";
	public static File    holidays = new File(homepath+"holidays.csv");

	public static String  start_date = "20141021";
	public static String  end_date   = "20160106";
	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyyMMdd");//change time format

	public static void main(String args[]) throws IOException, ParseException{
		File homepath_root = new File(homepath); homepath_root.mkdir();
		File respath_file  = new File(respath);  respath_file.mkdir();

		File results_day_ids_points = new File(respath+"day_id_points.csv");

		Date start_date_date = SDF_TS.parse(start_date);
		Date end_date_date   = SDF_TS.parse(end_date);

		Date date = start_date_date;

		while(date.before(end_date_date)){
			entireflow(SDF_TS.format(date),results_day_ids_points); // line = disaster_date = YYYYMMDD
			date = DateGetter.nextday_date(date);
		}

	}

	public static void entireflow(String target_date,File results_day_ids_points) throws IOException, ParseException{ // <-- for each disaster day

		HashMap<String, HashMap<Integer, Double>> result = new HashMap<String, HashMap<Integer, Double>>();
		runforday(result,target_date,results_day_ids_points);
		System.out.println("#done everything for: "+target_date);

	}

	public static void runforday(HashMap<String, HashMap<Integer, Double>> result, String day, File results_day_ids_points) throws IOException, ParseException{	

		BufferedWriter bw_idpoints = new BufferedWriter(new FileWriter(results_day_ids_points, true));

		if(!(new File(respath+day+".csv").exists())){

			File out_eachday = new File(respath+day+".csv");
			BufferedWriter bw_each = new BufferedWriter(new FileWriter(out_eachday));

			//disaster_date = YYYYMMDD
			SmallMethods.extractfromcommand(day); System.out.println("#done uncompressing ");

			HashMap<Integer, Double> resforday = new HashMap<Integer, Double>();
			File in = new File("/home/t-tyabe/Data/grid/0/tmp/ktsubouc/gps_"+day+".csv");

			HashMap<String, TreeMap<Integer,LonLat>> map = new HashMap<String, TreeMap<Integer,LonLat>>();

			Date d_date = SDF_TS.parse(day);
			if(d_date.before(SDF_TS.parse("20151101"))){
				map = GPSLogdataIntoMap.intomap7(in, max_id_count, bin, min, 1);
				if(map.keySet().size()==0){
					System.out.println("couldn't get 500000 ids so trying again...");
					map = GPSLogdataIntoMap.intomap7(in, max_id_count, bin, min, 1);
				}
			}
			else{
				map = GPSLogdataIntoMap.intomap6(in, max_id_count, bin, min);
			}

			Integer totalpoints = 0;
			for(String id : map.keySet()){
				totalpoints = totalpoints + map.get(id).size();
			}
			bw_idpoints.write(day+","+map.keySet().size()+","+String.valueOf(totalpoints));
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
			result.put(day, resforday);
			System.out.println("done "+day);

			for(int time = 0; time<1440/bin; time++){
				if(result.get(day).containsKey(time)){
					bw_each.write(day+","+String.valueOf(time)+","+String.valueOf(result.get(day).get(time)));
				}
				else{
					bw_each.write(day+","+String.valueOf(time)+",0");
				}
				bw_each.newLine();
			}
			bw_each.close();
			System.out.println("#calculated and written out "+day+" for first time.");
			in.delete();
		}
		bw_idpoints.close();
	}


}
