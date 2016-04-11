package ParalysisRate;

import java.text.ParseException;

public class testDateConvert {

	public static void main(String[] args) throws ParseException{
		
		String in = "20151231";
		String res = DateGetter.nextday(in);
		System.out.println(res);
		
	}
	
}
