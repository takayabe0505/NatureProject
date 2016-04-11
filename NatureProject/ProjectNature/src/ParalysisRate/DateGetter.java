package ParalysisRate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class DateGetter {

	public static HashSet<String> getTargetDates(String disDate, String dislog, File holidays) throws ParseException, IOException{

		BufferedReader br_h = new BufferedReader(new FileReader(holidays));
		HashSet<String> holi_set = new HashSet<String>();
		String line_h = null;
		while((line_h=br_h.readLine())!=null){
			String[] hs = line_h.split("/");
			String yyyymmdd = hs[0]+String.format("%02d", hs[1])+String.format("%02d", hs[1]);
			holi_set.add(yyyymmdd);
		}
		br_h.close();

		File dislogs = new File(dislog);
		HashSet<String> res = new HashSet<String>();
		HashSet<Date> DisDays = getDisDays(dislogs);

		String year  = disDate.substring(0,4);
		String month = disDate.substring(4,6);
		String day   = disDate.substring(6,8);
		Date disaster_date = SDF_TS.parse(year+"-"+month+"-"+day);
		String disaster_youbi = (new SimpleDateFormat("u")).format(disaster_date);

		if(disaster_youbi.equals("6")||disaster_youbi.equals("7")){
			for(int i=1; i<=28; i++){
				String day_candidate = String.valueOf(i);
				Date d = SDF_TS.parse(year+"-"+month+"-"+day_candidate);
				String youbi = (new SimpleDateFormat("u")).format(d);
				if((youbi.equals("6"))||(youbi.equals("7"))){
					if(!(DisDays.contains(d))){
						String day_2dig   = String.format("%02d", i);
						String date = year+month+day_2dig;
						res.add(date);
						if(res.size()==5){
							break;
						}
					}
				}
			}
		}
		else{
			for(int i=1; i<=28; i++){
				String day_candidate = String.valueOf(i);
				Date d = SDF_TS.parse(year+"-"+month+"-"+day_candidate);
				String youbi = (new SimpleDateFormat("u")).format(d);
				if(!((youbi.equals("6"))||(youbi.equals("7")))){
					if(!(DisDays.contains(d))){
						String day_2dig   = String.format("%02d", i);
						String date = year+month+day_2dig;
						if(!holi_set.contains(disaster_youbi)){
							res.add(date);
							if(res.size()==5){
								break;
							}
						}
					}
				}
			}
		}
		return res;
	}

	protected static final SimpleDateFormat SDF_TS = new SimpleDateFormat("yyyy-MM-dd");//change time format

	public static HashSet<Date> getDisDays(File dislogs) throws IOException, ParseException{
		HashSet<Date> res = new HashSet<Date>();
		BufferedReader br = new BufferedReader(new FileReader(dislogs));
		String line = null;
		while((line = br.readLine())!= null){
			String[] tokens = line.split(",");
			String[] ymd = tokens[0].split("/");
			String year = ymd[0];
			String month = ymd[1];
			String daytime = ymd[2];
			String[] d_t = daytime.split(" ");
			String day = d_t[0];

			Date d = SDF_TS.parse("2014-10-20");
			Date d2 = SDF_TS.parse("2015-11-07");
			Date date = SDF_TS.parse(year+"-"+month+"-"+day);
			if((date.after(d))&&(date.before(d2))){
				res.add(date);
			}
		}
		br.close();
		return res;
	}

	public static String nextday(String day) throws ParseException{

		String d_str = day.substring(0,4)+"-"+day.substring(4,6)+"-"+day.substring(6,8);
		Date d = SDF_TS.parse(d_str);

		Calendar nextCal = Calendar.getInstance();
		nextCal.setTime(d);
		nextCal.add(Calendar.DAY_OF_MONTH, 1);
		Date nextDate = nextCal.getTime();

		String d_next = new SimpleDateFormat("yyyyMMdd").format(nextDate);
		return d_next;
	}


}
