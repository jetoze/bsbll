package bsbll.research;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.util.Objects;

import javax.annotation.Nullable;

public final class PlayByPlayFileTest {
    public static void main(String[] args) throws Exception {
        File rootDir = new File("/Users/torgil/coding/data/bsbll/play-by-play-files/1930/");
        FileFilter filter = f -> {
            String name = f.getName();
            return name.endsWith(".EVA") || name.endsWith("EVN") || name.endsWith(".EDN");
        };
        for (File inputFile : rootDir.listFiles(filter)) {
            //System.out.println(inputFile.getAbsolutePath());
            Files.lines(inputFile.toPath())
                .filter(line -> line.startsWith("play"))
                .map(PlayByPlayFileTest::getPlayField)
                .filter(Objects::nonNull)
                //.filter(f -> f.startsWith("CS") && f.contains("E"))
                .forEach(EventParser::parse);
                //.forEach(System.out::println);
        }
    }

    @Nullable
    private static String getPlayField(String line) {
        String[] parts = line.split(",");
        return (parts.length >= 7)
                ? parts[6].trim()
                : null;
    }
}
