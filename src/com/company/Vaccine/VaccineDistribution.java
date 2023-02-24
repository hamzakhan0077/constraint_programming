package com.company.Vaccine;


import org.chocosolver.solver.ISolver;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.io.IOException;
import java.util.Arrays;


public class  VaccineDistribution {

    public static void main(String[] args) throws IOException {
//        String file ="vaccinations1.txt";
        Model model = new Model("Practice Problem");
        VaccinationData reader = new VaccinationData("src/com/company/Vaccine/vaccinations1.txt");


        int numCenters = reader.getNumCentres();
        int numDays = reader.getNumDays();
        int[] request = reader.getReqs();
        int[][] minrequest = reader.getMinReqs();
        int[] supplies = reader.getSupplies();



        System.out.println("Total Number of centres : "+ numCenters + "\n" +
                "Total Number of Days "+ numDays + "\n" +
                "Requests : " + Arrays.toString(request) +"\n"+
                "Minimum Request : "+ Arrays.deepToString(minrequest) +"\n"+
                "Supplies : "+ Arrays.toString(supplies) );
        int totalVacs = Arrays.stream(supplies).sum();

        IntVar sumOfAllDistributedVacc = model.intVar(0,totalVacs);  // need to minimze this
        IntVar [] sumOfCol = model.intVarArray(numDays,0,totalVacs); //


        IntVar[][] vccDistMatrix = model.intVarMatrix("Grid",  numCenters,  numDays,  0,totalVacs );



        // this is to handle the case where the distributed vaccines dosent exceed the
        // total number of the available vaccines days Eg if 20 on D1 cant give more than 20
        for( int j = 0; j < numDays; j++){



            model.sum(ArrayUtils.getColumn(vccDistMatrix,j),"=", sumOfCol[j]).post();

            model.sum(ArrayUtils.getColumn(vccDistMatrix,j),"<=", supplies[j]).post();
//            int sumOfAllDistributedVacc += Arrays.stream(vccDistMatrix[j]).sum();



        }
        model.sum(sumOfCol, "=",sumOfAllDistributedVacc).post();


        for(int c = 0; c < numCenters; c++ ){
//            System.out.println(Arrays.toString(vccDistMatrix[c]));
            // num vaccinations availiabe on D 0  to D n-1
//             req arry is the number of vaccines a centre needs on that day

//            System.out.println(model.sum(vccDistMatrix[c],">=", request[c]));


            model.sum(vccDistMatrix[c],">=", request[c]).post();

            // Sum of vaccination distribution on that day should not exceed
            // the amount of supplies available for distribution on that day



//            System.out.println(model.sum(vccDistMatrix[c],"<=", supplies[c]));


            // Each Centre should meet their minimum request on a specific day

            for(int d = 0; d < numDays; d++ ){

//                System.out.println(model.arithm(vccDistMatrix[c][d],"=",minrequest[c][d]));
                model.arithm(vccDistMatrix[c][d],">=",minrequest[c][d]).post();

            } // inner loop

        }// outer loop

        Solver solver = model.getSolver();
        model.setObjective(Model.MINIMIZE,sumOfAllDistributedVacc);


//        System.out.println(solver.solve());
//        System.out.println(solver.getSolutionCount());


    // mininizing the total number of vaccine issued
    // create a var
    // write a constraint
    // that makes sure that  the total number of vaccines that are issued

        //



        while (solver.solve()) {


            System.out.println(solver.getSolutionCount());
//            System.out.println(Arrays.deepToString(vccDistMatrix));





        } // while loop
        solver.printStatistics();





    }
}

