package com.company;
import org.chocosolver.solver.ISolver;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
public class Main {

    public static void main(String[] args) {
        System.out.println("TEst" +
                "");
        // Creating a model
        Model model = new Model("Practice Problem");

        // Create Variables
        IntVar v1 = model.intVar("V1", 1,5);
        IntVar v2 = model.intVar("V2",1,5);
        IntVar v3 = model.intVar("V3",1,5);
        IntVar v4 = model.intVar("V4",1,5);


        // post Constraints
        model.arithm(v1,"<=" ,v4,"-",1).post();
        model.arithm(v1,"<",v2).post();;
        model.arithm(v2,"+",v3,">",6).post();;
        model.arithm(v2,"+",v4,"=",5).post();;
        model.arithm(v4,"<",v3).post();

      // solve
       Solver solver = model.getSolver();
       while (solver.solve()) {
           System.out.println(solver.getSolutionCount());
           System.out.println(v1);
           System.out.println(v2);
           System.out.println(v3);
           System.out.println(v4);






   }














    }
}
