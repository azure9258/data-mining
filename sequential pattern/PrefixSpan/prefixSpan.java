/* Summary : Implementation of PrefixSpan Algorithm (finding frequent Sequential pattern) 
 * Last modified : 2014 / 09 / 12
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class prefixSpan {
	
	/* all numbers in test file would range from 1 to 1000 */
	public static final int MaxNum = 1000;
	
	/* filename of dataset */
	public static final String file1 = "simData[D1k C5.0 T3.0 S3.0 I2.0 N1000].txt";
	public static final String file2 = "simData[D10k C5.0 T3.0 S3.0 I2.0 N1000].txt";
	public static final String file3 = "simData[D100k C5.0 T3.0 S3.0 I2.0 N1000].txt";
	public static final String file4 = "simData[D1000k C5.0 T3.0 S3.0 I2.0 N1000].txt";
	
	public static double min_sup = 5; /* minimal support (%) */
	public static int num_min_sup = 5; /* the value of minimal support */
	
	static List<List<List<Integer>>> dataset = new ArrayList<List<List<Integer>>>(); /* save all the data in dateset */
	static List<candidate> mainL1 = new ArrayList<candidate>();
	static PrintWriter output = null;
	static long max_heap, temp_heap;
	static int numFreSeq = 0;
	
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader (new InputStreamReader(System.in)); /* user input */
		String line = ""; /* receive what reader read */
		String filename = ""; /* filename of dataset */
		long start_time, finish_time;

		/* select file to run */
		System.out.println("(1)simData[D1k C5.0 T3.0 S3.0 I2.0 N1000]\n(2)simData[D10k C5.0 T3.0 S3.0 I2.0 N1000]\n(3)simData[D100k C5.0 T3.0 S3.0 I2.0 N1000]\n(4)simData[D1000k C5.0 T3.0 S3.0 I2.0 N1000]");
		System.out.println("Select the dataset : ");
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(line.equals("1")){
			filename = new String(file1);
		}
		else if(line.equals("2")){
			filename = new String(file2);
		}
		else if(line.equals("3")){;
			filename = new String(file3);
		}
		else if(line.equals("4")){
			filename = new String(file4);
		}
		else{
			System.out.println("Please input correct number. ( 1 ~ 4 )");
			System.exit(0);
		}
		
		/* input minimal support*/
		
		System.out.println("Select the min_sup(%) : ");
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		min_sup = Double.parseDouble(line);
		
		/* record start time and used heap */
		start_time = System.currentTimeMillis();
		max_heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		
		try {
			output = new PrintWriter("result.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		/* read dataset from file */
		readFile(filename);
		
		temp_heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		if(temp_heap > max_heap){
			max_heap = temp_heap;
		}
		
		/* calculate the minimal support value*/
		num_min_sup = (int) Math.ceil(dataset.size() * min_sup * 0.01);
		System.out.println("minimal support : " + num_min_sup);
		
		runMainL1();
		runMainProjDB();
		
		temp_heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		if(temp_heap > max_heap){
			max_heap = temp_heap;
		}
		
		output.close();
		finish_time = System.currentTimeMillis();
		System.out.println("*****");
		System.out.println("total time : " + (finish_time-start_time)/1000.0 + " seconds");
		System.out.println("Frequent sequences count : " + numFreSeq);
		System.out.println("max memory : " + max_heap/1048576.0 + "(mb)");
		System.out.println("*****");
	}
	
	static void runLn1(List<List<Integer>> lastPrefix, List<candidate> subLn, List<List<List<Integer>>> DB){
		int counter;
		boolean flag;
		for(int x=0;x<subLn.size();x++){
			List<List<List<Integer>>> projDB = new ArrayList<List<List<Integer>>>();
			List<candidate> subLn1 = new ArrayList<candidate>(); /* Ex : a */
			List<candidate> subLn2 = new ArrayList<candidate>(); /* Ex : _a */
			List<Integer> removeLn1 = new ArrayList<Integer>(); /* Ex : a */
			List<Integer> removeLn2 = new ArrayList<Integer>(); /* Ex : _a */
			List<List<Integer>> prefix = List2Dcopy(lastPrefix);
			List<Integer> tempList1 = new ArrayList<Integer>(subLn.get(x).itemset);
			prefix.add(tempList1);
			
			for(int i=0;i<DB.size();i++){
				flag = false;
				for(int j=0;j<DB.get(i).size();j++){
					if(flag){
						break;
					}
					for(int k=0;k<DB.get(i).get(j).size();k++){
						if(DB.get(i).get(j).get(k).equals(subLn.get(x).itemset.get(0))){
							if(k != 0 && DB.get(i).get(j).get(0) == -1){
								break;
							}
							flag = true;
							List<List<Integer>> newSequence = new ArrayList<List<Integer>>();
							if((k+1) < DB.get(i).get(j).size()){
								List<Integer> seq1 = new ArrayList<Integer>();
								seq1.add(-1);
								for(int m=k+1;m<DB.get(i).get(j).size();m++){
									seq1.add(DB.get(i).get(j).get(m));
								}
								newSequence.add(seq1);
							}
							for(int m=j+1;m<DB.get(i).size();m++){
								List<Integer> seq1 = new ArrayList<Integer>(DB.get(i).get(m));
								newSequence.add(seq1);
							}
							projDB.add(newSequence);
							break;
						}
					}
				}
			}
			
			// case1 : a
			for(int y=0;y<mainL1.size();y++){
				counter = 0;
				for(int i=0;i<projDB.size();i++){
					flag = false;
					for(int j=0;j<projDB.get(i).size();j++){
						if(flag){
							break;
						}
						for(int k=0;k<projDB.get(i).get(j).size();k++){
							if(projDB.get(i).get(j).get(k).equals(mainL1.get(y).itemset.get(0))){
								if(projDB.get(i).get(j).get(0) == -1){
									break;
								}
								counter++;
								flag = true;
								break;
							}
						}
					}
				}
				if(counter >= num_min_sup){
					candidate candidate1;
					List<Integer> list1 = new ArrayList<Integer>();
					list1.add(mainL1.get(y).itemset.get(0));
					candidate1 = new candidate(counter, list1);
					subLn1.add(candidate1);
				}
				else{
					removeLn1.add(mainL1.get(y).itemset.get(0));
				}
			}
			outputResult(subLn1, prefix, 1);
			
			// case2 : _a
			for(int y=0;y<mainL1.size();y++){
				counter = 0;
				for(int i=0;i<projDB.size();i++){
					flag = false;
					for(int j=0;j<projDB.get(i).size();j++){
						if(flag){
							break;
						}
						for(int k=0;k<projDB.get(i).get(j).size();k++){
							if(projDB.get(i).get(j).get(k).equals(mainL1.get(y).itemset.get(0))){
								if(k != 0){
									if(projDB.get(i).get(j).get(0) == -1){
										counter++;
										flag = true;
										break;
									}
									if(prefix.get(prefix.size()-1).size() <= k){
										boolean check = true;
										List<Integer> subList = projDB.get(i).get(j).subList(0, k);
										for(int m=0;m<prefix.get(prefix.size()-1).size();m++){
											int index = subList.indexOf(prefix.get(prefix.size()-1).get(m));
											if(index == -1){
												check = false;
												break;
											}
										}
										if(check){
											counter++;
											flag = true;
											break;
										}
									}
									
								}
								break;
							}
						}
					}
				}
				if(counter >= num_min_sup){
					candidate candidate1;
					List<Integer> list1 = new ArrayList<Integer>();
					list1.add(mainL1.get(y).itemset.get(0));
					candidate1 = new candidate(counter, list1);
					subLn2.add(candidate1);
				}
				else{
					removeLn2.add(mainL1.get(y).itemset.get(0));
				}
			}
			outputResult(subLn2, prefix, 2);
			
			for(int i=0;i<removeLn1.size();i++){
				optimizeDB(projDB, removeLn1.get(i), 1);
			}
			for(int i=0;i<removeLn2.size();i++){
				optimizeDB(projDB, removeLn2.get(i), 2);
			}
			
			runLn1(prefix, subLn1, projDB);
			runLn2(prefix, subLn2, projDB);
		}
	}
	
	static void runLn2(List<List<Integer>> lastPrefix, List<candidate> subLn, List<List<List<Integer>>> DB){
		int counter;
		boolean flag;
		
		for(int x=0;x<subLn.size();x++){
			List<List<List<Integer>>> projDB = new ArrayList<List<List<Integer>>>();
			List<candidate> subLn1 = new ArrayList<candidate>(); /* Ex : a */
			List<candidate> subLn2 = new ArrayList<candidate>(); /* Ex : _a */
			List<Integer> removeLn1 = new ArrayList<Integer>(); /* Ex : a */
			List<Integer> removeLn2 = new ArrayList<Integer>(); /* Ex : _a */
			List<List<Integer>> prefix = List2Dcopy(lastPrefix);
			prefix.get(prefix.size()-1).add(subLn.get(x).itemset.get(0));
			
			for(int i=0;i<DB.size();i++){
				flag = false;
				for(int j=0;j<DB.get(i).size();j++){
					if(flag){
						break;
					}
					for(int k=0;k<DB.get(i).get(j).size();k++){
						if(DB.get(i).get(j).get(k).equals(subLn.get(x).itemset.get(0))){
							if(k != 0){
								if(DB.get(i).get(j).get(0) == -1){
									flag = true;
									List<List<Integer>> newSequence = new ArrayList<List<Integer>>();
									if((k+1) < DB.get(i).get(j).size()){
										List<Integer> seq1 = new ArrayList<Integer>();
										seq1.add(-1);
										for(int m=k+1;m<DB.get(i).get(j).size();m++){
											seq1.add(DB.get(i).get(j).get(m));
										}
										newSequence.add(seq1);
									}
									for(int m=j+1;m<DB.get(i).size();m++){
										List<Integer> seq1 = new ArrayList<Integer>(DB.get(i).get(m));
										newSequence.add(seq1);
									}
									projDB.add(newSequence);
									break;
								}
								if(lastPrefix.get(lastPrefix.size()-1).size() <= k){
									boolean check = true;
									List<Integer> subList = new ArrayList<Integer>(DB.get(i).get(j).subList(0, k));
									for(int m=0;m<lastPrefix.get(lastPrefix.size()-1).size();m++){
										int index = subList.indexOf(lastPrefix.get(lastPrefix.size()-1).get(m));
										if(index == -1){
											check = false;
											break;
										}
									}
									if(check){
										flag = true;
										List<List<Integer>> newSequence = new ArrayList<List<Integer>>();
										if((k+1) < DB.get(i).get(j).size()){
											List<Integer> seq1 = new ArrayList<Integer>();
											seq1.add(-1);
											for(int m=k+1;m<DB.get(i).get(j).size();m++){
												seq1.add(DB.get(i).get(j).get(m));
											}
											newSequence.add(seq1);
										}
										for(int m=j+1;m<DB.get(i).size();m++){
											List<Integer> seq1 = new ArrayList<Integer>(DB.get(i).get(m));
											newSequence.add(seq1);
										}
										projDB.add(newSequence);
										break;
									}
								}
							}
							
							break;
						}
					}
				}
			}
			
			// case1 : a
			for(int y=0;y<mainL1.size();y++){
				counter = 0;
				for(int i=0;i<projDB.size();i++){
					flag = false;
					for(int j=0;j<projDB.get(i).size();j++){
						if(flag){
							break;
						}
						for(int k=0;k<projDB.get(i).get(j).size();k++){
							if(projDB.get(i).get(j).get(k).equals(mainL1.get(y).itemset.get(0))){
								if(projDB.get(i).get(j).get(0) == -1){
									break;
								}
								counter++;
								flag = true;
								break;
							}
						}
					}
				}
				if(counter >= num_min_sup){
					candidate candidate1;
					List<Integer> list1 = new ArrayList<Integer>();
					list1.add(mainL1.get(y).itemset.get(0));
					candidate1 = new candidate(counter, list1);
					subLn1.add(candidate1);
				}
				else{
					removeLn1.add(mainL1.get(y).itemset.get(0));
				}
			}
			outputResult(subLn1, prefix, 1);
			
			// case2 : _a
			for(int y=0;y<mainL1.size();y++){
				counter = 0;
				for(int i=0;i<projDB.size();i++){
					flag = false;
					for(int j=0;j<projDB.get(i).size();j++){
						if(flag){
							break;
						}
						for(int k=0;k<projDB.get(i).get(j).size();k++){
							if(projDB.get(i).get(j).get(k).equals(mainL1.get(y).itemset.get(0))){
								if(k != 0){
									if(projDB.get(i).get(j).get(0) == -1){
										counter++;
										flag = true;
										break;
									}
									if(prefix.get(prefix.size()-1).size() <= k){
										boolean check = true;
										List<Integer> subList = projDB.get(i).get(j).subList(0, k);
										for(int m=0;m<prefix.get(prefix.size()-1).size();m++){
											int index = subList.indexOf(prefix.get(prefix.size()-1).get(m));
											if(index == -1){
												check = false;
												break;
											}
										}
										if(check){
											counter++;
											flag = true;
											break;
										}
									}
									
								}
								break;
							}
						}
					}
				}
				if(counter >= num_min_sup){
					candidate candidate1;
					List<Integer> list1 = new ArrayList<Integer>();
					list1.add(mainL1.get(y).itemset.get(0));
					candidate1 = new candidate(counter, list1);
					subLn2.add(candidate1);
				}
				else{
					removeLn2.add(mainL1.get(y).itemset.get(0));
				}
			}
			outputResult(subLn2, prefix, 2);
			
			for(int i=0;i<removeLn1.size();i++){
				optimizeDB(projDB, removeLn1.get(i), 1);
			}
			for(int i=0;i<removeLn2.size();i++){
				optimizeDB(projDB, removeLn2.get(i), 2);
			}
			
			runLn1(prefix, subLn1, projDB);
			runLn2(prefix, subLn2, projDB);
		}
	}
	
	static List<List<Integer>> List2Dcopy(List<List<Integer>> old){
		List<List<Integer>> newlist = new ArrayList<List<Integer>>();
		for(int i=0;i<old.size();i++){
			List<Integer> templist1 = new ArrayList<Integer>();
			for(int j=0;j<old.get(i).size();j++){
				templist1.add(old.get(i).get(j));
			}
			newlist.add(templist1);
		}
		return newlist;
	}
	
	static void runMainProjDB(){
		int counter;
		boolean flag;
		
		for(int x=0;x<mainL1.size();x++){
			List<List<List<Integer>>> projDB = new ArrayList<List<List<Integer>>>();
			List<candidate> subLn1 = new ArrayList<candidate>(); /* Ex : a */
			List<candidate> subLn2 = new ArrayList<candidate>(); /* Ex : _a */
			List<Integer> removeLn1 = new ArrayList<Integer>(); /* Ex : a */
			List<Integer> removeLn2 = new ArrayList<Integer>(); /* Ex : _a */
			List<List<Integer>> prefix = new ArrayList<List<Integer>>();
			List<Integer> prefix2 = new ArrayList<Integer>(); /* prefix2 is 1D List which is inside 2D List prefix */
			prefix2.add(mainL1.get(x).itemset.get(0));
			prefix.add(prefix2);
			
			for(int i=0;i<dataset.size();i++){
				flag = false;
				for(int j=0;j<dataset.get(i).size();j++){
					if(flag){
						break;
					}
					for(int k=0;k<dataset.get(i).get(j).size();k++){
						if(dataset.get(i).get(j).get(k).equals(mainL1.get(x).itemset.get(0))){
							flag = true;
							List<List<Integer>> newSequence = new ArrayList<List<Integer>>();
							if((k+1) < dataset.get(i).get(j).size()){
								List<Integer> seq1 = new ArrayList<Integer>();
								seq1.add(-1);
								for(int m=k+1;m<dataset.get(i).get(j).size();m++){
									seq1.add(dataset.get(i).get(j).get(m));
								}
								newSequence.add(seq1);
							}
							for(int m=j+1;m<dataset.get(i).size();m++){
								List<Integer> seq1 = new ArrayList<Integer>(dataset.get(i).get(m));
								newSequence.add(seq1);
							}
							projDB.add(newSequence);
							break;
						}
					}
				}
			}
			
			// case1 : a
			for(int y=0;y<mainL1.size();y++){
				counter = 0;
				for(int i=0;i<projDB.size();i++){
					flag = false;
					for(int j=0;j<projDB.get(i).size();j++){
						if(flag){
							break;
						}
						for(int k=0;k<projDB.get(i).get(j).size();k++){
							if(projDB.get(i).get(j).get(k).equals(mainL1.get(y).itemset.get(0))){
								if(projDB.get(i).get(j).get(0) == -1){
									break;
								}
								counter++;
								flag = true;
								break;
							}
						}
					}
				}
				if(counter >= num_min_sup){
					candidate candidate1;
					List<Integer> list1 = new ArrayList<Integer>();
					list1.add(mainL1.get(y).itemset.get(0));
					candidate1 = new candidate(counter, list1);
					subLn1.add(candidate1);
				}
				else{
					removeLn1.add(mainL1.get(y).itemset.get(0));
				}
			}
			outputResult(subLn1, prefix, 1);
			
			// case2 : _a
			for(int y=0;y<mainL1.size();y++){
				counter = 0;
				for(int i=0;i<projDB.size();i++){
					flag = false;
					for(int j=0;j<projDB.get(i).size();j++){
						if(flag){
							break;
						}
						for(int k=0;k<projDB.get(i).get(j).size();k++){
							if(projDB.get(i).get(j).get(k).equals(mainL1.get(y).itemset.get(0))){
								if(k != 0){
									if(projDB.get(i).get(j).get(0) == -1){
										counter++;
										flag = true;
										break;
									}
									if(prefix.get(prefix.size()-1).size() <= k){
										boolean check = true;
										List<Integer> subList = projDB.get(i).get(j).subList(0, k);
										for(int m=0;m<prefix.get(prefix.size()-1).size();m++){
											int index = subList.indexOf(prefix.get(prefix.size()-1).get(m));
											if(index == -1){
												check = false;
												break;
											}
										}
										if(check){
											counter++;
											flag = true;
											break;
										}
									}
									
								}
								break;
							}
						}
					}
				}
				if(counter >= num_min_sup){
					candidate candidate1;
					List<Integer> list1 = new ArrayList<Integer>();
					list1.add(mainL1.get(y).itemset.get(0));
					candidate1 = new candidate(counter, list1);
					subLn2.add(candidate1);
				}
				else{
					removeLn2.add(mainL1.get(y).itemset.get(0));
				}
			}
			outputResult(subLn2, prefix, 2);
			
			for(int i=0;i<removeLn1.size();i++){
				optimizeDB(projDB, removeLn1.get(i), 1);
			}
			
			for(int i=0;i<removeLn2.size();i++){
				optimizeDB(projDB, removeLn2.get(i), 2);
			}
			
			runLn1(prefix, subLn1, projDB);
			runLn2(prefix, subLn2, projDB);
		}
	}
	
	/* optimize projected database by eliminate infrequent symbols */
	static void optimizeDB(List<List<List<Integer>>> DB, int infreq, int choice){
		/*if(choice == 1){
			for(int i=0;i<DB.size();i++){
				for(int j=0;j<DB.get(i).size();j++){
					if(DB.get(i).get(j).size() > 0 && DB.get(i).get(j).get(0) == -1){
						continue;
					}
					DB.get(i).get(j).remove(new Integer(infreq));
					for(int k=0;k<DB.get(i).get(j).size();k++){
						if(DB.get(i).get(j).get(k).equals(infreq)){
							if(k!=0 && DB.get(i).get(j).get(k-1) == -1){
								break;
							}
							DB.get(i).get(j).remove(k);
							k--;
						}
					}
				}
			}
		}
		else{
			for(int i=0;i<DB.size();i++){
				for(int j=0;j<DB.get(i).size();j++){
					for(int k=0;k<DB.get(i).get(j).size();k++){
						if(DB.get(i).get(j).get(k).equals(infreq)){
							if(k!=0 && DB.get(i).get(j).get(0) == -1){
								DB.get(i).get(j).remove(k);
								k--;
							}
						}
					}
				}
			}
		}*/
	}
	
	static void runMainL1(){
		int counter;
		boolean flag; /* check if the sequence has find the identical item already */
		
		for(int x=1;x<=MaxNum;x++){
			counter = 0;
			for(int i=0;i<dataset.size();i++){
				flag = false;
				for(int j=0;j<dataset.get(i).size();j++){
					if(flag){
						break;
					}
					for(int k=0;k<dataset.get(i).get(j).size();k++){
						if(dataset.get(i).get(j).get(k).equals(x)){
							counter++;
							flag = true;
							break;
						}
					}
				}
			}
			if(counter >= num_min_sup){
				candidate candidate1;
				List<Integer> list1 = new ArrayList<Integer>();
				list1.add(x);
				candidate1 = new candidate(counter, list1);
				mainL1.add(candidate1);
			}
			else{
				if(counter!=0){
					optimizeDB(dataset, x, 1);
				}
			}
		}
		outputResult(mainL1, null, 1); 
		
		
	}
	
	// third parameter distinguish different case, case 1 : a, case2 : _a
	static void outputResult(List<candidate> can, List<List<Integer>> prefix, int choice){
		if(prefix == null){
			for(int i=0;i<can.size();i++){
				numFreSeq++;
				output.print("(");
				for(int j=0;j<can.get(i).itemset.size();j++){
					output.print(can.get(i).itemset.get(j));
				}
				output.println(") : " + can.get(i).support);
			}
		}
		else{
			if(choice == 1){
				for(int i=0;i<can.size();i++){
					numFreSeq++;
					output.print("(");
					for(int k=0;k<prefix.size();k++){
						output.print(prefix.get(k));
					}
					output.print(can.get(i).itemset);
					output.println(") : " + can.get(i).support);
				}
			}
			else{
				for(int i=0;i<can.size();i++){
					numFreSeq++;
					output.print("(");
					for(int k=0;k<prefix.size()-1;k++){
						output.print(prefix.get(k));
					}
					List<Integer> temp = new ArrayList<Integer>(prefix.get(prefix.size()-1));
					for(int j=0;j<can.get(i).itemset.size();j++){
						temp.add(can.get(i).itemset.get(j));
					}
					output.print(temp);
					output.println(") : " + can.get(i).support);
				}
			}
		}
	}
	
	static void readFile(String filename){
		BufferedReader br;
		String line = ""; /* receive what reader read */
		String[] tokens; /* used to receive the result of String.split */
		List<List<Integer>> transaction;
		List<Integer> list;
		int nowIndex;

		try {
			br = new java.io.BufferedReader(new java.io.FileReader(filename));
			while(true)
		    {
		        line = br.readLine();
		        if(line == null)
		           break;
		        //System.out.println(line);
		        tokens = line.split(" ");
		        transaction = new ArrayList<List<Integer>>();
		        nowIndex = 0;
		        while(nowIndex < tokens.length){
		        	if(tokens[nowIndex].equals("-1")){
		        		break;
		        	}
		        	if(tokens[nowIndex].equals("(")){
		        		list = new ArrayList<Integer>();
		        		nowIndex++;
		        		while(!tokens[nowIndex].equals(")")){
		        			list.add(new Integer(Integer.parseInt(tokens[nowIndex])));
		        			nowIndex++;
		        		}
		        		nowIndex++;
		        		transaction.add(list);
		        	}
		        }
		        dataset.add(transaction);
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
}
