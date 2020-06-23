package com.kishorekethineni.twitter_asignment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class Recycler_View_Adapter extends RecyclerView.Adapter<Recycler_View_Adapter.View_Holder> {

    List<Tweet_Data> list = Collections.emptyList();
    Context context;

    public Recycler_View_Adapter(List<Tweet_Data> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(View_Holder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.Tweet.setText(list.get(position).Tweet);
        holder.Date.setText(list.get(position).Date);
        holder.SNo.setText(list.get(position).SNo+")");
        holder.Name.setText(list.get(position).Name);
        //animate(holder);

    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class View_Holder extends RecyclerView.ViewHolder {

        TextView Tweet;
        TextView Date;
        TextView SNo;
        TextView Name;

        View_Holder(View itemView) {
            super(itemView);
            Tweet = (TextView) itemView.findViewById(R.id.tweet);
            Date = (TextView) itemView.findViewById(R.id.date);
            SNo = (TextView) itemView.findViewById(R.id.index);
            Name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}