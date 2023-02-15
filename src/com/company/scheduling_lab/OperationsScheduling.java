package com.company.scheduling_lab;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;




public class OperationsScheduling {
    public static void main(String[] args) throws IOException{

        Model model = new Model("Operations Scheduling");
        int numTasks =  7;
        int resourceTime = 24;

        int totalDurations = 25;


//        IntVar A = model.intVar("A",0,24);
//        IntVar B = model.intVar("B",0,24);
//        IntVar C = model.intVar("C",0,24);
//        IntVar D = model.intVar("D",0,24);
//        IntVar E = model.intVar("E",0,24);
//        IntVar F = model.intVar("F",0,24);
//        IntVar G = model.intVar("G",0,24);






    }


}
