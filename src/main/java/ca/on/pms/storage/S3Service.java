package ca.on.pms.storage;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

	private final S3Client s3Client;
	private final String bucketName;

	public S3Service(@Value("${aws.s3.bucket}") String bucketName, @Value("${aws.region}") String region) {

		this.bucketName = bucketName;

		this.s3Client = S3Client.builder().region(Region.of(region)).build();
	}

	@PostConstruct
	public void sanityCheck() {
		System.out.println("✅ S3Service initialized");
		System.out.println("   Bucket = " + bucketName);
	}

	// Upload method (encrypted bytes)
	public String uploadFile(byte[] data, String fileName) {

		String key = "tenant-documents/" + UUID.randomUUID() + "_" + fileName;

		s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(key).build(),
				software.amazon.awssdk.core.sync.RequestBody.fromBytes(data));

		return key;
	}

	// Download method
	public byte[] downloadFile(String key) {

		ResponseBytes<?> objectBytes = s3Client.getObject(
				GetObjectRequest.builder().bucket(bucketName).key(key).build(), ResponseTransformer.toBytes());

		return objectBytes.asByteArray();
	}
}
