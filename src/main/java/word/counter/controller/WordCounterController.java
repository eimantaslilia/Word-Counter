package word.counter.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import word.counter.service.WordCounterService;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/word-counter")
@RequiredArgsConstructor
public class WordCounterController {

    private final WordCounterService wordCounterService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<Map<String, Integer>> showCounts(@RequestPart(value = "file") List<MultipartFile> files) {
        return wordCounterService.retrieveListOfMapsByWordAndCount(files);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/download", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> downloadFileWithCounts(@RequestPart(value = "file") List<MultipartFile> files) {
        return createResponseEntity(wordCounterService.retrieveFilesWithCountInfo(files));
    }

    @SneakyThrows
    private ResponseEntity<Resource> createResponseEntity(File file) {
        var resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}