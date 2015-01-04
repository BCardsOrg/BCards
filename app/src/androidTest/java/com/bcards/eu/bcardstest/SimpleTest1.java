package com.bcards.eu.bcardstest;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by Eugen.Horovitz on 28/12/2014.
 */
public class SimpleTest1 extends TestCase{

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void numberAdder(){
        assertEquals(4, 3);
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }
}
