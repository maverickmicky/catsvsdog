package com.maverick.puzzle.catvsdog;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * Created by abhinav on 2017-03-21.
 */
public class CatVsDog {
	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		//		Stream<String> stream = in.lines().limit(numberOfLinesToBeRead);
		try {
			int numberOfTestCases = Integer.valueOf(in.readLine().trim());
			int[] satisfiedVoters = new int[numberOfTestCases];
			for (int i = 0; i < numberOfTestCases; i++) {
				String[] caseConfig = in.readLine().trim().split(" ");
				int totalCats = Integer.valueOf(caseConfig[0]);
				int totalDogs = Integer.valueOf(caseConfig[1]);
				int totalVoters = Integer.valueOf(caseConfig[2]);

				if (totalCats < 1 || totalCats > 100) {
					throw new IllegalArgumentException("Invalid number of cats, rule: 1<= cats <=100");
				}
				if (totalDogs < 1 || totalDogs > 100) {
					throw new IllegalArgumentException("Invalid number of dogs, rule: 1<= dogs <=100");
				}
				if (totalVoters < 0 || totalVoters > 500) {
					throw new IllegalArgumentException("Invalid number of voters, rule: 0<= voters <=500");
				}


				Map<String, Candidate> catVotesFor = new HashMap<>(totalCats);
				Map<String, Candidate> dogVotesFor = new HashMap<>(totalDogs);
				//				Map<String, Integer> catVotesAgainst = new HashMap<>(totalCats);
				//				Map<String, Integer> dogVotesAgainst = new HashMap<>(totalDogs);

				for (int j = 0; j < totalVoters; j++) {
					String[] vote = in.readLine().trim().toUpperCase().split(" ");
					if (vote.length != 2) {
						throw new IllegalArgumentException("Invalid number of votes, rule: only 2 votes, one for a cat and one for a dog");
					}
					if (vote[0].startsWith("C")) {
						int catNumber = Integer.valueOf(vote[0].split("C")[1]);
						if (catNumber > totalCats || catNumber < 1) {
							throw new IllegalArgumentException("Invalid cat voted");
						}
						if (catVotesFor.containsKey(vote[0])) {
							catVotesFor.get(vote[0]).rankUp();
						} else {
							catVotesFor.put(vote[0], new Candidate(1, 1));
						}
					} else if (vote[0].startsWith("D")) {
						int dogNumber = Integer.valueOf(vote[0].split("D")[1]);
						if (dogNumber > totalDogs || dogNumber < 1) {
							throw new IllegalArgumentException("Invalid dog voted");
						}
						if (dogVotesFor.containsKey(vote[0])) {
							dogVotesFor.get(vote[0]).rankUp();
						} else {
							dogVotesFor.put(vote[0], new Candidate(1, 1));
						}
					} else {
						throw new IllegalArgumentException("Invalid vote, only allowed for cats and dogs");
					}

					if (vote[1].startsWith("C")) {
						int catNumber = Integer.valueOf(vote[1].split("C")[1]);
						if (catNumber > totalCats || catNumber < 1) {
							throw new IllegalArgumentException("Invalid cat voted");
						}
						if (catVotesFor.containsKey(vote[1])) {
							catVotesFor.get(vote[1]).rankDown();
						} else {
							catVotesFor.put(vote[1], new Candidate(0, -1));
						}
					} else if (vote[1].startsWith("D")) {
						int dogNumber = Integer.valueOf(vote[1].split("D")[1]);
						if (dogNumber > totalDogs || dogNumber < 1) {
							throw new IllegalArgumentException("Invalid dog voted");
						}
						if (dogVotesFor.containsKey(vote[1])) {
							dogVotesFor.get(vote[1]).rankDown();
						} else {
							dogVotesFor.put(vote[1], new Candidate(0, -1));
						}
					} else {
						throw new IllegalArgumentException("Invalid vote, only allowed for cats and dogs");
					}
				}
				catVotesFor = sortByValue(catVotesFor);
				dogVotesFor = sortByValue(dogVotesFor);

				String topCatFor = catVotesFor.keySet().toArray()[0].toString();
				Integer topCatVotesFor = catVotesFor.get(topCatFor).totalVotes;

				String topDogFor = dogVotesFor.keySet().toArray()[0].toString();
				Integer topDogVotesFor = dogVotesFor.get(topDogFor).totalVotes;

				if (topCatVotesFor >= topDogVotesFor) {
					satisfiedVoters[i] = topCatVotesFor;
				} else {
					satisfiedVoters[i] = topDogVotesFor;
				}
			}
			Arrays.stream(satisfiedVoters).forEach(n -> System.out.println(n));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		return map.entrySet()
		          .stream()
		          .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
		          .collect(Collectors.toMap(
				          Map.Entry::getKey,
				          Map.Entry::getValue,
				          (e1, e2) -> e1,
				          HashMap::new
		          ));
	}

	static class Candidate implements Comparable {
		int totalVotes;
		int rank;

		public Candidate(int totalVotes, int rank) {
			this.totalVotes = totalVotes;
			this.rank = rank;
		}

		public void rankUp() {
			rank = +1;
			totalVotes += 1;
		}

		public void rankDown() {
			rank = -1;
		}

		@Override
		public int compareTo(Object o) {
			Candidate c = (Candidate) o;
			if (this.rank > c.rank) {
				return 1;
			} else if (this.rank < c.rank) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
