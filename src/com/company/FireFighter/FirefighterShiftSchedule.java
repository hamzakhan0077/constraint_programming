package com.company.FireFighter;
import org.chocosolver.solver.*;
import org.chocosolver.solver.search.strategy.Search;
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
        System.out.println("\n"+ "Minimum number of firefighters required  : "+ Arrays.toString(shiftMinimum)+"\n"+
                "Qualifications Required  : "+ Arrays.deepToString(qualsRequired)+"\n"+
                "Qualified Firefighter   : "+ Arrays.deepToString(qualfiedFirefighter)+"\n"+
                "Types of Qualifications : " + numQualification +"\n");
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

        IntVar[][] firefighterShiftGrid = model.intVarMatrix("Grid",  numShifts,  numFirefighter,  0,1 );

        // rows Firefighters
        // Cols Shifts
        IntVar [] qualfiedWorkingff = model.intVarArray(numFirefighter,0,1); //
        for( int j = 0; j == numShifts; j++){
            // Sum of each column of the Grid Must be equal to the minimum number in the min Firefighters required
//
//            System.out.println("-----------------------------------------------");
//            System.out.println(Arrays.toString(ArrayUtils.getColumn(firefighterShiftGrid, j)));
//            System.out.println("-----------------------------------------------");


            model.sum(ArrayUtils.getColumn(firefighterShiftGrid, j), ">=", shiftMinimum[j]).post();

            for (int f = 0;  f < numFirefighter; f++) {
                model.sum(firefighterShiftGrid[f],">=", shiftMinimum[f]).post();

                for (int numq = 0; numq == numQualification; numq++) {
                    // Assuming getNumQualification are the qualifications available
                    // i.e q0 q1 q2 ---- see the array of Qualified FF - Q0 [ firefighter with   qualification 0]
                    // only one array --> There is only one Qualification Q0


                    // Qualified firefighters array [F0, F1]
                    // Shit N - [1, 0] // firefighter 0 is working and firefighter 1 is not
                    // Compare that against the qualified array to see if the have the qualification and they are working
                    model.arithm(qualfiedWorkingff[f],"=",firefighterShiftGrid[numShifts][f] , "*", qualfiedFirefighter[numq][f]).post();

                    model.sum(qualfiedWorkingff,">=",qualsRequired[numq][j]).post();



                }
            }






        }
//        for( int f = 0; f < numFirefighter; f++){
//            model.sum(firefighterShiftGrid[f],">=", shiftMinimum[f]).post();
//
//
//
//        }


        Solver solver = model.getSolver();

        // total cost ?? --
        IntVar[] searchable = ArrayUtils.flatten(firefighterShiftGrid);
        solver.setSearch(Search.domOverWDegSearch(searchable));

        Solution solution = new Solution(model);
        while (solver.solve()) {

            solution.record();


//            System.out.println(solver.getSolutionCount());
//            System.out.println(Arrays.deepToString(firefighterShiftGrid));
        } // while loop



        solver.printStatistics();
















    }
}

