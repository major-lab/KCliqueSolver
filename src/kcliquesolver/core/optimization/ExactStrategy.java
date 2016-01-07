package kcliquesolver.core.optimization;

import kcliquesolver.core.interfaces.AbstractStrategy;
import kcliquesolver.core.models.Problem;
import kcliquesolver.core.models.Range;
import kcliquesolver.core.models.Solution;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;


/**
 * Implementation of a branch-and-bound algorithm with a not-too-bad
 * pruning function for the consensus problem.
 */

public class ExactStrategy extends AbstractStrategy {


    private double threshold;
    private boolean verbose;

    public boolean isVerbose() {
        return verbose;
    }

    public ExactStrategy(double suboptimal_threshold, boolean verbose_) {
        threshold = suboptimal_threshold;
        verbose = verbose_;
    }


    /**
     * Partial solutions for the branchAndBound-and-bound strategy.
     */
    class PartialSolution extends Solution {


        private ArrayList<Range> ranges;


        public PartialSolution(ArrayList<Integer> genes,
                               double score,
                               ArrayList<Range> ranges) {
            super(genes, score);
            this.ranges = ranges;
        }


        public ArrayList<Range> getRanges() {
            return ranges;
        }


        public String toString() {
            // add the genes
            StringBuilder builder = new StringBuilder();
            builder.append("genes ");
            for (Integer i : getGenes()) {
                builder.append(i);
                builder.append(" ");
            }
            builder.append(System.lineSeparator());

            // add the score
            builder.append("score ");
            builder.append(getScore());
            builder.append(System.lineSeparator());

            // add the remaining ranges
            builder.append("ranges");
            builder.append(System.lineSeparator());
            for (Range r : getRanges()) {
                builder.append("[ ");
                builder.append(r.getFirst());
                builder.append(" .. ");
                builder.append(r.getSecond());
                builder.append(" ]");
                builder.append(System.lineSeparator());
            }
            return builder.toString();
        }


        public Solution toSolution() {
            return new Solution(getGenes(), getScore());
        }
    }

    /**
     * find closest elements in other range and the get
     *
     * @param index          index of the element in the distance matrix
     * @param range          range of elements amongst which to find the closest
     * @param distanceMatrix distance matrix to explore
     * @return closest distance found in the other group of elements
     */
    public static double findSmallestDistanceRange(int index, Range range,
                                                   double[][] distanceMatrix) {

        double bestD = Double.POSITIVE_INFINITY;
        double distance;

        for (int i = range.getFirst(); i != range.getSecond(); ++i) {
            distance = distanceMatrix[index][i];
            if (distance < bestD) {
                bestD = distance;
            }
        }
        return bestD;
    }


    /**
     * Find the distance to closest neighbors for each object, in each other groups (range).
     *
     * @param ranges         (start, end) indices of other groups of elems to search for closest
     * @param distanceMatrix matrix of the distances to explore
     * @return distance to nearest neighbors in other groups
     */
    double[][] nearestDistancesOtherRanges(ArrayList<Range> ranges,
                                           double[][] distanceMatrix) {

        // matrix is (distanceMatrix.size() x ranges.size())
        double[][] elemRangeDistances = new double[distanceMatrix.length][ranges.size()];

        // for each elem, for each range, find the closest get between the two
        for (int elemIndex = 0; elemIndex != distanceMatrix.length; ++elemIndex) {
            for (int rangeIndex = 0; rangeIndex != ranges.size(); ++rangeIndex) {
                elemRangeDistances[elemIndex][rangeIndex] =
                        findSmallestDistanceRange(elemIndex,
                                ranges.get(rangeIndex), distanceMatrix);
            }
        }
        return elemRangeDistances;
    }


    /**
     * Checks whether or not a partial solution is completed.
     *
     * @param partialSolution a potentially complete solution
     * @return whether or not the solution is completed
     */
    boolean isCompleteSolution(PartialSolution partialSolution) {
        return (partialSolution.getRanges().size() == 0);
    }


