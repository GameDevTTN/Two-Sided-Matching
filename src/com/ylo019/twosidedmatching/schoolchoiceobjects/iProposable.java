/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ylo019.twosidedmatching.schoolchoiceobjects;

/**
 *
 * @author ylo019
 */
public interface iProposable extends iAgent {
    
    public void makeProposal();
    public void isEngaged(iRejectable rejectable);
    public void isRejected(iRejectable rejectable);
    
}
