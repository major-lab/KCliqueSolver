package kcliquesolver.core.io;

import kcliquesolver.core.models.Problem;
import kcliquesolver.core.models.Range;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Readers {

    /**
     * Read a file encoding both the (begin, end) indices of categories
     * along with the full distance matrix between all considered objects
     * @param fileName file path to open
     * @return Problem instance
     */
    public static Problem readDistancesFile(String fileName) throws InputMismatchException{
        double[][] distanceMatrix = null;
        ArrayList<Range> ranges = new ArrayList<>();

        try {
            File file = new File(fileName);
            Scanner in = new Scanner(file);

            // first line of the file must be
            // NumberOfObjects NumberOfRanges
            int numObjects = in.nextInt(); int numRanges = in.nextInt(); in.nextLine();

            // then follows NumberOfRange [begin, end[ coordinates
            // the end indices indicate that the object at position is not included
            for(int i =0 ; i != numRanges; ++i){
                ranges.add(new Range(in.nextInt(), in.nextInt()));
            }

            // then follows the distance matrix
            distanceMatrix = new double[numObjects][numObjects];
            int x = 0;
            int y;
            while (in.hasNextLine()) {
                in.nextLine();
                for (y = 0; y != numObjects; ++y) {
                    if (!in.hasNextDouble()){
                        throw new InputMismatchException("The distance matrix is not of specified size.");
                    }
                    distanceMatrix[x][y] = in.nextDouble();
                }
                // check that nothing is left
                // go to the next row
                x += 1;
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find specified file (" + fileName + ")");
        }
        return new Problem(distanceMatrix, ranges);
    }
}
