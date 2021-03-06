package com.bcards.eu.bcards;

import android.os.AsyncTask;

import com.bcards.eu.common.DataClassifyResult;
import com.bcards.eu.common.ICloudService;
import com.bcards.eu.common.IOnFieldsResultsReceived;
import com.bcards.eu.common.UrlFilePair;

/**
 * Created by Eugen.Horovitz on 29/12/2014.
 */
public class CloudService implements ICloudService {

    private static CloudService Instance ;

    public static CloudService getInstance()
    {
        if (Instance == null)
        {
            Instance = new CloudService();
        }
        return Instance;
    }

    private IOnFieldsResultsReceived FieldsReceivedListener;

    public  IOnFieldsResultsReceived getFieldsReceivedListener() {
        return FieldsReceivedListener;
    }

    public void setFieldsReceivedListener(IOnFieldsResultsReceived listener) {
        this.FieldsReceivedListener = listener;
    }

    @Override
    public DataClassifyResult UploadImage(UrlFilePair url, String filename) {
        return new DataClassifyResult();
    }

    @Override
    public DataClassifyResult UploadImageAsync(UrlFilePair urlFilePair, String filename) {
        PostImageTask pi = new PostImageTask();
        pi.execute(urlFilePair);
        return new DataClassifyResult();
    }

    @Override
    public void UploadResult(String url,DataClassifyResult result)
    {

    }

    @Override
    public void UploadResultAsync(String url,DataClassifyResult result)
    {

    }

    public class PostImageTask extends AsyncTask<UrlFilePair, Integer, DataClassifyResult> {

        //private final String LOG_TAG =PostImageTask.class.getSimpleName();

        protected DataClassifyResult doInBackground(UrlFilePair... files) {
            DataClassifyResult results = null;
            try {
                UrlFilePair urlFile = files[0];

                //String fileName = generateMockUri2().getPath();
                String result = "";//UrlHelper.uploadFileToServer(urlFile.Url, urlFile.FileName);
                //String result = UrlHelper.uploadFileToServer("http://10.10.10.43:8888/api/Files/UploadFiles",fileName);

                return JsonHelper.GetDocResultFromResponse(result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return results;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String[] fieldsOcr) {

            //UpdateFieldsView(fieldsOcr);
        }
    }
}
