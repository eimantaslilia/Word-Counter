package word.counter.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import word.counter.utility.CountingTask;
import word.counter.utility.FileUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class WordCounterService {

    public File retrieveFilesWithCountInfo(List<MultipartFile> files) {
        var listOfMaps = retrieveListOfMapsByWordAndCount(files);
        var file = FileUtils.createTempFile();
        return FileUtils.writeToFile(listOfMaps, file);

    }

    public List<Map<String, Integer>> retrieveListOfMapsByWordAndCount(List<MultipartFile> files) {
        var wordMap = countWordsToMap(files);
        return separateByFirstLetter(wordMap);
    }

    @SneakyThrows
    private Map<String, Integer> countWordsToMap(List<MultipartFile> files) {
        var tasks = files.stream()
                .map(this::handleFile)
                .collect(Collectors.toList());

        var executorService = Executors.newFixedThreadPool(3);
        var results = executorService.invokeAll(tasks);
        executorService.shutdown();

        return processWordMap(results);
    }

    private List<Map<String, Integer>> separateByFirstLetter(Map<String, Integer> wordMap) {
        Map<String, Integer> AGMap = new TreeMap<>();
        Map<String, Integer> HNMap = new TreeMap<>();
        Map<String, Integer> OUMap = new TreeMap<>();
        Map<String, Integer> VZMap = new TreeMap<>();

        wordMap.forEach((key, value) -> {
            char firstLetter = key.toUpperCase(Locale.ROOT).charAt(0);
            var capitalizedKey = StringUtils.capitalize(key);
            if (firstLetter >= 'A' && firstLetter <= 'G') {
                AGMap.put(capitalizedKey, value);
            } else if (firstLetter >= 'H' && firstLetter <= 'N') {
                HNMap.put(capitalizedKey, value);
            } else if (firstLetter >= 'O' && firstLetter <= 'U') {
                OUMap.put(capitalizedKey, value);
            } else if (firstLetter >= 'V' && firstLetter <= 'Z') {
                VZMap.put(capitalizedKey, value);
            }
        });
        return new LinkedList<>(List.of(AGMap, HNMap, OUMap, VZMap));
    }

    @SneakyThrows
    private CountingTask handleFile(MultipartFile file) {
        return new CountingTask(file.getInputStream());
    }

    private Map<String, Integer> processWordMap(List<Future<Map<String, Integer>>> results) {
        Map<String, Integer> wordMap = new HashMap<>();
        results.forEach(result -> {
            try {
                appendMap(wordMap, result.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        return wordMap;
    }

    private void appendMap(Map<String, Integer> totalWordMap, Map<String, Integer> currentMap) {
        currentMap.forEach((key, value) -> totalWordMap.merge(key, value, Integer::sum));
    }
}
