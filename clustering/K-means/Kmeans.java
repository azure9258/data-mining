/* Summary : Implementation of K-means Algorithm (clustering) 
 * Last modified : 2014 / 09 / 15
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class Kmeans {
	
	public static final String file1 = "1.3_Clustering.txt";
	public static final String file2 = "2.3_Clustering.txt";

	static List<point> points = new ArrayList<point>();
	static PrintWriter output = null;
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

		pointDimension = readFile(filename); // return the dimension of point
		
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
	int ID;
	
	public point(int id, int x1, int x2, int x3, int x4, int x5, int x6) {
		ID = id;
		pos[0] = x1;
		pos[1] = x2;
		pos[2] = x3;
		pos[3] = x4;
		pos[4] = x5;
		pos[5] = x6;
	}
}