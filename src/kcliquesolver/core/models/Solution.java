package kcliquesolver.core.models;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;

/**
 * Generic solution object used for optimization in inheriting solvers.
 */
public class Solution implements Comparable<Solution> {


    private final ArrayList<Integer> genes;
    private Double score;




    public void setGene(final int index, final int newValue) {
        genes.set(index, newValue);
    }

    /**
     * Constructor.
     *
     * @param genes index of the chosen genes in a table
     * @param score score of the current solution configuration (based on genes)
     */
    public Solution(ArrayList<Integer> genes, final double score) {
        this.genes = genes;
        this.score = score;
    }


    /**
     * Copy constructor
     *
     * @param other solution to copy
     */
    public Solution(Solution other) {
        this(other.getGenes(), other.getScore());
    }


    public void setScore(double score) {
        this.score = score;
    }

    public Double getScore() {

        return score;
    }

    public ArrayList<Integer> getGenes() {
        return new ArrayList<>(genes);
    }

    public String toString() {
        String stringRepresentation = "[";
        for (int i = 0; i != getGenes().size(); ++i) {
            stringRepresentation += getGenes().get(i) + " ";
        }
        stringRepresentation += " : " + getScore() + "]" + System.lineSeparator();
        return stringRepresentation;
    }


    @Override
    public int compareTo(@NotNull Solution other) {
        return getScore().compareTo(other.getScore());
    }


    @Override
    public boolean equals(Object obj) {
        // basic checks
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        // solution comparison
        final Solution other = (Solution) obj;
        return score.compareTo(other.getScore()) == 0 && genes.equals(other.genes);
    }
}
