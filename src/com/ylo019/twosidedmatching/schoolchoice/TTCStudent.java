package com.ylo019.twosidedmatching.schoolchoice;

import com.ylo019.twosidedmatching.schoolchoiceobjects.iRejectable;

public class TTCStudent extends Student {
	
	private TTCSchool lastApplication;
	
	public TTCStudent(String name) {
		super(name);
		lastApplication = null;
		// TODO Auto-generated constructor stub
	}
	
    @Override
    public void makeProposal() {
    	iterator = rejectables.listIterator();
        while (iterator.hasNext()) {
            iRejectable school = iterator.next();
            if (school.isFree()) {
            	if (school instanceof TTCSchool)
            		lastApplication = (TTCSchool)school;
                school.receivesProposal(this);
                return;
            }
        }
    }
    
    @Override
    public void isEngaged(iRejectable rejectable) {
    	super.isEngaged(rejectable);
    	lastApplication = null;
    }
    
    @Override
    public void isRejected(iRejectable rejectable) {
    	super.isRejected(rejectable);
    	lastApplication = null;
    }
    
    public TTCSchool getLastApplication() {
    	return lastApplication;
    }

}
