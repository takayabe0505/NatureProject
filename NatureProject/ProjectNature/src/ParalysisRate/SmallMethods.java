package ParalysisRate;

import java.io.File;
import java.text.ParseException;
import java.util.List;

import jp.ac.ut.csis.pflow.geom.GeometryChecker;
import jp.ac.ut.csis.pflow.geom.LonLat;


public class SmallMethods {

//	static File shapedir = new File("/home/t-tyabe/Data/Shutokenshp");
//	static File shapedir = new File("/home/t-tyabe/NatureExp/small_Tokyo");
	static File shapedir = new File("/home/t-tyabe/NatureExp/KanazawaSHP");
	
	static GeometryChecker gchecker = new GeometryChecker(shapedir);
	
	public static Integer convertintomins(String time, Double bin){
		Integer hour = Integer.valueOf(time.split(":")[0]);
		Integer mins = Integer.valueOf(time.split(":")[1]);
		//		Integer secs = Integer.valueOf(time.split(":")[2]);
		Integer total_mins = (int) Math.round((hour*60+mins)/bin);
		return total_mins;
	}
	
	public static String getCode2(String code, Integer count){
		if(code.equals("OD")){
			return String.valueOf(count);
		}
		else if(code.equals("ND")){
			return "98";
		}
		else if(code.equals("DD")){
			return "99";
		}
		else{
			return "shit";
		}
	}
	
	public static String convertYtime(String t){
		String[] x = t.split("T");
		String time = x[1].substring(0,8);
		String res = x[0]+ " " + time;
		return res;
	}

	public static String AreaOverlap(LonLat point){
		List<String> zonecodeList = gchecker.listOverlaps("JCODE",point.getLon(),point.getLat());
		if(zonecodeList == null || zonecodeList.isEmpty()) {
			return "no";
		}
		else{
			return "yes";
		}
	}
	
	public static String code_of_day(String day, String disaster_day) throws ParseException{
		if(day.equals(disaster_day)){
			return "DD"; //day of disaster
		}
		else if(day.equals(DateGetter.nextday_str(disaster_day))){
			return "ND"; //next day from disaster
		}
		else{
			return "OD"; //other day
		}		
	}
	
	public static void extractfromcommand(String date){
		ProcessBuilder pb = new ProcessBuilder("tar", "zxvf",
				"/tmp/bousai_data/gps_"+date+".tar.gz", 
				"-C","/home/t-tyabe/Data/");
		pb.inheritIO();
		try {
			Process process = pb.start();
			process.waitFor();
			//			System.out.println(pb.redirectInput());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		//		System.out.println("=======done=======");
		File out = new File("/home/t-tyabe/Data/grid/0/tmp/ktsubouc/gps_"+date);
		out.renameTo(new File("/home/t-tyabe/Data/grid/0/tmp/ktsubouc/gps_"+date+".csv"));
	}
	
	public static void extractfromcommand2(String date){
		ProcessBuilder pb = new ProcessBuilder("tar", "zxvf",
				"/tmp/bousai_data/gps_"+date+".tar.gz", 
				"-C","/home/t-tyabe/Data/");
		pb.inheritIO();
		try {
			Process process = pb.start();
			process.waitFor();
			//			System.out.println(pb.redirectInput());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		//		System.out.println("=======done=======");
		File out =   new File("/home/t-tyabe/Data/grid/0/tmp/hadoop-ktsubouc/data_"+date);
		out.renameTo(new File("/home/t-tyabe/Data/grid/0/tmp/hadoop-ktsubouc/data_"+date+".csv"));
	}
	
}
