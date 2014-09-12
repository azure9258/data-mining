/* Summary : Implementation of Apriori Algorithm (finding frequent itemsets by Apriori and generate association rule) 
 * Last modified : 2014 / 09 / 12
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class apriori {
	
	/* the biggest number of each dataset (range from 0 to NumX) */
	public static final int Num1 = 500;
	public static final int Num2 = 1000;
	public static final int Num3 = 1000;
	public static final int Num4 = 119;
	
	/* filename of dataset */
	public static final String file1 = "D1kT10N500.txt";
	public static final String file2 = "D10kT10N1k.txt";
	public static final String file3 = "D100kT10N1k.txt";
	public static final String file4 = "Mushroom.txt";
	
	public static double min_sup = 0.5; /* minimal support (%) */
	public static double min_conf = 0.5; /* minimal confidence */
	
	static List<List<Integer>> dataset = new ArrayList<List<Integer>>(); /* save all the data in dateset */
	static List<candidate>[] fcandidate = null;
	static PrintWriter outputRule = null;
	static int numRule = 0; // numbers of association rule
	
	public static void main(String[] args) {
		int num_min_sup = 0; /* the value of minimal support */
		int N = 0; /* how many number in dataset */
		String filename = ""; /* filename of dataset */
		BufferedReader br = new BufferedReader (new InputStreamReader(System.in)); /* user input */
		String line = ""; /* receive what reader read */
		int counter = 0; /* used to be a counter of support */
		int check = 0; /* used to check the condition */
		long start_time, finish_time;
		long start_heap, max_heap, temp_heap;
		PrintWriter output = null;
		int numItemset = 0;
		
		System.out.println("Select the dataset");
		System.out.println("(1)D1kT10N500 (2)D10kT10N1k (3)D100kT10N1k (4)Mushroom : ");
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(line.equals("1")){
			N = Num1+1;
			filename = new String(file1);
		}
		else if(line.equals("2")){
			N = Num2+1;
			filename = new String(file2);
		}
		else if(line.equals("3")){
			N = Num3+1;
			filename = new String(file3);
		}
		else if(line.equals("4")){
			N = Num4+1;
			filename = new String(file4);
		}
		else{
			System.out.println("Please input correct number. ( 1 ~ 4 )");
			System.exit(0);
		}
		
		System.out.println("Select the min_sup(%) : ");
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		min_sup = Double.parseDouble(line);
		
		System.out.println("Select the min_conf(%) : ");
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		min_conf = Double.parseDouble(line) / 100.0;
		
		
		start_time = System.currentTimeMillis();
		
		start_heap = max_heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		
		/* read dataset from file */
		readfile(filename);
		
		try {
			output = new PrintWriter("output.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		num_min_sup = (int) Math.ceil(dataset.size() * min_sup * 0.01);
		
		fcandidate = new ArrayList[N];
		for(int i=0;i<N;i++){
			fcandidate[i] = new ArrayList<candidate>();
		}
		/* create first round candidate C1 and remove infrequent set */
		for(int i=0;i<N;i++){
			counter = 0;
			for(int j=0;j<dataset.size();j++){
				List<Integer> temp = dataset.get(j);
				check = temp.indexOf(new Integer(i));
				if(check == -1){
					continue;
				}
				counter++;
			}
			
			/* only retain frequent candidate */
			if(counter >= num_min_sup){
				candidate cand1;
				List<Integer> list = new ArrayList<Integer>();
				list.add(new Integer(i));
				cand1 = new candidate(counter, list);
				fcandidate[0].add(cand1);
				output.print(list + " : " + counter + "\n");
			}
		}
		
		numItemset += fcandidate[0].size();
		System.out.println("L1 size : " + fcandidate[0].size());
		
		/* create second round candidate C2 by using C1 and remove infrequent set */
		for(int i=0;i<fcandidate[0].size();i++){
			// System.out.println(i);
			for(int j=i+1;j<fcandidate[0].size();j++){
				counter = 0;
				List<Integer> olist = new ArrayList<Integer>();
				olist.add(fcandidate[0].get(i).itemset.get(0));
				olist.add(fcandidate[0].get(j).itemset.get(0));
				
				for(int k=0;k<dataset.size();k++){					
					
					if(num_intersect(olist, dataset.get(k)) == 2){
						counter++;
					}
				}
				
				/* only retain frequent candidate */
				if(counter >= num_min_sup){
					candidate cand1;
					List<Integer> list = new ArrayList<Integer>();
					list.add(fcandidate[0].get(i).itemset.get(0));
					list.add(fcandidate[0].get(j).itemset.get(0));
					cand1 = new candidate(counter, list);
					fcandidate[1].add(cand1);
					output.print(list + " : " + counter + "\n");
				}
			}
		}
		
		numItemset += fcandidate[1].size();
		System.out.println("L2 size : " + fcandidate[1].size());
		
		/* create C3 ~ Cn by using C2 ~ Cn-1 and remove infrequent set 
		 * when x = 5 means using C5(fcandidate[4]) to find C6(fcandidate[5])
		 * */
		for(int x=2;x<N;x++){
			if(fcandidate[x-1].size() <= x){
				break;
			}
			for(int i=0;i<fcandidate[x-1].size();i++){
				for(int j=i+1;j<fcandidate[x-1].size();j++){
					/* only continue if first (k-1) items are same */
					for(check=0;check<x-1;check++){
						if(!fcandidate[x-1].get(i).itemset.get(check).equals(fcandidate[x-1].get(j).itemset.get(check))){
							break;
						}
					}
					if(check != x-1){
						continue;
					}
					
					/* check if it can be join */
					List<Integer> olist = new ArrayList<Integer>(fcandidate[x-1].get(i).itemset);
					olist.add(fcandidate[x-1].get(j).itemset.get(x-1));
					counter = 0;
					for(int k=j+1;k<fcandidate[x-1].size();k++){
						if(num_intersect(olist, fcandidate[x-1].get(k).itemset) == x){
							counter++;
						}
					}
					
					if(counter != x-1){
						continue;
					}
					
					/* join and scan dataset to get support */
					counter = 0;
					for(int k=0;k<dataset.size();k++){
						if(num_intersect(olist, dataset.get(k)) == x+1){
							counter++;
						}
					}
					
					/* generate frequent itemset into arraylist */
					if(counter >= num_min_sup){
						candidate cand1;
						cand1 = new candidate(counter, olist);
						fcandidate[x].add(cand1);
						output.print(olist + " : " + counter + "\n");
					}
				}
			}
			numItemset += fcandidate[x].size();
			System.out.printf("L%d size : %d\n", x+1, fcandidate[x].size());
		}
		output.close();
		System.out.println("numbers of Frequent itemset : " + numItemset);
		
		finish_time = System.currentTimeMillis();
		System.out.println("finding frequent itemsets cost " + (finish_time-start_time)/1000 + " seconds");
		
		/* finding frequent itemsets by Apriori above
		 * and generate association rule from now on */
		
		generateRule(N);
		System.out.println("numbers of Association rule : " + numRule);
		
		temp_heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		if(temp_heap > max_heap){
			max_heap = temp_heap;
		}
		
		finish_time = System.currentTimeMillis();
		System.out.println("total program cost " + (finish_time-start_time)/1000 + " seconds");
		System.out.println("initial heap : " + start_heap);
		System.out.println("max heap : " + max_heap);
	}
	
	static void generateRule(int N){
		int L = 0;
		
		try {
			outputRule = new PrintWriter("Rule.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while(true){
			if(L == N || fcandidate[L].size() == 0){
				break;
			}
			
			/* run all possible combination
			 * Ex: when L=1, fcandidate[L] would contain L2 frequent itemset [i1,i2]
			 * C(2,1) would get i1, i2. C(2,2) would get [i1,i2]. 
			 * */
			for(int i=1;i<=L;i++){
				runAllCombi(L, L+1, i); // fcandidate[L] run C(L+1,i)
			}
			
			
			L++;
		}
		outputRule.close();
		
	}
	
	/* fcandidate[L] run C(m,n) */
	static void runAllCombi(int L, int m, int n){
		List<Boolean> flag = new ArrayList<Boolean>();
		
		for(int i=0;i<n;i++){
			flag.add(true);
		}
		for(int i=0;i<m-n;i++){
			flag.add(false);
		}
		
		
		
		while(true){
			for(int i=0;i<fcandidate[L].size();i++){
				List<Integer> temp = new ArrayList<Integer>();
				int index;
				
				for(int j=0;j<flag.size();j++){
					if(flag.get(j)){
						temp.add(fcandidate[L].get(i).itemset.get(j));
					}
				}
				candidate candi = new candidate(0, temp);
				index = fcandidate[n-1].indexOf(candi);
				double confidence = (double)fcandidate[L].get(i).support / (double)fcandidate[n-1].get(index).support;
				if(confidence >= min_conf){
					List<Integer> tempList = new ArrayList<Integer>(fcandidate[L].get(i).itemset);
					tempList.removeAll(temp);
					numRule++;
					outputRule.printf("%s -> %s : %f%%\n", temp, tempList, confidence);
					//outputRule.printf("%s -> %s\n", temp, tempList);
				}
			}
			
			/* check if the final combination */
			boolean finish = true;
			for(int i=flag.size()-n;i<flag.size();i++){
				if(!flag.get(i)){
					finish = false;
					break;
				}
			}
			if(finish){
				break;
			}
			
			/* next possible combination */
			if(!flag.get(flag.size()-1)){
				for(int i=flag.size()-2;i>=0;i--){
					if(flag.get(i)){
						flag.set(i, false);
						flag.set(i+1, true);
						break;
					}
				}
			}
			else{
				int beGoingToNext = flag.size()-2;
				int count = 1;
				
				while(true){
					flag.set(beGoingToNext+1, false);
					if(!flag.get(beGoingToNext)){
						break;
					}
					beGoingToNext--;
					count++;
				}
				
				beGoingToNext--;
				
				while(true){
					if(flag.get(beGoingToNext)){
						break;
					}
					beGoingToNext--;
				}
				
				flag.set(beGoingToNext, false);
				flag.set(beGoingToNext+1, true);
				
				for(int i=beGoingToNext+2;i<beGoingToNext+2+count;i++){
					flag.set(i, true);
				}
			}

		}
	}
	
	/* return the numbers of List which is the intersection of L1 and L2 */
	static int num_intersect(List<Integer> L1, List<Integer> L2){
		List<Integer> newList = new ArrayList<Integer>(L1);
		newList.retainAll(L2);
		return newList.size();
	}
	
	static void readfile(String filename){
		BufferedReader br;
		String line = ""; /* receive what reader read */
		String[] tokens; /* used to receive the result of String.split */
		List<Integer> list;

		try {
			br = new java.io.BufferedReader(new java.io.FileReader(filename));
			while(true)
		    {
		        line = br.readLine();
		        if(line == null)
		           break;
		        //System.out.println(line);
		        tokens = line.split(", ");
		        list = new ArrayList<Integer>();
		        for(int i=0;i<tokens.length;i++){
		        	list.add(new Integer(Integer.parseInt(tokens[i])));
		        }
		        dataset.add(list);
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class candidate {
	int support;
	List<Integer> itemset;
	public candidate(int sup, List<Integer> set) {
		itemset = new ArrayList<Integer>(set);
		support = sup;
	}
	
	@Override
	public boolean equals(Object obj) {
		/* equal if both itemset are equal 
		 * (used for special purpose -> candidate.indexof() )
		 * when generate association rule
		 * */
		return ((candidate)obj).itemset.equals(itemset);
	}
}
