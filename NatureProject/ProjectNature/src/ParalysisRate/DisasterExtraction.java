package ParalysisRate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class DisasterExtraction {

	public static String code_pop_file   = "";

	/*
	 * this program calculates the disaster level(= sum of level*population) for each disaster
	 * 
	 */

	public static void main(String args[]) throws NumberFormatException, IOException{
		
		String disastertype = "rain";
		
		File in = new File("c:/users/t-tyabe/desktop/DisasterDataLogs/DisasterAlertData.csv");
		File out1 = new File("c:/users/t-tyabe/desktop/DisasterDataLogs/raw_DisasterAlertData.csv");
		File out2 = new File("c:/users/t-tyabe/desktop/DisasterDataLogs/matome_DisasterAlertData.csv");
		
		createAllDisasterFile(in,out1);
		
		matomeDisasterFile(out1,out2, disastertype);
		
		
	}

	public static void createAllDisasterFile(File in, File out) throws IOException{ //
		BufferedReader br = new BufferedReader(new FileReader(in));
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line = null;
		while((line=br.readLine()) != null){
			String[] tokens = line.split(",");
			String[] ymd = tokens[0].split(" "); //tokens[0] = yyyy/mm/dd hh:mm:ss
			String date = ymd[0];
			String hour = ymd[1].split(":")[0];
			String type = tokens[1];
			Double level = Double.parseDouble(tokens[2]);
			String[] codes = tokens[3].split(" ");

			Double productsum = 0d;
			for(String code : codes){
				if(get_codelist(code_pop_file).contains(code)){
					// get pop of code area
					Double code_pop = Double.parseDouble(code);
					productsum = productsum + level*code_pop;
				}
			}
			bw.write(date+","+type+","+hour+","+String.valueOf(productsum)+","+String.valueOf(codes.length)+","+String.valueOf(level));
			bw.newLine();
		}
		br.close();
		bw.close();
	}

	public static void matomeDisasterFile(File in, File out, String target_type) throws NumberFormatException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(in));
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line = null;

		HashMap<String, HashMap<String, Double>> date_hour_productsum = new HashMap<String, HashMap<String, Double>>();

		while((line=br.readLine()) != null){
			String[] tokens = line.split(",");
			String date = tokens[0];
			String type = tokens[1];
			String hour = tokens[2];
			String product_sum = tokens[3];
			//			String numof_codes = tokens[4];
			//			String level = tokens[5];

			if(type.equals(target_type)){
				if(date_hour_productsum.containsKey(date)){
					if(date_hour_productsum.get(date).containsKey(hour)){
						Double newvalue = date_hour_productsum.get(date).get(hour)+Double.parseDouble(product_sum);
						date_hour_productsum.get(date).put(hour, newvalue);
					}
					else{
						date_hour_productsum.get(date).put(hour, Double.parseDouble(product_sum));
					}
				}
				else{
					HashMap<String, Double> temp = new HashMap<String, Double>();
					Double product_sum_d = Double.parseDouble(product_sum);
					temp.put(hour, product_sum_d);
					date_hour_productsum.put(date, temp);
				}
			}
		}

		for(String d : date_hour_productsum.keySet()){
			for(String h : date_hour_productsum.get(d).keySet()){
				bw.write(d+","+h+","+String.valueOf(date_hour_productsum.get(d).get(h)));
				bw.newLine();
			}
		}

		br.close();
		bw.close();
	}

	public static HashSet<String> get_codelist(String code_file) throws IOException{
		BufferedReader br2 = new BufferedReader(new FileReader(new File(code_file)));
		HashSet<String> JISset = new HashSet<String>();
		String line2 = null;
		while((line2=br2.readLine())!=null){
			String[] tokens = line2.split(",");
			String code = tokens[0];
			JISset.add(code);
		}
		br2.close();
		return JISset;
	}

}
