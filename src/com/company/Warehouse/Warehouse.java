import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

/*
 * A class for solving a warehouse location problem
 * Demonstrates use of various global constraints including element
 * Data values are hard coded into the class
 */

public class Warehouse {

   public static void main(String[] args) {

      /*--------------- PROBLEM PARAMETERS ----------------*/
      /**/
      int numWHs = 5;           //number of warehouses
      int numShops = 10;        //number of shops
      int maintenance = 30;     //common maintenance cost per warehouse

      int[] capacity = {1,4,2,1,3}; //capacity of each warehouse

      int[][] supply = {          //supply[i][j] is the cost of supplying shop i from warehouse j
            {20, 24, 11, 25, 30},
            {28, 27, 82, 83, 74},
            {74, 97, 71, 96, 70},
            {2, 55, 73, 69, 61},
            {46, 96, 59, 83, 4},
            {42, 22, 29, 67, 59},
            {1, 5, 73, 59, 56},
            {10, 73, 13, 43, 96},
            {93, 35, 63, 85, 46},
            {47, 65, 55, 71, 95}};
      /**/
      /* SIMPLE TEST CASE 
		int numWHs = 3;
		int numShops = 4;
		int maintenance = 5;

		int[] capacity = {2,4,3};

		int[][] supply = {
				{4,6,8},
				{7,7,5},
				{4,5,6},
				{9,6,5}
		};
       */

      int maxShopCost = 0;    //the maximum supply cost for any shop
      for (int shop = 0; shop < numShops; shop++) {
         for (int wh = 0; wh < numWHs; wh++) {
            if (supply[shop][wh] > maxShopCost) {
               maxShopCost = supply[shop][wh];
            }
         }
      }

      int maxCapacity = 0; //the maximum capacity for any warehouse
      for (int wh = 0; wh < numWHs; wh++) {
         if (capacity[wh] > maxCapacity) {
            maxCapacity = capacity[wh];
         }
      }

      //display the computed problem parameters
      System.out.println("maxShopCost = " + maxShopCost);
      System.out.println("maxCapacity = " + maxCapacity);

      //Create the model

      Model model = new Model("Warehouse location");

      //Create the variables

      //an array of IntVars, stating for each shop which warehouse supplies it
      IntVar[] shopSupply = model.intVarArray("shopSupply", numShops, 0, numWHs-1);

      //an array of IntVars, stating the cost of supplying to each shop from its chosen warehouse
      IntVar[] shopCost = model.intVarArray("shopCost",  numShops,  0,  maxShopCost);

      //the cost of supplying all the shops
      IntVar allShopCost = model.intVar("allShopCost",  0,  maxShopCost*numShops);

      //an array of booleans, stating for each warehouse whether or not it is active
      IntVar[] whActive = model.intVarArray("whActive", numWHs, 0, 1);

      //the number of active warehouses
      IntVar numberActive = model.intVar("numberActive",  0,  numWHs);

      //the total maintenance cost (a ScaleView is just some other variable multiplied by a constant)
      //so this says the number of active warehouses multiplied by the constant maintenance cost is the total maintenance cost
      //Conceptually this is a constraint, but formally in Choco it defines an IntVar (and immediately constrains it)
      IntVar totalMaintenanceCost = model.intScaleView(numberActive, maintenance);

      //an array of Intvars, stating for each warehouse how many shops it supplies
      IntVar[] supplyCount = new IntVar[numWHs];
      for (int wh = 0; wh < numWHs; wh++) {
         supplyCount[wh] = model.intVar("supplyCount",  0,  capacity[wh]);
      }

      //the total cost of the chosen warehouses and warehouse,shop pairs
      IntVar totalCost = model.intVar("totalCost", 0, maxShopCost*numShops + numWHs*maintenance);

      /* ------------- CONSTRAINTS ---------- */

      //If a shop is supplied by a warehouse, then it is active
      //for a given shop s, if shopSupply[s] == w, then warehouse w is active, and so whActive[w] == 1
      //i.e. whActive[shopSupply[s]] == 1   (or 1 == whActive[shopSupply[s]], to match the order of arguments for element)
      for (int s = 0; s < numShops; s++) {
         model.element(model.intVar(1), whActive, shopSupply[s], 0).post();  //element requires an IntVar as 1st argument
      }

      //no warehouse can supply more than its capacity of shops
      //so for each warehouse, the count of the number of times it appears in shopSupply is the number of shops it supplies
      for (int wh = 0; wh < numWHs; wh++) {
         model.count(wh, shopSupply, supplyCount[wh]).post();
      }

      //the cost of a shop is the supply cost from its chosen warehouse
      //supply[s] is the array of costs per warehouse to supply shop s
      //so supply[s][w] is the cost of supplying s from warehouse w
      //shopSupply[s] is the warehouse chosen to supply shop
      //so supply[s][shopSupply[s]] is the cost of supplying s by its chosen warehouse
      //so we have   shopCost[s] = supply[s][shopSupply[s]]    (i.e. 'table' is supply[s], and index is shopSupply[s])
      for (int s = 0; s < numShops; s++) {
         model.element(shopCost[s], supply[s], shopSupply[s]).post();
      }

      //the cost of supply is the sum of the shop supply costs
      model.sum(shopCost, "=", allShopCost).post();

      //the maintenance cost for the warehouses is the number of active warehouses
      //model.count(model.intVar(1), whActive, numberActive).post();   //this is (I think) a less efficient way of doing it)
      model.sum(whActive, "=", numberActive).post();

      //the total cost is the sum of all shop costs plus the maintenance cost
      //totalMaintenceCost was defined and constrained in the Vaiables section
      model.arithm(totalMaintenanceCost, "+", allShopCost, "=", totalCost).post();



      //Solve the problem

      Solver solver = model.getSolver();
      
      //IntVar[] searchVars = ArrayUtils.concat(shopSupply, totalCost);
            
      //Search Strategy
      //solver.setSearch(Search.domOverWDegSearch(searchVars));
      //solver.setSearch(Search.inputOrderLBSearch(searchVars));
      //solver.setSearch(Search.activityBasedSearch(searchVars)); 		 
      //solver.setSearch(new ImpactBased(searchVars, 2,3,10, 0, false));

      model.setObjective(Model.MINIMIZE, totalCost);

      int numsolutions = 0;
      //      if (solver.solve()) {
      while (solver.solve()) { //print the solution
         numsolutions++;
         System.out.print("Solution " + numsolutions + ": ");

         //print out our own solution

         System.out.print("Active warehouses: ");
         for (int wh=0; wh < numWHs; wh++) {
            if (whActive[wh].getValue() == 1)
               System.out.print(wh + " ");
         }
         System.out.println(" (Maintenance cost = " + maintenance*numberActive.getValue() + ")");
         for (int shop = 0; shop < numShops; shop++) {
            System.out.println("Shop: " + shop + ": w " + shopSupply[shop].getValue() + " (cost " + shopCost[shop].getValue() + ")");
         }
         System.out.println("Total cost: " + totalCost.getValue());
      }
      solver.printStatistics();

   }

}
