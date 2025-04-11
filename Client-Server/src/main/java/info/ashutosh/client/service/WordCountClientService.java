package info.ashutosh.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Service
public class WordCountClientService {

    @Value("${server1.url}")
    private String server1Url;

    @Value("${server2.url}")
    private String server2Url;

    @Autowired
    private RestTemplate restTemplate;

    public WordCountClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Count words from the given URL and return a word frequency map
    public Map<String, Integer> countWordsFromFile(String fileUrl) {
        Map<String, Integer> wordCountMap = new HashMap<>();

        try {
            // Make the GET request to the file URL
            ResponseEntity<byte[]> response = restTemplate.getForEntity(fileUrl, byte[].class);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(response.getBody()), StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    word = word.toLowerCase().replaceAll("[^a-zA-Z]", ""); // Clean up word
                    wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return wordCountMap;
    }

    // Method to fetch data from both servers in parallel
    public Map<String, Integer> fetchAndCountWordsInParallel() throws InterruptedException {
        // To count words from both servers in parallel
        CountDownLatch latch = new CountDownLatch(2); // Two tasks: one for each server
        Map<String, Integer> finalWordCountMap = new HashMap<>();

        // Fetch data from server1 (Frankenstein) in a separate thread
        Thread server1Thread = new Thread(() -> {
            Map<String, Integer> server1WordCount = countWordsFromFile(server1Url);
            synchronized (finalWordCountMap) {
                mergeWordCounts(finalWordCountMap, server1WordCount);
            }
            latch.countDown();
        });

        // Fetch data from server2 (Dracula) in a separate thread
        Thread server2Thread = new Thread(() -> {
            Map<String, Integer> server2WordCount = countWordsFromFile(server2Url);
            synchronized (finalWordCountMap) {
                mergeWordCounts(finalWordCountMap, server2WordCount);
            }
            latch.countDown();
        });

        server1Thread.start();
        server2Thread.start();

        latch.await(); // Wait until both threads are done
        return finalWordCountMap;
    }

    // Helper method to merge word counts from both servers
    private void mergeWordCounts(Map<String, Integer> finalWordCountMap, Map<String, Integer> newWordCount) {
        newWordCount.forEach((word, count) -> finalWordCountMap.put(word, finalWordCountMap.getOrDefault(word, 0) + count));
    }
}
