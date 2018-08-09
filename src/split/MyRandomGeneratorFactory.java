package split;



public final class MyRandomGeneratorFactory
{
  public static MyIRndGenFactory factory = null;

  public static long seed = 12345L;

  private static MyIRndGenFactory initWithDefaultFactory()
  {
    factory = new MyDefaultRndGenFactory();
    factory.initCentralGenerator(seed);
    return factory;
  }

  public static synchronized MyIRndGenFactory getFactory()
  {
    return factory == null ? initWithDefaultFactory() : factory;
  }

  public static synchronized void setRndGenFactory(String rndGenFactoryClassName)
  {
    if (factory != null)
      throw new IllegalStateException("The random generator factory is already set");
    try {
      factory = (MyIRndGenFactory)Class.forName(rndGenFactoryClassName).newInstance();
      factory.initCentralGenerator(seed);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static synchronized void setSeed(long newSeed)
  {
    if (factory != null)
      throw new IllegalStateException("The central generator was initialized with the following seed:" + seed);
    seed = newSeed;
  }
}
