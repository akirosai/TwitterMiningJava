import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Tweet {

		String text;
		Map<String, Integer> termFrequency = new HashMap<String, Integer>();
		Date date;
		String user_id;
		String user_name;
		String screen_name;
		String latitude;
		String longitude;
		String tweetId;
		String country;
		Double strength = 1.0;
		Integer significantWordCount;
		public int numKeyWords;	

}
