package com.bcards.eu.common;

import android.util.Xml;

import javax.xml.validation.Schema;

/**
 * Created by Eugen.Horovitz on 29/12/2014.
 */
public class DataClassifyResultFieldCandidatesCandidate {
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getConfidance() {
        return confidance;
    }

    public void setConfidance(String confidance) {
        this.confidance = confidance;
    }

    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }

    public String getHeight() {
        return Height;
    }

    public void setHeight(String height) {
        Height = height;
    }

    public String getWidth() {
        return Width;
    }

    public void setWidth(String width) {
        Width = width;
    }

    public String value;

    public String confidance;

    public String X;

    public String Y;

    public String Height;

    public String Width;
}
