package org.example.lifechart.common.config;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;

@Configuration
public class AwsConfig {

	@Value("${aws.arn.role.message}")
	private String arnRoleMessage;

	@Value("${aws.key.access.id}")
	private String accessId;

	@Value("${aws.key.access.secret}")
	private String secretAccessKey;

	private boolean isRunningOnEc2() {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(
				"http://169.254.169.254/latest/meta-data/").openConnection();
			connection.setConnectTimeout(100); // 매우 짧게 설정
			connection.connect();
			return connection.getResponseCode() == 200;
		} catch (IOException e) {
			return false;
		}
	}

	@Bean
	public StsClient stsClient() {

		if(isRunningOnEc2()){
			return StsClient.builder()
				.region(Region.AP_NORTHEAST_2)
				.credentialsProvider(DefaultCredentialsProvider.create())
				.build();
		}

		AwsBasicCredentials credentials = AwsBasicCredentials.create(accessId, secretAccessKey);

		return StsClient.builder()
			.region(Region.AP_NORTHEAST_2)
			.credentialsProvider(StaticCredentialsProvider.create(credentials))
			.build();
	}

	@Bean
	public SqsClient sqsClient(StsClient stsClient) {
		return SqsClient.builder()
			.region(Region.AP_NORTHEAST_2)
			.credentialsProvider(StsAssumeRoleCredentialsProvider.builder()
				.stsClient(stsClient)
				.refreshRequest(r -> r
					.roleArn(arnRoleMessage)
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
					.roleArn(arnRoleMessage)
					.roleSessionName("sns-pub"))
				.build()
			)
			.build();
	}

}
