import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import RankClusRanker.Triple;

//import RankClusRanker.Triple;

//import spiffy.core.util.TwoDHashMap;

//import RankClusRanker.Pair;

//import RankClusRanker.Pair;


public class TestKeys {

	
	public TwoDHashMap<String, String, Integer> t;
	
	TestKeys(){
		 t = new TwoDHashMap();
	}
	//= new TwoDHashMap(keyToUser);
	//private Map<Pair<String, String>, Integer> keyToKey;
	//private Map<Pair<String, String>, Integer> userToUser;
	
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
	 
	public static void main(String[] args) {
		
		
		TestKeys testKeys = new TestKeys();
       
		
      
		String k1 = "shanika";
		String k2 = "ahinsa";
		Integer v = 1000;
		
		testKeys.t.put(k1, k2, v);
		
		System.out.println("Added key");
		String k3 = "shanika";
		String k4 = "ahinsa";
		
		if (testKeys.t.containsKey(k3,k4)){
			System.out.println(" Key: Value " + testKeys.t.get(k3,k4));
			Integer i = (Integer)testKeys.t.get(k1, k2);
			testKeys.t.put(k1, k2,  i +1);
			System.out.println(" Key: Value " + testKeys.t.get(k3,k4));
		}
		
		
		testKeys.t.put("shanika", "avanka", 100);
		testKeys.t.put("ajith", "shanika", 125);
		
		ArrayList<Triple> t = testKeys.t.getAll();
		
		for (Triple x: t){
			System.out.println("DEBUG: " +  x.k1 + " " + x.k2 + " " + x.v);
		}
		
		
		

	}

}

