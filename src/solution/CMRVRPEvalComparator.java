/*
* Log of changes:
* Aug 28, 2012 Implemented
*/

package solution;

import vrpModel.EMetric;
import vrpModel.Evaluation;
import heuristics.EComparisonResult;
import heuristics.IComparator;
public class CMRVRPEvalComparator implements IComparator{

	public EComparisonResult compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		Evaluation e1=(Evaluation)o1;
		Evaluation e2=(Evaluation)o2;
		try{
			double eCost1=(Double)e1.getMetric(EMetric.DISTANCE);
			double eCost2=(Double)e2.getMetric(EMetric.DISTANCE);
			if(eCost1<eCost2){
				return EComparisonResult.BETTER;
			}
			if(eCost1>eCost2){
				return EComparisonResult.WORSE;
			}else{
				return EComparisonResult.EQUAL;
			}
		}catch (Exception e){
			return EComparisonResult.INDETERMINATE;
		}
	}

}
