package split;


import umontreal.iro.lecuyer.rng.RandomStream;

public abstract class MyRandomizedHeuristic extends MyParametricHeuristic
{
  RandomStream rndGen;

  public MyRandomizedHeuristic(MyParamSet params, RandomStream rndGen)
  {
    super(params);
    this.rndGen = rndGen;
  }

  public RandomStream getRndGen()
  {
    return this.rndGen;
  }
}
