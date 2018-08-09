package split;
import com.Ostermiller.util.CSVParser;

import dataReaders.CSVReader4VRPSCD;
import dataWriters.RoutePlotterConsoleWriter;
import heuristics.IComparator;
import heuristics.SolEvalComparator;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;

import org.jdom.Element;

import setCoveringSolvers.ColumnSelector;
import setCoveringSolvers.IColumn;
import setCoveringSolvers.ISetCoveringSolver;
import setCoveringSolvers.SolBuilder;
import setCoveringSolvers.VRPColumn;
import setCoveringSolvers.VRPColumnWarehouse;
import setCoveringSolvers.VRPSetPartitioningSolver;
import splitProcedures.ArcEvaluator;
import splitProcedures.ColumnStoringGenericSplit;
import statCollector.StatCollector;
import vrpModel.Instance;
import vrpModel.Node;
import vrpModel.Route;
import vrpModel.Solution;

public class MyMSH {
	private static ArrayList<ArrayList<Integer>> sol = null;
	private static Solution ss;
	public static VRPColumnWarehouse columnWarehouse;
	private static ISetCoveringSolver hc = null;
	public static Hashtable nodesToRoute=null;
	public static Hashtable routedNodes=null;

	Element running;

	public MyMSH(String[] args, ArcEvaluator arcEvaluator, IComparator solComparator,boolean debugMode){
		nodesToRoute=null;
		routedNodes=null;
		columnWarehouse=null;
		this.running=MyMSH.run(args, arcEvaluator,new CMRVRPEvalComparator(),false);
	}

	public static  Element run(String[] args, ArcEvaluator arcEvaluator, IComparator solComparator,boolean debugMode)
	{
		
		nodesToRoute=null;
		routedNodes=null;
		columnWarehouse=null;
		
		int T = Integer.valueOf(args[1]).intValue();

//		Instance.instance=null;
		CSVReader4VRPSCD.instance(args[0]);
		System.out.println(args[0]);
			
		Instance.instance().setInsertions();
		
		nodesToRoute = Instance.instance().getNodes();
		routedNodes = new Hashtable();
		Route r = new Route();
		Node depot = Instance.instance().getNode(Integer.valueOf(0));
		r.addNode(depot);
		nodesToRoute.remove(depot.getKey());
		routedNodes.put(depot.getKey(), depot);
		System.out.println("Nodos a rutear p p p  "+nodesToRoute.size());
		System.out.println("Nodos ruteados "+routedNodes.size());
		ColumnStoringGenericSplit split = new ColumnStoringGenericSplit(arcEvaluator);

//		VRPColumnWarehouse
		columnWarehouse = (VRPColumnWarehouse)split.getColumns();//new VRPColumnWarehouse();//
		
		IComparator comparator = solComparator;

		ArrayList sols = new ArrayList();

		long cpuPhaseI = 0L;
		long intento=0;

		MySamplingMappingFunctionFactory factory = new MySamplingMappingFunctionFactory(Long.valueOf(args[2]).longValue(), split, comparator);
		ArrayList<MySamplingMappingFunction> functions = getSamplingFunctions(args[4], factory, debugMode);
		int nbSamplingFunctions = functions.size();
		VRPColumn.setSamplingFunctions(nbSamplingFunctions);

		int t = 0;

		SolEvalComparator comp = new SolEvalComparator(comparator);
		StatCollector.instance().startTimer();
		while (t < T) {
			for (int f = 0; f < functions.size(); f++) {
				if (debugMode) {
					System.out.println("************* Iteration " + t + " **************");
					System.out.println("TSP TOUR:");
				}
				columnWarehouse.setHeuristicIndex(f);
				Solution s;

				if (!isRNN((MySamplingMappingFunction)functions.get(f)))
					s = ((MySamplingMappingFunction)functions.get(f)).run(new Route(), (Hashtable)nodesToRoute.clone(), (Hashtable)routedNodes.clone());
				else
					s = ((MySamplingMappingFunction)functions.get(f)).run(r, (Hashtable)nodesToRoute.clone(), (Hashtable)routedNodes.clone());
				if (sols.size() < nbSamplingFunctions) {
					sols.add(s);
				}
				else if (comp.compare(s, (Solution)sols.get(f)) < 0)
					sols.set(f, s);
				if (debugMode) {
					s.printAttributes();
					s.printRoutes(10);
				}
				t++;
			}
		}
		StatCollector.instance().stopTimer();
		cpuPhaseI = StatCollector.instance().getTotalTime();

		Collections.sort(sols, new SolEvalComparator(comparator));

//		VRPSetPartitioningSolver hc = new VRPSetPartitioningSolver(1, Instance.instance().getN() - 1);



		ArrayList columns = new ArrayList();
		Collection cols = columnWarehouse.getColumns();
		Iterator it = cols.iterator();
		while (it.hasNext()) {
			columns.add((IColumn)it.next());
		}

		ArrayList initialSolution = ColumnSelector.getColumns((Solution)sols.get(0), columnWarehouse);

		if (debugMode) {
			columnWarehouse.printColumns();
		} 
		if(hc==null){
			hc=new VRPSetPartitioningSolver(1,Instance.instance().getN()-1);
		}

		hc.warmUp(initialSolution);

		StatCollector.instance().startTimer();
		columns = (ArrayList)hc.solve(columns);
		StatCollector.instance().stopTimer();
		long cpuPhaseII = StatCollector.instance().getTotalTime();

		Solution s2 = SolBuilder.buildSol(columns);
		sols.add(s2);

		RoutePlotterConsoleWriter solWriter = new RoutePlotterConsoleWriter();
		System.out.println("Solution");
		solWriter.printSolution(s2);
		System.out.println("valor de la fo: "+hc.getOF());

		Element run = new Element("run");

		Element e = new Element("algorithm");
		e.setText("MSH");
		run.addContent(e);

		e = new Element("config");
		e.setText(args[4]);
		run.addContent(e);

		e = new Element("iterations");
		e.setText(args[1]);
		run.addContent(e);

		e = new Element("seed");
		e.setText(args[2]);
		run.addContent(e);

		e = new Element("instance");
		e.setText(args[3]);
		run.addContent(e);

		e = new Element("of");
		double of = hc.getOF();
		e.setText(String.valueOf(of));
		run.addContent(e);

		e = new Element("cpu");
		e.setText(String.valueOf(cpuPhaseI + cpuPhaseII));
		run.addContent(e);

		e = new Element("date");
		e.setText(String.valueOf(System.currentTimeMillis()));
		run.addContent(e);

		retrieveSol(s2);
		setSs(s2);

		return run;
	}

