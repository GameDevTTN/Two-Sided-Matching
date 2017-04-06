/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoice;

import com.ylo019.twosidedmatching.schoolchoiceobjects.iRejectable;

/**
 *
 * @author ylo019
 */
public class AdaptiveStudent extends Student {

    public AdaptiveStudent(String name) {
        super(name);
    }
    
    @Override
    public void makeProposal() {
        while (iterator.hasNext()) {
            iRejectable school = iterator.next();
            if (school.isFreeSinceRound()) {
                school.receivesProposal(this);
                return;
            }
        }
    }
    
}
