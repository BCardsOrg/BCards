package com.bcards.eu.common;

import com.bcards.eu.bcards.DataFieldRecognition;

import java.util.List;

/**
 * Created by Eugen.Horovitz on 29/12/2014.
 */
public interface IOnFieldsResultsReceived {
    void fireFieldsResultsReceived(List<DataFieldRecognition> fields);
}
