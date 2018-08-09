package split;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import vrpModel.Instance;
import vrpModel.Node;
import vrpModel.Route;
import vrpModel.Solution;
import jCW.IJCWRouteMerger;
import jCW.IJCWSavingsCalculator;
import jCW.IJCWSavingsSelector;
import jCW.JCWConstraintChecker;
import jCW.Savings;

public abstract class MyJCWSavingsHeuristic
{
  private ESavingsCalculation strategy = ESavingsCalculation.STATIC;
  private IJCWRouteMerger routeMerger;
  private JCWConstraintChecker constraintChecker;
  private IJCWSavingsCalculator savingsCalculator;
  private IJCWSavingsSelector savingsSelector;
  protected Hashtable<Integer, Route> nodeRouteTable;

  public MyJCWSavingsHeuristic(ESavingsCalculation strategy, IJCWRouteMerger routeMerger, JCWConstraintChecker constraintChecker, IJCWSavingsCalculator savingsCalculator, IJCWSavingsSelector savingsSelector)
  {
    this.strategy = strategy;
    this.routeMerger = routeMerger;
    this.constraintChecker = constraintChecker;
    this.savingsCalculator = savingsCalculator;
    this.savingsSelector = savingsSelector;
    this.nodeRouteTable = new Hashtable();
  }

  public Solution run(Instance instance)
  {
    Solution sol = buildInitialSolution(instance);
    ArrayList savingsList = this.savingsCalculator.calculateSavings(sol, instance);
    while (!stop(savingsList)) {
      if (this.strategy == ESavingsCalculation.DYNAMIC)
      {
        savingsList = this.savingsCalculator.calculateSavings(sol, instance);
      }
      Savings savings = this.savingsSelector.selectSavings(savingsList);
      Route r_i = (Route)this.nodeRouteTable.get(Integer.valueOf(savings.getTailNode().getId()));
      Route r_j = (Route)this.nodeRouteTable.get(Integer.valueOf(savings.getHeadNode().getId()));
      if (this.constraintChecker.checkConstraints(r_i, r_j, savings, instance))
      {
        Route r = this.routeMerger.merge(r_i, r_j, savings, instance);
        sol.removeRoute(r_i);
        sol.removeRoute(r_j);
        sol.addRoute(r);
        updateNodeRouteTable(r, r_i);
        updateNodeRouteTable(r, r_j);
      }
      savingsList.remove(savings);
    }
    setSolutionAttributes(sol, instance);
    return sol;
  }

  protected void updateNodeRouteTable(Route hostRoute, Route mergingRoute)
  {
    for (int i = 1; i < mergingRoute.getSize() - 1; i++)
      this.nodeRouteTable.put(Integer.valueOf(mergingRoute.getNode(i).getId()), hostRoute);
  }

  public Solution buildInitialSolution(Instance instance)
  {
    Solution sol = new Solution();
    Node depot = instance.getNode(Integer.valueOf(0));
    Iterator it = instance.getNodeIterator();
    while (it.hasNext()) {
      Node node = (Node)it.next();
      if (!node.isDepot()) {
        Route r = new Route();
        r.addNode(depot);
        r.addNode(node);
        r.addNode(depot);
        setRouteAttributes(r, instance);
        sol.addRoute(r);
        this.nodeRouteTable.put(Integer.valueOf(node.getId()), r);
      }
    }
    setSolutionAttributes(sol, instance);
    return sol;
  }

  protected abstract boolean stop(ArrayList<Savings> paramArrayList);

  protected abstract void setSolutionAttributes(Solution paramSolution, Instance paramInstance);

  protected abstract void setRouteAttributes(Route paramRoute, Instance paramInstance);

  public static enum ESavingsCalculation
  {
    STATIC, 

    DYNAMIC;
  }
}
