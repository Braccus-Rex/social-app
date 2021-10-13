package com.example.flake.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.flake.myapplication.appsettings.urlsettings;
import com.example.flake.myapplication.parser.JSONParser;
import com.example.flake.myapplication.push.ApplicationConstants;
import com.example.flake.myapplication.push.Main2Activity;
import com.example.flake.myapplication.push.Utility;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private List<secret> persons = new ArrayList<>();
    private RecyclerView rv;
    int count = 0;
    private ProgressDialog pDialog;
    ProgressBar ssBar;
    ArrayList<String> idss = new ArrayList<>();
    // Создаем JSON парсер
    JSONParser jParser = new JSONParser();
    adapter adapter;
    ArrayList<HashMap<String, String>> productsList;
    boolean cache = true;
    // url получения списка всех продуктов
    private static String url_all_products = urlsettings.url_all_secrets;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHOTO = "photourl";
    private static final String TAG_DESCRIPTION = "description";

    RequestParams params = new RequestParams();
    GoogleCloudMessaging gcmObj;
    Context applicationContext;
    String regId = "";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    AsyncTask<Void, Void, String> createRegIdTask;

    public static final String REG_ID = "regId";
    public static final String USER_ID = "userID";


    // тут будет хранится список продуктов
    JSONArray products = null;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String DEVICE_ID;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Picasso.with(this).setIndicatorsEnabled(true);
        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        //   initializeAdapter();

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String wifiMac = wifiManager.getConnectionInfo().getMacAddress();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        applicationContext = MainActivity.this;

        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        DEVICE_ID = prefs.getString(USER_ID, "");
        String registrationId = prefs.getString(REG_ID, "");

        if (TextUtils.isEmpty(registrationId)) {
            if (checkPlayServices()) {
                String devicIMEI = telephonyManager.getDeviceId();
                DEVICE_ID = md5(wifiMac + devicIMEI);
                registerInBackground(DEVICE_ID);
            }
        }

        //  load();
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Math.abs(dy) > 4) {
                    if (dy > 0) {
                        fab.hide(true);
                    } else {
                        fab.show(true);
                    }
                }
            }
        });


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        // делаем повеселее

        mSwipeRefreshLayout.setColorScheme(R.color.blue, R.color.green, R.color.yellow, R.color.red);
