package com.company.FireFighter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class FireBrigadeData {
	
	private int s; //number of shifts
	private int f; //number of firefighters
	private int q; //number of qualifications
	private int m; //max number of consecutive shifts working
	private int b; //min number of shifts off between work periods
	private int l; //min number of shifts in the longer break
	private int w; //min number of shifts worked over the whole period for any firefighter
	private int[] shiftMinimum; //the min number of firefighters for each shift (must be length = s)
	private int[][] qualsRequired; //the qualifications required for each shift (must be size q x s)
	private int[][] qualifiedFirefighters; //the qualifications earned by each firefighter (must be size q x f)
	
    public FireBrigadeData(String filename) throws IOException {
    	/*
    	 * Assumes data is in file in the following format:
    	 *    number-of-shifts  number-of-firefighters    number-of-qualifications
    	 *    max-consecutive   min-break    min_longer-break    min-work
    	 *    a sequence of ints of length number-of-shifts, representing required number of firefighters for each shift
    	 *    a sequence of ints of length number-of-centres, representing total vaccines required by each centre
    	 * and then a sequence of number-of-qualifications lines, where each line is
    	 *    a sequence of ints of length  number-of-shifts, representing the shifts that required (1) that qualification
    	 * and then a sequence of number-of-qualifications lines, where each line is
    	 *    a sequence of ints of length number-of-firefighters, representing the firefighters that have (1) that qual.
    	 */
	    Scanner scanner = new Scanner(new File(filename));
	    s = scanner.nextInt();
	    f = scanner.nextInt();
	    q = scanner.nextInt();
	    m = scanner.nextInt();
	    b = scanner.nextInt();
	    l = scanner.nextInt();
	    w = scanner.nextInt();
	    System.out.println("" + s + " shifts; " + f + " firefighters; " + q + " qualifications");
	    System.out.println("" + m + " maximum block; " + b + " minimum break; " + l + " minimum longer break; " + w + " minimum shifts per firefighter");
	    shiftMinimum = new int[s];
	    System.out.println("Minimum firefighters per shift:");
	    for (int shift = 0; shift < s; shift++) {
	    	shiftMinimum[shift] = scanner.nextInt();
	    	System.out.print("" + shift + ": " + shiftMinimum[shift] + "; ");
	    }
	    System.out.println();
	    qualsRequired = new int[q][s];
	    int temp = 0;
	    System.out.println("Qualification requirements x shifts:");
	    for (int i=0;i<q;i++){
		    System.out.print("" + i + ": ");
	    	for (int j = 0; j<s; j++) {
	    		temp = scanner.nextInt();
	    		System.out.print(temp + " ");
	    		qualsRequired[i][j] = temp;
	    	}
	    	System.out.println();
	    }
	    qualifiedFirefighters = new int[q][f];
	    temp = 0;
	    System.out.println("Qualifications held x firefighters:");
	    for (int i=0;i<q;i++){
		    System.out.print("" + i + ": ");
	    	for (int j = 0; j<f; j++) {
	    		temp = scanner.nextInt();
	    		System.out.print(temp + " ");
	    		qualifiedFirefighters[i][j] = temp;
	    	}
	    	System.out.println();
	    }
	    scanner.close();	    
    }
    
    public int getNumShifts() {
    	return s;
    }
    
    public int getNumFirefighters() {
    	return f;
    }
    
    public int getNumQualifications() {
    	return q;
    }
    
    public int getMaxConsecutive() {
    	return m;
    }
    
    public int getMinBreak() {
    	return b;
    }
    
    public int getMinLongerBreak() {
    	return l;
    }
    
    public int getMinWork() {
    	return w;
    }
    
    public int[] getShiftMinimum() {
    	return shiftMinimum;
    }
    
    public int[][] getQualsRequired() {
    	return qualsRequired;
    }
    
    public int[][] getQualifiedFirefighters() {
    	return qualifiedFirefighters;
    }
    
}
