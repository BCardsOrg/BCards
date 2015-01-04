package com.bcards.eu.common;

/**
 * Created by Eugen.Horovitz on 29/12/2014.
 */
public interface ICloudService {
    DataClassifyResult UploadImage(UrlFilePair url,String filename);

    DataClassifyResult UploadImageAsync(UrlFilePair url,String filename);

    void UploadResult(String url,DataClassifyResult result);

    void UploadResultAsync(String url,DataClassifyResult result);
}
