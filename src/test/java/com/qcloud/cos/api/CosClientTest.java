package com.qcloud.cos.api;

import com.qcloud.cos.common.CosResponse;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * cos cloud test
 *
 * @author linux_china
 */
public class CosClientTest {
    private static CosClient cos;
    private static BucketOperation bucketOperation;

    @BeforeClass
    public static void setUp() {
        Integer appId = 123;
        String secretId = "xxxx";
        String secretKey = "yyyy";
        cos = new CosClient(appId, secretId, secretKey);
        bucketOperation = cos.getBucketOperation("bucketName");
    }

    @Test
    public void testCreateFolder() throws Exception {
        String directName = "/demo";
        CosResponse result = bucketOperation.createFolder(directName);
        System.out.println(result.isSuccess());
    }

    @Test
    public void testDeleteFolder() throws Exception {
        String directName = "/demo";
        CosResponse result = bucketOperation.deleteFolder(directName);
        System.out.println(result.isSuccess());
    }

    @Test
    public void testUploadStream() throws Exception {
        CosResponse result = bucketOperation.uploadStream("/ok.txt", "text/plain", new ByteArrayInputStream("good".getBytes()));
        System.out.println(result.isSuccess());
    }

    @Test
    public void testUploadFile() throws Exception {
        CosResponse result = bucketOperation.uploadFile("/2.jpg", new File(System.getProperty("user.home") + "/1.jpg"));
        System.out.println(result.isSuccess());
    }

    @Test
    public void testDeleteFile() throws Exception {
        CosResponse result = bucketOperation.deleteFile("/2.jpg");
        System.out.println(result.isSuccess());
    }

    @Test
    public void testAllOperations() throws Exception {
        long start = System.currentTimeMillis();
        CosResponse result = null;
        result = bucketOperation.createFolder("/sdk/");
        printErrorMessage(result);
        result = bucketOperation.uploadFile("/sdk/xx.txt", new File("/Users/linux_china/ok.txt"));
        printErrorMessage(result);
        result = bucketOperation.updateFileAttribute("/sdk/xx.txt", "author:linux_china");
        printErrorMessage(result);
        result = bucketOperation.getFileInfo("/sdk/xx.txt");
        printErrorMessage(result);
        result = bucketOperation.updateFolderAttribute("/sdk/", "color:red");
        printErrorMessage(result);
        result = bucketOperation.getFolderInfo("/sdk/");
        printErrorMessage(result);
        result = bucketOperation.getFolderList("/", 20, "", 0, BucketOperation.FolderPattern.Both);
        printErrorMessage(result);
        result = bucketOperation.deleteFile("/sdk/xx.txt");
        printErrorMessage(result);
        result = bucketOperation.deleteFolder("/sdk/");
        printErrorMessage(result);
        //FileInputStream方式上传
        bucketOperation.deleteFile("/stream1.txt");
        result = bucketOperation.uploadStream("/stream1.txt", "text/plain", new ByteArrayInputStream("good".getBytes()));
        printErrorMessage(result);
        result = bucketOperation.deleteFile("/stream1.txt");
        printErrorMessage(result);
        long end = System.currentTimeMillis();
        System.out.println("总用时：" + (end - start) + "毫秒");
        System.out.println("The End!");
    }

    public void printErrorMessage(CosResponse result) {
        if (!result.isSuccess()) {
            System.err.println(result.getMessage());
        }
    }

}
