package com.qcloud.cos.api;

import com.qcloud.cos.api.impl.BucketOperationImpl;

/**
 * COS Cloud
 *
 * @author linux_china
 */
public class CosClient {
    /**
     * COS CGI URL
     */
    public static final String COSAPI_CGI_URL = "http://web.file.myqcloud.com/files/v1/";
    /**
     * APP ID
     */
    private int appId;
    /**
     * secretID
     */
    private String secretId;
    /**
     * secretKey
     */
    private String secretKey;
    /**
     * timeout
     */
    private int timeout;

    /**
     * CosCloud
     *
     * @param appId     APP ID
     * @param secretId  secretID
     * @param secretKey secretKey
     */
    public CosClient(int appId, String secretId, String secretKey) {
        this(appId, secretId, secretKey, 60);
    }

    /**
     * CosCloud
     *
     * @param appId     APP ID
     * @param secretId  secretID
     * @param secretKey secretKey
     * @param timeout   timeout
     */
    public CosClient(int appId, String secretId, String secretKey, int timeout) {
        this.appId = appId;
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.timeout = timeout * 1000;
    }

    /**
     * get bucket operation
     *
     * @param bucketName bucket name
     * @return bucket operation
     */
    public BucketOperation getBucketOperation(String bucketName) {
        return new BucketOperationImpl(appId, secretId, secretKey, timeout, bucketName);
    }


}
