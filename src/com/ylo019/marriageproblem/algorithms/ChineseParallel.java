package com.ylo019.marriageproblem.algorithms;

public class ChineseParallel extends DeferredAcceptanceWithRounds {
	
	private int param;
	
	public ChineseParallel(int param) {
		this.param = param;
	}

	@Override
	protected boolean isNewRound() {
		// TODO Auto-generated method stub
		return (getRoundNumber() % param == 0);
	}
	
	@Override
	public String toString() {
		return (param == 1 ? "NB" : (param == 2 ? "SA" : "CP p=" + param));
	}

}
