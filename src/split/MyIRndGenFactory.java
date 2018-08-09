package split;

import randomGenerator.RandomGenerator;

public abstract class MyIRndGenFactory
{
  protected RandomGenerator centralGenerator = null;

  public RandomGenerator getCentralGenerator()
  {
    return this.centralGenerator;
  }

  public abstract RandomGenerator newRndGen();

  protected abstract void initCentralGenerator(long paramLong);
}
