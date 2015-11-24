package com.qcloud.cos.api;

import com.qcloud.cos.common.CosResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * COS bucket operation
 *
 * @author linux_china
 */
public interface BucketOperation {
    public enum FolderPattern {File, Folder, Both}

    /**
     * 创建文件夹
     *
     * @param remotePath 远程文件夹路径
     * @return COS Response
     * @throws Exception
     */
    public CosResponse createFolder(String remotePath) throws IOException;

    /**
     * 更新文件夹信息
     *
     * @param remotePath   远程文件夹路径
     * @param bizAttribute 更新信息
     * @return COS Response
     * @throws Exception
     */
    public CosResponse updateFolderAttribute(String remotePath, String bizAttribute) throws IOException;

    /**
     * 删除文件夹
     *
     * @param remotePath 远程文件夹路径
     * @return COS Response
     * @throws Exception
     */
    public CosResponse deleteFolder(String remotePath) throws IOException;


    /**
     * 目录列表
     *
     * @param remotePath 远程文件夹路径
     * @param num        拉取的总数
     * @param context    透传字段，查看第一页，则传空字符串。若需要翻页，需要将前一页返回值中的context透传到参数中。order用于指定翻页顺序。若order填0，则从当前页正序/往下翻页；若order填1，则从当前页倒序/往上翻页。
     * @param order      默认正序(=0), 填1为反序
     * @param pattern    拉取模式:只是文件，只是文件夹，全部
     * @return COS Response
     * @throws Exception
     */
    public CosResponse getFolderList(String remotePath, int num, String context, int order, FolderPattern pattern) throws IOException;

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
     * @throws Exception
     */
    public CosResponse getFolderList(String remotePath, String prefix, int num, String context, int order, FolderPattern pattern) throws IOException;

    /**
     * 获取文件夹信息
     *
     * @param remotePath 远程文件夹路径
     * @return COS Response
     * @throws Exception
     */
    public CosResponse getFolderInfo(String remotePath) throws IOException;

    /**
     * 更新文件信息
     *
     * @param remotePath   远程文件路径
     * @param bizAttribute 更新信息
     * @return COS Response
     * @throws Exception
     */
    public CosResponse updateFileAttribute(String remotePath, String bizAttribute) throws IOException;

    /**
     * 删除文件
     *
     * @param remotePath 远程文件路径
     * @return COS Response
     * @throws Exception
     */
    public CosResponse deleteFile(String remotePath) throws IOException;

    /**
     * 获取文件信息
     *
     * @param remotePath 远程文件路径
     * @return COS Response
     * @throws Exception
     */
    public CosResponse getFileInfo(String remotePath) throws IOException;

    /**
     * 单个文件上传
     *
     * @param remotePath 远程文件路径
     * @param localFile  本地文件路径
     * @return COS Response
     * @throws Exception
     */
    public CosResponse uploadFile(String remotePath, File localFile) throws IOException;

    /**
     * 流单个文件上传
     *
     * @param remotePath  远程文件路径
     * @param inputStream 文件流
     * @return COS Response
     * @throws Exception
     */
    public CosResponse uploadStream(String remotePath, String contentType, InputStream inputStream) throws IOException;

    /**
     * 分片上传第一步
     *
     * @param remotePath 远程文件路径
     * @param localFile  本地文件路径
     * @param sliceSize  切片大小（字节）
     * @return COS Response
     * @throws Exception
     */
    public CosResponse sliceUploadFileFirstStep(String remotePath, File localFile, int sliceSize) throws IOException;

    /**
     * 分片上传后续步骤
     *
     * @param remotePath 远程文件路径
     * @param localFile  本地文件路径
     * @param sessionId  分片上传会话ID
     * @param offset     文件分片偏移量
     * @param sliceSize  切片大小（字节）
     * @return COS Response
     * @throws Exception
     */
    public CosResponse sliceUploadFileFollowStep(String remotePath, File localFile,
                                                 String sessionId, long offset, int sliceSize) throws IOException;

    /**
     * 分片上传，默认切片大小为512K
     *
     * @param remotePath 远程文件路径
     * @param localFile  本地文件路径
     * @return COS Response
     * @throws Exception
     */
    public CosResponse sliceUploadFile(String remotePath, File localFile) throws IOException;

    /**
     * 分片上传
     *
     * @param remotePath 远程文件路径
     * @param localFile  本地文件路径
     * @param sliceSize  切片大小（字节）
     * @return COS Response
     * @throws Exception
     */
    public CosResponse sliceUploadFile(String remotePath, File localFile, int sliceSize) throws Exception;
}