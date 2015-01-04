package com.bcards.eu.bcards;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by Eugen.Horovitz on 28/12/2014.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity > {

    MainActivity mainActivity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
    }

    public MainActivityTest(){
        super(MainActivity.class);
    }

    @SmallTest
    public void testListViewNotNull(){
        ListView lv1 = (ListView) mainActivity.findViewById(R.id.listview_bcards_list);
        //lv1 = null;
        assertNotNull(lv1);
    }
}
