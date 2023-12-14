package de.themoep.s3redirector;

/*
 * s3redirector
 * Copyright (C) 2023 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.net.URL;
import java.util.Date;

import static de.themoep.s3redirector.Main.logDebug;

public class S3Adapter {

	private final AmazonS3 s3;
	private final long urlExpiration;

	/*
	 * Constructor
	 * @param endpoint The endpoint of the S3 server
	 * @param region The region of the S3 server
	 * @param accessKey The access key of the S3 server
	 * @param secretKey The secret key of the S3 server
	 * @param urlExpiration The expiration time of the signed url in milliseconds
	 */
	public S3Adapter(String endpoint, String region, String accessKey, String secretKey, long urlExpiration) {
		this.urlExpiration = urlExpiration;
		s3 = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
				.build();
		logDebug("Initialized S3 client for " + endpoint + " in " + region + " with access key " + accessKey + " and expiration " + urlExpiration + "ms");
	}

    /**
     * Gets the signed external url of an object
     * @param bucket  The name of the S3 bucket
     * @param address The address of the object
     * @return The external url of the object
     */
    public URL getObjectUrl(String bucket, String address) {
		if (s3.doesObjectExist(bucket, address)) {
			return s3.generatePresignedUrl(bucket, address, new Date(System.currentTimeMillis() + urlExpiration));
		}
		return null;
    }
}

