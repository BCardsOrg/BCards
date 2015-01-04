package com.bcards.eu.bcards;

import com.bcards.eu.common.DataClassifyResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.jar.JarEntry;

/**
 * Created by eu on 11/30/2014.
 */
public class JsonHelper {

    public static String[] GetOcrDataFromResponse2(String jSonResultString) throws JSONException {

        //{"Items": [{"id": "id1","candidates": [{"value": "val0","confidance": "conf0"},{"value": "val1","confidance": "conf1"}]}]}
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_ITEMS = "Items";
        final String OWM_ID = "id";
        final String OWM_CANDIDATES = "candidates";
        final String OWM_VALUE = "value";
        final String OWM_CONFIDENCE = "confidance";

        JSONObject jObject1 = new JSONObject(jSonResultString);

        JSONArray fieldArray = jObject1.getJSONArray(OWM_ITEMS);

        String[] resultStrs = new String[fieldArray.length()];
        for (int i = 0; i < fieldArray.length(); i++) {

            String id="";
            String cand1="";

            // Get the JSON object representing the day
            JSONObject product = fieldArray.getJSONObject(i);

            id = product.getString(OWM_ID);
            JSONArray candidates = product.getJSONArray(OWM_CANDIDATES);

            for (int j = 0; j < fieldArray.length(); j++) {
                JSONObject candidate = candidates.getJSONObject(j);
                    cand1 = candidate.getString(OWM_VALUE);//+ " - " + candidate.getString(OWM_CONFIDENCE);
                break;
            }
            if ( id.contains("kw") == false)
                resultStrs[i] = id + " - " + cand1;
        }
        return resultStrs;
    }


    public static DataClassifyResult GetDocResultFromResponse(String jSonResultString)
    {
        DataClassifyResult classifyResult = null;
        try {
            //ObjectMapper mapper = new ObjectMapper();
            //classifyResult = mapper.readValue(jSonResultString, DataClassifyResult.class);
            Gson gson = new Gson();
            classifyResult = gson.fromJson(jSonResultString, DataClassifyResult.class);
        }
        catch(Exception ex) {
           String s = ex.toString();
        }
        return  classifyResult;
    }

    public static DataClassifyResult GetDocResultFromResponseMock(String jSonResultString) throws JSONException
    {
        DataClassifyResult doc =  new DataClassifyResult();
        return doc;
    }
}