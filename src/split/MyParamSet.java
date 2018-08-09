package split;

import java.util.Hashtable;

public class MyParamSet
{
  private Hashtable<MyParameters.IntParam, Integer> intParams;
  private Hashtable<MyParameters.DoubleParam, Double> dblParams;

  public MyParamSet()
  {
    this.intParams = new Hashtable();
    this.dblParams = new Hashtable();
    setDefaultValues();
  }

  private void setDefaultValues()
  {
    setParam(MyParameters.IntParam.MAX_CPU, 2147483647);
    setParam(MyParameters.IntParam.ITERATIONS, 1);
    setParam(MyParameters.IntParam.IT_NO_IMPROV, 2147483647);
    setParam(MyParameters.IntParam.RESTARTS, 0);
    setParam(MyParameters.IntParam.TRIES, 1);
    setParam(MyParameters.DoubleParam.GAP, Double.valueOf(0.0D));
    setParam(MyParameters.DoubleParam.GAP_ABS, Double.valueOf(0.0D));
    setParam(MyParameters.IntParam.CANDIDATES, 1);
  }

  public void setParam(MyParameters.IntParam intParam, int value) {
    this.intParams.put(intParam, Integer.valueOf(value));
  }

  public void setParam(MyParameters.DoubleParam dblParam, Double value) {
    this.dblParams.put(dblParam, value);
  }

  public Integer getParam(MyParameters.IntParam intParam) {
    return (Integer)this.intParams.get(intParam);
  }

  public Double getParam(MyParameters.DoubleParam dblParam) {
    return (Double)this.dblParams.get(dblParam);
  }
}