//onRefresh();
        // initializeAdapter();

        adapter = new adapter(MainActivity.this, persons, 2, DEVICE_ID);


        rv.setAdapter(adapter);
        //if (false)   //supermatter !!!!!!!!!!!!!!!!!!!!!!
            loadCards();
        load(); // crashes
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        applicationContext,
                        "Устройство не поддерживает Play сервисы, приложение не будет работать",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            /*Toast.makeText(
                    applicationContext,
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();*/
        }
        return true;
    }

    private void registerInBackground(final String emailID) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(applicationContext);
                    }
                    regId = gcmObj
                            .register(ApplicationConstants.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    storeRegIdinSharedPref(applicationContext, regId, emailID);
                    /*Toast.makeText(
                            applicationContext,
                            "Registered with GCM Server successfully.\n\n"
                                    + msg, Toast.LENGTH_SHORT).show();*/
                } else {
                    /*Toast.makeText(
                            applicationContext,
                            "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();*/
                }
            }
        }.execute(null, null, null);
    }

    private void storeRegIdinSharedPref(Context context, String regId,
                                        String emailID) {
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putString(USER_ID, emailID);
        editor.commit();
        //storeRegIdinServer(regId, emailID);
    }

    private void storeRegIdinServer(String regId2, String emailID) {
        params.put("emailId", emailID);
        params.put("regId", regId);
        System.out.println("Email id = " + emailID + " Reg Id = " + regId);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.APP_SERVER_URL, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        /*Toast.makeText(applicationContext,
                                "Reg Id shared successfully with Web App ",
                                Toast.LENGTH_LONG).show();*/
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (statusCode == 404) {
                            /*Toast.makeText(applicationContext,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();*/
                        }
                        else if (statusCode == 500) {
                            /*Toast.makeText(applicationContext,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();*/
                        }
                        else {
                            /*Toast.makeText(
                                    applicationContext,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();*/
                        }
                    }
                });
    }

    public void loadCards() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            new LoadAllProducts().execute();
        }
        else Toast.makeText(this,"Нет доступа к интернету", Toast.LENGTH_LONG).show();
    }

    public void fabCLick(View v) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
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
        else Toast.makeText(this,"Нет доступа к интернету", Toast.LENGTH_SHORT).show();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Intent intent = new Intent(this, MainActivity23Activity.class);
                intent.putExtra("img", mCurrentPhotoPath);
                startActivityForResult(intent, 2);
            } else {
                /*int iid = data.getIntExtra("456", 0);
                persons.get(iid).isLiked = data.getBooleanExtra("453", false);
                if (persons.get(iid).isLiked) {
                    persons.get(iid).likes++;
                }
                else {
                    persons.get(iid).likes--;
                }*/
                loadCards();
            }
        } else {
            loadCards();
        }
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void load() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("MyObject", "");
        List<secret> persons1 = new ArrayList<>();
        persons1 = gson.fromJson(json, new TypeToken<List<secret>>() {
        }.getType());
        if (persons1!=null) {
            idss = new ArrayList<String>();
            for (int i = 0; i < persons1.size(); i++) {
                //!!!
                persons.add(persons1.get(i));
                idss.add(persons.get(i).id);

            }
//            adapter.lastPosition = 0;
            //          adapter.g = 0;

            adapter.lastPosition = 0;
            adapter.g = 0;
            adapter.notifyDataSetChanged();
        }
        //  persons = gson.fromJson(json, List.class);
    }

    private void initializeData() {

       /* persons.add(new secret("Кафе", "ул. Грибоедова", "lorem ipsum bipsum mipsum", 21, R.drawable.emma));
        persons.add(new secret("Кафе", "ул. Грибоедова", "lorem ipsum bipsum mipsum", 21, R.drawable.lavery));
        persons.add(new secret("Кафе", "ул. Грибоедова", "lorem ipsum bipsum mipsum", 21, R.drawable.lillie));*/

    }

    private void initializeAdapter() {
        adapter adapter = new adapter(this, persons, 2, DEVICE_ID);
        rv.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void save() {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        prefsEditor.clear();
        prefsEditor.commit();
        Gson gson = new Gson();
        prefsEditor.clear().commit();
        String json = gson.toJson(persons);
        prefsEditor.putString("MyObject", json);
        prefsEditor.commit();
    }

    @Override
    public void onRefresh() {
        loadCards();
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Перед началом фонового потока Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Получаем все продукт из url
         */
        protected String doInBackground(String... args) {
            // Будет хранить параметры
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userID", DEVICE_ID));
            // получаем JSON строк с URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "POST", params);

            Log.d("All Products: ", json.toString());

            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    // продукт найден
                    // Получаем масив из Продуктов
                    products = json.getJSONArray(TAG_PRODUCTS);

                    //  if (cache) persons = new ArrayList<>();
                    // перебор всех продуктов
                    idss.clear();
                    persons.clear();
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Сохраняем каждый json елемент в переменную
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);
                        String likesCount = c.getString("price");
                        String description = c.getString(TAG_DESCRIPTION);
                        String photourl = c.getString(TAG_PHOTO);
                        String location = c.getString("placename");
                        Boolean isLiked;
                        if (c.getString("isLiked").equals("0")) {
                            isLiked = false;
                        } else isLiked = true;

                        if (!idss.contains(id)) {
                            persons.add(0, new secret(name, location, description, Integer.parseInt(likesCount), R.drawable.emma, id, photourl, isLiked));
                            idss.add(id);
                        }
                    }
                } else {
                    // продукт не найден
                    // Запускаем Add New Product Activity
                   /* Intent i = new Intent(getApplicationContext(),
                            NewSecrets.class);
                    // Закрытие всех предыдущие activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * После завершения фоновой задачи закрываем прогрес диалог
         * *
         */
        protected void onPostExecute(String file_url) {
            // закрываем прогресс диалог после получение все продуктов
            //  pDialog.dismiss();
            // обновляем UI форму в фоновом потоке
            //    ssBar.startAnimation( AnimationUtils.loadAnimation(MainActivity.this, R.animator.fade));
            runOnUiThread(new Runnable() {
                public void run() {
                    cache = false;
                    save();
                    /**
                     * Обновляем распарсенные JSON данные в ListView
                     * */
                    adapter.lastPosition = 0;
                    adapter.g = 0;
                    // adapter adapter = new adapter(MainActivity.this,persons,2);
                    //rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    /*    ListAdapter adapter = new SimpleAdapter(
                            Secrets.this, productsList,
                            R.layout.activity_list_item, new String[] { TAG_PID,
                            TAG_NAME,TAG_DESCRIPTION},
                            new int[] { R.id.pid, R.id.name,R.id.description });
                    // обновляем listview
                    setListAdapter(adapter);*/

                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        }
    }
}
