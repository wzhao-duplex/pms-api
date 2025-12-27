package ca.on.pms.storage;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer; // ✅ Ensure this is imported
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final S3Client s3Client;

	@Value("${aws.s3.bucket}")
	private String bucketName;

	@PostConstruct
	public void sanityCheck() {
		System.out.println("✅ S3Service initialized");
		System.out.println("   Bucket = " + bucketName);
	}

	public String uploadFile(byte[] data, String fileName) {
		String key = "tenant-documents/" + UUID.randomUUID() + "_" + fileName;

		s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(key).build(), RequestBody.fromBytes(data));

		return key;
	}

	// ✅ FIXED METHOD
	public byte[] downloadFile(String key) {
		ResponseBytes<?> objectBytes = s3Client
				.getObject(GetObjectRequest.builder().bucket(bucketName).key(key).build(), ResponseTransformer.toBytes() // <---
																															// THIS
																															// WAS
																															// MISSING
				);

		return objectBytes.asByteArray();
	}
}