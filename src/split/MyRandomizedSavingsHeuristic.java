package split;

import heuristics.IRoutingProcedure;
import jCW.ClarkeAndWright;
import jCW.IJCWRouteMerger;
import jCW.IJCWSavingsCalculator;
import jCW.IJCWSavingsSelector;
import jCW.JCWConstraintChecker;

import jCW.RandomSelector;

import java.util.Hashtable;
import java.util.Iterator;

import randomGenerator.RandomGenerator;
import vrpModel.Instance;
import vrpModel.Node;
import vrpModel.Route;
import vrpModel.Solution;

public class MyRandomizedSavingsHeuristic extends MyParametricHeuristic
  implements IRoutingProcedure
{
  private MyClarkeAndWright cwh;

  public MyRandomizedSavingsHeuristic(MyParamSet params, IJCWSavingsCalculator savingsCalculator, IJCWRouteMerger routeMerger, RandomGenerator rndGen)
  {
    super(params);
    JCWConstraintChecker cc = new JCWConstraintChecker();
    IJCWSavingsSelector ss = new RandomSelector(rndGen, params.getParam(MyParameters.IntParam.CANDIDATES).intValue());
    this.cwh = new MyClarkeAndWright(MyJCWSavingsHeuristic.ESavingsCalculation.STATIC, routeMerger, cc, savingsCalculator, ss);
  }

  public Route run(Route partialRoute, Hashtable<Integer, Node> nodesToRoute, Hashtable<Integer, Node> routedNodes)
  {
    return (Route)this.cwh.run(Instance.instance()).getRoutesIterator().next();
  }
}