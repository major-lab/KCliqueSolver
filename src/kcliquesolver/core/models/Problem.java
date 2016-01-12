package kcliquesolver.core.models;


import java.util.ArrayList;
import java.util.InputMismatchException;

public class Problem {

    final ArrayList<Range> ranges;
    final double[][] distanceMatrix;


    public Problem(double[][] distanceMatrix, ArrayList<Range> ranges) {
        this.ranges = ranges;
        this.distanceMatrix = distanceMatrix;

        if (!(squareDistanceMatrix(distanceMatrix) &&
                noNegativeValues(distanceMatrix) &&
                correctRanges(ranges, distanceMatrix))) {
            throw new InputMismatchException("The distance matrix and ranges specified contain mistakes");
        }

    }


    public ArrayList<Range> getRanges() {
        return ranges;
    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }


    static boolean noNegativeValues(double[][] distanceMatrix) {
        for (int i = 0; i != distanceMatrix.length; ++i) {
            for (int j = 0; j != distanceMatrix.length; ++j) {
                if (distanceMatrix[i][j] < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean squareDistanceMatrix(double[][] distanceMatrix) {
        return (distanceMatrix.length == distanceMatrix[0].length);
    }


    /**
     * Verifies that the range size matches the overall size of the distance matrix
     * and that ranges are ordered, cover the whole interval and never overlap.
     * Should be done after the squareDistanceMatrix call (otherwise potential for mistake).
     *
     * @param ranges         [begin, end[ coordinates of each set of objects
     * @param distanceMatrix square distance matrix of distance between all objects
     * @return boolean, whether or not the ranges are correctly specifying the distance matrix
     */
    static boolean correctRanges(ArrayList<Range> ranges, double[][] distanceMatrix) {
        int lastIndex = 0;
        if (ranges.get(0).getFirst() != 0) {
            return false;
        }
        for (int x = 0; x != ranges.size() - 1; ++x) {
            Range range1 = ranges.get(x);
            Range range2 = ranges.get(x + 1);
            if (!(range1.getSecond() == range2.getFirst())) {
                return false;
            }
            lastIndex = range2.getSecond();
        }
        return lastIndex == distanceMatrix.length;
    }


    public String toString(){
        StringBuilder builder = new StringBuilder();
        // output the ranges
        builder.append("Ranges"); builder.append(System.lineSeparator());
        for(Range range : ranges){
            builder.append("(");
            builder.append(range.getFirst());
            builder.append(", ");
            builder.append(range.getSecond());
            builder.append(") ");
        }

        // output the distance matrix
        builder.append(System.lineSeparator());
        for (int x = 0; x != distanceMatrix.length; ++x){
            for (int y = 0; y != distanceMatrix[x].length; ++y){
                builder.append(distanceMatrix[x][y]);
                builder.append(" ");
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
}
