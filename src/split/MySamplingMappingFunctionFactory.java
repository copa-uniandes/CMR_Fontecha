package split;

import heuristics.BIFinder;
import heuristics.FIFinder;
import heuristics.IComparator;
import heuristics.NIFinder;
import jCW.GeometricSavingsCalculator;
import jCW.IJCWRouteMerger;
import jCW.IJCWSavingsCalculator;
import jCW.TSPRouteMerger;
import randomGenerator.RandomGenerator;
import splitProcedures.ISplittingProcedure;
import umontreal.iro.lecuyer.rng.MRG32k3a;
import umontreal.iro.lecuyer.rng.RandomStream;

public class MySamplingMappingFunctionFactory
{
	RandomStream rnd;
	RandomGenerator rndGen;
	ISplittingProcedure split;
	IComparator comparator;

	public MySamplingMappingFunctionFactory(long seed, ISplittingProcedure split, IComparator comparator)
	{
		MRG32k3a.setPackageSeed(getSeedVector(Long.valueOf(seed)));
		this.rnd = new MRG32k3a();
		MyRandomGeneratorFactory.setSeed(this.rnd.nextInt(0, 1000));
		this.rndGen = MyRandomGeneratorFactory.getFactory().getCentralGenerator();
		this.split = split;
		this.comparator = comparator;
	}

	public static MySamplingMappingFunction newSamplingFunction() {
		return null;
	}

	public MySamplingMappingFunction build(String function, int K, int T)
	{
		if (function.equals("RNN"))
			return newRNN(K, T);
		if (function.equals("RBI"))
			return newRBI(K, T);
		if (function.equals("RNI"))
			return newRNI(K, T);
		if (function.equals("RFI"))
			return newRFI(K, T);
		if (function.equals("RFI2"))
			return newRFI2(K, T);
		if (function.equals("RFI3"))
			return newRFI3(K, T);
		if (function.equals("RCW")) {
			return newRCW(K, T);
		}
		throw new IllegalStateException("Sampling function " + function + " is not declared in the factory");
	}

	private MySamplingMappingFunction newRNN(int K, int T)
	{
		MyParamSet params = new MyParamSet();
		params.setParam(MyParameters.IntParam.CANDIDATES, K);
		MyRandomizedNN nnHeuristic = new MyRandomizedNN(params, this.rnd);
		params = new MyParamSet();
		params.setParam(MyParameters.IntParam.ITERATIONS, T);
		MySamplingMappingFunction samplingMappingFunction = new MySamplingMappingFunction(params, nnHeuristic, this.split, this.comparator);
		samplingMappingFunction.setName("RNN(" + K + "," + T + ")");
		return samplingMappingFunction;
	}

	private MySamplingMappingFunction newRBI(int K, int T)
	{
		MyParamSet params = new MyParamSet();
		params.setParam(MyParameters.IntParam.CANDIDATES, Integer.valueOf(K).intValue());
		MyRandomizedGI giHeuristic = new MyRandomizedGI(params, this.rnd, new BIFinder());
		params = new MyParamSet();
		params.setParam(MyParameters.IntParam.ITERATIONS, T);
		MySamplingMappingFunction samplingMappingFunction = new MySamplingMappingFunction(params, giHeuristic, this.split, this.comparator);
		samplingMappingFunction.setName("RBI(" + K + "," + T + ")");
		return samplingMappingFunction;
	}

	private MySamplingMappingFunction newRNI(int K, int T)
	{
		MyParamSet params = new MyParamSet();
		params.setParam(MyParameters.IntParam.CANDIDATES, Integer.valueOf(K).intValue());
		MyRandomizedGI giHeuristic = new MyRandomizedGI(params, this.rnd, new NIFinder());
		params = new MyParamSet();
		params.setParam(MyParameters.IntParam.ITERATIONS, T);
		MySamplingMappingFunction samplingMappingFunction = new MySamplingMappingFunction(params, giHeuristic, this.split, this.comparator);
		samplingMappingFunction.setName("RNI(" + K + "," + T + ")");
		return samplingMappingFunction;
	}

	private MySamplingMappingFunction newRFI(int K, int T)
	{
		MyParamSet params = new MyParamSet();
		params.setParam(MyParameters.IntParam.CANDIDATES, Integer.valueOf(K).intValue());
		MyRandomizedGI giHeuristic = new MyRandomizedGI(params, this.rnd, new FIFinder(FIFinder.Strategy.MAX_MIN));
		params = new MyParamSet();
		params.setParam(MyParameters.IntParam.ITERATIONS, T);
		MySamplingMappingFunction samplingMappingFunction = new MySamplingMappingFunction(params, giHeuristic, this.split, this.comparator);
		samplingMappingFunction.setName("RFI(" + K + "," + T + ")");
		return samplingMappingFunction;
	}

	private MySamplingMappingFunction newRFI2(int K, int T)
	{
		MyParamSet params = new MyParamSet();
		params.setParam(MyParameters.IntParam.CANDIDATES, Integer.valueOf(K).intValue());
		MyRandomizedGI giHeuristic = new MyRandomizedGI(params, this.rnd, new FIFinder(FIFinder.Strategy.MAX));
		params = new MyParamSet();
		params.setParam(MyParameters.IntParam.ITERATIONS, T);
		MySamplingMappingFunction samplingMappingFunction = new MySamplingMappingFunction(params, giHeuristic, this.split, this.comparator);
		samplingMappingFunction.setName("RFI2(" + K + "," + T + ")");
		return samplingMappingFunction;
	}

	private MySamplingMappingFunction newRFI3(int K, int T)
	{
		MyParamSet params = new MyParamSet();
		params.setParam(MyParameters.IntParam.CANDIDATES, Integer.valueOf(K).intValue());
		MyRandomizedGI giHeuristic = new MyRandomizedGI(params, this.rnd, new FIFinder(FIFinder.Strategy.MIN_MAX));
		params = new MyParamSet();
		params.setParam(MyParameters.IntParam.ITERATIONS, T);
		MySamplingMappingFunction samplingMappingFunction = new MySamplingMappingFunction(params, giHeuristic, this.split, this.comparator);
		samplingMappingFunction.setName("RFI3(" + K + "," + T + ")");
		return samplingMappingFunction;
	}

	private MySamplingMappingFunction newRCW(int K, int T)
	{
		MyParamSet params = new MyParamSet();
		params.setParam(MyParameters.IntParam.CANDIDATES, Integer.valueOf(K).intValue());
		IJCWSavingsCalculator sc = new GeometricSavingsCalculator();
		IJCWRouteMerger rm = new TSPRouteMerger();
		MyRandomizedSavingsHeuristic rcw = new MyRandomizedSavingsHeuristic(params, sc, rm, this.rndGen);
		params = new MyParamSet();
		params.setParam(MyParameters.IntParam.ITERATIONS, T);
		MySamplingMappingFunction samplingMappingFunction = new MySamplingMappingFunction(params, rcw, this.split, this.comparator);
		samplingMappingFunction.setName("RCW(" + K + "," + T + ")");
		return samplingMappingFunction;
	}

	private long[] getSeedVector(Long s) {
		long[] seed = new long[6];
		seed[0] = s.longValue();
		seed[1] = (s.longValue() + seed[0]);
		seed[2] = (s.longValue() + seed[1]);
		seed[3] = (s.longValue() + seed[2]);
		seed[4] = (s.longValue() + seed[3]);
		seed[5] = (s.longValue() + seed[4]);
		return seed;
	}
}