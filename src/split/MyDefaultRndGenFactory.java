package split;

import randomGenerator.RandomGenerator;

public class MyDefaultRndGenFactory extends MyIRndGenFactory
{
  public RandomGenerator newRndGen()
  {
    return new MyDefaultRndGen(this.centralGenerator.nextInt(0, 2147483646));
  }

  public void initCentralGenerator(long seed)
  {
    if (this.centralGenerator != null)
      throw new IllegalStateException("The central generator is already intialized");
    this.centralGenerator = new MyDefaultRndGen(seed);
  }
}