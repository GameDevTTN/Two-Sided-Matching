package com.ylo019.marriageproblem.algorithms;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ylo019.marriageproblem.algorithms.MallowsOnlineImplementation.MallowsPreferencesContainer;

import Main.Observers.System.IO;
import Main.Observers.System.MessageType;
import Main.Observers.System.PostBox;
import Main.Settings.Configurations;
import Main.Settings.Format;
import Main.Settings.Settings;

public class MainApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainApp app = new MainApp();
		app.start(false, "140817/SET1/RealRun1");
	}
	
	private static iAlgorithm[][] ALGORITHMS = {standardAlgo(), chineseParallel(new int[]{2,3, 4, 8, 16, 32}), 
			basicDACB(new int[]{2,3,4,6,8,16, 32, 64, 128, 256, 512}),
			extendedDACB(new int[]{2,4,6, 8}, new int[]{50, 100, 200, 400, 800, 1600}),
			ttcFamily(new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048})};
	
	private static List<iAlgorithm> getAlgorithms() {
		List<iAlgorithm> list = new ArrayList<>();
		for (iAlgorithm[] arr : ALGORITHMS) {
			list.addAll(Arrays.asList(arr));
		}
		return list;
	}
	
	private static iAlgorithm[] standardAlgo() {
		return new iAlgorithm[] {new TopTradingCycle(), new DeferredAcceptance(), new EfficiencyAdjustedDeferredAcceptance(), new ChineseParallel(1), new DeferredAcceptanceWithCircuitBreaker(1)};
	}
	
	private static iAlgorithm[] chineseParallel(int[] params) {
		iAlgorithm[] output = new iAlgorithm[params.length];
		for (int i = 0; i < params.length; i++) {
			output[i] = new ChineseParallel(params[i]);
		}
		return output;
	}
	
	private static iAlgorithm[] basicDACB(int[] params) {
		iAlgorithm[] output = new iAlgorithm[params.length];
		for (int i = 0; i < params.length; i++) {
			output[i] = new DeferredAcceptanceWithCircuitBreaker(params[i]);
		}
		return output;
	}
	
	private static iAlgorithm[] extendedDACB(int[] k_params, int[] j_params) {
		iAlgorithm[] output = new iAlgorithm[k_params.length * j_params.length];
		for (int i = 0; i < k_params.length; i++) {
			for (int j = 0; j < j_params.length; j++) {
				output[i * j_params.length + j] = new DeferredAcceptanceWithCircuitBreaker(k_params[i], j_params[j]);
			}
		}
		return output;
	}
	
	private static iAlgorithm[] ttcFamily(int[] params) {
		iAlgorithm[] output = new iAlgorithm[params.length];
		for (int i = 0; i < params.length; i++) {
			output[i] = new TTCFamily(params[i]);
		}
		return output;
	}
	
	public void start(boolean isDebug, String path) {
    	System.out.println(LocalDateTime.now());
    	List<iAlgorithm> algorithms = getAlgorithms();
    	if (isDebug) {
    		Settings.PATH = "TESTDATA/" + path;
    	} else {
    		Settings.PATH = "GOODDATA/" + path;
    	}
        Configurations.getConfigurations().setOutput(new MessageType[]{MessageType.PRINT});
        IO.getConsole();
        Configurations.getConfigurations().init();
        //int minSize = 100;
        //int maxSize = 3000;
        int[] studentSizes = {4096, 8192};
        float[] studentMallows = new float[]{0.2f, 2.0f};
        float[] schoolMallows = new float[]{0.75f, 0.875f, 1.0f};
        int runSize = 2;
        int[] runs = new int[1]; //the googlesheet cannot handle more than runs.length = 2
        Arrays.fill(runs, runSize);
        //int currentSize = minSize;
        //print the heading once
        PostBox.broadcast(MessageType.PRINT, "SUMMARY");
        String sentence = "";
        for (String s : new String[]{"L_PE", "L_Improvable", "L_Borda", "BP", "L_Envious", "L_Rank1", "L_Rank2", "L_Rank3", "L_Rank4"}) {
            sentence += "\t" + s + "\tStd_Dev";
        }
        sentence += "\tCount\truns\tstudentMallows\tschoolMallows";

        PostBox.broadcast(MessageType.PRINT, sentence);

        for (int currentSize : studentSizes) {
        	System.out.println(LocalDateTime.now());
        	System.out.println(currentSize);
            for (float i : studentMallows) {
            	System.out.println("\t" + i);
                for (float j : schoolMallows) {
                	System.out.println("\t\t" + j);
        			for (int runsInSet : runs) {
        				System.out.println("\t\t\t" + runsInSet);
        				double[][] scores = new double[algorithms.size()][9];
        				double[][] xSquared = new double[algorithms.size()][9];
	                	for (int k = 0; k < runsInSet; k++) {
			        		MallowsPreferencesContainer left = MallowsOnlineImplementation.factory(currentSize, currentSize, i);
			        		MallowsPreferencesContainer right = MallowsOnlineImplementation.factory(currentSize, currentSize, j);
			        		for (int l = 0; l < algorithms.size(); l++) {
			        			if (isDebug || l % 1 == 0) {
			        				System.out.println(algorithms.get(l).toString());
			        			}
			        			int[] solutions = algorithms.get(l).solve(left, right);
			        			
			        			if (!GraphAddon.hasCycle(left, solutions)) {
			        				scores[l][0]++; //number of PE profiles
			        				xSquared[l][0]++; //1 squared = 1
			        			}
			        			
			        			double piAgents = GraphAddon.paretoImprovableAgents(left, solutions)/((double)currentSize);
			        			scores[l][1] += piAgents;
			        			xSquared[l][1] += Math.pow(piAgents, 2);
			        			//% of improvable agents
			        			
			        			double bordaSum = GraphAddon.bordaSum(left, solutions)/((double)currentSize * (currentSize - 1));
			        			scores[l][2] += bordaSum;
			        			xSquared[l][2] += Math.pow(bordaSum, 2);
			        			//borda count n-1 to 0
			        			
			        			double bpCount = GraphAddon.blockingPairsCount(left, right, solutions)/((double)currentSize * (currentSize - 1));
			        			scores[l][3] += bpCount;
			        			xSquared[l][3] += Math.pow(bpCount, 2);
			        			//number of blocking pairs
			        			
			        			double envyAgents = GraphAddon.enviousAgentCount(left, right, solutions)/((double)currentSize);
			        			scores[l][4] += envyAgents;
			        			xSquared[l][4] += Math.pow(envyAgents, 2);
			        			
			        			int[] ranks = GraphAddon.topRanks(4, left, solutions);
			        			for (int m = 0; m < 4; m++) {
			        				scores[l][m + 5] += ranks[m];
			        				xSquared[l][m + 5] += Math.pow(ranks[m], 2);
			        			}
		        				if (isDebug) {
				        			//System.out.println(Arrays.toString(solutions));
			        			}
			        		}	
	//	                	for (int l = 0; l < algorithms.size(); l++) {
	//	                		if (scores[l][5] > scores[6][5]) {
	//	                			System.out.println(Arrays.deepToString(scores));
	//	                			System.out.println(left.toString());
	//	                			System.out.println(right.toString());
	//	                			throw new RuntimeException("halt");
	//	                		}
	//	                	}
			        		if (isDebug) {
			        			System.out.println(i);
				        		System.out.println(Format.Format(left));
				        		System.out.println(Format.Format(right));
			        		}
	                	}
	                	for (int l = 0; l < algorithms.size(); l++) {
	                		String string = algorithms.get(l).toString();
	                		for (int m = 0; m < scores[l].length; m++) {
	                			string += "\t" + Format.Format(scores[l][m]/((double)runsInSet));
	                			double stdDev = Math.pow(xSquared[l][m]/((double)runsInSet) - Math.pow(scores[l][m]/((double)runsInSet),  2), 0.5);
	                			string += "\t" + Format.Format(stdDev);
	                		}
	                		string += "\t" + currentSize + "\t" + runsInSet + "\t" + Format.Format(i) + "\t" + Format.Format(j);
	                		PostBox.broadcast(MessageType.PRINT, string);
	                	}
        			}
                }
            }
            //currentSize += (currentSize < 10 ? 1 : (currentSize < 100 ? 10 : (currentSize < 1000 ? 100 : 1000)));
        }
        
        IO.getConsole().close();
        System.out.println(LocalDateTime.now());
	}

}
