package DisasterDateRelated;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class GetDisasterDates {

	protected static final SimpleDateFormat YMD = new SimpleDateFormat("yyyy-MM-dd");//change time format

	public static void main(String[] args) throws IOException, ParseException{
		File in = new File("c:/users/t-tyabe/Desktop/DisasterAlertData_20160224.csv");
		File out = new File("c:/users/t-tyabe/Desktop/DisasterAlertData_Kanazawa.csv");
		File jiscodes = new File("c:/users/t-tyabe/desktop/JIScodes_kanazawa.csv");

		choosebyAreaDateType(in,out,jiscodes,"2014-10-21","2016-01-06");
	}

	public static File choosebyAreaDateType(File in, File out, File codes, String date, String enddate) throws IOException, ParseException{
		BufferedReader br2 = new BufferedReader(new FileReader(codes));
		HashSet<String> JISset = new HashSet<String>();
		String line2 = null;
		while((line2=br2.readLine())!=null){
			String[] tokens = line2.split(",");
			String code = tokens[0];
			JISset.add(code);
		}
		br2.close();

		Date startdate = YMD.parse(date);
		Date enddat    = YMD.parse(enddate);

		//		int count = 0;
		BufferedReader br = new BufferedReader(new FileReader(in));
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line = br.readLine();
		while((line = br.readLine())!= null){
			String[] tokens = line.split("\t");
			String type = tokens[1];
			Integer level = Integer.valueOf(tokens[2]);
			if(level>=3){
				if((type.equals("rain"))||(type.equals("warn"))||(type.equals("emg1"))||(type.equals("dosha"))){
					String[] ymd = tokens[0].split("/");
					String daytime = ymd[2];
					String[] d_t = daytime.split(" ");
					String dt = ymd[0]+"-"+ymd[1]+"-"+d_t[0];
					Date disdate = YMD.parse(dt);
					if((disdate.after(startdate))&&(disdate.before(enddat))){
						String[] jiscodes = tokens[3].split(" ");
						if(jiscodes[0].equals("ALL")){
							bw.write(tokens[0] +","+ type +","+tokens[2] +","+"8 11 12 13 14");
							bw.newLine();
						}
						else{
							HashSet<String> temp = new HashSet<String>();
							for(String jiscode : jiscodes){
								if(JISset.contains(jiscode)){
									temp.add(jiscode);
								}
							}
							if(temp.size()>0){
								String c = arraytostr(temp);
								bw.write(tokens[0] +","+ type +","+tokens[2] +","+ c);
								bw.newLine();
							}
						}
					}
				}
			}
		}
		br.close();
		bw.close();
		//		System.out.println("number of disasters in shutoken : " + count);
		return out;
	}

	public static String arraytostr(HashSet<String> temp){
		String tmp = temp.toString();
		String tmp2 = tmp.replace("[", "");
		String tmp3 = tmp2.replace("]", "");
		String tmp4 = tmp3.replace(",", "");
		return tmp4;
	}

}
