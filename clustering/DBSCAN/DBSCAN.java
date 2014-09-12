/* Summary : Implementation of DBSCAN Algorithm (clustering)
 * Last modified : 2014 / 09 / 12
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;


public class DBSCAN {
	
	public static final String file1 = "1.3_Clustering.txt";
	public static final String file2 = "2.3_Clustering.txt";
	public static double radius = 100.0; 
	public static int Eps = 1; 
	
	static List<point> points = new ArrayList<point>();
	static PrintWriter output = null;
	static long max_heap, temp_heap;
	static int pointDimension = 0;
	
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader (new InputStreamReader(System.in)); /* user input */
		String line = ""; /* receive what reader read */
		String filename = ""; /* filename of dataset */
		long start_time, finish_time;
		
		/* select file to run */
		System.out.println("(1)1.3_Clustering\n(2)2.3_Clustering");
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
		else{
			System.out.println("Please input correct number. ( 1 or 2 )");
			System.exit(0);
		}
		
		/* input minimal radius*/
		System.out.println("Select the radius : ");
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		radius = Double.parseDouble(line);
		
		/* input minimal radius*/
		System.out.println("Select the Eps : ");
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Eps = Integer.parseInt(line);

		/* record start time and used heap */
		start_time = System.currentTimeMillis();
		max_heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		
		try {
			output = new PrintWriter("result.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		pointDimension = readFile(filename); // return the dimension of point
		classification(); // classify the point is core, border or noise point
		clustering();
		
		temp_heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		if(temp_heap > max_heap){
			max_heap = temp_heap;
		}
		
		output.close();
		finish_time = System.currentTimeMillis();
		System.out.println("*****");
		System.out.println("total time : " + (finish_time-start_time)/1000.0 + " seconds");
		System.out.println("max memory : " + max_heap/1048576.0 + "(mb)");
		System.out.println("*****");
	}
	
	static void clustering(){
		int nowLabel = 0;
		for(int i=0;i<points.size();i++){
			if(points.get(i).attr == 1){
				if(points.get(i).label == -1){
					nowLabel++;
					points.get(i).label = nowLabel;
					output.println("[cluster" + nowLabel + "]");
					outputPoint(points.get(i));
					for(int j=0;j<points.get(i).neighbor.size();j++){
						if(points.get(points.get(i).neighbor.get(j)).label == -1){
							outputPoint(points.get(points.get(i).neighbor.get(j)));
							points.get(points.get(i).neighbor.get(j)).label = nowLabel;
						}
					}
				}
			}
		}
		
	}
	
	static void outputPoint(point p1){
		output.print(p1.ID + " ");
		for(int i=0;i<pointDimension;i++){
			output.print(p1.pos[i] + " ");
		}
		output.println();
	}
	
	static void classification(){
		int N = points.size();
		boolean flag;
		
		for(int i=0;i<N;i++){
			for(int j=0;j<N;j++){
				if(i==j){
					continue;
				}
				if((double)distance(points.get(i), points.get(j), pointDimension) <= radius){
					points.get(i).neighbor.add(j);
				}
			}
			if(points.get(i).neighbor.size()+1 >= Eps){
				points.get(i).attr = 1;
			}
		}
		
		for(int i=0;i<N;i++){
			if(points.get(i).attr == 1){
				continue;
			}
			flag = false;
			for(int j=0;j<points.get(i).neighbor.size();j++){
				if(points.get(points.get(i).neighbor.get(j)).attr == 1){
					flag = true;
					break;
				}
			}
			if(flag){
				points.get(i).attr = 2;
			}
			else{
				points.get(i).attr = 3;
			}
		}
	}
	
	static double distance(point p1, point p2, int Nco){
		double counter = 0.0;
		int sub;
		
		for(int i=0;i<Nco;i++){
			sub = p1.pos[i] - p2.pos[i];
			counter += sub * sub;
		}
		
		return Math.sqrt(counter);
	}
	
	static int readFile(String filename){
		BufferedReader br;
		String line = ""; /* receive what reader read */
		String[] tokens; /* used to receive the result of String.split */
		int nCoordinate = 0;

		try {
			br = new java.io.BufferedReader(new java.io.FileReader(filename));
			
			line = br.readLine();
			tokens = line.split(", ");
			nCoordinate = tokens.length - 1;
			line = br.readLine();
			
			while(true)
		    {
		        line = br.readLine();
		        if(line == null)
		           break;
		        //System.out.println(line);
		        tokens = line.split(", ");
		        if(nCoordinate == 6){
		        	point newPoint = new point(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]));
		        	points.add(newPoint);
		        	continue;
		        }
		        if(nCoordinate == 4){
		        	point newPoint = new point(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]), 0, 0);
		        	points.add(newPoint);
		        	continue;
		        }
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nCoordinate;
	}

}

class point {
	int[] pos = new int[6];
	int attr; // 0:default; 1:core; 2:border; 3:noise;
	List<Integer> neighbor;
	int label;
	int ID;
	
	public point(int id, int x1, int x2, int x3, int x4, int x5, int x6) {
		ID = id;
		label = -1;
		attr = 0;
		neighbor = new ArrayList<Integer>();
		pos[0] = x1;
		pos[1] = x2;
		pos[2] = x3;
		pos[3] = x4;
		pos[4] = x5;
		pos[5] = x6;
	}
}
