package kcliquesolver.core.io;



import com.opencsv.CSVWriter;
import kcliquesolver.core.models.Solution;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;

public final class Writers {

    /**
     *
     * @param solutions
     */
    public static void printUniqueSolutions(ArrayList<Solution> solutions, Writer writer) throws IOException {


        CSVWriter csvWriter = new CSVWriter(writer, ',', CSVWriter.NO_QUOTE_CHARACTER);

        HashSet<Solution> uniqueSolutions = new HashSet<>();
        for (int index = 0; index != solutions.size(); ++index) {
            Solution solution = solutions.get(index);
            if (!uniqueSolutions.contains(solution)) {
                uniqueSolutions.add(solution);

                // score;gene1;gene2;gene3;gene4...
                csvWriter.writeNext(solution.toString().split(","));
            }
        }
        csvWriter.close();
    }


}
