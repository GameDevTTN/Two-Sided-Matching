package com.ylo019.marriageproblem.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MallowsOnlineImplementation {
	
	private List<Integer> array = new ArrayList<>();
	
	private MallowsOnlineImplementation(int size) {
        
        for (int i = 0; i < size; i++) {
            array.add(i + 1);
        }
        Collections.shuffle(array);

	}
	
	public static MallowsPreferencesContainer factory(int agentCount, int objectCount, double param) {
		if (param < 0.0d || param > 2.0d) {
			//make an exception, if param == MAGIC_NUMBER, return a 2-sided container
			System.out.println(param);
			throw new RuntimeException("Mallow Preferences Container factory: param out of bounds");
		}
		if (param <= 1.0d) {
			MallowsOnlineImplementation moi = new MallowsOnlineImplementation(objectCount);
			MallowsPreferencesContainer container = moi.new MallowsPreferencesContainer(agentCount, param, moi.array);
			return container;
		} else {
			MallowsOnlineImplementation moi = new MallowsOnlineImplementation(objectCount);
			MallowsPreferencesContainer container = moi.new TwoTieredPreferencesContainer(agentCount, param - 1, moi.array);
			return container;
		}
	}
	
	public class TwoTieredPreferencesContainer extends MallowsPreferencesContainer {
		
		protected TwoTieredPreferencesContainer(int count, double param, List<Integer> array) {
			mallowsParam = param;
			for (int i = 0; i < count; i++) {
				preferencesList.add(new TwoTieredPreferences(array));
			}
		}
		
	}
	
	public class MallowsPreferencesContainer {
		
		protected List<MallowsPreferences> preferencesList = new ArrayList<>(); 
		protected double mallowsParam;
		
		protected MallowsPreferencesContainer() {}
		
		protected MallowsPreferencesContainer(int count, double param, List<Integer> array) {
			mallowsParam = param;
			for (int i = 0; i < count; i++) {
				preferencesList.add(new MallowsPreferences(array));
			}
		}
		
		public int size() {
			return preferencesList.size();
		}
		
		public List<MallowsPreferences> getPreferences() {
			return new ArrayList<MallowsPreferences>(preferencesList);
		}
		
		@Override
		public String toString() {
			return preferencesList.toString().replaceAll("], ", "]\n");
		}
		
		public class TwoTieredPreferences extends MallowsPreferences {
			
			//use rankedItems.size to determine how many items are there originally
			//if rankedIndexCounter < (strictly less than) 0.5 * rankedItems.size, ignore the last
			//0.5 * rankedItems.size items in unrankedItems
			
			
			protected TwoTieredPreferences(List<Integer> array) {
				super(array);
			}
			
			@Override
			protected void rankMoreItems() {
				if (unrankedItems.isEmpty()) {
					return;
				}
                double random = Math.random();
                double chance = 0.0;
                boolean topOrBottom = rankedIndexCounter < rankedItems.length/2;
                //if false, then rank all, if true, only use unrankedItems.size() - rankedItems.length/2
                int maxRank = (topOrBottom ? unrankedItems.size() - rankedItems.length/2 : unrankedItems.size());
                for (int i = 0; i < maxRank; i++) {
                    if (MallowsPreferencesContainer.this.mallowsParam == 1.0) {
                        chance += (1.0/maxRank);
                    } else {
                        chance += ((1 - MallowsPreferencesContainer.this.mallowsParam) 
                        		* Math.pow(MallowsPreferencesContainer.this.mallowsParam, i))
                        		/(1 - Math.pow(MallowsPreferencesContainer.this.mallowsParam, maxRank));
                    }
                    if (random < chance || i == (maxRank - 1)) {
                        rankedItems[rankedIndexCounter++] = unrankedItems.remove(i);
                        break;
                    }
                }
			}
			
		}
		
		
		public class MallowsPreferences {
			
			protected List<Integer> unrankedItems;
			protected int[] rankedItems;
			protected int rankedIndexCounter = 0;
			
			protected MallowsPreferences(List<Integer> array) {
				unrankedItems = new ArrayList<>(array);
				rankedItems = new int[array.size()];
			}
			
			public int size() {
				return rankedItems.length;
			}
			
			@Override
			public String toString() {
				return Arrays.toString(Arrays.copyOfRange(rankedItems, 0, rankedIndexCounter));
			}
			
			public int get(int index) { //0 based
				if (index < 0 || index >= size()) {
					throw new RuntimeException("MallowsPreferences get(index): index out of bounds");
				}
				while (index >= rankedIndexCounter) {
					if (unrankedItems.isEmpty()) {
						throw new RuntimeException("MallowsPreferences get(index): unrankedItems is empty");
					}
					rankMoreItems();
				}
				return rankedItems[index];
			}
			
			public int find(int[] currentMatching, int rank) {
				for (int i = 0; i < rankedIndexCounter; i++) {
					if (currentMatching[rankedItems[i] - 1] == 0) {
						if (--rank == 0) {
							return rankedItems[i]; //1 based
						}
					}
				}
				while (!unrankedItems.isEmpty()) {
					rankMoreItems();
					if (currentMatching[rankedItems[rankedIndexCounter - 1] - 1] == 0) {
						if (--rank == 0) {
							return rankedItems[rankedIndexCounter - 1]; //1 based
						}
					}
				}
				return 0;
			}
			
			public boolean doesPrefers(int item1, int item2) { //item number, i.e. 1 based
				
				/*
				 * returns true if and only if agent prefers item1 to item2
				 */
				
				if (item1 <= 0 || item2 <= 0 || item1 > size() || item2 > size()) {
					throw new RuntimeException("MallowsPreferences compare(item1, item2): at least one of the item index is out of bounds");
				}
				for (int i = 0; i < rankedIndexCounter; i++) {
					if (rankedItems[i] == item1) {
						return true;
					} else if (rankedItems[i] == item2) {
						return false;
					}
				}
				while (!unrankedItems.isEmpty()) {
					rankMoreItems();
					if (rankedItems[rankedIndexCounter - 1] == item1) {
						return true;
					} else if (rankedItems[rankedIndexCounter - 1] == item2) {
						return false;
					}
				}
				throw new RuntimeException("MallowsPreferences compare(item1, item2): cannot find either items");
			}
			
			protected void rankMoreItems() {
				if (unrankedItems.isEmpty()) {
					return;
				}
                double random = Math.random();
                double chance = 0.0;
                for (int i = 0; i < unrankedItems.size(); i++) {
                    if (MallowsPreferencesContainer.this.mallowsParam == 1.0) {
                        chance += (1.0/unrankedItems.size());
                    } else {
                        chance += ((1 - MallowsPreferencesContainer.this.mallowsParam) 
                        		* Math.pow(MallowsPreferencesContainer.this.mallowsParam, i))
                        		/(1 - Math.pow(MallowsPreferencesContainer.this.mallowsParam, unrankedItems.size()));
                    }
                    if (random < chance || i == (unrankedItems.size() - 1)) {
                        rankedItems[rankedIndexCounter++] = unrankedItems.remove(i);
                        break;
                    }
                }
			}

			private void createAll() {
				// TODO Auto-generated method stub
				while (!unrankedItems.isEmpty()) {
					rankMoreItems();
				}
			}
		}


		public String getAll() {
			// TODO Auto-generated method stub
			for (MallowsPreferences mp : preferencesList) {
				mp.createAll();
			}
			return toString();
		}
	}


}
