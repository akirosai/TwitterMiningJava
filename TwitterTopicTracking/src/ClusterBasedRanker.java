import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ClusterBasedRanker extends TwitterRanker {
	
	private Map<String, Cluster> clusterMap;
	
	class Cluster {
		String id;
		List<Tweet> cTweetList;
		Integer clusterSize = 0;
		Double clusterStrength = 0.0;
		
		Cluster(String index) {
			this.cTweetList = new ArrayList<Tweet>();
			this.clusterStrength = 1.0;
			this.clusterSize = 0;
			this.id = index;
		}	

	}
	
	public ClusterBasedRanker(String output, String keyWordFile) throws FileNotFoundException, IOException {
		super(output, keyWordFile);
		clusterMap = new HashMap<String, Cluster>();
		//System.out.println("DEBUG:CI: Created new cluster map");
		//System.out.println("DEBUG:CI: " + clusterMap.size());
		//System.out.println("DEBUG:CI Reference = " + clusterMap);
		//System.out.println("DEBUG:CI Size = " + clusterMap.size());
	}
	
	@Override
	public void computeRanking(){	
		//System.out.println("DEBUG:CI Reference = " + this.clusterMap);
		//System.out.println("DEBUG:CI Size = " + this.clusterMap.size());
		for (Tweet tweet: tweets) {
			double value = -1;
			int significantWordCount = 0;
			String clusterIndex = null;
			String maxValueToken = null;
			System.out.println("#######################################");
			for (Map.Entry<String, Integer> entry : tweet.termFrequency.entrySet()) {
				//System.out.println("STRENGTH_ARG: " + entry.getKey() + " " + entry.getValue() + " " + Math.log(documentSize/ documentFrequency.get(entry.getKey())));
				
				System.out.println("DEBUG:CI:TERM: " + clusterIndex + " " + entry.getKey());
			
				if (entry.getKey().charAt(0) == '#') {
					if (clusterIndex == null){
						clusterIndex = entry.getKey();
					} else {
						clusterIndex = clusterIndex.concat(entry.getKey());
					}
				    //System.out.println("DEBUG:CI:HASH: " + clusterIndex);
			    }
				
				
				// Find the token with the least frequency (most significant)
				if(keyWords.containsKey(entry.getKey())){
					//System.out.println("DEBUG:CI:VALUE: " + entry.getKey()) + " " + (Math.log(documentSize/documentFrequency.get(entry.getKey())));
				    if(Math.log(documentSize/documentFrequency.get(entry.getKey())) > value){
				    	value = Math.log(documentSize/documentFrequency.get(entry.getKey()));
				    	maxValueToken = entry.getKey();
				    	//System.out.println("DEBUG:CI:MVTOKEN: " + maxValueToken);
				    }
				    //System.out.println("STRENGTH_VAL: " + value);
					significantWordCount = significantWordCount + entry.getValue();
			    }
			}
			
			if (clusterIndex == null){
				clusterIndex = maxValueToken;
			}
			
			if (clusterIndex == null){
				clusterIndex = "FOO";
			}
			
			//System.out.println("DEBUG:CI:Cluster Index = " + clusterIndex ); 
			
			// If the cluster with the same key exists add to the tweet list of the cluster.
			// If not create a new cluster with the clusterIndex as the key.
			//System.out.println("DEBUG:CI Reference = " + clusterMap);
			//System.out.println("DEBUG:CI Length = " + clusterMap.size());
			Cluster c;
			
			if (clusterMap.containsKey(clusterIndex)){
				//System.out.println("DEBUG:CI Adding to existing cluster" );
				c = clusterMap.get(clusterIndex);
				c.cTweetList.add(tweet);
				c.clusterSize = c.clusterSize + 1;
			}
			else{
				//System.out.println("DEBUG:CI Creating new cluster" );
				c = new Cluster(clusterIndex);
				c.cTweetList.add(tweet);
				c.clusterSize = c.clusterSize + 1;
				clusterMap.put(clusterIndex, c);
							
			}
			
		}
	
		// Sort the collection based on the strength
		//sortClusters();
		
		//printRankingData();
		
	}		

	
	private void sortClusters(){
		List<Map.Entry<String, Cluster>> mapEntries = new LinkedList<Map.Entry<String, Cluster>>(
				clusterMap.entrySet());
			
		Collections.sort(mapEntries, new Comparator<Map.Entry<String, Cluster>>() {
			@Override
			public int compare(Map.Entry<String, Cluster> p1, Map.Entry<String, Cluster> p2) {
	            return (p1.getValue().clusterSize).compareTo(p2.getValue().clusterSize);
	        }
		});
		
		Collections.reverse(mapEntries);
	}
	
	@Override
	public void printRankingData() throws Exception  {
		
		System.out.println("DEBUG: Printing Keywords");		
		sortAndPrintIntegerMap("KEYWORD:", keyWords); 
	
		System.out.println("DEBUG: Printed Keywords");	
		
		pw.newLine();
		pw.newLine();
		
		List<Map.Entry<String, Cluster>> mapEntries = new LinkedList<Map.Entry<String, Cluster>>(
				clusterMap.entrySet());
			
		Collections.sort(mapEntries, new Comparator<Map.Entry<String, Cluster>>() {
			@Override
			public int compare(Map.Entry<String, Cluster> p1, Map.Entry<String, Cluster> p2) {
	            return (p1.getValue().clusterSize).compareTo(p2.getValue().clusterSize);
	        }
		});
		
		Collections.reverse(mapEntries);
		
		System.out.println("DEBUG: Printing Cluster Map");	
		
		for (Map.Entry<String, Cluster> c : mapEntries) {
			System.out.println("DEBUG:AA " + c.getKey() + " " + c.getValue().clusterSize);
			pw.write("CLUSTER: " + c.getKey() + " " + c.getValue().clusterSize);
			pw.newLine();
			pw.newLine();
			pw.flush();
			printTweetList("TWEETS:", c.getValue().cTweetList);
			pw.flush();
		}
		
		System.out.println("DEBUG: Printed Cluster Map");	
	}
	
}
