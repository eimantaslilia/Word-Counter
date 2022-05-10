package word.counter.utility;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@UtilityClass
public class FileUtils {

    @SneakyThrows
    public File writeToFile(List<Map<String, Integer>> listOfMaps, File file) {
        var mapOfSeparators = instantiateMapOfSeparators();
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < 4; i++) {
                var map = listOfMaps.get(i);
                appendGroupSeparator(bf, mapOfSeparators, i);
                map.entrySet()
                        .forEach(entry -> writeLine(bf, entry));
            }
            bf.flush();
            return file;
        }
    }

    @SneakyThrows
    private void appendGroupSeparator(BufferedWriter bf, Map<Integer, String> mapOfSeparators, int separator) {
        if (separator != 0) {
            bf.newLine();
        }
        bf.write(mapOfSeparators.get(separator));
        bf.newLine();
    }

    @SneakyThrows
    private void writeLine(BufferedWriter bf, Map.Entry<String, Integer> entry) {
        bf.write(entry.getKey() + ":" + entry.getValue());
        bf.newLine();
    }

    @SneakyThrows
    public File createTempFile() {
        return File.createTempFile("xyz", UUID.randomUUID().toString());
    }

    private Map<Integer, String> instantiateMapOfSeparators() {
        Map<Integer, String> mapOfSeparators = new TreeMap<>();
        mapOfSeparators.put(0, "A to G");
        mapOfSeparators.put(1, "H to N");
        mapOfSeparators.put(2, "O to U");
        mapOfSeparators.put(3, "V to Z");
        return mapOfSeparators;
    }
}
