package split;

import java.util.Random;

import randomGenerator.RandomGenerator;

public class MyDefaultRndGen
  implements RandomGenerator
{
  private Random rndGen;

  protected MyDefaultRndGen()
  {
    this.rndGen = new Random();
  }

  protected MyDefaultRndGen(long seed)
  {
    this.rndGen = new Random(seed);
  }
  public double nextDouble() {
    double rnd;
    do rnd = this.rndGen.nextDouble();
    while ((rnd == 0.0D) || (rnd == 1.0D));
    return rnd;
  }

  public int nextInt(int i, int j)
  {
    return i + this.rndGen.nextInt(j + 1);
  }
}