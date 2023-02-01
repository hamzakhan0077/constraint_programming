package com.company;


import org.chocosolver.solver.ISolver;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import java.io.IOException;


public class VaccineDistribution {

    public void VaccineDistribution() throws IOException {
        String file ="vaccinations2.txt";
        Model model = new Model("Practice Problem");
        VaccinationData reader = new VaccinationData(file);


        int numCenters = reader.getNumCentres();
        int numDays = reader.getNumDays();
        int[] request = reader.getReqs();
        int[][] minrequest = reader.getMinReqs();
        int[] supplies = reader.getSupplies();

        System.out.println("Total Number of centres : "+ numCenters + "\n" +
                "Total Number of Days "+ numDays + "\n" );




        IntVar[][] square = model.intVarMatrix("Grid",  numCenters,  numDays,  0,  20);





    }
}
