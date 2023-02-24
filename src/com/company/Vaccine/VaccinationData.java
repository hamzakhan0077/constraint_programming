package com.company.Vaccine;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class VaccinationData {
	
	private int c; //number of centres
	private int d; //number of days
	private int[] supplies; //the total number of vaccines available each day (must be length = d)
	private int[] reqs; //the required vaccines for each centre (must be length = c)
	private int[][] minReq; //the minimum vaccines required, per centre, per day (must be size c x d)
	
    public VaccinationData(String filename) throws IOException {
    	/*
    	 * Assumes data is in file in the following format:
    	 *    number-of-centres  number-of-days
    	 *    a sequence of ints of length number-of-days, representing total vaccines available by day
    	 *    a sequence of ints of length number-of-centres, representing total vaccines required by each centre
    	 * and then a sequence of number-of-centres lines, where each line is
    	 *    a sequence of ints of length  number of days, representing the minimum vaccines for a centre each day
    	 */
	    Scanner scanner = new Scanner(new File(filename));
	    c = scanner.nextInt();
	    d = scanner.nextInt();
	    supplies = new int[d];
	    for (int day = 0; day < d; day++) {
	    	supplies[day] = scanner.nextInt();
	    }
	    reqs = new int[c];
	    for (int centre = 0; centre < c; centre++) {
	    	reqs[centre] = scanner.nextInt();
	    }
	    minReq = new int[c][d];
	    int temp = 0;
	    for (int i=0;i<c;i++){
	    	for (int j = 0; j<d; j++) {
	    		temp = scanner.nextInt();
//	    		System.out.println(temp);
	    		minReq[i][j] = temp;
	    	}
	    }
	    scanner.close();
    }
    
    public int getNumCentres() {
    	return c;
    }
    
    public int getNumDays() {
    	return d;
    }
    
    public int[] getSupplies() {
    	return supplies;
    }
    
    public int[] getReqs() {
    	return reqs;
    }
    
    public int[][] getMinReqs() {
    	return minReq;
    }
    
}
