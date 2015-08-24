
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

//import ClusterBasedRanker.Cluster;

//import cluster.TwitterParser.Cluster;
//import cluster.TwitterParser.DocumentVector;



public abstract class TwitterRanker {
	
	protected Map<String, Integer> keyWords;
	protected Map<String, Double> documentFrequency;
	protected List<Tweet> tweets;
	protected long documentSize;
	protected String output;
	protected NumberFormat nf = NumberFormat.getInstance();
	protected BufferedWriter pw, pw_tt, pw_kc, pw_uu, pw_ku, pw_kk, pw_kt, pw_tc;
	protected BufferedWriter pw_t = null;
	private int totalProcessed = 0;
	protected Boolean addTweetToList = true;
	protected BufferedWriter  pw_t_1, pw_t_2, pw_t_3, pw_t_4;
	

/*	class Cluster implements Comparable<Cluster> {
		private String id;
		private List<Tweet> featureVectors;
		private Date timeOfFirstInsertion;
		private Double clusterStrength = 0.0;
		
		@Override
		public int compareTo(Cluster o) {
			// return this.featureVectors.size() - o.featureVectors.size();
			return (int) (this.clusterStrength - o.clusterStrength);
		}
	}*/
	
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
	
	public TwitterRanker(){
		this.documentFrequency = new HashMap<String, Double>();
		this.documentSize = 0;
		this.keyWords = new HashMap<String, Integer>();
	
		this.tweets = new ArrayList<Tweet>();
		nf.setMaximumFractionDigits(3);
	}
	
	
	public TwitterRanker(String output, String keyWordFile) throws FileNotFoundException, IOException{
		
		this();
		this.output = output;
		System.out.println("I am here CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
		System.out.println(output);
		System.out.println(keyWordFile);
		readAndStore(keyWordFile, keyWords);
		System.out.println("I am here AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		try {
			// Construct the BufferedWriter object
			pw = new BufferedWriter(new PrintWriter(output));
		/*	pw_tt = new BufferedWriter(new PrintWriter(output + "_Total_Tweets"));
			pw_kc = new BufferedWriter(new PrintWriter(output + "_Key_Count"));
			pw_tc = new BufferedWriter(new PrintWriter(output + "_Term_Count"));
			pw_kk = new BufferedWriter(new PrintWriter(output + "_Key_Key"));
			pw_uu = new BufferedWriter(new PrintWriter(output + "_User_User"));
			pw_ku = new BufferedWriter(new PrintWriter(output + "_Key_User"));
			pw_kt = new BufferedWriter(new PrintWriter(output + "_Key_Terms"));*/
			pw_t = new BufferedWriter(new PrintWriter(output + "_tweets" ));
			pw_t_1 = new BufferedWriter(new PrintWriter(output + "_tweets_1" ));
			pw_t_2 = new BufferedWriter(new PrintWriter(output + "_tweets_2" ));
			pw_t_3 = new BufferedWriter(new PrintWriter(output + "_tweets_3" ));
			pw_t_4 = new BufferedWriter(new PrintWriter(output + "_tweets_4" ));
			System.out.println("I am here BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("DEBUG: Opening outputfile failed");
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("DEBUG: Opening outputfile failed");
			throw e;
		}

	}
	
	public void parse() {	
			
		// Load JSON File
		long startTime = System.currentTimeMillis();
		
		// File JSONFile = new File(input);
		String JSONstring;
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss ZZZZZ yyyy");
		TimeZone tz = TimeZone.getTimeZone("+0000");
		sdf.setTimeZone(tz);
		Date firstTweetTime = new Date();
		long i = 1;
		long ccc = 1;
	

		Scanner sc = new Scanner(System.in);
		
		// get first line
		boolean firstline = true;
		
		// long i = 1;
		System.out.println("DEBUG:  Scanning File");
		
		while (sc.hasNextLine()) {
			
			//System.out.println("DEBUG: Read the next line from stdin");
			ccc = ccc + 1;

			JSONstring = sc.nextLine();
			Object obj;
			try {
				
				
				obj = JSONValue.parse(JSONstring);
			
				//System.out.println("DEBUG: String New " + JSONstring);
				
			    JSONObject jobj = (JSONObject) obj;
		
				String tweet = (String) jobj.get("text");
				Date date = sdf.parse((String) jobj.get("created_at"));
				String tweetId = (String) jobj.get("id_str");
				JSONObject x = (JSONObject) jobj.get("user");

				// JSONObject x = (JSONObject)JSONValue.parse(juser);

				String user_id = null;
				String user_name = null;
				String screen_name = null;
				System.out.println("I am here 1");
				
				if (x != null) {
					user_id = (String) x.get("id_str");
					user_name = (String) x.get("name");
					screen_name = (String) x.get("screen_name");
					String g = "";
					try {
						System.out.println("Comparing ..... ");
						if( screen_name.compareTo(g) == 0){
							System.out.println("DEBUG: empty Replaced with DUMMY");
							screen_name = "DUMMY";
						}
						System.out.println("Compared  1..... ");
						/*g = null;
						if( screen_name.compareTo(g) == 0){
							System.out.println("DEBUG: null Replaced with DUMMY");
							screen_name = "DUMMY";
						}
						System.out.println("Compared 2 ..... ");*/
					} catch (Error e){
						System.out.println("Error comparing= " + e.getMessage());		
					}
				}
				
				screen_name = screen_name.replaceAll("\\s","");
				
				//screen_name.replace(" ", "_");

				System.out.println("I am here 2 " + screen_name);
				
				String country = null;

				JSONObject y = (JSONObject) jobj.get("place");

				if (y != null) {
					country = (String) y.get("country");
				}

				
				addNewTweet(tweet, date, user_id, user_name, screen_name,
						tweetId, country);

			

			} catch (Exception e) {
				System.out.println("Error = " + e.getMessage());
			}
			
			//System.out.println("DEBUG: Waiting to read from stdin");
		}

		sc.close();
		
		computeRanking();
		
		try{
			// Modified by Shanika
			//printRankingData();
			pw.close();
			pw_tt.close();
			pw_kc.close();
			pw_kk.close();
			pw_ku.close();
			pw_uu.close();
			pw_kt.close();
			pw_tc.close();
			
		}
		catch(Exception e){
			System.out.print("Print data failed " + e);
		}
		
	}
	

	private static String removeDiacriticalMarks(String string) {
		return Normalizer.normalize(string, Form.NFD).replaceAll(
				"\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	protected Tweet addNewTweet(String text, Date date, String user_id,
			String user_name, String screen_name, String tId, String country) {

		// System.out.println("PER_BENCHMARK: Adding Tweet: " +
		// documentFrequency.size() + " " + dateFormat.format(new Date()));
		
		Tweet tweet = new Tweet();
		
		String tweetText = new String(text);

		tweetText = tweetText.replaceAll("[^\\p{Print}]", "");
		tweetText = tweetText.replaceAll("\\p{Cntrl}", "");
		
		ArrayList<String> terms = tokenizeText(tweetText);
		
		if (terms.size() == 0){
			//System.out.println("Tweet dropped");
			return tweet;
		}
		else {
			totalProcessed++;
			//System.out.println(totalProcessed + " tweets added to the list");
		}
		
		
		
		tweet.text = new String(tweetText);
		tweet.date = date;
		tweet.user_id = user_id;
		tweet.user_name = user_name;
		tweet.screen_name = screen_name;
		tweet.tweetId = tId;
		tweet.country = country;
		tweet.numKeyWords = 0;

        System.out.println("ADDING TWEET TID = "  + tId);
		for (String term : terms) {
			System.out.println("TWEET TERM = " + term);
			if (tweet.termFrequency.containsKey(term)) {
				tweet.termFrequency.put(term, tweet.termFrequency.get(term) + 1);

			} else {
				
				tweet.termFrequency.put(term, 1);
				boolean isKeyword = updateLists(term);
				if (isKeyword)
					tweet.numKeyWords++;
			}
		}
		
		//System.out.println("DEBUG: NUM_TERMS = " + tweet.termFrequency.size());
		if (addTweetToList) {
			tweets.add(tweet);
		}
		//System.out.println("DEBUG: LENGTH = " + tweets.size());
		++documentSize;
		
		return tweet;
	}

	
	public boolean updateLists(String term){
		boolean keyword=false;
		// Increment the count of the documents containing the keyword
		if (keyWords.containsKey(term)) {
			keyWords.put(term, keyWords.get(term) + 1);
			keyword = true;
		}		
		
		/*if (keyWords.containsKey(term.substring(1, term.length() ))) {
			//keyWords.put(term, keyWords.get(term) + 1);
			keyword = true;
		}	
		*/
		
		// Increment the document frequency count
		if (documentFrequency.containsKey(term)) {
			documentFrequency.put(term,
					documentFrequency.get(term) + 1);
		} else {
			documentFrequency.put(term, (double) 1);
		}			
		
		return keyword;
	}
	
	public void readAndStore(String fileName, Map<String, Integer> dataStore) {
		
		//System.out.println("SHANIKA =" + keyWordFile);
		File aFile = new File(fileName);
		String[] s = null;
		try {
			BufferedReader input = new BufferedReader(new FileReader(aFile));
			String line = null; // not declared within while loop
			while ((line = input.readLine()) != null) {
				//System.out.println(line);
				dataStore.put(line.toLowerCase(), 0);
				
			}
			input.close();
		} catch (IOException ex) {
			System.out.println("File read failed");
			ex.printStackTrace();
		}
		

		List<Map.Entry<String, Integer>> keyWordEntries = new LinkedList<Map.Entry<String, Integer>>(
				dataStore.entrySet());
		for (Map.Entry<String, Integer> c : keyWordEntries) {
			//System.out.println("SHANIKA " + c);
		}

	}

	
	
	public void computeRanking(){	
		
		// Compute the strength and keyWordCount
		for (Tweet tweet: tweets) {
			double value = 0;
			int significantWordCount = 0;
			for (Map.Entry<String, Integer> entry : tweet.termFrequency.entrySet()) {
				//System.out.println("STRENGTH_ARG: " + entry.getKey() + " " + entry.getValue() + " " + Math.log(documentSize/ documentFrequency.get(entry.getKey())));
			    value = value + entry.getValue()
						* Math.log(documentSize
								/ documentFrequency.get(entry.getKey()));
			    //System.out.println("STRENGTH_VAL: " + value);
			    significantWordCount = significantWordCount + entry.getValue();
				//featureVectorA.put(entry.getKey(), value);
			}
			tweet.strength = value;
			tweet.significantWordCount = significantWordCount;
		}
		
		sortTweetsAscendingStrength();
		
		//printRankingData();
		
	}		
	
	protected void sortTweetsAscendingStrength(){
		// Sort the collection based on the strength
				Collections.sort(tweets, new Comparator<Tweet>() {
					@Override
					public int compare(Tweet p1, Tweet p2) {
		                return p1.strength.compareTo(p2.strength);
		            }
				});
				Collections.reverse(tweets);
	}
	
	private ArrayList<String> tokenizeText(String tweetText){
		
        ArrayList<String> terms = new ArrayList<String>();
        Boolean tweetContainsKeyword = false;
        
        String normalized = removeDiacriticalMarks(tweetText);
        
	
		if (!normalized.equals(tweetText)) {
			
			return terms;
		}



		tweetText = tweetText.toLowerCase();


		String[] terms1 = tweetText.split("\\s+");
		if (terms1.length <= 3) {
			return terms;
		}


		for (String term : terms1) {
			int l = term.length();
			if (!term.contains("http://")) {

				while (term.endsWith(",") || term.endsWith(".")
						|| term.endsWith("?") || term.endsWith("!")
						|| term.endsWith(":") || term.endsWith(";")) {

					term = term.substring(0, l - 1);
					l = l - 1;
				}
				while (term.startsWith("#")) {
					term = term.substring(1, l);
					l = l - 1;
				}
				
				if (term.length() >= 3){
					terms.add(term);
					if (keyWords.containsKey(term)) {
						tweetContainsKeyword = true;
					}
				}
			}

		}
		
		if (tweetContainsKeyword){
			return terms;
		} else {
			return new ArrayList<String>();
		}

	}
	
	protected void pruneTweet(Tweet tw){
		
		List<Map.Entry<String, Integer>> mapEntries = new LinkedList<Map.Entry<String, Integer>>(
				tw.termFrequency.entrySet());
		for (Map.Entry<String, Integer> m: mapEntries){
			if (!documentFrequency.containsKey(m.getKey())){
				//System.out.println("DEBUG: Removed from Tweet " + m.getKey());
				tw.termFrequency.remove(m.getKey());
			}
		}
				
	}
	
	protected void pruneDocumentFrequnecyVector(){
		
        System.out.println("DEBUG: Document Frequency Before: " + documentFrequency.size());
		List<Map.Entry<String, Double>> mapEntries = new LinkedList<Map.Entry<String, Double>>(
				documentFrequency.entrySet());
			
		Collections.sort(mapEntries, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Map.Entry<String, Double> p1, Map.Entry<String, Double> p2) {
	            return p1.getValue().compareTo(p2.getValue());
	        }
		});
		
		for (Map.Entry<String, Double> m: mapEntries){
			if (m.getValue() == 1.0){
				//System.out.println("DEBUG: Removing Key " + m.getKey() + " " + m.getValue());
				documentFrequency.remove(m.getKey());
			}
			else{
				//System.out.println("DEBUG: Stopped Removing Key " + m.getKey() + " " + m.getValue());
				break;
			}
		}
		System.out.println("DEBUG: Document Frequency After: " + documentFrequency.size());
	}
	
	protected void sortAndPrintIntegerMap(String tag, Map<String, Integer> mapName) throws Exception{
		
		List<Map.Entry<String, Integer>> mapEntries = new LinkedList<Map.Entry<String, Integer>>(
				mapName.entrySet());
			
		Collections.sort(mapEntries, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> p1, Map.Entry<String, Integer> p2) {
	            return p1.getValue().compareTo(p2.getValue());
	        }
		});
		
	
		for (Map.Entry<String, Integer> c : mapEntries) {
			if (documentFrequency.containsKey(c.getKey())){
				//System.out.println(c.getKey() + " " + c.getValue() + " " + Math.log(documentSize/documentFrequency.get(c.getKey())) );
				pw.write(tag + c.getKey() + " " + c.getValue() + " " +   documentFrequency.get(c.getKey()) +  " " + nf.format(Math.log(documentSize/documentFrequency.get(c.getKey()))) );
				pw.newLine();
			}
			else{
				//System.out.println("Tweets do not contain word: " + c.getKey());
				pw.write(tag  + "Tweets do not contain word: " + c.getKey());
				pw.newLine();
			}
		}
			
		
	}
	
	
	
	protected void printTweetList(String tag, List<Tweet> tweets) throws Exception {
		
		int count = 0;
		double max_strength=0;
		for (Tweet tweet: tweets) {
			// Write the twit ID, twit strength and userId to file
			//pw.write(tweet.tweetId + " " + tweet.strength + " " + tweet.user_id + " " + tweet.text);
			if (count == 0){
				max_strength  = tweet.strength;
			}	
			count = count  + 1;
			pw.write(tag + "Summary " + tweet.tweetId + " " + nf.format(tweet.strength/max_strength) + " " + tweet.user_id + " " + tweet.screen_name);
			pw.newLine();
			pw.write(tag + "Details " + tweet.tweetId + " " + tweet.text);
			pw.newLine();
		}
		
		
	}
	
	
	protected void printTweetList(String tag, List<Tweet> tweets, double max_strength) throws Exception {
		
		int count = 0;
		for (Tweet tweet: tweets) {
			// Write the twit ID, twit strength and userId to file
			//pw.write(tweet.tweetId + " " + tweet.strength + " " + tweet.user_id + " " + tweet.text);
					count = count  + 1;
			pw.write(tag + "Summary " + tweet.tweetId + " " + nf.format(tweet.strength/max_strength) + " " + tweet.user_id + " " + tweet.screen_name);
			pw.newLine();
			pw.write(tag + "Details " + tweet.tweetId + " " + tweet.text);
			pw.newLine();
		}
		
		
	}
	
	
	public abstract void printRankingData() throws Exception ;
	
	
	
}
