package com.maverick.puzzle.catvsdog;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * Created by abhinav on 2017-03-21.
 */
public class CatVsDog {
    private static int totalCats = 0;
    private static int totalDogs = 0;
    private static int totalVoters = 0;
    private static final String catVoteRegex = "C";
    private static final String dogVoteRegex = "D";

    public static void main(String[] args) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            int numberOfTestCases = Integer.valueOf(in.readLine().trim());
            int[] satisfiedVoters = new int[numberOfTestCases];
            for (int i = 0; i < numberOfTestCases; i++) {
                String[] caseConfig = in.readLine().trim().split(" ");
                totalCats = Integer.valueOf(caseConfig[0]);
                totalDogs = Integer.valueOf(caseConfig[1]);
                totalVoters = Integer.valueOf(caseConfig[2]);

                validateTotalPetsAndVoters();

                Map<String, PetCandidate> catVotes = new HashMap<>(totalCats);
                Map<String, PetCandidate> dogVotes = new HashMap<>(totalDogs);

                // read all votes line by line
                for (int j = 0; j < totalVoters; j++) {
                    String[] votes = in.readLine().trim().toUpperCase().split(" ");
                    if (votes.length != 2) {
                        throw new IllegalArgumentException("Invalid number of votes, rule: only 2 votes, one for a cat and one for a dog");
                    }
                    String keepVote = votes[0];
                    String throwVote = votes[1];
                    //check the vote to KEEP the pet
                    if (keepVote.startsWith(catVoteRegex)) {
                        validatePetNumber(totalCats, keepVote, catVoteRegex);

                        if (catVotes.containsKey(keepVote)) { //already voted before
                            catVotes.get(keepVote).rankUp();
                        } else { //new candidate voted
                            catVotes.put(keepVote, PetCandidate.createPetCandidateRankUp());
                        }
                    } else if (keepVote.startsWith(dogVoteRegex)) {
                        validatePetNumber(totalDogs, keepVote, dogVoteRegex);

                        if (dogVotes.containsKey(keepVote)) {  //already voted for
                            dogVotes.get(keepVote).rankUp();
                        } else { //new candidate voted
                            dogVotes.put(keepVote, PetCandidate.createPetCandidateRankUp());
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid vote, only allowed for cats and dogs");
                    }

                    //check the vote to THROW-OUT the pet
                    if (throwVote.startsWith(catVoteRegex)) {
                        validatePetNumber(totalCats, throwVote, catVoteRegex);

                        if (catVotes.containsKey(throwVote)) { //already voted before
                            catVotes.get(throwVote).rankDown();
                        } else { //new candidate voted
                            catVotes.put(throwVote, PetCandidate.createPetCandidateRankDown());
                        }
                    } else if (throwVote.startsWith(dogVoteRegex)) {
                        validatePetNumber(totalDogs, throwVote, dogVoteRegex);

                        if (dogVotes.containsKey(throwVote)) { //already voted before
                            dogVotes.get(throwVote).rankDown();
                        } else { //new candidate voted
                            dogVotes.put(throwVote, PetCandidate.createPetCandidateRankDown());
                        }
                    } else {
                        throw new IllegalArgumentException("Invalid vote, only allowed for cats and dogs");
                    }
                }

                catVotes = sortByValue(catVotes);
                dogVotes = sortByValue(dogVotes);

                String topCat = catVotes.entrySet().iterator().next().getKey();
                Integer topCatVotes = catVotes.get(topCat).totalVotes;

                String topDog = dogVotes.entrySet().iterator().next().getKey();
                Integer topDogVotes = dogVotes.get(topDog).totalVotes;

                if (topCatVotes >= topDogVotes) {
                    satisfiedVoters[i] = topCatVotes;
                } else {
                    satisfiedVoters[i] = topDogVotes;
                }
            }
            Arrays.stream(satisfiedVoters).forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void validateTotalPetsAndVoters() {
        if (totalCats < 1 || totalCats > 100) {
            throw new IllegalArgumentException("Invalid number of cats, rule: 1<= cats <=100");
        }
        if (totalDogs < 1 || totalDogs > 100) {
            throw new IllegalArgumentException("Invalid number of dogs, rule: 1<= dogs <=100");
        }
        if (totalVoters < 0 || totalVoters > 500) {
            throw new IllegalArgumentException("Invalid number of voters, rule: 0<= voters <=500");
        }
    }

    private static void validatePetNumber(int totalPets, String vote, String regex) {
        int petNumber = Integer.valueOf(vote.split(regex)[1]);
        if (petNumber > totalPets || petNumber < 1) {
            throw new IllegalArgumentException("Invalid pet number voted");
        }
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    static class PetCandidate implements Comparable {
        int totalVotes;
        int rank;

        private PetCandidate(int totalVotes, int rank) {
            this.totalVotes = totalVotes;
            this.rank = rank;
        }

        static PetCandidate createPetCandidateRankUp() {
            return new PetCandidate(1,1);
        }

        static PetCandidate createPetCandidateRankDown() {
            return new PetCandidate(0,-1);
        }

        void rankUp() {
            rank = +1;
            totalVotes += 1;
        }

        void rankDown() {
            rank = -1;
        }

        @Override
        public int compareTo(Object o) {
            PetCandidate c = (PetCandidate) o;
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
