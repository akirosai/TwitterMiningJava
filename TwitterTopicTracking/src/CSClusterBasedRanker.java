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

//import TwitterRanker.Tweet;


public class CSClusterBasedRanker extends TwitterRanker {
	
	List<Cluster> clusters;
	private double comparisonThreshold = 0.3; 
	
	class Cluster implements Comparable<Cluster> {
		long id;
		List<Tweet> cTweetList;
		Integer clusterSize = 0;
		Double clusterStrength = 0.0;
		Map<String, Integer> centroidVector;
		Date timeOfFirstInsertion;
		long centroidDate;
		Date timeOfLastInsertion;
		
		Cluster(Tweet tw) {
			this.cTweetList = new ArrayList<Tweet>();
			this.centroidVector = new HashMap<String, Integer>();
			this.cTweetList.add(tw);
			recalculateCentroidVector(tw);
			this.clusterStrength = 1.0;
			this.clusterSize = 1;
			this.id = clusters.size() + 1;
			this.centroidDate = tw.date.getTime();
		}	
		
		@Override
		public int compareTo(Cluster o) {
			// return this.featureVectors.size() - o.featureVectors.size();
			return (int) (this.clusterStrength - o.clusterStrength);
		}
		
		public void insertInCluster(Tweet tweet) {
			cTweetList.add(tweet);
			this.timeOfLastInsertion = (Date) tweet.date.clone();
			recalculateCentroidVector(tweet);
			// insertWordClusterMap(featureVector);
			// this.updateStrength();
		}

		private void recalculateCentroidVector(Tweet tweet) {
			//centroidVector.clear();
			long newtime = 0;
			
			Map<String, Integer> termFrequency = tweet.termFrequency;
			for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
				String key = entry.getKey();
				if (centroidVector.containsKey(key)) {
					int value = centroidVector.get(key)
								+ entry.getValue();
					centroidVector.put(key, value);
						// //System.out.println("SUM " + key + ":" + value +
						// ", ");
				} else {
					centroidVector.put(key, entry.getValue());
				}
			}
			this.centroidDate = this.centroidDate + tweet.date.getTime();
		}
		
