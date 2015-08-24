import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;




public class TwitterSortingApp {
	
	enum RankingScheme {
		KEYWORD_BASED, KEYWORD_OPINIONWORD_BASED, CLUSTER_BASED_RANKING
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException{
		
		TwitterSorter sorter;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
		RankingScheme rankingScheme = RankingScheme.KEYWORD_BASED;
		
		System.out.println("sorter " +  args.length + " " + args[0]);
		if (args.length == 2) {
			if (Integer.parseInt(args[0]) == 1) {
				sorter = new TwitterSorter(args[1]);
				sorter.parse();
			} 
		}
		else {
			System.out.println("Invalid number of arguments");
		}
	}

}
