package kcliquesolver.core.util;

import optimization.Solution;

import java.lang.System;import java.util.ArrayList;
import java.util.HashSet;

public final class Writers {

    /**
     *
     * @param solutions
     * @param <T>
     */
    public static <T> void printUniqueSolutions(ArrayList<Solution> solutions) {

        HashSet<Solution> uniqueSolutions = new HashSet<>();
        for (int index = 0; index != solutions.size(); ++index) {
            Solution solution = solutions.get(index);
            if (!uniqueSolutions.contains(solution)) {
                uniqueSolutions.add(solution);

                // score;gene1;gene2;gene3;gene4
                System.out.print(solution.getScore() + ";");
                for (int i = 0; i != solution.getGenes().size()-1; ++i) {
                    System.out.print(solution.getGenes().get(i) +";");
                }

                // print the last one (if there's more than one)
                if(solution.getGenes().size() > 1){
                    System.out.print(solution.getGenes().get(solution.getGenes().size()-1));
                }
                System.out.println();
            }
            System.out.println();
        }
    }


}
