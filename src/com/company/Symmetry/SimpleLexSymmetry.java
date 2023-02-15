import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;


public class SimpleLexSymmetry {

	public static void main(String[] args) {
		
		Model model = new Model();
		
		/*----------VARIABLES--------------*/
		
		//the matrix of 0/1 variables
		IntVar[][] matrix = model.intVarMatrix("matrix",  4, 3, 0, 1);
	
		
		/*---------CONSTRAINTS-------------*/
		
		model.sum(ArrayUtils.flatten(matrix), "=", model.intVar(7)).post();
		
      for (int row1 = 0; row1 < 4; row1++) {
         for (int row2 = row1 + 1; row2 < 4; row2++) {
            //Note - can't do a simple scalar constraint, since both arrays are decision variables
            IntVar[] product = model.intVarArray(3,0,1);
            for (int col = 0; col < 3; col++) {
               model.times(matrix[row1][col], matrix[row2][col], product[col]).post();
            }
            model.sum(product, "<=", 1).post();
         }
      }
        
      
      model.lexChainLessEq(matrix).post();
      model.lexChainLessEq(ArrayUtils.transpose(matrix)).post();
       
      /*-------------SEARCH------------------*/
      //Solve the problem
        
		Solver solver = model.getSolver();
		
        
      //if (solver.solve()) {
      while (solver.solve()) { //print the solution
         System.out.println("Solution " + solver.getSolutionCount() + ":");

         //print out our own solution
            
         for (int row = 0; row < 4; row ++) {
            for (int col = 0; col < 3; col++) {
               System.out.print(matrix[row][col].getValue() + " ");
            }
            System.out.println();
         }
         System.out.println();
        
      }
      solver.printStatistics();
        
	}
        
}