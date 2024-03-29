package com.company.FireFighter;
import org.chocosolver.solver.*;
import org.chocosolver.solver.constraints.nary.automata.FA.FiniteAutomaton;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;


import org.chocosolver.solver.Model;

public class FireBrigadeSchedule {
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

        IntVar sumOfAllShifts = model.intVar(0,numShifts*numFirefighter);
        IntVar [] sumOfCol = model.intVarArray(numShifts,0,numShifts);

        // Fix Regex SEE Notes
        String ones = "1";
        String zero = "0";
//        String consecOnes = new String(new char[maxConsecutive]).replace("\0", ones);
//
//        String consecZeros = new String(new char[minLongerBreak]).replace("\0", zero);
        String consecOnes = Integer.toString(maxConsecutive);

        String consecZeros = Integer.toString(minLongerBreak);


//        String regexStr = String.format("%s*1|%s*0", consecZeros, consecOnes);

        // Square ---> One period with tow shifts of  -- Two 00 must be present
        // Star ---> No More than 2 working shifts in a row so only 11 two ones in a row
        // Triangle --> Atleast one shift off between work blocks one 0 after 11

//        String regexSquareStr = String.format("(%s)1+", consecZeros);
//        String regexStarStr = String.format("(%s)0+", consecOnes);
//        String regexTriangleStr = "(0|1)*";
        String regexSquareStr = String.format("(0|1)*1{%s}0(0|1)*", consecZeros);
        String regexStarStr = String.format("(0|1)*0{%s}1(1|0)*", consecOnes);
        String regexTriangleStr = "(0|1)*";
//        String regexSquareStr = String.format("(00|1)+(0{%s})[^0]", consecZeros);
//        String regexStarStr = String.format("(11|0)+(1{%s})[^1]", consecOnes);
//        String regexTriangleStr = "(0|1)*";



        FiniteAutomaton regexpSquare = new FiniteAutomaton(regexSquareStr);
        FiniteAutomaton regexpStar = new FiniteAutomaton(regexStarStr);
        FiniteAutomaton regexpTriangle = new FiniteAutomaton(regexTriangleStr);

        System.out.println("\n" + "Minimum number of firefighters required  : " + Arrays.toString(shiftMinimum) + "\n" +
                "Qualifications Required  : " + Arrays.deepToString(qualsRequired) + "\n" +
                "Qualified Firefighter   : " + Arrays.deepToString(qualfiedFirefighter) + "\n" +
                "Types of Qualifications : " + numQualification + "\n");
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


        /*
        default IntVar[][] intVarMatrix(int dim1,
                                int dim2,
                                int lb,
                                int ub,
                                boolean boundedDomain)
          name - prefix name of the variables to create. The variable in row i and col j will be named name[i][j]
          dim1 - number of rows in the matrix
          dim2 - number of columns in the matrix
            lb - initial domain lower bound of each variable
            ub - initial domain upper bound of each variable*/

        // Main Grid where Rows are Firefighter and Cols are Sifts, Domain {0,1}
        IntVar[][] firefighterShiftGrid = model.intVarMatrix("Grid", numFirefighter, numShifts, 0, 1);


        IntVar[][] QualifiedGrid = model.intVarMatrix("QualGrid",numQualification,numFirefighter ,0, 1);



        // rows Firefighters
        // Cols Shifts
        // going one loop per constraint to Improve readability


        for( int j = 0; j < numShifts; j++){
            // Sum of each column of the Grid Must be equal to the minimum number in the min Firefighters required
//            System.out.println(Arrays.toString(shiftMinimum));
//            System.out.println(Arrays.deepToString(firefighterShiftGrid));

            model.sum(ArrayUtils.getColumn(firefighterShiftGrid, j), ">=", shiftMinimum[j]).post();
        }

        for( int k = 0; k < numFirefighter; k++){
            model.sum(firefighterShiftGrid[k], ">=", minWork).post();
        }



        // need to find the way to do IntVar * Constants
        // Qualified FF for each Qual  * Vector of All the FF working for a specific shift
        // Times Constraint does a Variable * By a constant

