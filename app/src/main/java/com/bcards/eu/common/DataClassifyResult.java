package com.bcards.eu.common;

import android.util.Xml;

import java.util.List;

import javax.xml.validation.Schema;

/**
 * Created by Eugen.Horovitz on 29/12/2014.
 */
public class DataClassifyResult {
    public String getDocID() {
        return DocID;
    }

    public void setDocID(String docID) {
        DocID = docID;
    }

    public List<DataClassifyResultField> getItems() {
        return Items;
    }

    public void setItems(List<DataClassifyResultField> items) {
        Items = items;
    }

    public String DocID;
    public List<DataClassifyResultField> Items;
}