		public void updateStrength() {

			double value = 0.0;
			System.out.println("DEBUG: updateStrength()  Size:" + cTweetList.size());
			for (Map.Entry<String, Integer> entry : this.centroidVector.entrySet()) {
				//System.out.println("DEBUG: updateStrength() Centroid: key " + entry.getKey() + " value " + entry.getValue());
				if (keyWords.containsKey(entry.getKey())){
					System.out.println("DEBUG: updateStrength() CentroidKey: key " + entry.getKey() + " value " + entry.getValue());
					value = value + entry.getValue()* Math.log(documentSize/documentFrequency.get(entry.getKey()));
					System.out.println("DEBUG: updateStrength() CentroidKey: keyWeight " + Math.log(documentSize/documentFrequency.get(entry.getKey())));
				}
			}
			
			this.clusterStrength = value/cTweetList.size();
			System.out.println("DEBUG: updateStrength() clusterStrength " + this.clusterStrength);

		}

		
	}
	
	
	public CSClusterBasedRanker(String output, String keyWordFile) throws FileNotFoundException, IOException {
		super(output, keyWordFile);
		clusters = new ArrayList<Cluster>();
	}
	
	
	private void addToClusters(Tweet tweet) {
		
		
		if (clusters.size() == 0) {
			clusters.add(new Cluster(tweet));
		} else {
			boolean match = false;
			// Compare and add to new clusters, or create a new cluster
			int numComparison = 0;
			double maxScore = compareVector(tweet, clusters.get(0));
			Cluster maxCluster = clusters.get(0);

			for (Cluster cluster : clusters) {
				numComparison++;
				double comparisonScore = compareVector(tweet, cluster);

				
				if (comparisonScore >= comparisonThreshold
						&& comparisonScore >= maxScore) {
					
					maxScore = comparisonScore;
					maxCluster = cluster;

					match = true;
					
					System.out.println("DEBUG: Comparison score: " + comparisonScore );
					
					
				}
			}

			if (match == true) {
				maxCluster.insertInCluster(tweet);
				System.out.println("DEBUG: Tweet added to Cluster ");
				// maxCluster.id + " Strength = " + maxCluster.clusterStrength);
			} else {
				clusters.add(new Cluster(tweet));
				System.out.println("DEBUG: New Cluster Created " + clusters.size());
				
			}
			// //System.out.println("Number of comparisons :" + numComparison);
		}
	}
	
	
	private double compareVector(Tweet tweet, Cluster cluster) {
		Map<String, Double> featureVectorA = new HashMap<String, Double>();
		Map<String, Double> featureVectorB = new HashMap<String, Double>();
		
		System.out.println("Tweet =" + tweet.tweetId + " Cluster = " + cluster.id);
		
		for (Map.Entry<String, Integer> entry : tweet.termFrequency.entrySet()) {
			
			double value = entry.getValue()
						* Math.log((double)(documentSize+1)
								/ (double)documentFrequency.get(entry.getKey()));
			featureVectorA.put(entry.getKey(), value);
			System.out.println("DEBUG Feature Vector A: " + entry.getKey() + " " + entry.getValue() + " " + documentSize + " " + 
																			       documentFrequency.get(entry.getKey()) + " " + value);
			
		}

		for (Map.Entry<String, Integer> entry : cluster.centroidVector
				.entrySet()) {
			double value = (entry.getValue()/cluster.cTweetList.size())
						* Math.log((double)(documentSize + 1)
								/ (double)documentFrequency.get(entry.getKey()));
			featureVectorB.put(entry.getKey(), value);
			//System.out.println("DEBUG Feature Vector B: " + entry.getKey() + " " + entry.getValue() + " " + value);
			System.out.println("DEBUG Feature Vector B: " + entry.getKey() + " " + entry.getValue() + " " + documentSize + " " + 
				       documentFrequency.get(entry.getKey()) + " " + value);
			
		}

		/*double timedifferencesq = Math.pow(dv.date.getTime()
				- cluster.centroidDate.getTime(), 2);
		double timefactor = Math.pow(Math.E,
				-(timedifferencesq / doublesigmasq));*/

		double timefactor = 1.0;
		
		double sim = cosineSimilarity(featureVectorA, featureVectorB) * timefactor;
		System.out.println("DEBUG Cosine Similarity: " + sim);
		return sim;
	}

	private double cosineSimilarity(Map<String, Double> featureVectorA,
			Map<String, Double> featureVectorB) {
		double score = 0;
		double lengthA;
		double lengthB;
		double sum;

		sum = 0;
		for (Map.Entry<String, Double> entry : featureVectorA.entrySet()) {
			sum += entry.getValue() * entry.getValue();
		}
		lengthA = Math.sqrt(sum);

		//System.out.println("DEBUG: LenghtA " + lengthA);
		sum = 0;
		for (Map.Entry<String, Double> entry : featureVectorB.entrySet()) {
			sum += entry.getValue() * entry.getValue();
		}
		lengthB = Math.sqrt(sum);
		//System.out.println("DEBUG: LenghtB " + lengthB);

		sum = 0;
		for (Map.Entry<String, Double> entry : featureVectorA.entrySet()) {
			if (featureVectorB.containsKey(entry.getKey())) {
				sum += entry.getValue() * featureVectorB.get(entry.getKey());
			}
		}
		score = sum / (lengthA * lengthB);

		return score;
	}

	
	
	@Override
	public void computeRanking(){	
		
		// pruneDocumentFrequnecyVector();
		System.out.println("DEBUG: Total Tweets = " + tweets.size());
		
		for (Tweet tweet: tweets) {
			// pruneTweet(tweet);
			addToClusters(tweet);			
		}		
		
		System.out.println("DEBUG: Total Clusters = " + clusters.size());
		for (Cluster cluster: clusters){
			cluster.updateStrength();
		}
	}		

	
	protected void printCluster(String tag, Cluster c) throws Exception {
		
		System.out.println("DEBUG:AA " + c.id + " " + c.clusterStrength);
		pw.write(tag + c.id + " " + c.cTweetList.size() + " " + c.clusterStrength);
		pw.newLine();
		pw.newLine();
		pw.flush();
		
		for (Map.Entry<String, Integer> entry : c.centroidVector.entrySet()) {
			double value = (entry.getValue()/c.cTweetList.size())
						* Math.log(documentSize
								/ documentFrequency.get(entry.getKey()));
			pw.write(tag + ": CENTROID: " + entry.getKey() + " " + entry.getValue() + " " + value);
			pw.newLine();
			
		}
		
	}
	
	@Override
	public void printRankingData() throws Exception  {
		
		System.out.println("DEBUG: Printing Keywords");		
		sortAndPrintIntegerMap("KEYWORD:", keyWords); 
	
		System.out.println("DEBUG: Printed Keywords");	
		
		pw.newLine();
		pw.newLine();
		
		Comparator<Cluster> clusterStrengthComparator = new Comparator<Cluster>() {

			@Override
			public int compare(Cluster o1, Cluster o2) {
				return o1.clusterStrength.compareTo(o2.clusterStrength);
			}

		};
		
		Collections.sort(clusters, clusterStrengthComparator);
		
		Collections.reverse(clusters);
		
		System.out.println("DEBUG: Printing Cluster Map");	
		
		pw.write("CLUSTER_SUMMARY: " + tweets.get(0).user_id  + " " + tweets.size() + " " + (double)clusters.size()/(double)tweets.size() + " " + tweets.get(0).user_name);
		
		for (Cluster c : clusters) {
			
			pw.newLine();
			printCluster("CLUSTERS: ", c);
			printTweetList("TWEETS:", c.cTweetList);
			pw.flush();
		}
		
		System.out.println("DEBUG: Printed Cluster Map");	
	}
	
}
