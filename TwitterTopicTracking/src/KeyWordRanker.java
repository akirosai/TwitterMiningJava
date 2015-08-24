import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





public class KeyWordRanker extends TwitterRanker {
	
	public KeyWordRanker(String output, String keyWordFile) throws FileNotFoundException, IOException {
		super(output, keyWordFile);
	}
	
	@Override
	public void computeRanking(){			
		// Compute the strength and keyWordCount
		for (Tweet tweet: tweets) {
			double value = 0;
			int significantWordCount = 0;
			for (Map.Entry<String, Integer> entry : tweet.termFrequency.entrySet()) {
				//System.out.println("STRENGTH_ARG: " + entry.getKey() + " " + entry.getValue() + " " + Math.log(documentSize/ documentFrequency.get(entry.getKey())));
			    if(keyWords.containsKey(entry.getKey())){
					value = value + entry.getValue()
							* Math.log(documentSize
									/ documentFrequency.get(entry.getKey()));
				    //System.out.println("STRENGTH_VAL: " + value);
					significantWordCount = significantWordCount + entry.getValue();
			    }
			}
			tweet.strength = value;
			tweet.significantWordCount = significantWordCount;
		}
		
		// Sort the collection based on the strength
		sortTweetsAscendingStrength();
		
		//printRankingData();
		
	}		

	@Override
	public void printRankingData() throws Exception  {
		sortAndPrintIntegerMap("KEYWORD:", keyWords); 
	
		pw.newLine();
		pw.newLine();
		
		printTweetList("TWEETS:", tweets);
	}
	
}
