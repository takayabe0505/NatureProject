package ParalysisRate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class CalculatePRate {

	public static TreeMap<Integer, Double> calculate(TreeMap<Integer,LonLat> inmap){

		TreeMap<Integer, Double> res = new TreeMap<Integer, Double>(); 

		Integer starttime = 0;
		LonLat firstpoint = new LonLat(0,0);
		List<Entry<Integer, LonLat>> entries = new ArrayList<Entry<Integer, LonLat>>(inmap.entrySet());

		//Comparator Ç≈ Map.Entry ÇÃílÇî‰är
		Collections.sort(entries, new Comparator<Entry<Integer, LonLat>>() {
			//î‰ärä÷êî
			@Override
			public int compare(Entry<Integer, LonLat> o1, Entry<Integer, LonLat> o2) {
				return o1.getKey().compareTo(o2.getKey());    //è∏èá
			}
		});

		for(Entry<Integer, LonLat> e : entries){
			Integer t = e.getKey();
			Integer timelength = t - starttime;
			if(!(firstpoint.getLon()==0)){
				if((timelength!=0)||(starttime>0)){
					Double distance = e.getValue().distance(firstpoint);
					Double velocity = distance/(double)timelength;
					Double velocity_km = velocity/1000d;
					BigDecimal bi = new BigDecimal(String.valueOf(velocity_km));
					Double vel_digit = bi.setScale(2,BigDecimal.ROUND_DOWN).doubleValue();
//					System.out.println(e.getKey()+" "+e.getValue()+", "+starttime+" "+firstpoint+", "+velocity);
					for(int i = starttime; i<t; i++){
						res.put(i, vel_digit);
					}
				}
			}
			starttime = t;
			firstpoint = e.getValue();
		}
		return res;
	}
}