        /*
        * Times
        *default Constraint times(IntVar X,
                         int Y,
                         IntVar Z)
        * Creates a multiplication constraint: X * Y = Z
        * Z - result variable
        *
        * Below three nested loops
        * Takes a shift (COLUMN of INTVARS from firefighterShitGrid)
        * For every Firefighter
        * Multiply with the row in the Qualified firefighter matrix
        * Put the result in the Qualified Grid
        * Qualified Grid will  have rows as Qualifications , Cols as FF
        * {0 if the working ff DONT have the qual, }
        * {1 if the do}
        * Sum of each of this row will be compared against the minimum Working Qualified ff required for that shift
        *
        * */

//----------------------------- USING model.times-----------------------------------------------------------------------
//        System.out.println("------------------------------------------USING model.times() ----------------------------------------");
//        for(int s = 0;s < numShifts; s++) {
//            IntVar[] tarCol = ArrayUtils.getColumn(firefighterShiftGrid, s);
//            for (int f = 0; f < tarCol.length; f++) {
//                for (int q = 0; q < numQualification; q++) {
//                    model.times(tarCol[f], qualfiedFirefighter[q][f], QualifiedGrid[q][f]).post();
//                }
//
//
//            }
//        }
        /*
         * For each shift
         * go through each qualifcation.
         * SUM of Each row in Qualified GRID (Qualification Q0,Q1 ....)
         * must be greater than or equal to the Constant number of qualified ff required for that shift
         * */
//        for(int shift = 0; shift < numShifts; shift++) {
//            for (int i = 0; i < numQualification; i++) {
//                model.sum(QualifiedGrid[i], ">=", qualsRequired[i][shift]).post();
//            }
//        }
//----------------------------------------------------------------------------------------------------------------------

//------------------------------------------USING SCALAR ----------------------------------------------------------------
        System.out.println("------------------------------------------USING SCALAR ----------------------------------------");
        // SCALAR MINIMIZES BETTER
        for(int s = 0;s < numShifts; s++){
            IntVar[] tarCol = ArrayUtils.getColumn(firefighterShiftGrid, s);
            for(int qq = 0; qq < numQualification ; qq++) {
                model.scalar(tarCol, qualfiedFirefighter[qq],">=",qualsRequired[qq][s]).post();
            }
        }
// ____________________________________________________________________________________________________________________


        for(int ff = 0; ff < numFirefighter; ff++){


            model.regular(firefighterShiftGrid[ff],regexpSquare ).post();
            model.regular(firefighterShiftGrid[ff],regexpStar ).post();
//            model.regular(firefighterShiftGrid[ff],regexpTriangle).post();

        }

        for(int c = 0; c < numShifts; c++){
            model.sum(ArrayUtils.getColumn(firefighterShiftGrid,c), "=",sumOfCol[c]).post();


        }
        model.sum(sumOfCol,"=",sumOfAllShifts).post();
    Solver solver = model.getSolver();
    model.setObjective(Model.MINIMIZE,sumOfAllShifts);

    // total cost ?? --
    IntVar[] searchable = ArrayUtils.flatten(firefighterShiftGrid);
        solver.setSearch(Search.domOverWDegSearch(searchable));
    Solution solution = new Solution(model);
        while(solver.solve())
    {
        solution.record();
//            System.out.println(solver.getSolutionCount());
//            System.out.println(Arrays.deepToString(firefighterShiftGrid));
        for(int i =0; i < numFirefighter; i++ ){
            for(int j=0; j < numShifts; j++){

                System.out.print(firefighterShiftGrid[i][j]+" ");


            }
            System.out.println("");

        }


    } // while loop

//        if (solver.getSolutionCount() > 0) {
//
//            for (int val = 0; val <= numShifts; val++) {
//                for (int f = 0; f <= numFirefighter; f++) {
//                    System.out.println("Objective value = " + solution.getIntVal(firefighterShiftGrid[val][f]));
//                }
//            }
//        }

        solver.printStatistics();
        System.out.println("\n ----------REGEX---------------- ");
        System.out.println(regexSquareStr);
        System.out.println(regexStarStr);
//        System.out.println(regexTriangleStr);
    }


}



