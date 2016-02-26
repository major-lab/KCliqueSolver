package kcliquesolver.core.io;

import com.opencsv.CSVReader;
import kcliquesolver.core.models.Problem;
import kcliquesolver.core.models.Range;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Readers {


    /**
     * Read the distance matrix in an header-less csv format (the separator is ",").
     * <p/>
     * <p/>
     * Assuming m categories with a total of n objects, columns will be
     * category (integer, [0..m]) distance (double, will be n distance rows and columns)
     * Size of the matrix should be (n rows x n+1 columns)
     * <p/>
     * WARNINGS:
     * - the matrix isn't necessarily symmetrical.
     * - the objects must be grouped together (separated by category)
     * - e.g. 0,0,2,3
     * 0,2,0,2
     * 1,3,2,0
     * not
     * 0,0,2,3
     * 1,2,0,2
     * 0,3,2,0
     *
     * @param fileName file path to open
     * @return Problem instance
     */
    public static Problem readDistancesFile(String fileName) throws IOException {
        // TODO: is there a better way than to prefill the problem instance with bogus data?
        Problem problem;

        CSVReader reader = new CSVReader(new FileReader(fileName));
        ArrayList<String[]> rows = new ArrayList<>();
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            rows.add(nextLine);
        }


        ArrayList<Range> ranges = new ArrayList<>();

        // separate by categories
        int rangeBegin = 0;
        for (int rowIndex = 1; rowIndex != rows.size(); ++rowIndex) {
            if (rows.get(rowIndex - 1)[0].compareTo(rows.get(rowIndex)[0]) != 0) {
                // new range!
                ranges.add(new Range(rangeBegin, rowIndex));
                rangeBegin = rowIndex;
            }
        }

        if (rangeBegin != rows.size()) {
            // last one is not in there
            ranges.add(new Range(rangeBegin, rows.size()));
        }

        double[][] distanceMatrix = new double[rows.size()][rows.size()];
        for (int rowIndex = 0; rowIndex != rows.size(); ++rowIndex) {
            String[] row = rows.get(rowIndex);
            double[] distances = new double[rows.size()];
            for (int i = 1; i != row.length; ++i) {
                distances[i-1] = Double.parseDouble(row[i]);
            }
            distanceMatrix[rowIndex] = distances;
        }

        problem = new Problem(distanceMatrix, ranges);


        return problem;
    }
}




