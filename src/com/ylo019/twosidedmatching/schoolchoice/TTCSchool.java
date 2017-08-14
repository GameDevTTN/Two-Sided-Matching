package com.ylo019.twosidedmatching.schoolchoice;

import java.util.ArrayList;
import java.util.List;

import com.ylo019.twosidedmatching.schoolchoiceobjects.iProposable;

public class TTCSchool extends School {
	
	private iProposable onlyAcceptableStudent;
	private List<iProposable> applications = new ArrayList<>();
	private boolean isFreeSinceRound;

	public TTCSchool(String name, int capacity) {
		super(name, capacity);
		// TODO Auto-generated constructor stub
		isFreeSinceRound = true;
		onlyAcceptableStudent = null;
	}
	
	@Override
	public boolean isFreeSinceRound() {
		return isFreeSinceRound;
	}
	
	@Override
	public void setPreference(iProposable[] proposables) {
		super.setPreference(proposables);
		onlyAcceptableStudent = proposable.get(0);
	}
	
	@Override
	public void receivesProposal(iProposable proposer) {
		if (enrolled.size() < capacity) {
			if (proposer == onlyAcceptableStudent) {
				enrolled.add(proposer);
				proposer.isEngaged(this);
			} else {
				//search chain
				applications.add(proposer);
				List<TTCStudent> proposerChain = new ArrayList<>();
				if (proposer instanceof TTCStudent)
					proposerChain.add((TTCStudent)proposer);
				while (proposerChain.get(proposerChain.size() - 1).getLastApplication() != null) {
					TTCStudent nextStudent = proposerChain.get(proposerChain.size() - 1);
					TTCSchool nextStudentWants = ((TTCStudent) nextStudent).getLastApplication();
					TTCStudent schoolWants = ((TTCStudent)nextStudentWants.onlyAcceptableStudent);
					if (schoolWants == proposer) {
						for (TTCStudent p : proposerChain) {
							p.getLastApplication().enrolled.add(p);
							p.getLastApplication().applications.remove(p);
							p.isEngaged(p.getLastApplication());
						}
						//should not need a break here, as isEngaged should set getLastApplication to null
					} else {
						proposerChain.add(schoolWants);
					}
				}				
			}
		} else {
			proposer.isRejected(this);
		}
	}
	
	@Override
	public void newRound() {
		isFreeSinceRound = isFree();
		for (iProposable student : applications) {
			student.isRejected(this);
		}
		applications.clear();
		for (iProposable student : proposable) {
			if (student.isFree()) {
				onlyAcceptableStudent = student;
				return;
			}
		}
	}

}
