package split;

import java.util.Hashtable;
import vrpModel.Neighbor;
import vrpModel.Node;

public class MyNNFinder
{
  public static Neighbor findNN(Node node, Hashtable<Integer, Node> routedNodes, int k)
  {
    int neighborIndex = 1;
    int neighborCount = 0;
    Neighbor neighbor = null;
    while (neighborCount < k) {
      neighbor = node.getNeighbor(neighborIndex - 1);
      while (routedNodes.contains(neighbor.getNode())) {
        neighborIndex++;
        neighbor = node.getNeighbor(neighborIndex - 1);
      }
      neighborCount++;
      neighborIndex++;
    }
    return neighbor;
  }
}