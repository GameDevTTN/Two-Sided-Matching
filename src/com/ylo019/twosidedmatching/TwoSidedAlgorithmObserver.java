/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching;

import Main.Observers.System.MessageType;
import Main.Observers.System.PostBox;
import Main.Observers.iAlgorithmObserver;
import MatchingAlgorithm.Auxiliary.PreferenceProfile;
import Pair.Pair;
import java.util.Observable;

/**
 *
 * @author ylo019
 */
public class TwoSidedAlgorithmObserver implements iAlgorithmObserver<iTwoSidedAlgorithm> {
    
    private iTwoSidedAlgorithm algorithm;
    
    public TwoSidedAlgorithmObserver(iTwoSidedAlgorithm algo) {
        if (algo != null) {
            algorithm = algo;
        } else {
            throw new RuntimeException("AlgorithmObserver(iAlgorithm): algo is null");
        }
    }

    @Override
    public void init() {
        PostBox.listen(this, MessageType.PREFERENCE);
    }

    @Override
    public void update(Observable o, Object o1) {
        if (o instanceof PostBox && o1 instanceof Pair) {
            PostBox p = (PostBox)o;
            Pair p1 = (Pair)o1;
            switch (p.getSource()) {
                case PREFERENCE:
                    if (p1.getS() instanceof PreferenceProfile && p1.getT() instanceof PreferenceProfile) {
                        PostBox.broadcast(MessageType.TABLE, new Pair<>(algorithm.getName(),algorithm.solve((PreferenceProfile) p1.getS(), (PreferenceProfile) p1.getT())));
                    }
                    break;
            }
        }
    }
    
    @Override
    public String getName() {
        return algorithm.getName();
    }
    
}
