package org.example.lifechart.common.config;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.imds.Ec2MetadataClient;
import software.amazon.awssdk.imds.Ec2MetadataResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;

@Slf4j
@Configuration
public class AwsConfig {

	@Value("${aws.arn.role.message}")
	private String arnRoleMessage;

	@Value("${aws.key.access.id}")
	private String accessId;

	@Value("${aws.key.access.secret}")
	private String secretAccessKey;

	private StsAssumeRoleCredentialsProvider credential;

	private boolean isRunningOnEc2() {

		try (Ec2MetadataClient client = Ec2MetadataClient.create()){
			Ec2MetadataResponse res = client.get("/latest/meta-data/instance-id");

			if(res.asString() != null) return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}


	@PostConstruct
	private void setCredential() {

		StsClient stsClient;

		if(isRunningOnEc2()){
			log.info("Running in EC2. Using DefaultCredentials");

			stsClient = StsClient.builder()
				.region(Region.AP_NORTHEAST_2)
				.credentialsProvider(DefaultCredentialsProvider.create())
				.build();
		} else {
			log.info("Running in local. Using AWS IAM User");

			AwsBasicCredentials credential = AwsBasicCredentials.create(accessId, secretAccessKey);

			stsClient = StsClient.builder()
				.region(Region.AP_NORTHEAST_2)
				.credentialsProvider(StaticCredentialsProvider.create(credential))
				.build();
		}

		this.credential = StsAssumeRoleCredentialsProvider.builder()
			.stsClient(stsClient)
			.refreshRequest(r -> r
				.roleArn(arnRoleMessage)
				.roleSessionName("notification")
			).build();

	}

	@Bean
	public SqsClient sqsClient() {
		log.info("Create SqsClient. ARN Role : {}", arnRoleMessage);

		return SqsClient.builder()
			.region(Region.AP_NORTHEAST_2)
			.credentialsProvider(credential)
			.build();
	}

	@Bean
	public SnsClient snsClient() {
		log.info("Create SnsClient. ARN Role : {}", arnRoleMessage);

		return SnsClient.builder()
			.region(Region.AP_NORTHEAST_2)
			.credentialsProvider(credential)
			.build();
	}

}
