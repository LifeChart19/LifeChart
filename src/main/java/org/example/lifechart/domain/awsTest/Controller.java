package org.example.lifechart.domain.awsTest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(("awsTest"))
public class Controller {

    private final AwsTestPort awsTestPort;

    @GetMapping("sqs-pub")
    public ResponseEntity<?> sqsPub() {
        return ResponseEntity.ok(
                awsTestPort.sendSQS()
        );
    }

    @GetMapping("sqs-sub")
    public ResponseEntity<?> sqsSub() {
        return ResponseEntity.ok(
                awsTestPort.receiveAndDelete()
        );
    }

    @GetMapping("sns-pub")
    public ResponseEntity<?> snsPub() {
        return ResponseEntity.ok(
                awsTestPort.sendSNS()
        );
    }

}
