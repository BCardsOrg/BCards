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
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bcards.eu.common.DataClassifyResult;
import com.bcards.eu.common.DataClassifyResultField;
import com.bcards.eu.common.DataClassifyResultFieldCandidatesCandidate;

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
import Catalano.Imaging.Filters.Crop;
import Catalano.Imaging.Tools.Blob;
import Catalano.Imaging.Tools.BlobDetection;
import Catalano.Math.Geometry.PointsCloud;

public class GenericResultActivity extends Activity {

    //camera take pictures
    final int TAKE_PHOTO_CODE = 1099;
    Uri _photoFileUri2;
    PlaceholderFragment myFragment2;
    String resizedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_result);
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

                Bitmap resized = Bitmap.createScaledBitmap(imageBitmap2,(int)(imageBitmap2.getWidth()*0.2), (int)(imageBitmap2.getHeight()*0.2), true);
                //Bitmap bw = UseCatalano3(resized);
                Bitmap bw = UseCatalano3(resized);
                saveToInternalSorage2(bw,_photoFileUri2);

                Bitmap corners = UseCatalano2(bw);
                //saveToInternalSorage(resized,_photoFileUri2);

                ImageView iv = (ImageView)findViewById(R.id.imageView1);
                iv.setImageBitmap(corners);
                iv.setVisibility(View.VISIBLE);


                //byte[] bytes = baos.toByteArray();
                //String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                String encodedString = "";//Base64.encodeToString(bytes, Base64.DEFAULT);

                PostDataToCloud(this.getTitle().toString());
                //ImageView iv = (ImageView)findViewById(R.id.imageView1);
                //iv.setImageBitmap(imageBitmap2);
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

    private Bitmap UseCatalano3(Bitmap resized) {
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
        return b2;
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

        ArrayList<IntPoint> lstMax = new ArrayList<>();
        lstMax.add(new IntPoint(0,0));
        lstMax.add(new IntPoint(0,0));
        int heightMax=0;
        int widthMax=0;

        for (Blob blob : blobs) {
            ArrayList<IntPoint> lst = PointsCloud.GetBoundingRectangle(blob.getPoints());

            int height = Math.abs(lst.get(0).x - lst.get(1).x);
            int width = Math.abs(lst.get(0).y - lst.get(1).y);

            heightMax = Math.abs(lstMax.get(0).x - lstMax.get(1).x);
            widthMax = Math.abs(lstMax.get(0).y - lstMax.get(1).y);

            if (height+width >width+heightMax ) {
                lstMax = lst;
            }
            //if( height > 100 && width > 350)
                //fg.DrawRectangle(lst.get(0).x, lst.get(0).y, width, height);
        }

        fg.DrawRectangle(lstMax.get(0).x, lstMax.get(0).y, widthMax, heightMax);

        //JOptionPane.showMessageDialog(null, image.toIcon());

        Crop c = new Crop(lstMax.get(0).x, lstMax.get(0).y, widthMax, heightMax);
        c.ApplyInPlace(fb);

        Bitmap b2 = fb.toBitmap();

        //Bitmap resizedbitmap1=Bitmap.createBitmap(b2,lstMax.get(0).x, lstMax.get(0).y, widthMax, heightMax);

        //saveToInternalSorage2(resizedbitmap1,_photoFileUri2);

        //Bitmap bmp3 = scaleCenterCrop(b2, widthMax, heightMax);
        //saveToInternalSorage2(bmp3,_photoFileUri2);

        //return bmp3;

        saveToInternalSorage2(b2,_photoFileUri2);
        return b2;
    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        //Canvas canvas = new Canvas(dest);
        //canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    private String saveToInternalSorage2(Bitmap bitmapImage, Uri uriOriginal){

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

            Intent myIntent = new Intent(GenericResultActivity.this, SettingsActivity.class);
            myIntent.putExtra("key", "moshe"); //Optional parameters
            GenericResultActivity.this.startActivity(myIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public String documentType ;
        public ArrayAdapter<String> fieldFirstName;
        public ArrayAdapter<String> fieldLastName;
        public ArrayAdapter<String> fieldPosition;
        public ArrayAdapter<String> fieldEmail;
        public ArrayAdapter<String> fieldMobile;
        public ArrayAdapter<String> fieldWebsite;

        public LinearLayout mLinearLayout;
        public Button mButtonContact;

        Spinner sp1;
        Spinner sp2;
        Spinner sp3;
        Spinner sp4;
        Spinner sp5;
        Spinner sp6;

        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        TextView textView5;
        TextView textView6;
        //public MyArrayAdapter m_fields;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            final View rootView = inflater.inflate(R.layout.fragment_generic_result, container, false);


            Intent intent = getActivity().getIntent();
            if (intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                documentType = intent.getStringExtra(Intent.EXTRA_TEXT);
                this.getActivity().setTitle(documentType);
            }

            ImageView iv = (ImageView)rootView.findViewById(R.id.imageView1);
            iv.setVisibility(View.GONE);

            SetLayoutAdapter(rootView);

            mButtonContact.setOnClickListener(
                    new View.OnClickListener()
                    {
                        public void onClick(View view)
                        {
                            addAsContactConfirmed(getActivity(),null);
                        }
                    });

            iv.setOnClickListener(
                    new View.OnClickListener()
                    {
                        public void onClick(View view)
                        {
                            //Intent ourIntent = new Intent(rootView.getContext(), GenericResultActivity.class)
                                    //.putExtra(Intent.EXTRA_TEXT, "" );
                            //ourIntent.putExtra(Intent.EXTRA_TITLE, getClass().toString());
                            //startActivity(ourIntent);
                        }
                    });

            return rootView;
        }

        public void addAsContactConfirmed(final Context context, final Object person) {

            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

            intent.putExtra(ContactsContract.Intents.Insert.NAME, sp1.getSelectedItem().toString() +" " + sp2.getSelectedItem().toString());

            intent.putExtra(ContactsContract.Intents.Insert.PHONE, sp3.getSelectedItem().toString());
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, sp4.getSelectedItem().toString());
            intent.putExtra(ContactsContract.Intents.Insert.COMPANY, sp6.getSelectedItem().toString());
            intent.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, "12121");
            intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, sp5.getSelectedItem().toString());
            //intent.putExtra(ContactsContract.Intents.Insert.NOTES, "web");

            context.startActivity(intent);
        }

        private void SetLayoutAdapter(View rootView) {
            List<String> productsList = new ArrayList<String>();

            fieldFirstName = new ArrayAdapter<String>(getActivity(), R.layout.list_item_bcard,R.id.list_item_bcards_textview,new ArrayList<String>());
            fieldLastName = new ArrayAdapter<String>(getActivity(), R.layout.list_item_bcard,R.id.list_item_bcards_textview,new ArrayList<String>());
            fieldPosition = new ArrayAdapter<String>(getActivity(), R.layout.list_item_bcard,R.id.list_item_bcards_textview,new ArrayList<String>());

            fieldEmail = new ArrayAdapter<String>(getActivity(), R.layout.list_item_bcard,R.id.list_item_bcards_textview,new ArrayList<String>());
            fieldMobile = new ArrayAdapter<String>(getActivity(), R.layout.list_item_bcard,R.id.list_item_bcards_textview,new ArrayList<String>());
            fieldWebsite = new ArrayAdapter<String>(getActivity(), R.layout.list_item_bcard,R.id.list_item_bcards_textview,new ArrayList<String>());


            //ListView lv1 = (ListView) rootView.findViewById(R.id.listview_bcards_list);
            //lv1.setAdapter(fieldsOcr);

            mLinearLayout = (LinearLayout) rootView.findViewById(R.id.linearAll);
            mLinearLayout.setVisibility(View.GONE);

            mButtonContact = (Button) rootView.findViewById(R.id.btnContact);
            mButtonContact.setVisibility(View.GONE);

            sp1 = (Spinner) rootView.findViewById(R.id.spinner1);
            sp1.setAdapter(fieldFirstName);

            sp2 = (Spinner) rootView.findViewById(R.id.spinner2);
            sp2.setAdapter(fieldLastName);

            sp3 = (Spinner) rootView.findViewById(R.id.spinner3);
            sp3.setAdapter(fieldPosition);

            sp4 = (Spinner) rootView.findViewById(R.id.spinner4);
            sp4.setAdapter(fieldEmail);

            sp5 = (Spinner) rootView.findViewById(R.id.spinner5);
            sp5.setAdapter(fieldMobile);

            sp6 = (Spinner) rootView.findViewById(R.id.spinner6);
            sp6.setAdapter(fieldWebsite);

            textView1 = (TextView) rootView.findViewById(R.id.textView1);

            textView2 = (TextView) rootView.findViewById(R.id.textView2);

            textView3 = (TextView) rootView.findViewById(R.id.textView3);

            textView4 = (TextView) rootView.findViewById(R.id.textView4);

            textView5 = (TextView) rootView.findViewById(R.id.textView5);

            textView6 = (TextView) rootView.findViewById(R.id.textView6);
        }
    }

    /// POST Picture with Data
    private void PostDataToCloud(String b64Image) {
        PostImageTask pi = new PostImageTask();
        pi.execute(b64Image);
    }


    public class PostImageTask extends AsyncTask<String, Integer, String> {

        //private final String LOG_TAG =PostImageTask.class.getSimpleName();

        protected String doInBackground(String... userCodes) {
            String results = null;
            try {
                String docType = userCodes[0];
                results = SendImageString(docType);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return results;
        }

        private String SendImageString(String docType) throws Exception {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String key = getResources().getString(R.string.pref_baseurl_key);
            String def = getResources().getString(R.string.pref_baseurl_default);
            String baseUrl = prefs.getString(key, def);

            String fileName = _photoFileUri2.getPath();
            //String fileName = resizedPath;

            //String fileName = generateMockUri2().getPath();
            String result = UrlHelper.uploadFileToServer(baseUrl + "/api/values", fileName ,docType);
            //String result = UrlHelper.uploadFileToServer("http://10.10.10.43:8888/api/Files/UploadFiles",fileName);

            return result;
            //return results;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(String fieldsOcr) {
            DataClassifyResult docClasify = JsonHelper.GetDocResultFromResponse(fieldsOcr);
            UpdateFieldsView(docClasify);
        }

        private void UpdateFieldsView(DataClassifyResult fields) {
            String[] formatedData = null;
            PlaceholderFragment myFragment = (PlaceholderFragment)getFragmentManager().findFragmentByTag("MY_FRAGMENT");
            if (myFragment == null) {
                //Toast.makeText(this, "Failed to create directory: ", Toast.LENGTH_LONG).show();
                return;
            }

            if (myFragment.isVisible()) {

                myFragment.mLinearLayout.setVisibility(View.VISIBLE);
                myFragment.mButtonContact.setVisibility(View.VISIBLE);

                myFragment.fieldFirstName.clear();
                myFragment.fieldLastName.clear();
                myFragment.fieldPosition.clear();
                myFragment.fieldEmail.clear();
                myFragment.fieldMobile.clear();
                myFragment.fieldWebsite.clear();

                if (fields == null) {
                    formatedData = new String[1];
                    formatedData[0] = "No results";
                    myFragment.fieldFirstName.addAll(formatedData);
                    return;
                }

                if (myFragment.documentType.equals("stubs")) {

                    myFragment.fieldFirstName.addAll(GetValues(fields, "fCustomerName"));
                    myFragment.fieldLastName.addAll(GetValues(fields, "fAccountNo"));
                    myFragment.fieldPosition.addAll(GetValues(fields, "fCompanyStreet"));

                    myFragment.fieldEmail.addAll(GetValues(fields, "fCompanyZipCode"));
                    myFragment.fieldMobile.addAll(GetValues(fields, "fCompanyState"));
                    myFragment.fieldWebsite.addAll(GetValues(fields, "fAmountDue"));

                    myFragment.textView1.setText(R.string.stubs_customerName);
                    myFragment.textView2.setText(R.string.stubs_accountNo);
                    myFragment.textView3.setText(R.string.stubs_companyStreet);

                    myFragment.textView4.setText(R.string.stubs_companyZipCode);
                    myFragment.textView5.setText(R.string.stubs_companyState);
                    myFragment.textView6.setText(R.string.stubs_amountDue);
                    myFragment.mButtonContact.setVisibility(View.GONE);
                }
                else{
                    myFragment.fieldFirstName.addAll(GetValues(fields, "fName"));
                    myFragment.fieldLastName.addAll(GetValues(fields, "fLastname"));
                    myFragment.fieldPosition.addAll(GetValues(fields, "fPosition"));

                    myFragment.fieldEmail.addAll(GetValues(fields, "fEmail"));
                    myFragment.fieldMobile.addAll(GetValues(fields, "fMobilephone"));
                    myFragment.fieldWebsite.addAll(GetValues(fields, "fCompanyWebsite"));

                    myFragment.textView1.setText(R.string.field_firstName);
                    myFragment.textView2.setText(R.string.field_lastName);
                    myFragment.textView3.setText(R.string.field_position);

                    myFragment.textView4.setText(R.string.field_email);
                    myFragment.textView5.setText(R.string.field_mobile);
                    myFragment.textView6.setText(R.string.field_website);
                    myFragment.mButtonContact.setVisibility(View.VISIBLE);
                }
            }
        }

        List<String> GetValues(DataClassifyResult fields,String fieldName)
        {
            List<String> candidates = new ArrayList<String>();
            for (DataClassifyResultField field : fields.Items) {
                if (field.id.equals(fieldName)) {
                    for (DataClassifyResultFieldCandidatesCandidate candidate : field.candidates) {
                        candidates.add(candidate.value);
                    }
                }
            }
            return candidates;
        }
    }
}


