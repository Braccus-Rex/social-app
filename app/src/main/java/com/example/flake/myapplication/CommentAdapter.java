package com.example.flake.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.PersonViewHolder> {


    private Context context;
    private int actionbarHeight = 0;

    public int lastPosition = -1;

    ArrayList<String> ids = new ArrayList<>();
    private DisplayMetrics displaymetrics;

    public CommentAdapter(Context context) {

    }

    String PACKAGE = "P";

    public boolean find(String id) {
        for (String a : ids) {
            if (a == id) return true;

        }
        return false;
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {


        TextView text;

        ImageView personPhoto;
        String id;

        PersonViewHolder(View itemView) {
            super(itemView);

            text = (TextView) itemView.findViewById(R.id.commentText);

            personPhoto = (ImageView) itemView.findViewById(R.id.icon);

        }

    }

    List<comment> comments;

    CommentAdapter(Context context, List<comment> comments, int k) {
        this.comments = comments;
        this.context = context;
        this.actionbarHeight = k;
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);

        return pvh;
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, int i) {

        final int i1 = i;
        personViewHolder.text.setText(comments.get(i).text);


        personViewHolder.personPhoto.setImageResource(R.mipmap.ic_launcher);
        displaymetrics = new DisplayMetrics();

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


}