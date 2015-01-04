package com.bcards.eu.common;

import android.util.Xml;

import java.util.List;

import javax.xml.validation.Schema;

/**
 * Created by Eugen.Horovitz on 29/12/2014.
 */
public class DataClassifyResultField {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DataClassifyResultFieldCandidatesCandidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<DataClassifyResultFieldCandidatesCandidate> candidates) {
        this.candidates = candidates;
    }

    public String id;

    public List<DataClassifyResultFieldCandidatesCandidate> candidates;

}
