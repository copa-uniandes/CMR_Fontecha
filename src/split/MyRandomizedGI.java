package split;

import java.util.Hashtable;

import umontreal.iro.lecuyer.rng.RandomStream;
import vrpModel.Insertion;
import vrpModel.Instance;
import vrpModel.Node;
import vrpModel.Route;
import heuristics.IRoutingProcedure;
import heuristics.InsertionFinder;

public class MyRandomizedGI extends MyRandomizedHeuristic
implements IRoutingProcedure
{
InsertionFinder finder = null;

public MyRandomizedGI(MyParamSet params, RandomStream rndGen, InsertionFinder finder)
{
  super(params, rndGen);
  this.finder = finder;
}

public Route run(Route partialRoute, Hashtable<Integer, Node> nodesToRoute, Hashtable<Integer, Node> routedNodes)
{
  if ((partialRoute == null) || (partialRoute.getSize() == 0)) partialRoute = initRandom(nodesToRoute, routedNodes);
  while (nodesToRoute.size() > 0) {
    int k = this.rndGen.nextInt(1, Math.min(nodesToRoute.size(), this.paramSet.getParam(MyParameters.IntParam.CANDIDATES).intValue()));
    Insertion nextInsertion = this.finder.findInsertion(partialRoute, routedNodes, nodesToRoute, k);
    partialRoute.insertNode(nextInsertion.getPosition(), nextInsertion.getNode());
    routedNodes.put(nextInsertion.getNode().getKey(), (Node)nodesToRoute.remove(nextInsertion.getNode().getKey()));
  }
  return partialRoute;
}

private Route initRandom(Hashtable<Integer, Node> nodesToRoute, Hashtable<Integer, Node> routedNodes)
{
  int initNodeIndex = this.rndGen.nextInt(1, Instance.instance().getN() - 1);
  Node initNode = Instance.instance().getNode(Integer.valueOf(initNodeIndex));
  Node depot = Instance.instance().getNode(Integer.valueOf(0));
  Route route = new Route();
  route.addNode(depot);
  route.addNode(initNode);
  route.addNode(depot);
  nodesToRoute.remove(depot.getKey());
  nodesToRoute.remove(initNode.getKey());
  routedNodes.put(depot.getKey(), depot);
  routedNodes.put(initNode.getKey(), initNode);
  return route;
}
}