package kcliquesolver.core.util;

import kcliquesolver.core.models.Pair;
import rna2d.core.util.Pair;
import optimization.Problem;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Integer;import java.lang.String;import java.lang.System;import java.util.ArrayList;
import java.util.Scanner;

public class Reader {

    /**
     * Read a file encoding both the (begin, end) indices of categories
     * along with the full distance matrix between all considered objects
     * @param fileName file path to open
     * @return
     */
    public static Problem readDistancesFile(String fileName){
        double[][] distanceMatrix = null;
        ArrayList<Pair<Integer, Integer>> ranges = new ArrayList<>();

        try {
            File file = new File(fileName);
            Scanner in = new Scanner(file);

            // first line of the file must be
            // NumberOfObjects NumberOfRanges
            int numObjects = in.nextInt(); int numRanges = in.nextInt(); in.nextLine();

            // then follows NumberOfRange [begin, end[ coordinates
            // the end indices indicate that the object at position is not included
            for(int i =0 ; i != numRanges; ++i){
                int begin = in.nextInt(); int end = in.nextInt();
                ranges.add(new Pair<>(begin, end));
                in.nextLine();
            }

            // then follows the distance matrix
            distanceMatrix = new double[numObjects][numObjects];
            int x = 0;
            int y;
            while (in.hasNextLine()) {
                for (y = 0; y != numObjects; ++y) {
                    distanceMatrix[x][y] = in.nextDouble();
                }
                // go to the next row
                in.nextLine();
                x += 1;
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find specified file (" + fileName + ")");
        }
        return new Problem()
    }
}
