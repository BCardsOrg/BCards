package com.bcards.eu.bcards;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.ContextWrapper;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Catalano.Core.IntPoint;
import Catalano.Imaging.Concurrent.Filters.BradleyLocalThreshold;
import Catalano.Imaging.Concurrent.Filters.Grayscale;
import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.FastGraphics;
import Catalano.Imaging.Tools.Blob;
import Catalano.Imaging.Tools.BlobDetection;
import Catalano.Math.Geometry.PointsCloud;

public class MainActivity extends Activity {

    //camera take pictures
    final int TAKE_PHOTO_CODE = 1099;
    Uri _photoFileUri2;
    PlaceholderFragment myFragment2;
    String resizedPath;

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

        }
    }

    public void onBtnClicked(View v){
        if(v.getId() == R.id.btnCam){
            TakePicture();

            //Intent ourIntent = new Intent(v.getContext(), MyCameraActivity.class)
                    //.putExtra(Intent.EXTRA_TEXT, "");
            //startActivity(ourIntent);
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
        Bitmap imageBitmap2 = null;

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "User Canceled", Toast.LENGTH_LONG).show();
            return;
        }

        switch(requestCode) {
            case TAKE_PHOTO_CODE: {
                Log.d("CameraDemo", "Pic saved");
                Toast.makeText(getBaseContext(), "Sending to be recognized ...",
                        Toast.LENGTH_SHORT).show();

                if (_photoFileUri2 == null) {
                    Toast.makeText(this, "Failed send image.. Please try again: ", Toast.LENGTH_LONG).show();
                    return;
                }

                //imageBitmap2 = BitmapFactory.decodeFile(_photoFileUri2.getPath());
                imageBitmap2 = loadImage(_photoFileUri2.getPath());

                //
                //saveToInternalSorage(resized,_photoFileUri2);

                //ImageIcon imageIcon = new ImageIcon("src/slime.png");

                //Icon pie = new ImageIcon(ImageIO.read( FirstUI.class.getResourceAsStream( "pie.png" ) ) );


                //base64
                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //imageBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object

                //Bitmap resized = Bitmap.createScaledBitmap(imageBitmap2,(int)(imageBitmap2.getWidth()*0.2), (int)(imageBitmap2.getHeight()*0.2), true);
                //UseCatalano2(imageBitmap2);
                //saveToInternalSorage(resized,_photoFileUri2);


                //byte[] bytes = baos.toByteArray();
                //String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                String encodedString = "";//Base64.encodeToString(bytes, Base64.DEFAULT);

                PostDataToCloud(encodedString);
                ImageView iv = (ImageView)findViewById(R.id.imageView1);
                iv.setImageBitmap(imageBitmap2);
                break;
            }
        }

        if(imageBitmap2 != null) {
            //imageView.setImageBitmap(imageBitmap);
        }

        //notify galery
        //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
        //Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }

    private Bitmap loadImage(String imgPath) {
        BitmapFactory.Options options;
        try {
            options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
            return bitmap;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void UseCatalano3(Bitmap resized) {
        //breadley
        //String p1= _photoFileUri2.getPath();
        FastBitmap fb = new FastBitmap(resized);

        // Convert to grayscale
        Grayscale g = new Grayscale();
        g.applyInPlace(fb);


        //SusanCornersDetector susan = new SusanCornersDetector();
        //ArrayList<IntPoint> lst = susan.ProcessImage(fb);


        //If you want to detect using Harris
        //HarrisCornersDetector harris = new HarrisCornersDetector();
        //ArrayList<IntPoint> lst2 = harris.ProcessImage(fb);

        // Apply Bradley local threshold
        BradleyLocalThreshold bradley = new BradleyLocalThreshold();
        bradley.applyInPlace(fb);

        //breadley

        Bitmap b2 = fb.toBitmap();
        saveToInternalSorage(b2,_photoFileUri2);
    }

    private Bitmap UseCatalano2(Bitmap resized) {
        FastBitmap fb = new FastBitmap(resized);

        // Convert to grayscale
        //Grayscale g = new Grayscale();
        //g.applyInPlace(fb);

        //Threshold t = new Threshold();
        //t.applyInPlace(fb);

        BlobDetection bd = new BlobDetection();
        ArrayList<Blob> blobs = bd.ProcessImage(fb);

        fb.toRGB();
        FastGraphics fg = new FastGraphics(fb);
        //FastGraphics g = fb.();
        fg.setColor(255,0,0);

        for (Blob blob : blobs) {
            ArrayList<IntPoint> lst = PointsCloud.GetBoundingRectangle(blob.getPoints());

            int height = Math.abs(lst.get(0).x - lst.get(1).x);
            int width = Math.abs(lst.get(0).y - lst.get(1).y);

            if (height > 300 && width >600) {
                fg.DrawRectangle(lst.get(0).x, lst.get(0).y, width, height);
            }
        }

        //JOptionPane.showMessageDialog(null, image.toIcon());

        Bitmap b2 = fb.toBitmap();
        saveToInternalSorage(b2,_photoFileUri2);
        return b2;
    }

    private String saveToInternalSorage(Bitmap bitmapImage, Uri uriOriginal){

        resizedPath = uriOriginal.getPath()+".resized.jpg";



        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        //File mypath3=new File(directory,"profile.jpg");

        FileOutputStream fos = null;

        try {

            fos = new FileOutputStream(resizedPath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
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
        public MyArrayAdapter m_fields;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            SetLayoutAdapter(rootView);

            //SetAdapter2(rootView);

            return rootView;
        }

        private void SetAdapter2(View rootView) {
            List<DataFieldRecognition> fieldsRecognition = new ArrayList<DataFieldRecognition>();
            m_fields = new MyArrayAdapter(getActivity(),fieldsRecognition);
            ListView lv1 = (ListView) rootView.findViewById(R.id.listview_bcards_list);
            lv1.setAdapter(fieldsOcr);
        }

        private void SetLayoutAdapter(View rootView) {
            List<String> productsList = new ArrayList<String>();

            fieldsOcr = new ArrayAdapter<String>(getActivity(), R.layout.list_item_bcard,R.id.list_item_bcards_textview,productsList);

            ListView lv1 = (ListView) rootView.findViewById(R.id.listview_bcards_list);

            lv1.setAdapter(fieldsOcr);
        }
    }

    /// POST Picture with Data
    private void PostDataToCloud(String b64Image) {
        PostImageTask pi = new PostImageTask();
        pi.execute(b64Image);
    }


    public class PostImageTask extends AsyncTask<String, Integer, String[]> {

        //private final String LOG_TAG =PostImageTask.class.getSimpleName();

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

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String key = getResources().getString(R.string.pref_baseurl_key);
            String def = getResources().getString(R.string.pref_baseurl_default);
            String baseUrl = prefs.getString(key, def);

            String fileName = _photoFileUri2.getPath();
            //String fileName = resizedPath;

            //String fileName = generateMockUri2().getPath();
            String result = UrlHelper.uploadFileToServer(baseUrl + "/api/values", fileName);
            //String result = UrlHelper.uploadFileToServer("http://10.10.10.43:8888/api/Files/UploadFiles",fileName);

            return JsonHelper.GetOcrDataFromResponse(result);
            //return results;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String[] fieldsOcr) {

            UpdateFieldsView(fieldsOcr);
        }

        private void UpdateFieldsView(String[] formatedData) {

            PlaceholderFragment myFragment = (PlaceholderFragment)getFragmentManager().findFragmentByTag("MY_FRAGMENT");
            if (myFragment == null) {
                //Toast.makeText(this, "Failed to create directory: ", Toast.LENGTH_LONG).show();
                return;
            }


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


