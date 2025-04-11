package info.ashutosh.client.service;

import info.ashutosh.client.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class WordCountClientService2 {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server1.url}")
    private String server1Url;

    @Value("${server2.url}")
    private String server2Url;

    public ApiResponse<Map<String, Integer>> getTopWordsFromBothServers() throws Exception {
        CompletableFuture<Map<String, Integer>> future1 = fetchWordCount(server1Url, server1Url);
        CompletableFuture<Map<String, Integer>> future2 = fetchWordCount(server2Url, server1Url);

        CompletableFuture.allOf(future1, future2).join();

        Map<String, Integer> data1 = future1.get();
        Map<String, Integer> data2 = future2.get();

        if (data1.isEmpty() && data2.isEmpty()) {
            return new ApiResponse<>("FAILURE", "Both Server1 and Server2 are down.", null);
        }
        if (data1.isEmpty()) {
            return new ApiResponse<>("FAILURE", "Server1 is down. Please try after sometime.", null);
        }
        if (data2.isEmpty()) {
            return new ApiResponse<>("FAILURE", "Server2 is down. Please try after sometime.", null);
        }

        Map<String, Integer> combinedWordCount = new HashMap<>(data1);
        data2.forEach((k, v) -> combinedWordCount.merge(k, v, Integer::sum));

        Map<String, Integer> top5Words = combinedWordCount.entrySet()
                .parallelStream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        return new ApiResponse<>("SUCCESS", "Top 5 words fetched successfully", top5Words);
    }

    @Async
    public CompletableFuture<Map<String, Integer>> fetchWordCount(String url, String serverName) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return CompletableFuture.completedFuture(countWords(response.getBody()));
            }
        } catch (Exception e) {
            System.out.println(serverName + " error: " + e.getMessage());
        }
        // return empty map on failure
        return CompletableFuture.completedFuture(new HashMap<>());
    }

    public Map<String, Integer> countWords(String content) {
        Map<String, Integer> wordCountMap = new HashMap<>();

        // Pattern to match words (any non-space continuous characters)
        Pattern pattern = Pattern.compile("\\S+");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String word = matcher.group();  // Extract the word as-is (no modification)
            wordCountMap.merge(word, 1, Integer::sum);  // Merge is optimal for counting
        }

        return wordCountMap;
    }
}
