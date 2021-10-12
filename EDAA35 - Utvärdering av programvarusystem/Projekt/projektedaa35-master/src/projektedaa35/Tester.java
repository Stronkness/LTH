package projektedaa35;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Tester {
	private LinkedList<Integer> ls;
	private TreeSet<Integer> ts;
	private File file;
	private boolean header;
	private int N;
	
	public Tester(int N, File file, boolean header) {
		 ls = new LinkedList<Integer>();
		 ts = new TreeSet<Integer>();
		 
		 
		 this.file = file;
		 this.N = N;
		 this.header = header;
	}
	
	// söker upp ETT element som finns i filen N gånger. -> O(n) så klarar stor indata och stora N 
	public void testSearching() throws IOException {
		fillValues();
		
		ArrayList<Long> listRes = new ArrayList<Long>();
		ArrayList<Long> treeRes = new ArrayList<Long>();
				
		long startTime;
		long stopTime;
		long res; 
		
		ArrayList<Integer> randNums = new ArrayList<Integer>();
		
		for (int i = 0; i < ls.size(); i++) {
			int randIndex = (int) ((Math.random())*ls.size()); 
			int randNum = ls.get(randIndex);
			randNums.add(randNum);
		}
		
		for (int i = 0; i < N; i++) {
			
			startTime = System.nanoTime();	
			// använder indexoff här för det är den form av sökning linkedlist har
			for(int randNum : randNums) {
				ls.indexOf(randNum);
			}
			
	    	stopTime = System.nanoTime();
	    	res = (stopTime - startTime) ;
	    	
	    	listRes.add(res);
	    	
	    	// run tests for tree
	    	startTime = System.nanoTime();
	    	for(int randNum : randNums) {
				ts.contains(randNum);
			}
	    	
	    	stopTime = System.nanoTime();
	    	res = (stopTime - startTime) ;
	    	
	    	treeRes.add(res);
		}
		
		
		writeToOutFile("treesearching" + N + ".txt", treeRes);
		writeToOutFile("listsearching" + N + ".txt", listRes);
		
		clearValues();
	}
	
	// lägger till alla element till listan N gånger -> O(N * antalet fil element) -> seg tidskomplexitet för stora N 
	public void testAdding() throws IOException {
		ArrayList<Long> listRes = new ArrayList<Long>();
		ArrayList<Long> treeRes = new ArrayList<Long>();
				
		long startTime;
		long stopTime;
		long res; 
		
		ArrayList<Integer> fileNumbers = new ArrayList<Integer>();
		
		Scanner fileScanner = new Scanner(file);
		while (fileScanner.hasNextLine()) {
			String data = fileScanner.nextLine();
		    fileNumbers.add(Integer.parseInt(data));
		}	
		fileScanner.close();
		
		for (int i = 0; i < N; i++) {
			LinkedList<Integer> tempList = new LinkedList<Integer>();
			TreeSet<Integer> tempTree = new TreeSet<Integer>();
			
			startTime = System.nanoTime();	
			
			for(int num : fileNumbers) {
				tempList.add(num);
			}
			
			
	    	stopTime = System.nanoTime();
	    	res = (stopTime - startTime) ;
	    	
	    	listRes.add(res);
	    	
	    	startTime = System.nanoTime();
	    	
	    	for(int num : fileNumbers) {
				tempTree.add(num);
			}
	    	
	    	stopTime = System.nanoTime();
	    	res = (stopTime - startTime) ;
	    	
	    	treeRes.add(res);
		}
		
		
		writeToOutFile("treeadding" + N + ".txt", treeRes);
		writeToOutFile("listadding" + N + ".txt", listRes);
	}
	
	
	private void writeToOutFile(String name, ArrayList<Long> res) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(name));
		
		if (header == true) {
			writer.write("index tid(mikrosekunder)");
			writer.newLine();
		}
		
		for (int i = 0; i < res.size(); i++) {
			writer.write(i + "," + Long.toString(res.get(i)));
			writer.newLine();
		}
	    writer.close();
	}
	

	private void fillValues() throws IOException  {
		Scanner fileScanner = new Scanner(file);
	    
	    while (fileScanner.hasNextLine()) {
	      String data = fileScanner.nextLine();
	      ls.add(Integer.parseInt(data));
	      ts.add(Integer.parseInt(data));
	    }	
	    fileScanner.close();
	}
	
	private void clearValues() {
		ls.clear();
		ts.clear();
	}
	
	

}
