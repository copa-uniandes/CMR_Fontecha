package split;

import jCW.IJCWRouteMerger;
import jCW.IJCWSavingsCalculator;
import jCW.IJCWSavingsSelector;
import jCW.JCWConstraintChecker;
import jCW.Savings;
import java.util.ArrayList;
import vrpModel.EArcAttribute;
import vrpModel.EMetric;
import vrpModel.ENodeAttribute;
import vrpModel.ERouteAttribute;
import vrpModel.Evaluation;
import vrpModel.Instance;
import vrpModel.Route;
import vrpModel.Solution;

public class MyClarkeAndWright extends MyJCWSavingsHeuristic
{
  public MyClarkeAndWright(MyJCWSavingsHeuristic.ESavingsCalculation strategy, IJCWRouteMerger routeMerger, JCWConstraintChecker constraintChecker, IJCWSavingsCalculator savingsCalculator, IJCWSavingsSelector savingsSelector)
  {
    super(strategy, routeMerger, constraintChecker, savingsCalculator, savingsSelector);
  }

  protected boolean stop(ArrayList<Savings> savingsList)
  {
    return savingsList.isEmpty();
  }

  protected void setSolutionAttributes(Solution initSol, Instance instance)
  {
  }

  protected void setRouteAttributes(Route r, Instance instance)
  {
    double distance = 0.0D;

    double service = ((Double)r.getNode(0).getAttribute(ENodeAttribute.SERVICE_TIME)).doubleValue();
    for (int i = 1; i < r.getSize(); i++) {
      distance += ((Double)instance.getArc(r.getNode(i - 1), r.getNode(i)).getAttribute(EArcAttribute.DISTANCE)).doubleValue();

      service += ((Double)r.getNode(0).getAttribute(ENodeAttribute.SERVICE_TIME)).doubleValue();
    }

    r.setAttribute(ERouteAttribute.SERVICE_TIME, new Double(service));
    r.setAttribute(ERouteAttribute.VEHICLE_TYPE, new Integer(1));
    Evaluation eval = new Evaluation();
    eval.setMetric(EMetric.DISTANCE, Double.valueOf(distance));
    r.setAttribute(ERouteAttribute.EVALUATION, eval);
  }


}