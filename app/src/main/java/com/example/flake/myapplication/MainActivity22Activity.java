package com.example.flake.myapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.flake.myapplication.appsettings.urlsettings;
import com.example.flake.myapplication.parser.JSONParser;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity22Activity extends AppCompatActivity implements ObservableScrollViewCallbacks {
    Toolbar toolbar;
    int top;
    LinearLayout ll;
    View d, fkmg;
    JSONParser jParser = new JSONParser();
    ImageView im;
    private float mParallaxImageHeight;
    String PACKAGE = "P";
    List<comment> comments = new ArrayList<>();
    TextView header;
    TextView adress;
    CommentAdapter adapter;
    TextView text;
    RecyclerView rv;
    TextView likes;
    String DEVICE_ID;
    int actionBarHeight = 0;
    int height, width, ww, h, w;
    private DisplayMetrics displaymetrics;
    private String postID;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_CID = "commentID";
    private static final String TAG_COMMENT_TEXT = "CommentText";
    ObservableScrollView scrollView;
    LinearLayout layout;
    private int mBaseTranslationY;
    private int baseheight;
    ImageView likeButton;
    Boolean isLiked;
    /*//Button burron_send;
    //MaterialEditText comment;
    //int tr;
    //Space space;
    //RelativeLayout footer;
    String tr2;
    int targetH;
    int targetW;*/

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity22);
        scrollView = (ObservableScrollView) findViewById(R.id.list);
        scrollView.setScrollViewCallbacks(this);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        baseheight = scrollView.getHeight();
        im = (ImageView) findViewById(R.id.imageView);
        d = (View) findViewById(R.id.viewst);
        fkmg = (View) findViewById(R.id.fakeimg);
        // layout = (LinearLayout) findViewById(R.id.field);
        ll = (LinearLayout) findViewById(R.id.rl);


        Intent intent = getIntent();
        int id = intent.getIntExtra(PACKAGE + ".photoID", 0);
        int lik = intent.getIntExtra(PACKAGE + ".likes", 0);
        top = intent.getIntExtra(PACKAGE + ".top", 0);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.LOLLIPOP) {
            top = top - getStatusBarHeight();  // Do something for froyo and above versions
           // getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDark));
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        h = intent.getIntExtra(PACKAGE + ".height", 0);
        w = intent.getIntExtra(PACKAGE + ".width", 0);
        int left = intent.getIntExtra(PACKAGE + ".left", 0);
        String post = intent.getStringExtra(PACKAGE + ".text");
        String url = intent.getStringExtra(PACKAGE + ".photourl");
        postID = intent.getStringExtra(PACKAGE + ".postID");
        String adr = intent.getStringExtra(PACKAGE + ".adress");
        String titl = intent.getStringExtra(PACKAGE + ".title");
        // String post= intent.getStringExtra(PACKAGE+".text");
        //String text = intent.getIntExtra(PACKAGE + "photoID", 0);
        //  title = (TextView)findViewById(R.id.adress);
        if (url!=null)
        Picasso.with(MainActivity22Activity.this).load(url).transform(cropPosterTransformation).error(R.drawable.emma).noPlaceholder().into(im);

        adress = (TextView) findViewById(R.id.location);
        text = (TextView) findViewById(R.id.maintext);
        likes = (TextView) findViewById(R.id.likescount1);
        //footer = (RelativeLayout) findViewById(R.id.footer);
        //space = (Space) findViewById(R.id.space);
        header = (TextView) findViewById(R.id.header);
        likes.setText(String.valueOf(lik));
        //burron_send = (Button) findViewById(R.id.button_send);
        likeButton = (ImageView) findViewById(R.id.like1);
        MaterialRippleLayout rippleLayout = (MaterialRippleLayout) findViewById(R.id.ripple1);
        text.setText(post);
        adress.setText(adr);
        header.setText(titl);
        BitmapDrawable bd = (BitmapDrawable) im.getDrawable();
        height = bd.getBitmap().getHeight();
        width = bd.getBitmap().getWidth();
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String wifiMac = wifiManager.getConnectionInfo().getMacAddress();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String devicIMEI = telephonyManager.getDeviceId();
        DEVICE_ID = md5(wifiMac + devicIMEI);
        //    text.setText(DEVICE_ID);
        displaymetrics = new DisplayMetrics();
        if (intent.getBooleanExtra(PACKAGE + ".isLiked", false)) {
            likeButton.setImageResource(R.drawable.liked_heart);
            isLiked = true;
        } else {
            likeButton.setImageResource(R.drawable.heart);
            isLiked = false;
        }
        rippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiked == false) {
                    isLiked = true;
                    likeButton.setImageResource(R.drawable.liked_heart);
                    int count = Integer.parseInt(likes.getText().toString()) + 1;
                    likes.setText(String.valueOf(count));
                    new set_like().execute("1", postID);
                } else {
                    isLiked = false;
                    likeButton.setImageResource(R.drawable.heart);
                    int count = Integer.parseInt(likes.getText().toString()) - 1;
                    likes.setText(String.valueOf(count));
                    new set_like().execute("0", postID);
                }
            }
        });

        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        toolbar.setTitle(titl);
        setSupportActionBar(toolbar);
        ww = displaymetrics.widthPixels;
        // im.getLayoutParams().height = ww * height/width ;
        //  im.setX(left);
        im.getLayoutParams().height = h;
        im.getLayoutParams().width = w;
        TypedValue tv = new TypedValue();

        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        im.setY(top);
        fkmg.getLayoutParams().height = displaymetrics.heightPixels + actionBarHeight * 2;
        mParallaxImageHeight = ww * height / width;
        // getSupportActionBar().setTitle(titl);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final ObjectAnimator oa = ObjectAnimator.ofFloat(im, "y", 0f);
        oa.setDuration(500);
        Handler handler = new Handler();

        /*ViewTreeObserver vto = footer.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                footer.getViewTreeObserver().removeOnPreDrawListener(this);
                targetH = footer.getMeasuredHeight();
                targetW = footer.getMeasuredWidth();
                return true;
            }
        });*/

        final Runnable r = new Runnable() {
            public void run() {
                AnimatorSet set = new AnimatorSet();

                set.play(oa);
                set.start();
                ValueAnimator va = ValueAnimator.ofInt(w, ww);
                va.setDuration(500);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        im.getLayoutParams().width = value.intValue();
                        im.requestLayout();
                    }
                });
                va.start();
                ValueAnimator va1 = ValueAnimator.ofInt(h, ww * height / width);
                va1.setDuration(500);
                va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        im.getLayoutParams().height = value.intValue();
                        im.requestLayout();
                    }
                });
                va1.start();
                ValueAnimator va2 = ValueAnimator.ofInt(displaymetrics.heightPixels + actionBarHeight * 2, ww * height / width);
                va2.setDuration(500);
                va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        fkmg.getLayoutParams().height = value.intValue();
                        fkmg.requestLayout();
                    }
                });
                va2.start();
            }
        };


        handler.postDelayed(r, 500);
        /* im.getLayoutParams().height=ww * height/width;
        im.getLayoutParams().width=ww;
        fkmg.getLayoutParams().height = ww * height/width;*/


        // im.requestLayout();
        //comment = (MaterialEditText) findViewById(R.id.comment_input);

        /*burron_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new send_commment().execute(postID, comment.getText().toString());
            }
        });*/

        rv = (RecyclerView) findViewById(R.id.rv1c);
        MyLinearLayoutManager llm = new MyLinearLayoutManager(this, 1, false);

        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        adapter = new CommentAdapter(MainActivity22Activity.this, comments, 2);
        rv.setAdapter(adapter);
        //new GetComments().execute();
    }

    @Override
    public void onBackPressed() {
        /*final ObjectAnimator oa = ObjectAnimator.ofFloat(im, "y", top);
        oa.setDuration(500);

                AnimatorSet set = new AnimatorSet();

                set.play(oa);
                set.start();
                ValueAnimator va = ValueAnimator.ofInt(ww, w);
                va.setDuration(500);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        im.getLayoutParams().width = value.intValue();
                        im.requestLayout();
                    }
                });
                va.start();
                ValueAnimator va1 = ValueAnimator.ofInt(ww * height/width,h);
                va1.setDuration(500);
                va1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        im.getLayoutParams().height = value.intValue();
                        im.requestLayout();
                    }
                });
                va1.start();
                ValueAnimator va2 = ValueAnimator.ofInt( ww * height/width,displaymetrics.heightPixels+actionBarHeight*2);
                va2.setDuration(500);
                va2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        fkmg.getLayoutParams().height = value.intValue();
                        fkmg.requestLayout();
                    }
                });
                va2.start();



        final Runnable r1 = new Runnable() {
            public void run() {
*/
        Intent intent = new Intent();
        intent.putExtra("456", postID);
        intent.putExtra("453", isLiked);
        setResult(RESULT_OK, intent);
        finishFromChild(this);
        //finish();
  /*              overridePendingTransition(0,R.animator.fade);
            }
        };


        Handler handler = new Handler();

        handler.postDelayed(r1, 500);
    */
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

    }



    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        float alpha = Math.max(Math.min(1, (float) scrollY / mParallaxImageHeight), 0);
        // toolbar.setAlpha(alpha);
        int color1 = Math.min(255, Math.round(255 * alpha));
        d.setAlpha(alpha);
        toolbar.setBackgroundColor(Color.argb(color1, 63, 81, 181));
        im.getLayoutParams().height = Math.max(0, Math.round(mParallaxImageHeight - scrollY));
        /*if(dragging) {
            footer.setVisibility(View.VISIBLE);
            footer.setY(Math.max(displaymetrics.heightPixels - scrollY, displaymetrics.heightPixels - footer.getHeight() - 48));
        }
        else {
            footer.setVisibility(View.INVISIBLE);
        }*/

        //toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        im.requestLayout();
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

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    private Transformation cropPosterTransformation = new Transformation() {

        @Override
        public Bitmap transform(Bitmap source) {
            int targetWidth = displaymetrics.widthPixels;
            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (targetWidth * aspectRatio);
            Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
            if (result != source) {
                // Same bitmap is returned if sizes are the same
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "cropPosterTransformation"; //+ desiredWidth;
        }
    };

    /*class GetComments extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {
            // Будет хранить параметры
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("postID", postID));
            // получаем JSON строк с URL
            JSONObject json = jParser.makeHttpRequest(urlsettings.url_get_comments, "POST", params);

            Log.d("All Products: ", json.toString());

            try {
                // Получаем SUCCESS тег для проверки статуса ответа сервера
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    // продукт найден
                    // Получаем масив из Продуктов
                    JSONArray products = json.getJSONArray(TAG_PRODUCTS);

                    //  if (cache) persons = new ArrayList<>();
                    // перебор всех продуктов
                    comments.clear();
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);

                        // Сохраняем каждый json елемент в переменную
                        String id = c.getString(TAG_CID);
                        String name = c.getString(TAG_COMMENT_TEXT);


                        // Создаем новый HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // добавляем каждый елемент в HashMap ключ => значение
                        map.put(TAG_CID, id);
                        map.put(TAG_COMMENT_TEXT, name);
                        comments.add(i, new comment(name, id));
                        // добавляем HashList в ArrayList
                        //productsList.add(map);
                        tr=i;
                        tr2=id;
                    }
                    /*for (int i = 0; i < 4; i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_CID, tr2 + i);
                        map.put(TAG_COMMENT_TEXT, "dfgfdgdf");
                        comments.add(tr+1, new comment("dfgfdgdf", tr2 + i));
                    }*/
               /* } else {
                    // продукт не найден
                    // Запускаем Add New Product Activity
                   /* Intent i = new Intent(getApplicationContext(),
                            NewSecrets.class);
                    // Закрытие всех предыдущие activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);*/
               /* }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * ����� ��������� �������� �������� ������
         */
        /*protected void onPostExecute(String result) {
            Log.e("d", "Response from server: " + result);

            // showing the server response in an alert dialog

            runOnUiThread(new Runnable() {
                public void run() {
                    //    save();
                    /**
                     * Обновляем распарсенные JSON данные в ListView
                     * */
                    // adapter adapter = new adapter(MainActivity.this,persons,2);
                    //rv.setAdapter(adapter);
                    /*adapter.notifyDataSetChanged();
                    /*space.setLayoutParams(new LinearLayout.LayoutParams(targetW, targetH));
                    space.requestLayout();*/

                    /*    ListAdapter adapter = new SimpleAdapter(
                            Secrets.this, productsList,
                            R.layout.activity_list_item, new String[] { TAG_PID,
                            TAG_NAME,TAG_DESCRIPTION},
                            new int[] { R.id.pid, R.id.name,R.id.description });
                    // обновляем listview
                    setListAdapter(adapter);*//*


                }
            });
            comment.setText("");
            super.onPostExecute(result);
        }

    }*/

    class set_like extends AsyncTask<String, String, String> {

        /**
         * ????? ????????? ? ??????? ?????? ?????????? ???????? ??????
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * ???????? ????????
         */
        protected String doInBackground(String... args) {
            String responseString = null;
            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlsettings.set_like);

                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("postID", args[1]));
                    nameValuePairs.add(new BasicNameValuePair("userID", DEVICE_ID));
                    if (args[0] == "1") {
                        nameValuePairs.add(new BasicNameValuePair("method", "1"));
                    } else {
                        nameValuePairs.add(new BasicNameValuePair("method", "0"));
                    }
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity httpEntity = response.getEntity();
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    if (statusCode == 200) {
                        responseString = EntityUtils.toString(httpEntity);
                    } else {
                        responseString = "Error occurred! Http Status Code: " + statusCode;
                    }
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        /**
         * ????? ????????? ???????? ???????? ??????
         */
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

    class send_commment extends AsyncTask<String, String, String> {

        /**
         * ????? ????????? ? ??????? ?????? ?????????? ???????? ??????
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * ???????? ????????
         */
        protected String doInBackground(String... args) {
            String responseString = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(urlsettings.send_comment);

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                Charset chars = Charset.forName("UTF-8");
                entityBuilder.setCharset(chars);
                ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);

                entityBuilder.addPart("postID", new StringBody(args[0], contentType));
                //entityBuilder.addPart("userID", new StringBody(DEVICE_ID, contentType));
                entityBuilder.addPart("CommentText", new StringBody(args[1], contentType));

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

        /**
         * ????? ????????? ???????? ???????? ??????
         */
        protected void onPostExecute(String result) {
            //new GetComments().execute();
            super.onPostExecute(result);
        }

    }
}
