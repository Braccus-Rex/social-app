package com.example.flake.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flake.myapplication.appsettings.urlsettings;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.ProgressView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity23Activity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = MainActivity23Activity.class.getSimpleName();
    ImageView imageView;
    String mCurrentPhotoPath;
    int targetW;
    int targetH;
    private String servernamefile;
    MaterialEditText inputName;
    MaterialEditText inputDesc;
    private ProgressView pv_linear_colors;
    protected GoogleApiClient mGoogleApiClient;
    protected TextView raen;
    protected Location location;

    public static final String REG_ID = "regId";
    public static final String USER_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity23);

        imageView = (ImageView) findViewById(R.id.imageView44);
        inputName = (MaterialEditText) findViewById(R.id.inputName);
        inputDesc = (MaterialEditText) findViewById(R.id.inputDesc);
        raen = (TextView) findViewById((R.id.location1));

        pv_linear_colors = (ProgressView) findViewById(R.id.progress_pv_linear_colors);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mCurrentPhotoPath = getIntent().getStringExtra("img");
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                targetH = imageView.getMeasuredHeight();
                targetW = imageView.getMeasuredWidth();
                setPic();
                return true;
            }
        });
    }

    private void setPic() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void capture_img(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                setPic();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity23, menu);
        return true;
    }

    public void uploadCards() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            new CreateNewProduct().execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            if (TextUtils.isEmpty(inputName.getText()) || TextUtils.isEmpty(inputDesc.getText())){
                Snackbar.make(findViewById(android.R.id.content), Html.fromHtml("<i>Заполните все поля</i>"), Snackbar.LENGTH_LONG).show();
            }
            else uploadCards();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address returnedAddress = addresses.get(0);
                raen.setText(String.valueOf(returnedAddress.getThoroughfare()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();
    }

    class CreateNewProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pv_linear_colors.getLayoutParams().height = 5;
            pv_linear_colors.requestLayout();
            pv_linear_colors.start();
        }

        protected String doInBackground(String... args) {
            String responseString = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(urlsettings.url_create_secrets);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                Charset chars = Charset.forName("UTF-8");
                entityBuilder.setCharset(chars);
                ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);

                SharedPreferences prefs = getSharedPreferences("UserDetails",
                        Context.MODE_PRIVATE);
                String userid = prefs.getString(USER_ID, "");
                String gcmid = prefs.getString(REG_ID, "");

                entityBuilder.addPart("name", new StringBody(inputName.getText().toString(), contentType));
                entityBuilder.addPart("uid", new StringBody(userid, contentType));
                entityBuilder.addPart("price", new StringBody("0", contentType));
                entityBuilder.addPart("description", new StringBody(inputDesc.getText().toString(), contentType));
                entityBuilder.addPart("placename", new StringBody(raen.getText().toString(), contentType));
                entityBuilder.addPart("gcmid", new StringBody(gcmid, contentType));

                File file = new File(mCurrentPhotoPath);

                if (file != null) {
                    entityBuilder.addBinaryBody("photo", file);
                }

                HttpEntity entity = entityBuilder.build();
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                HttpEntity httpEntity = response.getEntity();
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(httpEntity);
                } else {
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            pv_linear_colors.stop();
            pv_linear_colors.getLayoutParams().height = 0;
            pv_linear_colors.requestLayout();

            // showing the server response in an alert dialog
            servernamefile = result;
            showAlert(result);

            super.onPostExecute(result);
        }

    }

    private void showAlert(String message) {
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();*/
        Intent intent = new Intent();
        setResult(2, intent);
        finish();
    }
}