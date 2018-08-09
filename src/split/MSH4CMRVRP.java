package split;

import dataReaders.CSVReader4VRPSCD;

public class MSH4CMRVRP {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length == 0){
			args=new String[7];
			args[0]= "./data/CMR-Zona2/Zona2.ini";
			args[3] = "Primera de John";
			args[1] = "100";
			args[2] = "1234";
			args[4] = "./config/RNI(6).RNN(3).RFI(6).RBI(6).csv";
			args[5] = "./output/tests/MSH4CMRVRP/debug/MSH4CMRVRP.DEBUG.xml";
		}
	
		CSVReader4VRPSCD.instance(args[0]);
			
		//CMRVRPArcEvaluator arcEvaluator=new CMRVRPArcEvaluator(gvrp, input, T);
		
	}
}
