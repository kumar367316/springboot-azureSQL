package com.htc.pclconverter.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobProperties;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

@Service
public class AzureBlobAdapter {

	@Autowired
	BlobClientBuilder client;

	@Value("{blob.connection-string}")
	private String storageConnectionString;

	public String upload(MultipartFile[] files, String prefixName) {
		String result = "successfully upload document";
		if (files != null && files.length > 0) {
			try {
				// implement your own file name logic.
				for (MultipartFile file : files) {
					String fileName = file.getOriginalFilename();
					client.blobName(fileName).buildClient().upload(file.getInputStream(), file.getSize());
				}

			} catch (IOException e) {
				result = "Error in upload document";
				e.printStackTrace();
			}
		}
		return result;
	}

	public String getFile(String name) {
		String storageConnection = "DefaultEndpointsProtocol=https;AccountName=pcl1;AccountKey=qjdSFfLVHAgRYqybiDTIxiddxi0kgpJzCGvyMzK2KFQJkrvhDX3scFwOkJu5EIL/5L8AZopeBGEPZLCiMVaShw==";
		try {
			CloudStorageAccount account = CloudStorageAccount.parse(storageConnection);
			CloudBlobClient serviceClient = account.createCloudBlobClient();
			File file = new File("D:\\PMA-Doc\\work-doc-test");
			if (!file.exists()) {
				file.mkdir();
			}
			CloudBlobContainer container = serviceClient.getContainerReference("htc-pcl-product-images");
			container.createIfNotExists();
			Iterable<ListBlobItem> blobs = container.listBlobs();
			for (ListBlobItem blob : blobs) {
				String[] str1 = blob.getUri().toString().split("/");
				File f = new File(file.getAbsolutePath() + "\\" + str1[4]);
				CloudBlockBlob cloudBlob = (CloudBlockBlob) blob;
				cloudBlob.downloadToFile(f.toString());
			}
		} catch (StorageException | IOException | URISyntaxException | InvalidKeyException exception) {

			System.exit(-1);
		}

		return "success";
	}

	public boolean deleteFile(String name) {
		try {
			client.blobName(name).buildClient().delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

}