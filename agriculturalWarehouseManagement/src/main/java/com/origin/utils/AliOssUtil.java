package com.origin.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.origin.config.AliOssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class AliOssUtil {

    private final AliOssProperties properties;

    public String upload(byte[] bytes, String objectName) {
        OSS ossClient = new OSSClientBuilder().build(
                properties.getEndpoint(),
                properties.getAccessKeyId(),
                properties.getAccessKeySecret());
        try {
            ossClient.putObject(properties.getBucketName(), objectName,
                    new ByteArrayInputStream(bytes));
        } finally {
            ossClient.shutdown();
        }
        String filePath = "https://" + properties.getBucketName() + "."
                + properties.getEndpoint() + "/" + objectName;
        log.info("文件上传到: {}", filePath);
        return filePath;
    }
}
