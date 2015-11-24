package com.qcloud.cos.api.impl;

import com.qcloud.cos.api.BucketOperation;
import com.qcloud.cos.api.CosCloud;
import com.qcloud.cos.common.CosRequest;
import com.qcloud.cos.common.CosResponse;
import com.qcloud.cos.common.Sign;
import com.qcloud.cos.common.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * bucket operation implementation
 *
 * @author linux_china
 */
public class BucketOperationImpl implements BucketOperation {
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
     * bucket name
     */
    private String bucketName;

    public BucketOperationImpl(int appId, String secretId, String secretKey, int timeout, String bucketName) {
        this.bucketName = bucketName;
        this.appId = appId;
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.timeout = timeout;
    }

    /**
     * 更新文件夹信息
     *
     * @param remotePath   远程文件夹路径
     * @param bizAttribute 更新信息
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse updateFolderAttribute(String remotePath, String bizAttribute) throws IOException {
        remotePath = standardizationRemotePath(remotePath);
        return updateFileAttribute(remotePath, bizAttribute);
    }

    /**
     * 更新文件信息
     *
     * @param remotePath   远程文件路径
     * @param bizAttribute 更新信息
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse updateFileAttribute(String remotePath, String bizAttribute) throws IOException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("op", "update");
        data.put("biz_attr", bizAttribute);
        String sign = Sign.appSignatureOnce(appId, secretId, secretKey, (remotePath.startsWith("/") ? "" : "/") + remotePath, bucketName);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Authorization", sign);
        header.put("Content-Type", "application/json");
        return CosRequest.build(getPathUrl(remotePath), header, data, timeout).post();
    }

    /**
     * 删除文件夹
     *
     * @param remotePath 远程文件夹路径
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse deleteFolder(String remotePath) throws IOException {
        remotePath = standardizationRemotePath(remotePath);
        return deleteFile(remotePath);
    }

    /**
     * 删除文件
     *
     * @param remotePath 远程文件路径
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse deleteFile(String remotePath) throws IOException {
        if (remotePath.equals("/")) {
            throw new IOException("can not delete bucket using aip! go to http://console.qcloud.com/cos to operate bucket");
        }
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("op", "delete");
        String sign = Sign.appSignatureOnce(appId, secretId, secretKey, (remotePath.startsWith("/") ? "" : "/") + remotePath, bucketName);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Authorization", sign);
        header.put("Content-Type", "application/json");
        return CosRequest.build(getPathUrl(remotePath), header, data, timeout).post();
    }

    /**
     * 获取文件夹信息
     *
     * @param remotePath 远程文件夹路径
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse getFolderInfo(String remotePath) throws IOException {
        remotePath = standardizationRemotePath(remotePath);
        return getFileInfo(remotePath);
    }

    /**
     * 获取文件信息
     *
     * @param remotePath 远程文件路径
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse getFileInfo(String remotePath) throws IOException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("op", "stat");
        long expired = System.currentTimeMillis() / 1000 + 60;
        String sign = Sign.appSignature(appId, secretId, secretKey, expired, bucketName);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Authorization", sign);
        return CosRequest.build(getPathUrl(remotePath), header, data, timeout).get();
    }

    /**
     * 创建文件夹
     *
     * @param remotePath 远程文件夹路径
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse createFolder(String remotePath) throws IOException {
        remotePath = standardizationRemotePath(remotePath);
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("op", "create");
        long expired = System.currentTimeMillis() / 1000 + 60;
        String sign = Sign.appSignature(appId, secretId, secretKey, expired, bucketName);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Authorization", sign);
        header.put("Content-Type", "application/json");
        return CosRequest.build(getPathUrl(remotePath), header, data, timeout).post();
    }

    /**
     * 目录列表
     *
     * @param remotePath 远程文件夹路径
     * @param num        拉取的总数
     * @param context    透传字段，查看第一页，则传空字符串。若需要翻页，需要将前一页返回值中的context透传到参数中。order用于指定翻页顺序。若order填0，则从当前页正序/往下翻页；若order填1，则从当前页倒序/往上翻页。
     * @param order      默认正序(=0), 填1为反序
     * @param pattern    拉取模式:只是文件，只是文件夹，全部
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse getFolderList(String remotePath, int num, String context, int order, FolderPattern pattern) throws IOException {
        remotePath = standardizationRemotePath(remotePath);
        return getFolderList(remotePath, "", num, context, order, pattern);
    }

    /**
     * 目录列表,前缀搜索
     *
     * @param remotePath 远程文件夹路径
     * @param prefix     读取文件/文件夹前缀
     * @param num        拉取的总数
     * @param context    透传字段，查看第一页，则传空字符串。若需要翻页，需要将前一页返回值中的context透传到参数中。order用于指定翻页顺序。若order填0，则从当前页正序/往下翻页；若order填1，则从当前页倒序/往上翻页。
     * @param order      默认正序(=0), 填1为反序
     * @param pattern    拉取模式:只是文件，只是文件夹，全部
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse getFolderList(String remotePath, String prefix, int num, String context, int order, FolderPattern pattern) throws IOException {
        remotePath = standardizationRemotePath(remotePath);
        String url = getPathUrl(remotePath) + Utils.urlEncode(prefix);
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("op", "list");
        data.put("num", num);
        data.put("context", context);
        data.put("order", order);
        String[] patternArray = {"eListFileOnly", "eListDirOnly", "eListBoth"};
        data.put("pattern", patternArray[pattern.ordinal()]);
        long expired = System.currentTimeMillis() / 1000 + 60;
        String sign = Sign.appSignature(appId, secretId, secretKey, expired, bucketName);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Authorization", sign);
        return CosRequest.build(url, header, data, timeout).get();
    }

    /**
     * 单个文件上传
     *
     * @param remotePath 远程文件路径
     * @param localFile  本地文件路径
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse uploadFile(String remotePath, File localFile) throws IOException {
        String sha1 = Utils.getFileSha1(localFile);
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("op", "upload");
        data.put("sha", sha1);
        long expired = System.currentTimeMillis() / 1000 + 60;
        String sign = Sign.appSignature(appId, secretId, secretKey, expired, bucketName);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Authorization", sign);
        return CosRequest.build(getPathUrl(remotePath), header, data, timeout).uploadLocalFile(localFile);
    }

    /**
     * 流单个文件上传
     *
     * @param remotePath  远程文件路径
     * @param inputStream 文件流
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse uploadStream(String remotePath, String contentType, InputStream inputStream) throws IOException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        byte[] content = IOUtils.toByteArray(inputStream);
        String sha1 = DigestUtils.sha1Hex(content);
        data.put("op", "upload");
        data.put("sha", sha1);
        long expired = System.currentTimeMillis() / 1000 + 60;
        String sign = Sign.appSignature(appId, secretId, secretKey, expired, bucketName);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Authorization", sign);
        return CosRequest.build(getPathUrl(remotePath), header, data, timeout).uploadByteArray(contentType, content);
    }

    /**
     * 分片上传第一步
     *
     * @param remotePath 远程文件路径
     * @param localFile  本地文件路径
     * @param sliceSize  切片大小（字节）
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse sliceUploadFileFirstStep(String remotePath, File localFile, int sliceSize) throws IOException {
        String sha1 = Utils.getFileSha1(localFile);
        System.out.println(sha1);
        long fileSize = localFile.length();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("op", "upload_slice");
        data.put("sha", sha1);
        data.put("filesize", fileSize);
        data.put("slice_size", sliceSize);
        long expired = System.currentTimeMillis() / 1000 + 60;
        String sign = Sign.appSignature(appId, secretId, secretKey, expired, bucketName);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Authorization", sign);
        return CosRequest.build(getPathUrl(remotePath), header, data, timeout).post();
    }

    /**
     * 分片上传后续步骤
     *
     * @param remotePath 远程文件路径
     * @param localFile  本地文件路径
     * @param sessionId  分片上传会话ID
     * @param offset     文件分片偏移量
     * @param sliceSize  切片大小（字节）
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse sliceUploadFileFollowStep(String remotePath, File localFile,
                                                 String sessionId, long offset, int sliceSize) throws IOException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("op", "upload_slice");
        data.put("session", sessionId);
        data.put("offset", offset);
        long expired = System.currentTimeMillis() / 1000 + (60 * 60 * 24);
        String sign = Sign.appSignature(appId, secretId, secretKey, expired, bucketName);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Authorization", sign);
        return CosRequest.build(getPathUrl(remotePath), header, data, timeout).uploadSliceFile(localFile, offset, sliceSize);
    }

    /**
     * 分片上传，默认切片大小为512K
     *
     * @param remotePath 远程文件路径
     * @param localFile  本地文件路径
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse sliceUploadFile(String remotePath, File localFile) throws IOException {
        return sliceUploadFile(remotePath, localFile, 512 * 1024);
    }

    /**
     * 分片上传
     *
     * @param remotePath 远程文件路径
     * @param localFile  本地文件路径
     * @param sliceSize  切片大小（字节）
     * @return COS Response
     * @throws IOException IO Exception
     */
    public CosResponse sliceUploadFile(String remotePath, File localFile, int sliceSize) throws IOException {
        CosResponse result = sliceUploadFileFirstStep(remotePath, localFile, sliceSize);
        if (!result.isSuccess()) {
            return result;
        }
        Map<String, Object> data = result.getData();
        if (data.containsKey("access_url")) {
            return result;
        } else {
            String sessionId = (String) data.get("session");
            sliceSize = Integer.valueOf(data.get("slice_size").toString());
            long offset = Long.valueOf(data.get("offset").toString());
            int retryCount = 0;
            while (true) {
                result = sliceUploadFileFollowStep(remotePath, localFile, sessionId, offset, sliceSize);
                if (!result.isSuccess()) {
                    //当上传失败后会重试3次
                    if (retryCount < 3) {
                        retryCount++;
                    } else {
                        return result;
                    }
                } else {
                    data = result.getData();
                    if (data.containsKey("offset")) {
                        offset = Long.valueOf(data.get("offset").toString()) + sliceSize;
                    } else {
                        break;
                    }
                }
            }
        }
        return null;
    }

    private String getPathUrl(String remotePath) {
        return CosCloud.COSAPI_CGI_URL + appId + "/" + bucketName + encodeRemotePath(remotePath);
    }


    /**
     * 远程路径Encode处理
     *
     * @param remotePath remote path
     * @return encoded url path
     */
    private String encodeRemotePath(String remotePath) {
        if (remotePath.equals("/")) {
            return remotePath;
        }
        boolean endWithSlash = remotePath.endsWith("/");
        String[] part = remotePath.split("/");
        remotePath = "";
        for (String s : part) {
            if (!s.equals("")) {
                if (!remotePath.equals("")) {
                    remotePath += "/";
                }
                remotePath += Utils.urlEncode(s);
            }
        }
        remotePath = (remotePath.startsWith("/") ? "" : "/") + remotePath + (endWithSlash ? "/" : "");
        return remotePath;
    }

    /**
     * 标准化远程路径
     *
     * @param remotePath 要标准化的远程路径
     * @return 标准化后的路径
     */
    private String standardizationRemotePath(String remotePath) {
        if (!remotePath.startsWith("/")) {
            remotePath = "/" + remotePath;
        }
        if (!remotePath.endsWith("/")) {
            remotePath += "/";
        }
        return remotePath;
    }
}
