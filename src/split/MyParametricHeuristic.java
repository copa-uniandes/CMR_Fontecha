package split;

import heuristics.IHeuristic;

public abstract class MyParametricHeuristic
implements IHeuristic
{
protected MyParamSet paramSet;
protected String algName;

public MyParametricHeuristic(MyParamSet params)
{
  this.paramSet = params;
}

public MyParamSet getParamters()
{
  return this.paramSet;
}

public String getName()
{
  return this.algName;
}

public void setName(String name)
{
  this.algName = name;
}
}