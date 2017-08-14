package com.ylo019.twosidedmatching.schoolchoice;


public class StackBasedSchoolChoice {

	public double[] start(Student[] students, School[] schools) {
        for (Student s : students) {
            while (s.isFree()) {
                s.makeProposal();
            }
            for (School school : schools) {
            	school.newRound();
            }
        }
        int[][] leftRanks = new int[students.length][];
        for (int i = 0; i < students.length; i++) {
        	leftRanks[i] = students[i].getRanks();
        }
        int[][] rightRanks = new int[schools.length][];
        for (int i = 0; i < schools.length; i++) {
        	rightRanks[i] = schools[i].getRanks();
        }
        int blockingPair = 0;
        for (Student s : students) {
            blockingPair += s.howManyBlockingPairs();
        }
		return Helper.ranksToScore(leftRanks, rightRanks, (double)blockingPair/(students.length * schools.length));
	}

}
