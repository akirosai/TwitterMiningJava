import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class RankClusRanker extends TwitterRanker {
	
	private TwoDHashMap<String, String, Integer> keyToUser;
	private TwoDHashMap<String, String, Integer> keyToKey;
	private TwoDHashMap<String, String, Integer> userToUser;
	private TwoDHashMap<String, String, Integer> keyToTerms;
	
	class Triple<K1, K2, V> {
		K1 k1;
		K2 k2;
		V v;
		
		Triple(K1 k1, K2 k2, V v){
			this.k1 = k1;
			this.k2 = k2;
			this.v = v;
		}
	}
	
	class TwoDHashMap<K1, K2, V> {
		HashMap<K1, HashMap<K2, V>> m;
		
		TwoDHashMap(){
		  m =  new HashMap<K1, HashMap<K2, V>>();
		}	
		
		public Boolean containsKey(K1 k1, K2 k2){
			Boolean result = false;
			
			if (m.containsKey(k1)){
				HashMap<K2, V> value = m.get(k1);
				if( value.containsKey(k2)){
					result = true;
				}
			}
			return result;
		}
		
		
		public V get(K1 k1, K2 k2){
			V v = null;
			if (m.containsKey(k1)){
				HashMap<K2, V> value = m.get(k1);
				if( value.containsKey(k2)){
					return (V)value.get(k2);
				}
			}
			return v;
		}
		
		
		public ArrayList<Triple> getAll(){
			
			ArrayList<Triple> a = new ArrayList<Triple>();
			
			for (Map.Entry<K1, HashMap <K2, V>> entry: m.entrySet()){
				K1 k1 = entry.getKey();
				K2 k2 = null;
				V v = null;		
				HashMap <K2, V> value = entry.getValue();
				for (Map.Entry <K2, V> inner_value: value.entrySet()){
					k2 = inner_value.getKey();
					v = inner_value.getValue();
					Triple t = new Triple(k1, k2, v);
					a.add(t);
				}
				
			}
			return a;
			
		}
		
		
		public void put(K1 k1, K2 k2, V v){
			
			if (m.containsKey(k1)){
				HashMap<K2, V> value = m.get(k1);
				if(value.containsKey(k2)){
					value.remove(k2);
					value.put(k2, v);
				}
				else{
					value.put(k2, v);
				}
			}
			else{
			   HashMap<K2, V> value = new HashMap<K2, V>();
			   value.put(k2, v);
			   m.put(k1, value);
			}
			
		}
		
		
		
		
	}
	
	
	
	
	public RankClusRanker(String output, String keyWordFile) throws FileNotFoundException, IOException {
		
		super(output, keyWordFile);
		System.out.println("Constructer of RankClusRanker");
		keyToUser = new TwoDHashMap<String, String, Integer>();	
		userToUser = new TwoDHashMap<String, String, Integer>();	
		keyToKey = new TwoDHashMap<String, String, Integer>();	
		keyToTerms = new TwoDHashMap<String, String, Integer>();	
		addTweetToList = false;
	}
	
	
	
    private void addTweetToMap(Tweet tweet){	
    	    ArrayList<String> keyWordsInTweet = new  ArrayList<String>();
    	    ArrayList<String> mentionsInTweet = new  ArrayList<String>();
    	    
    	    String k1 = null;
    	    String k2 = tweet.screen_name;
    	    
    	    System.out.println("ADDING tweet to map =" + tweet.tweetId);
			
    	    for (Map.Entry<String, Integer> entry : tweet.termFrequency.entrySet()) {
				// If the term is a key word or a hash tag add the to weight with users
				
				//System.out.println("DEBUG: " + entry.getKey() + " " + tweet.screen_name);
				k1 = entry.getKey();
		    	
		    	
			    //if((keyWords.containsKey(k1)) || (k1.charAt(0) == '#')){	
			    if((keyWords.containsKey(k1))){	
			    	if (!keyWordsInTweet.contains(k1)) {
				    	keyWordsInTweet.add(k1);
				    	
				    	if(keyToUser.containsKey(k1, k2)){	
				    		//System.out.println("DEBUG: Map contains key "); 
				    		Integer x = (Integer)keyToUser.get(k1, k2);
				    		keyToUser.put(k1, k2, x + 1);
				    		System.out.println("DEBUG: Incrementing Key " + k1 + " " + k2 + " " + keyToUser.get(k1, k2) );
				    	} else{
				    		//System.out.println("DEBUG: Adding Key " + k1 + " " + tweet.screen_name);
				    		keyToUser.put(k1, k2, 1);
				    		System.out.println("DEBUG: Added Key"); 
				    	}
			    	}
				
			    }	    
			    
			    if (k1.charAt(0) == '@'){
			    	String k3 = k1.substring(1);
			    	System.out.println("DEBUG:SUBSTRING" + k1 + " " + k3);
			    	if (k3.length() > 0) {
			    		mentionsInTweet.add(k3);
			    	}
			    }
			}
			
			// If the tweet has mentions create a relationship between the user and the mentioned user
			if (mentionsInTweet.size() > 0){
				System.out.println("DEBUG: Adding mention relationship");
				for (String s: mentionsInTweet) {
					if(userToUser.containsKey(k2, s)){		
			    		Integer x = (Integer)userToUser.get(k2, s);
			    		//userToUser.put(s, k2, x + 1);
			    		userToUser.put(k2, s, x + 1);
			    		
			    	} else{
			    		//userToUser.put(s, k2, 1);
			    		userToUser.put(k2, s, 1);
			    	}
				}
				
			}
			
			String t1, t2;
			if (keyWordsInTweet.size() > 1){
				System.out.println("DEBUG: Adding key-to-key relationship");
				for (int i = 0; i < keyWordsInTweet.size(); i++) {
					t1 = keyWordsInTweet.get(i);
					for (int j = i+1; j < keyWordsInTweet.size(); j++ ){
						t2 = keyWordsInTweet.get(j);
						if(keyToKey.containsKey(t1, t2)){		
				    		Integer x = (Integer)keyToKey.get(t1, t2);
				    		keyToKey.put(t1, t2, x + 1);
				    		keyToKey.put(t2, t1, x + 1);
				    		
				    	} else{
				    		keyToKey.put(t1, t2, 1);
				    		keyToKey.put(t2, t1, 1);
				    	}
					}		
				}
			}
			
			
			for (Map.Entry<String, Integer> entry : tweet.termFrequency.entrySet()) {
				
				// If the term is a key word or a hash tag add the to weight with users	
				//System.out.println("DEBUG: " + entry.getKey() + " " + tweet.screen_name);
				t1 = entry.getKey();
		    	System.out.println("KEY_TO_TERM: Adding term = " + t1);
		    	if((!keyWords.containsKey(t1))) {
		    		if ( keyWordsInTweet.size() >= 2) {
					    for (int i = 0; i < keyWordsInTweet.size(); i++) {
					    	    k1 = keyWordsInTweet.get(i);
					    	    System.out.println("KEY_TO_TERM: Adding term = " + t1 + " key " + k1);
								if(keyToTerms.containsKey(k1, t1)){		
						    		Integer x = (Integer)keyToTerms.get(k1, t1);
						    		keyToTerms.put(k1, t1, x + 1);
						    		//keyToKey.put(t2, t1, x + 1);
						    		
						    	} else{
						    		System.out.println("KEY_TO_TERM: Adding term = " + t1 + "key" + k1);
						    		keyToTerms.put(k1, t1, 1);

						    	}
									
						}
		    	   }
		    	}
			
			}
			
			
			
    }
	
    

    

	public void computeRanking(){	
		//addTweetToMap();
	}		

	
	
	@Override
	public void printRankingData() throws Exception  {
		
		pw_tt.write("Total_Tweets: " + documentSize);
		pw_tt.newLine();
		
		List<Map.Entry<String, Integer>> mapEntries = new LinkedList<Map.Entry<String, Integer>>(
				keyWords.entrySet());
		
		for (Map.Entry<String, Integer> c : mapEntries) {
			pw_kc.write("Key_Count: "  + c.getKey() + " " + c.getValue());
			pw_kc.newLine();
		}
		
		List<Map.Entry<String, Double>> mapEntriesD = new LinkedList<Map.Entry<String, Double>>(
				documentFrequency.entrySet());
		
		for (Map.Entry<String, Double> c : mapEntriesD) {
			pw_tc.write("Term_Count: "  + c.getKey() + " " + c.getValue());
			pw_tc.newLine();
		}
		
		ArrayList<Triple> t = keyToUser.getAll();
		
		for (Triple x: t){
			System.out.println("DEBUG: " +  x.k1 + " " + x.k2 + " " + x.v);
			pw_ku.write("Key_User: " + x.k1 + " " + x.k2 + " " + x.v);
			pw_ku.newLine();
		}
		
		pw_ku.newLine();
		pw_ku.newLine();
		
		t = userToUser.getAll();
		
		for (Triple x: t){
			System.out.println("DEBUG: " +  x.k1 + " " + x.k2 + " " + x.v);
			pw_uu.write("User_User: " + x.k1 + " " + x.k2 + " " + x.v);
			pw_uu.newLine();
		}
		
		pw_uu.newLine();
        pw_uu.newLine();
        
        t = keyToKey.getAll();
        
		for (Triple x: t){
			System.out.println("DEBUG: " +  x.k1 + " " + x.k2 + " " + x.v);
			pw_kk.write("Key_Key: " + x.k1 + " " + x.k2 + " " + x.v);
			pw_kk.newLine();
		}
		
		 t = keyToTerms.getAll();
	        
		for (Triple x: t){
			System.out.println("DEBUG: " +  x.k1 + " " + x.k2 + " " + x.v);
			pw_kt.write("Key_Terms: " + x.k1 + " " + x.k2 + " " + x.v);
			pw_kt.newLine();
		}
			
		
		
		
	}
	
}
