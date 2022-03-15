package pt.up.fc.dcc.mooshak.content.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;

/**
 * Static methods on dates and times.
 *   
 * @author José Paulo Leal zp@dcc.fc.up.pt
 *
 */
public class Dates {
	
	private static TimeZone NOTZ = new SimpleTimeZone(0,"NOTZ");

	enum MooshakDate { 
			DATE_TIME_FORMAT("yyyy-MM-dd HH:mm:ss"),
			DATE_FORMAT("yyyy-MM-dd HH:mm:ss"),
			MULTI_DAY_TIME_FORMAT("DDD HH:mm"),	// have to remove one day
			TIME_FORMAT("HH:mm:ss")	;
	
		DateFormat dateFormat;

		MooshakDate(String format) {
			dateFormat = new SimpleDateFormat(format);
			dateFormat.setTimeZone(NOTZ);
		}
	
		DateFormat getDateFormat() {
			return dateFormat;
		}
		
		String format(Date date) {
			return dateFormat.format(date);
		}
		
		Date parse(String date) throws MooshakContentException {
			try {
				return dateFormat.parse(date);
			} catch (ParseException cause) {
				throw new MooshakContentException("Invalid date format",cause);
			}
		}
	};
	
	
	public static String show(Date dateTime) {
	
		return MooshakDate.DATE_TIME_FORMAT.format(dateTime);
	}

	public static String showDate(Date date) {
		
		return MooshakDate.DATE_FORMAT.format(date);
	}
	
	private static final Date FULL_DAY =  new Date(24L*60L*60L*1000L);
	
	public static String showTime(Date time) {
		
		if (time.after(FULL_DAY)) {
			Date adjustedTime = new Date(time.getTime() - FULL_DAY.getTime());
			return MooshakDate.MULTI_DAY_TIME_FORMAT.format(adjustedTime);
		} else
			return MooshakDate.TIME_FORMAT.format(time);
	}
	
	
	
	public static Date parse(String date) throws MooshakContentException {
	
			return MooshakDate.DATE_FORMAT.parse(date);
	}

}
