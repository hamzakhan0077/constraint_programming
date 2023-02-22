package com.company.FireFighter;
import org.chocosolver.solver.ISolver;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;


import org.chocosolver.solver.Model;

import java.io.IOException;

public class FirefighterShiftSchedule {
    public static void main(String[] args) throws IOException {
        Model model = new Model("Firefighter Shift Scheduler");
        FireBrigadeData reader = new FireBrigadeData("src/com/company/FireFighter/fire0.txt");

        int numShifts = reader.getNumShifts();//returns the number of shifts in the problem
        int numFirefighter = reader.getNumFirefighters();//returns the number of firefighters to be scheduled
        int numQualification = reader.getNumQualifications();//returns the number of qualifications that may be required
        int maxConsecutive = reader.getMaxConsecutive(); //returns the maximum number of consecutive shifts allowed to be worked
        int minBreak = reader.getMinBreak(); //returns the minimum number of consecutive shifts off between any two work blocks
        int minLongerBreak = reader.getMinLongerBreak();//returns the minimum period of consecutive shifts off that must appear at least once
        int minWork = reader.getMinWork();//returns the minimum number of shifts to be assigned to each firefighter
        int[] shiftMinimum = reader.getShiftMinimum(); //returns an array with the minimum number of firefighters required for each shift
        int[][] qualsRequired = reader.getQualsRequired();//returns a 2d matrix (num quals x num shifts), where at least matrix[i][j] firefighters with qualification i must be scheduled in shift j
        int[][] qualfiedFirefighter = reader.getQualifiedFirefighters(); //returns a 2d matrix (num quals x num fighters), where matrix[i][j] means qualification i is held by firefighter j
        System.out.println("\n"+  Arrays.toString(shiftMinimum));
//
//        System.out.println("Total Number of Shifts : "+ numShifts + "\n" +
//                "Total Number of Firefighters "+ numFirefighter + "\n" +
//                "Types of Qualifications : " + numQualification +"\n"+
//                "Maximum number of Consecutive shifts allowed : "+ maxConsecutive+"\n"+
//                "Minimum number of consecutive shifts off : "+ minBreak+"\n"+
//                "Minimum period of consecuetive shifts that must appear  : "+ minLongerBreak+"\n"+
//                "Minimum number of shifts to be assigned to each firefighter  : "+ minWork+"\n"+
//                "Minimum number of firefighters required  : "+ Arrays.toString(shiftMinimum)+"\n"+
//                "Qualifications Required  : "+ Arrays.deepToString(qualsRequired)+"\n"+
//                "Qualified Firefighter   : "+ Arrays.deepToString(qualfiedFirefighter)+"\n"
//                );
        System.out.println(minWork);


        IntVar[][] firefighterShiftGrid = model.intVarMatrix("Grid",  numShifts,  numFirefighter,  0,maxConsecutive );
        // Row - Firefighter
        // Col - Shifts


        // For each row
        for( int j = 0; j < numShifts; j++){


            // Sum of each column of the Grid Must be equal to the minimum number in the min Firefighters required
            model.sum(ArrayUtils.getColumn(firefighterShiftGrid,j),">=", shiftMinimum[j]).post();

        }
        // we want to run this for each row - i.e. For each firefighter
        for( int j = 0; j < numShifts; j++){
        // minimum number of times that firefighter must work
        // is  Sum or rows (Firefightrs total shift)  >=  minimum shift
        model.sum(firefighterShiftGrid[],">=", minWork).post();


        }
//        Solver solver = model.getSolver();
//        while (solver.solve()) {
//            System.out.println(solver.getSolutionCount());
//        } // while loop
//        solver.printStatistics();
















    }
}

