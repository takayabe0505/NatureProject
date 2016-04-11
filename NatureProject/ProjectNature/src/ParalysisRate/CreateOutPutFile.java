package ParalysisRate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateOutPutFile {

	public static void modify_2days(File in, File out) throws IOException{
		
		HashMap<String, ArrayList<String>> OD_data = new HashMap<String, ArrayList<String>>();
		
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=br.readLine())!=null){
			String[] tokens = line.split(",");
//			String date = tokens[0];
			String day_code = tokens[1];
			String time = tokens[2];
			String activeness_val = tokens[3];
			if(day_code.equals("OD")){
				if(OD_data.containsKey(time)){
					OD_data.get(time).add(activeness_val);
				}
				else{
					ArrayList<String> list = new ArrayList<String>();
					list.add(activeness_val);
					OD_data.put(time, list);
				}
			}
		}
		br.close();
		
		BufferedReader br2 = new BufferedReader(new FileReader(in));
		BufferedWriter bw  = new BufferedWriter(new FileWriter(out));
		String line2 = null;
		while((line2=br.readLine())!=null){
			String[] tokens = line2.split(",");
//			String date = tokens[0];
			String day_code = tokens[1];
			String time = tokens[2];
			String activeness_val = tokens[3];
			if(day_code.equals("DD")){
				bw.write("1,"+time+","+activeness_val);
			}
			else if(day_code.equals("ND")){
				String next_time = String.valueOf(Integer.valueOf(time)+143);
				bw.write("1,"+next_time+","+activeness_val);
			}
			bw.newLine();
		}
		for(String time : OD_data.keySet()){
			String avg_activeness = String.valueOf(get_avg(OD_data.get(time)));
			bw.write("2,"+time+","+avg_activeness);
			bw.newLine();
			String next_time2 = String.valueOf(Integer.valueOf(time)+143);
			bw.write("2,"+next_time2+","+avg_activeness);
			bw.newLine();
		}
		br2.close();
		bw.close();
	}
	
	public static void modify_1day(File in, File out) throws IOException{
		
		HashMap<String, ArrayList<String>> OD_data = new HashMap<String, ArrayList<String>>();
		
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line = null;
		while((line=br.readLine())!=null){
			String[] tokens = line.split(",");
//			String date = tokens[0];
			String day_code = tokens[1];
			String time = tokens[2];
			String activeness_val = tokens[3];
			if(day_code.equals("OD")){
				if(OD_data.containsKey(time)){
					OD_data.get(time).add(activeness_val);
				}
				else{
					ArrayList<String> list = new ArrayList<String>();
					list.add(activeness_val);
					OD_data.put(time, list);
				}
			}
		}
		br.close();
		
		BufferedReader br2 = new BufferedReader(new FileReader(in));
		BufferedWriter bw  = new BufferedWriter(new FileWriter(out));
		String line2 = null;
		while((line2=br.readLine())!=null){
			String[] tokens = line2.split(",");
//			String date = tokens[0];
			String day_code = tokens[1];
			String time = tokens[2];
			String activeness_val = tokens[3];
			if(day_code.equals("DD")){
				bw.write("1,"+time+","+activeness_val);
			}
			else if(day_code.equals("ND")){
				bw.write("3,"+time+","+activeness_val);
			}
			bw.newLine();
		}
		for(String time : OD_data.keySet()){
			String avg_activeness = String.valueOf(get_avg(OD_data.get(time)));
			bw.write("2,"+time+","+avg_activeness);
			bw.newLine();
		}
		br2.close();
		bw.close();
	}
	
	public static double get_avg(ArrayList<String> list){
		Double sum = 0d;
		for(String e : list){
			Double e_d = Double.parseDouble(e);
			sum = sum + e_d;
		}
		Double avg = sum/(double)list.size();
		return avg;
	}
	
}
