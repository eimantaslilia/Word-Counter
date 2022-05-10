package word.counter.service;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.Map.entry;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class WordCounterServiceTest {

    private static final String TEST_FILE_FOLDER_PATH = "/files/";
    private static final String TEST_FILE_1 = TEST_FILE_FOLDER_PATH + "test1.txt";
    private static final String TEST_FILE_2 = TEST_FILE_FOLDER_PATH + "test2.txt";
    private static final String TEST_FILE_3 = TEST_FILE_FOLDER_PATH + "test3.txt";
    private static final List<String> TEST_FILE_LOCATIONS = List.of(TEST_FILE_1, TEST_FILE_2, TEST_FILE_3);

    private final WordCounterService service = new WordCounterService();

    @Test
    void shouldCountWordsFromAllFiles() {
        var testMaps = initTestMaps();

        var result = service.retrieveListOfMapsByWordAndCount(TEST_FILE_LOCATIONS.stream()
                .map(fileName -> new MockMultipartFile(fileName, readFile(fileName)))
                .collect(Collectors.toList()));

        assertThat(result, hasSize(4));
        for (int i = 0; i < 4; i++) {
            var expectedMap = testMaps.get(i);
            var actualMap = result.get(i);
            assertEquals(expectedMap, actualMap);
        }
    }

    private byte[] readFile(String path) {
        try (var is = new ClassPathResource(path).getInputStream()) {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new RuntimeException(String.format("failed to read file from %s due to %s", path, e.getMessage()));
        }
    }

    private List<Map<String, Integer>> initTestMaps() {
        Map<String, Integer> AGMap = new TreeMap<>(Map.ofEntries(
                entry("Adomas", 3),
                entry("Bulve", 4),
                entry("Gandras", 1)
        ));
        Map<String, Integer> HNMap = new TreeMap<>(Map.ofEntries(
                entry("Henrikas", 2),
                entry("Jonas", 6),
                entry("Liuksas", 3),
                entry("Moneta", 5),
                entry("Neringa", 6)
        ));
        Map<String, Integer> OUMap = new TreeMap<>(Map.ofEntries(
                entry("Oras", 4),
                entry("Uostas", 1)
        ));
        Map<String, Integer> VZMap = new TreeMap<>(Map.ofEntries(
                entry("Vizija", 5),
                entry("Zebras", 4)
        ));

        return new LinkedList<>(List.of(AGMap, HNMap, OUMap, VZMap));
    }
}