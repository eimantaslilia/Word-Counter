package word.counter.utility;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CountingTask implements Callable<Map<String, Integer>> {

    private final InputStream inputStream;

    @Override
    public Map<String, Integer> call() {
        Map<String, Integer> wordMap = new HashMap<>();
        try (Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                handleLine(wordMap, line);
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return wordMap;
    }

    private void handleLine(Map<String, Integer> wordMap, String line) {
        Arrays.stream(line.split(" ")).forEach(word -> {
            if (Character.isAlphabetic(word.charAt(0))) {
                wordMap.merge(StringUtils.capitalize(word), 1, Integer::sum);
            }
        });
    }
}