    /**
     * Expand a partial solution into all its potential "children"
     * and add them, if they are promising enough (bound), to the
     * search space (which is a max heap)
     *
     * @param partialSolution the solution that will be branched
     * @param distanceMatrix  matrix of distances for the bounding
     * @param searchSpace     max-heap containing partial solutions
     * @param cutoff          maximal potential value a solution can have an be added to the search space
     */
    void branchAndBound(PartialSolution partialSolution,
                        double[][] distanceMatrix,
                        PriorityQueue<PartialSolution> searchSpace,
                        double cutoff) {

        assert (!isCompleteSolution(partialSolution));

        // separate the ranges, the one to explore and the rest
        Range rangeToExplore = partialSolution.getRanges().get(0);
        ArrayList<Range> newRanges = new ArrayList<>();

        for (int i = 1; i != partialSolution.getRanges().size(); ++i) {
            newRanges.add(partialSolution.getRanges().get(i));
        }

        for (int newGene = rangeToExplore.getFirst();
             newGene != rangeToExplore.getSecond();
             ++newGene) {

            // create the new genes
            ArrayList<Integer> newGenes = new ArrayList<>();
            for (int g = 0; g != partialSolution.getGenes().size(); ++g) {
                newGenes.add(partialSolution.getGenes().get(g));
            }
            newGenes.add(newGene);

            // BRANCH
            // create the new solution, score it and add it if its worth it
            // TODO check the estimation used here for the potential best score, its broken

            PartialSolution newPartialSolution = new PartialSolution(newGenes, Double.POSITIVE_INFINITY, newRanges);
            newPartialSolution.setScore(calculateSumOfPairs(newPartialSolution, distanceMatrix));


            // BOUND
            if (newPartialSolution.getScore() <= cutoff) {
                searchSpace.add(newPartialSolution);
            }
        }
    }

    class WidthComparator implements Comparator<Range> {
        @Override
        public int compare(Range p1, Range p2) {
            Integer width1 = p1.getSecond() - p1.getFirst();
            Integer width2 = p2.getSecond() - p2.getFirst();
            return width1.compareTo(width2);
        }
    }


    /**
     * Applies the exact strategy on a problem instance
     *
     * @param problem problem instance
     * @return list of solutions found by the strategy
     */
    public ArrayList<Solution> solve(Problem problem) {
        double[][] distanceMatrix = problem.getDistanceMatrix();
        ArrayList ranges = problem.getRanges();

        // use a priority queue to represent the current search space
        PriorityQueue<PartialSolution> searchSpace = new PriorityQueue<>();
        ArrayList<PartialSolution> satisfyingSolutions = new ArrayList<>();
        double bestScore = Double.POSITIVE_INFINITY;
        double leeway = threshold * (ranges.size() * (ranges.size() - 1));
        double scoreThreshold = bestScore + leeway;

        // sort the ranges by their size (to avoid big branching at the start)
        ArrayList<Range> sortedRanges = new ArrayList<>(ranges);
        Collections.sort(sortedRanges, new WidthComparator());


        // initialize the search space with the empty partial solution
        searchSpace.add(new PartialSolution(new ArrayList<Integer>(), Double.POSITIVE_INFINITY, sortedRanges));
        double[][] elemToRangeDistanceMatrix = nearestDistancesOtherRanges(sortedRanges, distanceMatrix);


        /*
        Branch & Bound main loop
        TODO fix that bounding estimation
        */
        while (searchSpace.size() > 0) {
            scoreThreshold = bestScore + leeway;
            PartialSolution currentSolution = searchSpace.poll();

            if (isCompleteSolution(currentSolution)) {
                // check if worth adding to satisfying solutions
                if ((currentSolution.getScore() <= scoreThreshold)) {
                    satisfyingSolutions.add(currentSolution);
                }
                // check if the score was actually the best seen and update if it is
                if (currentSolution.getScore() < bestScore) {
                    bestScore = currentSolution.getScore();
                }
            } else {  // not a complete solution
                branchAndBound(currentSolution, distanceMatrix, searchSpace, scoreThreshold);
            }
        }

        // filter out solutions by score knowing now the best possible score
        ArrayList<Solution> bestSolutions = new ArrayList<>();
        for (int i = 0; i != satisfyingSolutions.size(); ++i) {
            // add only those satisfying scoring constraints
            if (satisfyingSolutions.get(i).getScore() <= scoreThreshold) {
                bestSolutions.add(satisfyingSolutions.get(i).toSolution());
            }
        }
        return bestSolutions;
    }
}
