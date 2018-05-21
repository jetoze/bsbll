package bsbll.lahman;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Splits the Lahman Batting, Pitching, and Fielding CSV files into individual files per year.
 * <p>
 * The result of running this splitter on the Batting.csv file is as follows (analogously for
 * pitching and fielding):
 * <pre>
 * ROOT
 *   |
 *   |-- Batting.csv (original file)
 *   |
 *   |-- batting (new output folder)
 *         |
 *         |-- batting-1871.csv
 *         |-- batting-1872.csv
 *         |
 *         | [...]
 *         |
 *         |-- batting-2016.csv
 *         |-- batting-2017.csv
 * </pre>   
 */
public final class StatsFileSplitter {
    private final File root;
    private final Type type;
    
    private StatsFileSplitter(File root, Type type) {
        this.root = root;
        this.type = type;
    }

    public void split() throws IOException {
        File outputFolder = new File(root, type.name().toLowerCase());
        outputFolder.mkdir();
        File original = new File(root, type.fileName);
        List<String> lines = Files.readAllLines(original.toPath());
        List<String> linesForYear = new ArrayList<>();
        String year = null;
        for (String line : lines) {
            String y = line.split(",", 3)[1];
            if (!linesForYear.isEmpty()) {
                if (!y.equals(year)) {
                    writeFile(outputFolder, year, linesForYear);
                    linesForYear = new ArrayList<>();
                }
            }
            year = y;
            linesForYear.add(line);
        }
        if (!linesForYear.isEmpty()) {
            writeFile(outputFolder, year, linesForYear);
        }
    }
    
    private void writeFile(File folder, String year, List<String> linesForYear)
            throws IOException {
        File newFile = new File(folder, type.name().toLowerCase() + "-" + year + ".csv");
        Files.write(newFile.toPath(), linesForYear);
    }

    private static enum Type {
        BATTING("Batting.csv"), 
        PITCHING("Pitching.csv"), 
        FIELDING("Fielding.csv");
        
        private final String fileName;

        private Type(String fileName) {
            this.fileName = fileName;
        }
    }
    
    public static void main(String[] args) throws Exception {
        File root = new File("/Users/torgil/coding/data/bsbll/baseballdatabank-master/core/");
        StatsFileSplitter splitter = new StatsFileSplitter(root, Type.FIELDING);
        splitter.split();
    }

}
