package info.ashutosh.client.controller;

import info.ashutosh.client.model.ApiResponse;
import info.ashutosh.client.service.WordCountClientService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WordCountController {

    @Autowired
    private WordCountClientService2 wordCountClientService;

    // Endpoint to fetch and display top 5 most frequent words
    @GetMapping("/count-words")
    public ApiResponse<Map<String, Integer>> getTopWords() throws Exception {
            ApiResponse<Map<String, Integer>> topWordsFromBothServers = wordCountClientService.getTopWordsFromBothServers();
            return topWordsFromBothServers;

    }
}
