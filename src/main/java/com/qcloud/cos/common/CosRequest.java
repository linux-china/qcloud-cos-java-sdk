package com.qcloud.cos.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Map;

/**
 * COS Request
 *
 * @author linux_china
 */
public class CosRequest {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private String url;
    private Map<String, String> headers;
    private Map<String, Object> data;
    private HttpClient httpClient;

    public static CosRequest build(String url, Map<String, String> headers, Map<String, Object> data, int timeout) {
        return new CosRequest(url, headers, data, timeout);
    }

    public CosRequest(String url, Map<String, String> headers, Map<String, Object> data, int timeout) {
        this.url = url;
        this.headers = headers;
        if (this.headers == null) {
            this.headers = Collections.emptyMap();
        }
        this.data = data;
        if (this.data == null) {
            this.data = Collections.emptyMap();
        }
        this.httpClient = createHttpClient(timeout);
    }

    public CosResponse uploadByteArray(String contentType, byte[] content) throws Exception {
        HttpPost httpPost = createPostMethod();
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        if (data != null) {
            for (String key : data.keySet()) {
                multipartEntityBuilder.addPart(key, new StringBody(data.get(key).toString(), ContentType.TEXT_PLAIN));
            }
        }
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        ByteArrayBody body = new ByteArrayBody(content, ContentType.create(contentType), fileName);
        multipartEntityBuilder.addPart("fileContent", body);
        httpPost.setEntity(multipartEntityBuilder.build());
        return convertToCosResponse(httpClient.execute(httpPost));
    }

    public CosResponse uploadLocalFile(File localFile) throws Exception {
        HttpPost httpPost = createPostMethod();
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        if (!data.isEmpty()) {
            for (String key : data.keySet()) {
                multipartEntityBuilder.addPart(key, new StringBody(data.get(key).toString(), ContentType.TEXT_PLAIN));
            }
        }
        multipartEntityBuilder.addPart("fileContent", new FileBody(localFile));
        httpPost.setEntity(multipartEntityBuilder.build());
        return convertToCosResponse(httpClient.execute(httpPost));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public CosResponse uploadSliceFile(File localFile, long offset, int sliceSize) throws Exception {
        HttpPost httpPost = createPostMethod();
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        if (!data.isEmpty()) {
            for (String key : data.keySet()) {
                multipartEntityBuilder.addPart(key, new StringBody(data.get(key).toString(), ContentType.TEXT_PLAIN));
            }
        }
        //分片上传
        DataInputStream ins = new DataInputStream(new FileInputStream(localFile));
        ins.skip(offset);
        int len = (int) (offset + sliceSize > localFile.length() ? localFile.length() - offset : sliceSize);
        byte[] bufferOut = new byte[len];
        ins.read(bufferOut);
        ContentBody contentBody = new ByteArrayBody(bufferOut, localFile.getName());//new ByteArrayBody(bytes, fileName);
        multipartEntityBuilder.addPart("fileContent", contentBody);
        httpPost.setEntity(multipartEntityBuilder.build());
        return convertToCosResponse(httpClient.execute(httpPost));
    }

    public CosResponse post() throws Exception {
        HttpPost httpPost = createPostMethod();
        if (headers.containsKey("Content-Type") && headers.get("Content-Type").equals("application/json")) {
            String json = objectMapper.writeValueAsString(data);
            StringEntity stringEntity = new StringEntity(json);
            httpPost.setEntity(stringEntity);
        } else {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            if (!data.isEmpty()) {
                for (String key : data.keySet()) {
                    multipartEntityBuilder.addPart(key, new StringBody(data.get(key).toString(), ContentType.TEXT_PLAIN));
                }
            }
            httpPost.setEntity(multipartEntityBuilder.build());
        }
        return convertToCosResponse(httpClient.execute(httpPost));
    }

    public CosResponse get() throws Exception {
        String paramStr = "";
        for (String key : data.keySet()) {
            if (!paramStr.isEmpty()) {
                paramStr += '&';
            }
            paramStr += key + '=' + Utils.urlEncode(data.get(key).toString());
        }
        if (url.indexOf('?') > 0) {
            url += '&' + paramStr;
        } else {
            url += '?' + paramStr;
        }
        HttpGet httpGet = createGetMethod();
        return convertToCosResponse(httpClient.execute(httpGet));
    }

    public static HttpClient createHttpClient(int timeout) {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout);
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfigBuilder.build()).build();
    }

    public HttpPost createPostMethod() {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("accept", "*/*");
        httpPost.setHeader("connection", "Keep-Alive");
        httpPost.setHeader("user-agent", "qcloud-java-sdk");
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return httpPost;
    }

    public HttpGet createGetMethod() {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("accept", "*/*");
        httpGet.setHeader("connection", "Keep-Alive");
        httpGet.setHeader("user-agent", "qcloud-java-sdk");
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return httpGet;
    }

    private CosResponse convertToCosResponse(HttpResponse httpResponse) {
        try {
            return objectMapper.readValue(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"), CosResponse.class);
        } catch (Exception ignore) {
            return null;
        }
    }

}
