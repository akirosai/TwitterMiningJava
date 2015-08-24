import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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



public class DailySummary {
	protected Map<String, Integer> keyWords;
	protected Map<String, Double> DF;
	protected long documentSize;
	protected String output;
	protected NumberFormat nf = NumberFormat.getInstance();
	protected BufferedWriter pw_kt, pw_df;

	public static void main(String[] args)
	{
		//Precede the twitter ranking day by day.

	}
	public DailySummary(String input, String output)
	{
		this.DF = new HashMap<String, Double>();
		this.documentSize = 0;
		this.keyWords = new HashMap<String, Integer>();
		

		
		
	}
	public static void readCount()
	{
		//read in the current pieces of DF in a day
		//output as a congregated file

	}

}
