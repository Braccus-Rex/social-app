package com.example.flake.myapplication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.flake.myapplication.appsettings.urlsettings;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.flake.myapplication.MyApplication;

public class adapter extends RecyclerView.Adapter<adapter.PersonViewHolder> {

    private Transformation cropPosterTransformation = new Transformation() {

        @Override public Bitmap transform(Bitmap source) {
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

        @Override public String key() {
            return "cropPosterTransformation"; //+ desiredWidth;
        }
    };



    private Context context;
    private int actionbarHeight=0;
    String DEVICE_ID;

    public int lastPosition=-1;

    ArrayList<String> ids=new ArrayList<>();
    private DisplayMetrics displaymetrics;
    int g=0;
    public adapter(Context context) {

    }
    String PACKAGE="P";

    public static final String REG_ID = "regId";
    public static final String USER_ID = "userID";

    public boolean find(String id) {
        for (String a : ids){
            if (a==id) return true;

        }
        return false;
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView title;
        TextView adress;
        TextView text;
        TextView likes;
        ImageView personPhoto;
        ImageView likeButton;
        MaterialRippleLayout rippleLayout;
        public Boolean isLiked = false;
        String id;
        int ids1=0;
        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            title = (TextView)itemView.findViewById(R.id.header);
            adress = (TextView)itemView.findViewById(R.id.location);
            text = (TextView)itemView.findViewById(R.id.maintext);
            likes = (TextView)itemView.findViewById(R.id.likescount);
            personPhoto = (ImageView)itemView.findViewById(R.id.image);
            likeButton = (ImageView)itemView.findViewById(R.id.like);
            rippleLayout = (MaterialRippleLayout)itemView.findViewById(R.id.ripple);
        }
    }

    List<secret> persons;

    adapter(Context context, List<secret> persons,int k, String device_id){
        this.persons = persons;  this.context = context;
        this.actionbarHeight=k;
        Context cont = MyApplication.getInstance();
        SharedPreferences prefs = cont.getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        this.DEVICE_ID = prefs.getString(USER_ID, "");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //g=0;
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);

        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, final int i) {
        personViewHolder.title.setText(persons.get(i).title);
        personViewHolder.adress.setText(persons.get(i).adress);
        personViewHolder.id=persons.get(i).id;
        final  int  i1=i;
        personViewHolder.text.setText(persons.get(i).text);

        personViewHolder.likes.setText(String.valueOf(persons.get(i).likes));

        if(persons.get(i).isLiked){
            personViewHolder.likeButton.setImageResource(R.drawable.liked_heart);
            personViewHolder.isLiked = true;
        }
        else{
            personViewHolder.likeButton.setImageResource(R.drawable.heart);
            personViewHolder.isLiked = false;
        }

       // personViewHolder.personPhoto.setImageResource(persons.get(i).photoId);
        displaymetrics = new DisplayMetrics();

        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        Picasso.with(context).load(persons.get(i).photourl).transform(cropPosterTransformation).error(R.drawable.emma).into(personViewHolder.personPhoto);

        setAnimation(personViewHolder, i);

        personViewHolder.rippleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!personViewHolder.isLiked) {
                    personViewHolder.isLiked = true;
                    personViewHolder.likeButton.setImageResource(R.drawable.liked_heart);
                    int count = Integer.parseInt(personViewHolder.likes.getText().toString()) + 1;
                    personViewHolder.likes.setText(String.valueOf(count));
                    persons.get(i).isLiked = true;
                    persons.get(i).likes++;
                    new set_like().execute("1", personViewHolder.id);
                } else {
                    personViewHolder.isLiked = false;
                    persons.get(i).isLiked = false;
                    persons.get(i).likes--;
                    personViewHolder.likeButton.setImageResource(R.drawable.heart);
                    int count = Integer.parseInt(personViewHolder.likes.getText().toString()) - 1;
                    personViewHolder.likes.setText(String.valueOf(count));
                    new set_like().execute("0", personViewHolder.id);
                }
            }
        });

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] screenLocation = new int[2];
                personViewHolder.personPhoto.getLocationOnScreen(screenLocation);

                int x = personViewHolder.personPhoto.getHeight();
                Intent subActivity = new Intent(context,
                        MainActivity22Activity.class);
                subActivity.
                        putExtra(PACKAGE + ".left", screenLocation[0]).
                        putExtra(PACKAGE + ".top", screenLocation[1]).
                        putExtra(PACKAGE + ".photourl", persons.get(i1).photourl).
                        putExtra(PACKAGE + ".title", persons.get(i1).title).
                        putExtra(PACKAGE + ".adress", persons.get(i1).adress).
                        putExtra(PACKAGE + ".postID", persons.get(i1).id).
                        putExtra(PACKAGE + ".text", persons.get(i1).text).
                        putExtra(PACKAGE + ".likes", persons.get(i1).likes).
                        putExtra(PACKAGE + ".isLiked", persons.get(i1).isLiked).
                        putExtra(PACKAGE + ".width", personViewHolder.personPhoto.getWidth()).
                        putExtra(PACKAGE + ".height", personViewHolder.personPhoto.getHeight())
                ;
                ((Activity) context).startActivityForResult(subActivity, 4);
                ((Activity) context).overridePendingTransition(R.animator.fadeo, R.animator.fade);
                //screenLocation[0],screenLocation[1], v.getWidth(), v.getHeight());
            }
        };

        personViewHolder.cv.setOnClickListener(clickListener);
        /*personViewHolder.personPhoto.setOnClickListener(clickListener);
        personViewHolder.text.setOnClickListener(clickListener);
        personViewHolder.adress.setOnClickListener(clickListener);
        personViewHolder.title.setOnClickListener(clickListener);*/
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }


    private void setAnimation(final PersonViewHolder viewToAnimate1, int position){

        int[] screenLocation = new int[2];
        viewToAnimate1.cv.getLocationOnScreen(screenLocation);
        ids.add(viewToAnimate1.id);
        Rect scrollBounds = new Rect();
        View viewToAnimate = viewToAnimate1.cv;
        // If the bound view wasn't previously displayed on screen, it's animated
        if ((! ids.contains(viewToAnimate1.id) ) && (position<4) &&(g<displaymetrics.heightPixels))
        {
            g+=viewToAnimate.getHeight()+ viewToAnimate1.personPhoto.getHeight();

            viewToAnimate.setAlpha(0);
             ObjectAnimator oa1 = ObjectAnimator.ofFloat(viewToAnimate, "translationY", 300f,0f);
            oa1.setDuration(500);
             ObjectAnimator oa = ObjectAnimator.ofFloat(viewToAnimate, "alpha", 1);
            oa.setDuration(300);

            AnimatorSet set = new AnimatorSet();
            set.setStartDelay(position * 250);
            set.playTogether(oa, oa1);

            set.start();

            lastPosition = position;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                   g=10000;
                }
            }, 50);
        }
    }

    class set_like extends AsyncTask<String, String, String> {

        /**
         * ????? ????????? ? ??????? ?????? ?????????? ???????? ??????
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * ???????? ????????
         **/
        protected String doInBackground(String... args) {
            String responseString = null;
            try
            {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlsettings.set_like);

                try {
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("postID", args[1]));
                    nameValuePairs.add(new BasicNameValuePair("userID", DEVICE_ID));
                    if(args[0]=="1"){
                        nameValuePairs.add(new BasicNameValuePair("method", "1"));
                    }
                    else {
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
                    }
                    else {
                        responseString = "Error occurred! Http Status Code: " + statusCode;
                    }
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return responseString;
        }

        /**
         * ????? ????????? ???????? ???????? ??????
         **/
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }
}