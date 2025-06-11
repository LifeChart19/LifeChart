package org.example.lifechart.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;

@Configuration
public class AwsConfig {

	@Value("${aws.arn.role.sqssns}")
	private String ARN_ROLE_SQSSNS;

	@Bean
	public StsClient stsClient() {
		return StsClient.builder()
			.region(Region.AP_NORTHEAST_2)
			.credentialsProvider(DefaultCredentialsProvider.create())
			.build();
	}

	@Bean
	public SqsClient sqsClient(StsClient stsClient) {
		return SqsClient.builder()
			.region(Region.AP_NORTHEAST_2)
			.credentialsProvider(StsAssumeRoleCredentialsProvider.builder()
				.stsClient(stsClient)
				.refreshRequest(r -> r
					.roleArn(ARN_ROLE_SQSSNS)
					.roleSessionName("sqs"))
				.build()
			)
			.build();
	}

	@Bean
	public SnsClient snsClient(StsClient stsClient) {
		return SnsClient.builder()
			.region(Region.AP_NORTHEAST_2)
			.credentialsProvider(StsAssumeRoleCredentialsProvider.builder()
				.stsClient(stsClient)
				.refreshRequest(r -> r
					.roleArn(ARN_ROLE_SQSSNS)
					.roleSessionName("sns-pub"))
				.build()
			)
			.build();
	}

}
