package com.example.shibin.myapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shibin.myapplication.R;
import com.example.shibin.myapplication.model.getdirection.Distance_;
import com.example.shibin.myapplication.model.getdirection.Duration;
import com.example.shibin.myapplication.model.getdirection.Duration_;
import com.example.shibin.myapplication.model.getdirection.Step;

import java.util.ArrayList;

/**
 * @author shibin
 * @version 1.0
 * @date 29/05/17
 */

public class RouteInfoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Step> steps;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int routeTime;
    private int routeDistance;

    public RouteInfoRecyclerAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = ((Activity)context).getLayoutInflater().from(context);
    }

    public void setData(ArrayList<Step> steps) {
        this.steps = steps;
        notifyDataSetChanged();
    }

    // in minuts
    public int getTravelTime() {
        int travelTime = 0;
        for(Step step : steps) {
            Duration_ duration = step.getDuration();
            travelTime += duration.getValue();
        }
        return travelTime / 60;
    }

    // in KM
    public int getTravelDistance() {
        int distance = 0;
        for(Step step : steps) {
            Distance_ distance_ = step.getDistance();
            distance += distance_.getValue();
        }
        return distance / 1000;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View v1 = mLayoutInflater.inflate(R.layout.recycler_item_direction_list, parent, false);
        return new ListViewHolder(v1);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Step step = steps.get(position);
        ListViewHolder listViewHolder = (ListViewHolder) holder;
        if(step.getManeuver() != null && step.getManeuver().equals(Step.DIRECTION_LEFT)) {
            listViewHolder.navigation.setImageResource(R.drawable.ic_arrow_left);
            listViewHolder.navigation.setRotation(0f);
        } else if(step.getManeuver() != null &&  step.getManeuver().equals(Step.DIRECTION_RIGHT)) {
            listViewHolder.navigation.setImageResource(R.drawable.ic_arrow_right);
            listViewHolder.navigation.setRotation(0f);
        }
        listViewHolder.distnace.setText(step.getDistance().getText());
        listViewHolder.locationText.setText(Html.fromHtml(step.getHtmlInstructions()));
    }

    @Override
    public int getItemCount() {
        return steps == null ? 0 : steps.size();
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        ImageView navigation;
        TextView locationText, distnace;

        public ListViewHolder(final View itemView) {
            super(itemView);
            navigation = (ImageView) itemView.findViewById(R.id.navigation);
            locationText = (TextView) itemView.findViewById(R.id.locationText);
            distnace = (TextView) itemView.findViewById(R.id.distnace);
        }
    }
}
