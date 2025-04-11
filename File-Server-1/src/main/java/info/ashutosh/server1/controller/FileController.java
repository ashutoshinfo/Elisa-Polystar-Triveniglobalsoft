package info.ashutosh.server1.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file")
public class FileController {

    @GetMapping("/frankenstein")
    public ResponseEntity<StreamingResponseBody> getFrankenstein() throws IOException, InterruptedException {
//        Thread.sleep(5000);
        InputStream is = new ClassPathResource("frankenstein.txt").getInputStream();
        return ResponseEntity.ok(outputStream -> {
            StreamUtils.copy(is, outputStream);
        });
    }
}