	private static boolean isRNN(MySamplingMappingFunction samplingMappingFunction)
	{
		return samplingMappingFunction.getName().contains("RNN");
	}

	private static ArrayList<MySamplingMappingFunction> getSamplingFunctions(String file, MySamplingMappingFunctionFactory factory, boolean debugMode)
	{
		ArrayList functions = new ArrayList();
		try {
			File configFile = new File(file);
			CSVParser parser = new CSVParser(new FileInputStream(configFile));
			parser.setCommentStart("#;!");
			parser.setEscapes("nrtf", "\n\r\t\f");

			boolean moreLines = true;
			while (moreLines) {
				String[] line = parser.getLine();
				if (line == null)
					break;
				functions.add(factory.build(line[0], Integer.valueOf(line[1]).intValue(), Integer.valueOf(line[2]).intValue()));
				((MySamplingMappingFunction)functions.get(functions.size() - 1)).setDebugMode(debugMode);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return functions;
	}

	private static void retrieveSol(Solution s)
	{
		sol = new ArrayList();
		Iterator it = s.getRoutesIterator();
		while (it.hasNext()) {
			Route mr = (Route)it.next();
			Iterator it2 = mr.getSequenceIterator();
			ArrayList route = new ArrayList();
			while (it2.hasNext()) {
				route.add(Integer.valueOf(((Node)it2.next()).getId()));
			}
			sol.add(route);
		}
	}

	public static ArrayList<ArrayList<Integer>> getSolution() {
		if (sol == null)
			throw new IllegalStateException("There is no available solution. To access a solution you must have run the .run method first");
		return sol;
	}

	public static Solution getSs() {
		return ss;
	}

	public static void setSs(Solution ss1) {
		ss = ss1;
	}

	public static ArrayList<ArrayList<Integer>> getSol() {
		return sol;
	}

	public Element getRunning() {
		return running;
	}

	public static void setSol(ArrayList<ArrayList<Integer>> sol) {
		MyMSH.sol = sol;
	}

	public void setRunning(Element running) {
		this.running = running;
	}
	
	public static void setSetPartitioningSolver(ISetCoveringSolver solver)
	  {
	    hc = solver;
	  }
}
