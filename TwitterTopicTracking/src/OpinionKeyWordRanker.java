import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OpinionKeyWordRanker extends TwitterRanker {
	
	private Map<String, Integer> opinionWords;
	
	public OpinionKeyWordRanker(String output, String keyWordFile, String opinionWordFile) throws FileNotFoundException, IOException{
		super(output, keyWordFile);
		this.opinionWords = new HashMap<String, Integer>();
		readAndStore(opinionWordFile, opinionWords);
	}

	@Override
	public boolean updateLists(String term){
		super.updateLists(term);
		if (opinionWords.containsKey(term)) {
			opinionWords.put(term, opinionWords.get(term) + 1);
		}
		return true;
	}
	
	@Override
	public void computeRanking(){			
		// Compute the strength and keyWordCount
		for (Tweet tweet: tweets) {
			double value = 0;
			int significantWordCount = 0;
			for (Map.Entry<String, Integer> entry : tweet.termFrequency.entrySet()) {
				//System.out.println("STRENGTH_ARG: " + entry.getKey() + " " + entry.getValue() + " " + Math.log(documentSize/ documentFrequency.get(entry.getKey())));
			    if(keyWords.containsKey(entry.getKey()) || opinionWords.containsKey(entry.getKey())){
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
		sortAndPrintIntegerMap("KEYWORD: ", keyWords); 
		pw.newLine();
		pw.newLine();
		sortAndPrintIntegerMap("OPINION: ", opinionWords);
		pw.newLine();
		pw.newLine();
		printTweetList("TWEETS:", tweets);
	}
}
