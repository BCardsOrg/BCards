package com.bcards.eu.bcards;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public ArrayAdapter<String> docTypesArray;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            List<String> docs = new ArrayList<String>();
            docs.add("bcards");
            docs.add("stubs");
            docTypesArray = new ArrayAdapter<String>(getActivity(), R.layout.list_item_doc_types,R.id.list_item_doc_type_textview,docs);

            ListView listView = (ListView) rootView.findViewById(R.id.list_doc_types);
            listView.setAdapter(docTypesArray);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String docType = docTypesArray.getItem(position);
                    Intent ourIntent = new Intent(rootView.getContext(), GenericResultActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, docType );
                    ourIntent.putExtra(Intent.EXTRA_TITLE, getClass().toString());
                    startActivity(ourIntent);
                }
            });

            final EditText editText = (EditText) rootView.findViewById(R.id.editText);

            Button btnAdd = (Button) rootView.findViewById(R.id.btnAddDoc);

            btnAdd.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View view) {
                            if( editText.getText().toString().length() == 0)
                            {
                                Toast.makeText(getActivity(), "Please fill the document type first", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                docTypesArray.add(editText.getText().toString());
                            }
                        }
                    });

            return rootView;
        }
    }
}
