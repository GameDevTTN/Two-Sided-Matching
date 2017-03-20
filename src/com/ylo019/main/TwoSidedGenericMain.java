/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.main;

import Main.Observers.Auxiliary.PreferenceType;
import Main.Observers.BordaRelated.BordaOrderBias;
import Main.Observers.BordaRelated.BordaScoreRaw;
import Main.Observers.BordaRelated.BordaWorstAgentToRank;
import Main.Observers.BordaRelated.PluralityScoreRaw;
import Main.Observers.CompareTables;
import Main.Observers.EquivalentAlgorithm;
import Main.Observers.LaTeXTablePrinter;
import Main.Observers.LogWriter;
import Main.Observers.PreferenceOrder;
import Main.Observers.PreferencesCounter;
import Main.Observers.Proportionality.SDProportionalSummary;
import Main.Observers.Proportionality.SDProportionalityAgentCount;
import Main.Observers.Proportionality.SDProportionalityChecker;
import Main.Observers.ResultCollatorWrapper;
import Main.Observers.System.IO;
import Main.Observers.System.MessageType;
import Main.Observers.System.PostBox;
import Main.Observers.Timer;
import Main.Observers.UtilitiesRelated.CustomUtilityModelRaw;
import Main.Observers.UtilitiesRelated.ExponentialUtilityPercentageOfMax;
import Main.Observers.UtilitiesRelated.ExponentialUtilityRaw;
import Main.Observers.iResultsCollator;
import Main.Settings.Configurations;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import MatchingAlgorithm.Auxiliary.Restrictions.LimitedByAgentProposal;
import MatchingAlgorithm.Auxiliary.Restrictions.LimitedByItemHeldBy;
import MatchingAlgorithm.Auxiliary.Restrictions.RestrictionFactoryAdaptor;
import MatchingAlgorithm.Auxiliary.Restrictions.iRestriction;
import MatchingAlgorithm.Taxonomy.GenericImplementation;
import Pair.Pair;
import UtilityModels.AntiPluralityModel;
import UtilityModels.PluralityModel;
import com.ylo019.twosidedmatching.GaleShapley;
import com.ylo019.twosidedmatching.GeneralisedImmediateAndDeferredAcceptance;
import com.ylo019.twosidedmatching.NoMemoryQueue;
import com.ylo019.twosidedmatching.NoMemoryStack;
import com.ylo019.twosidedmatching.TwoSidedAlgorithmObserver;
import com.ylo019.twosidedmatching.TwoSidedHungarianWrapper;
import com.ylo019.twosidedmatching.TwoSidedTaxonomy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ordinalpreferencegenerator.ICRandom;
import ordinalpreferencegenerator.Mallows;
import ordinalpreferencegenerator.iOrdinalIterator;

/**
 *
 * @author ylo019
 */
public class TwoSidedGenericMain {
    
