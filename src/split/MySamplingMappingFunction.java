package split;

import heuristics.EComparisonResult;
import heuristics.IComparator;
import heuristics.IRoutingProcedure;
import java.util.Hashtable;
import splitProcedures.ISplittingProcedure;
import statCollector.StatCollector;
import statCollector.StatTag;
import vrpModel.ESolutionAttribute;
import vrpModel.Evaluation;
import vrpModel.Node;
import vrpModel.Route;
import vrpModel.Solution;

public class MySamplingMappingFunction extends MyParametricHeuristic
{
  private IRoutingProcedure routingProcedure;
  private ISplittingProcedure clusteringProcedure;
  private IComparator comparator;
  private boolean debugMode;

  public void setDebugMode(boolean debugMode)
  {
    this.debugMode = debugMode;
  }

  public MySamplingMappingFunction(MyParamSet params, IRoutingProcedure routingProcedure, ISplittingProcedure clusteringProcedure, IComparator comparator)
  {
    super(params);
    this.routingProcedure = routingProcedure;
    this.clusteringProcedure = clusteringProcedure;
    this.comparator = comparator;
    this.algName = getClass().getName();
  }

  public Solution run(Route initialRoute, Hashtable<Integer, Node> nodesToRoute, Hashtable<Integer, Node> routedNodes)
  {
    int it = 1;
    Solution bestSolution = null;
    while (it <= this.paramSet.getParam(MyParameters.IntParam.ITERATIONS).intValue())
    {
      Route r = this.routingProcedure.run(initialRoute.clone(), (Hashtable)nodesToRoute.clone(), (Hashtable)routedNodes.clone());

      if (this.debugMode) {
        r.printSequence(10);
      }

      Solution s = this.clusteringProcedure.split(r);
      StatTag tag = new StatTag(it, StatCollector.instance().getCurrentTime(), this.algName);
      s.setAttribute(ESolutionAttribute.STAT_TAG, tag);

      if (it == 1) {
        bestSolution = s;
      }
      else if (this.comparator.compare((Evaluation)s.getAttribute(ESolutionAttribute.EVALUATION), (Evaluation)bestSolution.getAttribute(ESolutionAttribute.EVALUATION)) == EComparisonResult.BETTER) {
        bestSolution = s;
      }

      it++;
    }
    return bestSolution;
  }
}
