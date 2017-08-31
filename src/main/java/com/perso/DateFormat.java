package com.perso;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class DateFormat {

	public static void main(String[] args) {

		
		Date date = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("CET"));
			
			String result = simpleDateFormat.format(new Date());
			System.out.println(result);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		
		
	}

}
