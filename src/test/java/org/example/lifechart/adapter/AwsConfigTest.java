package org.example.lifechart.adapter;

import static org.springframework.test.util.AssertionErrors.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@ActiveProfiles("test")
@SpringBootTest
public class AwsConfigTest {

	@Autowired
	private SqsClient sqsClient;

	@Autowired
	private SnsClient snsClient;

	@Test
	void config_AwsConfig_Created(){


	}

}