    /*
    NoMemoryStack is not broadcasting the iProbabilityMatrix
    */
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        TwoSidedGenericMain gm = new TwoSidedGenericMain();
        TwoSidedGenericMain.modifySettings();
        gm.start();
        PostBox.broadcast(MessageType.PRINT, "Time taken: " + (System.currentTimeMillis() - start)/1000 + "s");
    }
    
    
    public void start() {
        
        IO.getConsole();
        //CsvLog.getConsole();
        Configurations.getConfigurations().init();
        //Settings.init();
        do {
            PostBox.broadcast(MessageType.PRINT, Configurations.getConfigurations().peekIterator().getName());
            PostBox.broadcast(MessageType.PRINT, "Beginning of Iterator " + Configurations.getConfigurations().peekIterator().getName());
            iOrdinalIterator op = Configurations.getConfigurations().peekIterator();
            if (!Configurations.getConfigurations().nextIterator()) {
                break;
            }
            iOrdinalIterator op2 = Configurations.getConfigurations().peekIterator();
            int ii = 0;
            while (op.hasNext() && op2.hasNext()) {
                PreferenceProfile left = op.getNext();
                PreferenceProfile right = op2.getNext();
                PostBox.broadcast(MessageType.PREFERENCE, new Pair<>(left, right));
                PostBox.broadcast(MessageType.SYSTEM, new Pair<>("End Preference", ""));
                System.out.println(++ii);
            }
            PostBox.broadcast(MessageType.PRINT, "End of size");
            PostBox.broadcast(MessageType.SYSTEM, new Pair<>("End Size",""));
        } while (Configurations.getConfigurations().nextIterator());
            
        PostBox.broadcast(MessageType.PRINT, "End of calculation");
        PostBox.broadcast(MessageType.SYSTEM, new Pair<>("End Calculation", ""));
        IO.getConsole().close();
        //CsvLog.getConsole().close();
    }
    
    private static void modifySettings() {
        Configurations c = Configurations.getConfigurations();
        c.setAlgorithms(getAlgorithms());
        c.setResultCollator(getResultCollator());
        c.setPreferenceIterator(getPreferenceProfiles());
        c.setOutput(getOutput());
    }
    
    private static MessageType[] getOutput() {
        return new MessageType[]{MessageType.PREFERENCE, 
//            MessageType.ALGORITHM_NAME, 
//            MessageType.DETAILS,
            MessageType.OUTPUT, 
            MessageType.SUMMARY, 
            MessageType.SYSTEM, 
            MessageType.PRINT};

    }
    
    private static iOrdinalIterator[] getPreferenceProfiles() {
        return new iOrdinalIterator[]{new ICRandom(10000, 20, 20), new ICRandom(10000, 20, 20), new Mallows(10000, 30, 30, 0.4f), new Mallows(10000, 30, 30, 0.4f)}; //no
//        return new iOrdinalIterator[]{new IC(3, 3), new ICRandom(100000, 4, 4), new ICRandom(100000, 5, 5), new ICRandom(50000, 6, 6), new ICRandom(50000, 7, 7)};
//        return new iOrdinalIterator[]{new IC(3, 3), new ICRandom(10000, 4, 4), new ICRandom(10000, 5, 5)}; //no
//        return new iOrdinalIterator[]{new ICRandom(50000, 10), new ICRandom(50000, 15), new ICRandom(50000, 20), new ICRandom(50000, 25), new ICRandom(50000, 30), new ICRandom(50000, 35), new ICRandom(50000, 40), new ICRandom(50000, 45), new ICRandom(50000, 50)};
//        return new iOrdinalIterator[]{new Mallows(100000, 5, 5, 0f),new Mallows(100000, 5, 5, 0.1f),new Mallows(100000, 5, 5, 0.2f), new Mallows(100000, 5, 5, 0.3f), new Mallows(100000, 5, 5, 0.4f),
//                                    new Mallows(100000, 5, 5, 0.5f), new Mallows(100000, 5, 5, 0.6f), new Mallows(100000, 5, 5, 0.7f), new Mallows(100000, 5, 5, 0.8f), new Mallows(100000, 5, 5, 0.9f),
//                                    new Mallows(100000, 10, 10, 0f),new Mallows(100000, 10, 10, 0.1f),new Mallows(100000, 10, 10, 0.2f), new Mallows(100000, 10, 10, 0.3f), new Mallows(100000, 10, 10, 0.4f),
//                                    new Mallows(100000, 10, 10, 0.5f), new Mallows(100000, 10, 10, 0.6f), new Mallows(100000, 10, 10, 0.7f), new Mallows(100000, 10, 10, 0.8f), new Mallows(100000, 10, 10, 0.9f),
//                                    new Mallows(100000, 15, 15, 0f),new Mallows(100000, 15, 15, 0.1f),new Mallows(100000, 15, 15, 0.2f), new Mallows(100000, 15, 15, 0.3f), new Mallows(100000, 15, 15, 0.4f),
//                                    new Mallows(100000, 15, 15, 0.5f), new Mallows(100000, 15, 15, 0.6f), new Mallows(100000, 15, 15, 0.7f), new Mallows(100000, 15, 15, 0.8f), new Mallows(100000, 15, 15, 0.9f),
//                                    new Mallows(100000, 20, 20, 0f),new Mallows(100000, 20, 20, 0.1f),new Mallows(100000, 20, 20, 0.2f), new Mallows(100000, 20, 20, 0.3f), new Mallows(100000, 20, 20, 0.4f),
//                                    new Mallows(100000, 20, 20, 0.5f), new Mallows(100000, 20, 20, 0.6f), new Mallows(100000, 20, 20, 0.7f), new Mallows(100000, 20, 20, 0.8f), new Mallows(100000, 20, 20, 0.9f)};
//        return new iOrdinalIterator[]{new Mallows(10000, 10, 10, 0f),new Mallows(10000, 10, 10, 0.1f),new Mallows(10000, 10, 10, 0.2f), new Mallows(10000, 10, 10, 0.3f), new Mallows(10000, 10, 10, 0.4f),
                                    //new Mallows(10000, 10, 10, 0.5f), new Mallows(10000, 10, 10, 0.6f), new Mallows(10000, 10, 10, 0.7f), new Mallows(10000, 10, 10, 0.8f), new Mallows(10000, 10, 10, 0.9f)}; //no
    }
    //at for n = 10, at about 0.65 Mallows, NB out-performs YS
    
    
    private static TwoSidedAlgorithmObserver[] getAlgorithms() {
        ArrayList<TwoSidedAlgorithmObserver> out = new ArrayList<>();
        out.add(new TwoSidedAlgorithmObserver(new NoMemoryStack()));
        out.add(new TwoSidedAlgorithmObserver(new NoMemoryQueue()));
        out.add(new TwoSidedAlgorithmObserver(new GaleShapley()));
        out.add(new TwoSidedAlgorithmObserver(new GeneralisedImmediateAndDeferredAcceptance()));
        out.add(new TwoSidedAlgorithmObserver(new GeneralisedImmediateAndDeferredAcceptance(1)));
        out.add(new TwoSidedAlgorithmObserver(new GeneralisedImmediateAndDeferredAcceptance(2)));
        out.add(new TwoSidedAlgorithmObserver(new GeneralisedImmediateAndDeferredAcceptance(30)));
        for (int i = 0; i < 10; i++) {
            final int j = i;
            out.add(new TwoSidedAlgorithmObserver(new GaleShapley(new RestrictionFactoryAdaptor() {

                @Override
                public iRestriction[] getRestrictions(int agent, int item) {
                    return new iRestriction[]{new LimitedByAgentProposal(agent, j)};
                }
            })));
            out.add(new TwoSidedAlgorithmObserver(new GaleShapley(new RestrictionFactoryAdaptor() {

                @Override
                public iRestriction[] getRestrictions(int agent, int item) {
                    return new iRestriction[]{new LimitedByItemHeldBy(item, j)};
                }
            })));
        }
        boolean fixedOrder = true;
        out.add(new TwoSidedAlgorithmObserver(new TwoSidedTaxonomy(new GenericImplementation(true, true, true, false, false), PreferenceType.TWO_SIDED_PROPOSER, fixedOrder)));
        out.add(new TwoSidedAlgorithmObserver(new TwoSidedTaxonomy(new GenericImplementation(true, true, true, false, false), PreferenceType.TWO_SIDED_PROPOSEE, fixedOrder)));
        out.add(new TwoSidedAlgorithmObserver(new TwoSidedTaxonomy(new GenericImplementation(true, true, false, false, false), PreferenceType.TWO_SIDED_PROPOSER, fixedOrder)));
        out.add(new TwoSidedAlgorithmObserver(new TwoSidedTaxonomy(new GenericImplementation(true, true, false, false, false), PreferenceType.TWO_SIDED_PROPOSEE, fixedOrder)));
        out.add(new TwoSidedAlgorithmObserver(new TwoSidedHungarianWrapper()));
        //boolean fixOrder = true; always fixing order
        TwoSidedAlgorithmObserver[] arr = new TwoSidedAlgorithmObserver[0];
        arr = out.toArray(arr);
        return arr;
    }
    
    private static iResultsCollator[] getResultCollator() {
        List<iResultsCollator> list = new ArrayList<iResultsCollator>();
        list.addAll(Arrays.asList(new iResultsCollator[]{new LogWriter(), new PreferencesCounter(), new PreferenceOrder(),
            new CompareTables(), new EquivalentAlgorithm(), 
            new ResultCollatorWrapper(new CustomUtilityModelRaw(new PluralityModel()), PreferenceType.TWO_SIDED_PROPOSER),
            new ResultCollatorWrapper(new CustomUtilityModelRaw(new PluralityModel()), PreferenceType.TWO_SIDED_PROPOSEE),
            new ResultCollatorWrapper(new CustomUtilityModelRaw(new AntiPluralityModel()), PreferenceType.TWO_SIDED_PROPOSER),
            new ResultCollatorWrapper(new CustomUtilityModelRaw(new AntiPluralityModel()), PreferenceType.TWO_SIDED_PROPOSEE)}));
        for (double d : getExponentialParams()) {
            list.add(new ResultCollatorWrapper(new ExponentialUtilityRaw(d), PreferenceType.TWO_SIDED_PROPOSER));
            list.add(new ResultCollatorWrapper(new ExponentialUtilityRaw(d), PreferenceType.TWO_SIDED_PROPOSEE));
            list.add(new ResultCollatorWrapper(new ExponentialUtilityPercentageOfMax(d), PreferenceType.TWO_SIDED_PROPOSER));
            list.add(new ResultCollatorWrapper(new ExponentialUtilityPercentageOfMax(d), PreferenceType.TWO_SIDED_PROPOSEE));
        }
        iResultsCollator[] arg1 = new iResultsCollator[]{new BordaScoreRaw(), new BordaWorstAgentToRank(), new BordaOrderBias(), new PluralityScoreRaw(),
            new SDProportionalSummary(), new SDProportionalityChecker(), new SDProportionalityAgentCount()};
        iResultsCollator[] arg2 = new iResultsCollator[]{new BordaScoreRaw(), new BordaWorstAgentToRank(), new BordaOrderBias(), new PluralityScoreRaw(),
            new SDProportionalSummary(), new SDProportionalityChecker(), new SDProportionalityAgentCount()};
        List<iResultsCollator> twoSided = new ArrayList<>();
        for (iResultsCollator arg : arg1) {
            twoSided.add(new ResultCollatorWrapper(arg, PreferenceType.TWO_SIDED_PROPOSER));
        }
        for (iResultsCollator arg : arg2) {
            twoSided.add(new ResultCollatorWrapper(arg, PreferenceType.TWO_SIDED_PROPOSEE));
        }
        list.addAll(twoSided);
        list.addAll(Arrays.asList(new iResultsCollator[]{new LaTeXTablePrinter(), new Timer()}));
        iResultsCollator[] array = new iResultsCollator[0];
        array = list.toArray(array);
        return array;
    }
    
    private static double[] getExponentialParams() {
        //return new double[]{-1.0, -0.75, -0.5, -0.25, 0.0, 0.5, 1.0, 1.5, 2.0};
        return new double[]{0.0};
    }
    
}
