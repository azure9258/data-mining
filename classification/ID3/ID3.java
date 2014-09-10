import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class ID3 {

	public static final String file1 = "CUSTOMER.txt";
	
	public static List<customer> dataset = new ArrayList<customer>();
	
	public static void main(String[] args) {
		
		
		readFile(file1);

		for(int i=0;i<dataset.size();i++){
			System.out.println(dataset.get(i).value);
		}
	}
	
	static void readFile(String filename){
		BufferedReader br;
		String line = ""; /* receive what reader read */
		String[] tokens; /* used to receive the result of String.split */

		try {
			br = new java.io.BufferedReader(new java.io.FileReader(filename));
			
			line = br.readLine();
			tokens = line.split(",");
			for(int i=0;i<tokens.length;i++){
				customer.property.add(tokens[i].substring(1, tokens[i].length()-1));
			}
			
			line = br.readLine();
			if(line != null){
				tokens = line.split(",");
				customer newCus = new customer();
				for(int i=0;i<tokens.length;i++){
					if(tokens[i].charAt(0)=='\"' && tokens[i].charAt(tokens[i].length()-1)=='\"'){
						newCus.value.add(tokens[i].substring(1, tokens[i].length()-1));
						customer.type.add("String");
					}
					else{
						newCus.value.add(tokens[i].substring(0, tokens[i].length()));
						customer.type.add("int");
					}
				}
				dataset.add(newCus);
			}
			
			while(true)
		    {
		        line = br.readLine();
		        if(line == null)
		           break;
		        //System.out.println(line);
		        tokens = line.split(",");
				customer newCus = new customer();
				for(int i=0;i<tokens.length;i++){
					if(tokens[i].charAt(0)=='\"' && tokens[i].charAt(tokens[i].length()-1)=='\"'){
						newCus.value.add(tokens[i].substring(1, tokens[i].length()-1));
					}
					else{
						newCus.value.add(tokens[i].substring(0, tokens[i].length()));
					}
				}
				dataset.add(newCus);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class customer {
	static List<String> property = new ArrayList<String>();
	static List<String> type = new ArrayList<String>();
	List<String> value = new ArrayList<String>();
}