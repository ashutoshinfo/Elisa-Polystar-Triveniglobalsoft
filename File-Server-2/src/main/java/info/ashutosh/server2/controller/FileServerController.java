package info.ashutosh.server2.controller;

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
public class FileServerController {

    @GetMapping("/{name}")
    public ResponseEntity<StreamingResponseBody> getFrankenstein() throws IOException {
        InputStream is = new ClassPathResource("dracula.txt").getInputStream();
        return ResponseEntity.ok(outputStream -> {
            StreamUtils.copy(is, outputStream);
        });
    }
}
