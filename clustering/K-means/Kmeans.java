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

	static List<List<point>> clusterResult;
	static List<point> points = new ArrayList<point>();
	static List<point> centroids = new ArrayList<point>();
	static List<point> newCentroids = new ArrayList<point>();
	static PrintWriter output = null;
	static int Nclusters;
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
		
		System.out.println("Specify the number of clusters K : ");
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Nclusters = Integer.parseInt(line);
		
		/* record start time*/
		start_time = System.currentTimeMillis();

		try {
			output = new PrintWriter("result.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// read file and return the dimension of point
		pointDimension = readFile(filename);
		
		// check if it might get error
		if(Nclusters > points.size()){
			System.out.println("number of clusters K must smaller or equal to the number of points in the file" + filename);
			System.exit(0);
		}
		
		// select K points randomly as the initial centroids
		SelectPoints();
		
		// start clustering
		clustering();
		
		// output result clustering
		outputCluster();
		
		output.close();
		finish_time = System.currentTimeMillis();
		System.out.println("total time : " + (finish_time-start_time)/1000.0 + " seconds");
	}
	
	static void outputCluster(){
		for(int i=0;i<clusterResult.size();i++){
			output.println("[cluster" + (i+1) + "]");
			for(int j=0;j<clusterResult.get(i).size();j++){
				outputPoint(clusterResult.get(i).get(j));
			}
		}
	}
	
	static void outputPoint(point p1){
		output.print(p1.ID + ", ");
		for(int i=0;i<pointDimension;i++){
			output.print(p1.pos[i] + " ");
		}
		output.println();
	}
	
	static void clustering(){
		int count = 1;
		int index;
		double dis;
		double[] sum = new double[6];
		boolean flag;
		
		while(true){
			System.out.println("round " + count);
			count++;
			
			clusterResult = new ArrayList<List<point>>();
			for(int i=0;i<centroids.size();i++){
				clusterResult.add(new ArrayList<point>());
			}
			for(int i=0;i<points.size();i++){
				index = 0;
				dis = distance(points.get(i), centroids.get(0));
				for(int j=1;j<centroids.size();j++){
					if(distance(points.get(i), centroids.get(j)) < dis){
						index = j;
						dis = distance(points.get(i), centroids.get(j));
					}
				}
				clusterResult.get(index).add(points.get(i));
			}
			
			
			// calculate new centroid
			newCentroids.clear();
			for(int i=0;i<centroids.size();i++){
				sum[0] = sum[1] = sum[2] = sum[3] = sum[4] = sum[5] = 0.0;
				for(int j=0;j<clusterResult.get(i).size();j++){
					sum[0] += clusterResult.get(i).get(j).pos[0];
					sum[1] += clusterResult.get(i).get(j).pos[1];
					sum[2] += clusterResult.get(i).get(j).pos[2];
					sum[3] += clusterResult.get(i).get(j).pos[3];
					sum[4] += clusterResult.get(i).get(j).pos[4];
					sum[5] += clusterResult.get(i).get(j).pos[5];
				}
				newCentroids.add(new point(0, 
						(int)(sum[0]/clusterResult.get(i).size()), 
						(int)(sum[1]/clusterResult.get(i).size()), 
						(int)(sum[2]/clusterResult.get(i).size()), 
						(int)(sum[3]/clusterResult.get(i).size()), 
						(int)(sum[4]/clusterResult.get(i).size()), 
						(int)(sum[5]/clusterResult.get(i).size()))
				);
			}
			
			// check if the centroid is almost unchanged
			flag = true;
			for(int i=0;i<centroids.size();i++){
				if(distance(centroids.get(i), newCentroids.get(i)) > 5.0){
					flag = false;
					break;
				}
			}
			if(flag){
				break;
			}
			else{
				centroids = new ArrayList<point>();
				for(int i=0;i<newCentroids.size();i++){
					centroids.add(newCentroids.get(i));
				}
			}
			
		}
	}
	
	static double distance(point p1, point p2){
		double counter = 0.0;
		int sub;
		
		for(int i=0;i<pointDimension;i++){
			sub = p1.pos[i] - p2.pos[i];
			counter += sub * sub;
		}
		
		return Math.sqrt(counter);
	}
	
	static void SelectPoints(){
		List<Integer> selectID = new ArrayList<Integer>();
		int nowN = points.size();
		int getNumber;
		
		for(int i=0;i<nowN;i++){
			selectID.add(i);
		}
		
		for(int i=0;i<Nclusters;i++){
			getNumber = selectID.remove((int)(Math.random()*nowN));
			centroids.add(new point(0, points.get(getNumber).pos[0], points.get(getNumber).pos[1], points.get(getNumber).pos[2], points.get(getNumber).pos[3], points.get(getNumber).pos[4], points.get(getNumber).pos[5]));
			nowN--;
		}
		
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
			br.close();
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