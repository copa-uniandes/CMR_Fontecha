package split;

import heuristics.IRoutingProcedure;

import java.util.Hashtable;

import umontreal.iro.lecuyer.rng.RandomStream;
import vrpModel.Neighbor;
import vrpModel.Node;
import vrpModel.Route;

public class MyRandomizedNN extends MyRandomizedHeuristic
  implements IRoutingProcedure
{
  public MyRandomizedNN(MyParamSet params, RandomStream rndGen)
  {
    super(params, rndGen);
  }

  public Route run(Route partialRoute, Hashtable<Integer, Node> nodesToRoute, Hashtable<Integer, Node> routedNodes)
  {
    while (nodesToRoute.size() > 0) {
      int nnIndex = this.rndGen.nextInt(1, Math.min(nodesToRoute.size(), this.paramSet.getParam(MyParameters.IntParam.CANDIDATES).intValue()));
      Node lastNode = partialRoute.getNode(partialRoute.getSize() - 1);
      Neighbor neighbor = MyNNFinder.findNN(lastNode, routedNodes, nnIndex);
      partialRoute.addNode(neighbor.getNode());
      routedNodes.put(neighbor.getNode().getKey(), (Node)nodesToRoute.remove(neighbor.getNode().getKey()));
    }
    partialRoute.addNode(partialRoute.getNode(0));
    return partialRoute;
  }
}