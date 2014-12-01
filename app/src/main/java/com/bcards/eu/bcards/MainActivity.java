package com.bcards.eu.bcards;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {

    //camera take pictures
    final int TAKE_PHOTO_CODE = 1099;
    Uri _photoFileUri2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {


            FragmentTransaction fragTrans = getFragmentManager().beginTransaction();
            PlaceholderFragment myFragment = new PlaceholderFragment();

            //fragTrans.replace(android.R.id.content, myFragment, "MY_FRAGMENT");

            fragTrans.add(R.id.container, myFragment,"MY_FRAGMENT")
                    .commit();


           // getFragmentManager().beginTransaction()
                    //.replace(R.id.MainFrameLayout,myFragment,"MY_FRAGMENT")
                    //.addToBackStack("YOUR_SOURCE_FRAGMENT_TAG").commit();

        }
    }

    public void onBtnClicked(View v){
        if(v.getId() == R.id.btnCam){
            TakePicture();
        }
    }

    private void TakePicture() {
        _photoFileUri2 = generateTimeStampPhotoFileUri2();


        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, _photoFileUri2);

        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        Bitmap imageBitmap = null;

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "User Canceled", Toast.LENGTH_LONG).show();
            return;
        }

        switch(requestCode) {
            case TAKE_PHOTO_CODE: {
                Log.d("CameraDemo", "Pic saved");
                Toast.makeText(getBaseContext(), "Sending to be recognized ...",
                        Toast.LENGTH_SHORT).show();

                imageBitmap = BitmapFactory.decodeFile(_photoFileUri2.getPath());

                //base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] bytes = baos.toByteArray();
                //String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                String encodedString = "";//Base64.encodeToString(bytes, Base64.DEFAULT);

                PostDataToCloud(encodedString);
                break;
            }
        }

        if(imageBitmap != null) {
            //imageView.setImageBitmap(imageBitmap);
        }

        //notify galery
        //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
        //Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }

    File getPhotoDirectory() {
        File outputDir = null;
        String externalStorageState = Environment.getExternalStorageState();
        if(externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File pictureDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outputDir = new File(pictureDir, "BCards");
            if (!outputDir.exists()) {
                if(!outputDir.mkdirs()) {
                    Toast.makeText(this, "Failed to create directory: " + outputDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    outputDir = null;
                }
            }
        }

        return outputDir;
    }

    Uri generateMockUri3() {
        Uri photoFileUri = null;
        File outputDir = getPhotoDirectory();

        if(outputDir != null) {
            String photoFileName = "IMG_201411330_230241.jpg";

            File photoFile = new File(outputDir, photoFileName);
            photoFileUri = Uri.fromFile(photoFile);
        }
        return photoFileUri;
    }

    Uri generateTimeStampPhotoFileUri2() {
        Uri photoFileUri = null;
        File outputDir = getPhotoDirectory();

        if(outputDir != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
            String photoFileName = "IMG_" + timeStamp + ".jpg";

            File photoFile = new File(outputDir, photoFileName);
            photoFileUri = Uri.fromFile(photoFile);
        }


        return photoFileUri;
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

            Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
            myIntent.putExtra("key", "moshe"); //Optional parameters
            MainActivity.this.startActivity(myIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public ArrayAdapter<String> fieldsOcr;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            List<String> productsList = new ArrayList<String>();

            fieldsOcr = new ArrayAdapter<String>(getActivity(), R.layout.list_item_bcard,R.id.list_item_bcards_textview,productsList);

            ListView lv1 = (ListView) rootView.findViewById(R.id.listview_bcards_list);

            lv1.setAdapter(fieldsOcr);

            return rootView;
        }
    }

    /// POST Picture with Data
    private void PostDataToCloud(String b64Image) {
        PostImageTask pi = new PostImageTask();
        pi.execute(b64Image);
    }


    public class PostImageTask extends AsyncTask<String, Integer, String[]> {

        private final String LOG_TAG =PostImageTask.class.getSimpleName();

        protected String[] doInBackground(String... userCodes) {
            String[] results = null;
            try {
                String image64 = userCodes[0];
                results = SendImageString(image64);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return results;
        }

        private String[] SendImageString(String imgContent) throws Exception {

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("PublisherId","1091");
            map.put("Id","1234");
            map.put("Description","descr");
            map.put("ImageContent",imgContent);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String key = getResources().getString(R.string.pref_baseurl_key);
            String def = getResources().getString(R.string.pref_baseurl_default);
            String baseUrl = prefs.getString(key, def);

            String fileName = _photoFileUri2.getPath();
            //String fileName = generateMockUri2().getPath();
            String result = UrlHelper.uploadFileToServer(baseUrl + "/api/values", fileName);

            String[] results = JsonHelper.GetOcrDataFromResponse(result);
            return results;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String[] fieldsOcr) {

            UpdateFieldsView(fieldsOcr);
        }

        private void UpdateFieldsView(String[] formatedData) {

            PlaceholderFragment myFragment = (PlaceholderFragment)getFragmentManager().findFragmentByTag("MY_FRAGMENT");
            if (myFragment.isVisible()) {

                myFragment.fieldsOcr.clear();
                if (formatedData == null) {
                    formatedData = new String[1];
                    formatedData[0] = "No results";
                }
                myFragment.fieldsOcr.addAll(formatedData);
            }
        }
    }
}
