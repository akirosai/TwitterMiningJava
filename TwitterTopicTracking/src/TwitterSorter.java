import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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



public class TwitterSorter {
	
	
	protected List<Tweet> tweets;
	protected String output;
	protected BufferedWriter pw = null;
	
	public class Tweet {
		String text;
		String user_id;
	}
	
	
	public TwitterSorter(String output) throws FileNotFoundException, IOException{
		this.tweets = new ArrayList<Tweet>();
		this.output = output;
		System.out.println(output);
	}
	
	public void parse() throws FileNotFoundException, IOException {	
		
		
		
		// File JSONFile = new File(input);
		String JSONstring;
		long ccc = 0;

		Scanner sc = new Scanner(System.in);
		
		// get first line
		boolean firstline = true;
		
		// long i = 1;
		System.out.println("DEBUG:  Scanning File");
		
		while (sc.hasNextLine()) {
			
			System.out.println("DEBUG: Read the next line from stdin");
			ccc = ccc + 1;

			JSONstring = sc.nextLine();
			Object obj;
			try {
				obj = JSONValue.parse(JSONstring);
			
			    JSONObject jobj = (JSONObject) obj;
		
				
				JSONObject x = (JSONObject) jobj.get("user");

				// JSONObject x = (JSONObject)JSONValue.parse(juser);

				String user_id = null;
				
				if (x != null) {
					user_id = (String) x.get("id_str");	
				}

				Tweet t = new Tweet();
				t.user_id = user_id;
				t.text = JSONstring;
				tweets.add(t);

			} catch (Exception e) {
				System.out.println("Error = " + e.getMessage());
			}
			
			//System.out.println("DEBUG: Waiting to read from stdin");
		}

		sc.close();
		
		System.out.println("Total number processed  " + ccc);
		
		System.out.println("Sorting Tweets");
		sortTweetsAscendingUserId();
		
		printTweets();
		
		
	}
	

	
	
	protected void sortTweetsAscendingUserId(){
		// Sort the collection based on the strength
				Collections.sort(tweets, new Comparator<Tweet>() {
					@Override
					public int compare(Tweet p1, Tweet p2) {
		                return p1.user_id.compareTo(p2.user_id);
		            }
				});
				
	}
	
	private void printTweets() throws FileNotFoundException, IOException{
		String previous_user_id = "";
		String filename = "";
		System.out.println("Tweet List Length = " + tweets.size());
		
		for (Tweet tweet: tweets) {
			System.out.println("User Information " + tweet.user_id + " " + previous_user_id);
			if ( (previous_user_id.compareTo(tweet.user_id) != 0) || previous_user_id == "" ) {
				System.out.println("Closing existing file and opening new");
				try {
					pw.close();
				}
				catch (Exception e){
					System.out.println("DEBUG: Closing outputfile failed");
				}
				
				try {
					// Construct the BufferedWriter object
					filename = output + "_" + tweet.user_id;
					System.out.println("DEBUG: filename = " + filename);
					pw = new BufferedWriter(new PrintWriter(filename));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					System.out.println("DEBUG: Opening outputfile failed");
					throw e;
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("DEBUG: Opening outputfile failed");
					throw e;
				}
				
				previous_user_id = tweet.user_id;
			}
			else {
			   System.out.println("Writing to already opened file");
			}
			
			System.out.println("Writing to file " + filename);
			pw.write(tweet.text);
			pw.newLine();
			//pw.newLine();

		}	
		
		pw.close();
	}
	
	
}
