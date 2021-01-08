package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.quakereport.Model.item;

import java.util.ArrayList;
import java.util.List;

public class item_adapter extends RecyclerView.Adapter<item_adapter.VH> {
    private List<item> list;
    private Context context;

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    interface OnClickListener{
        void onClick(String url);
    }

    public void setList(List<item> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public item_adapter( Context context) {
        this.context = context;
        this.list=new ArrayList<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final item item=list.get(position);

        holder.mag.setText(String.valueOf(item.getMag()));
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.mag.getBackground();
        int megColor=getMagnitudeColor(item.getMag());
        magnitudeCircle.setColor(megColor);

        if(item.getLocation().contains("of")) {
            holder.sec_loc.setText(item.getLocation().split("of")[0] + "of");
            holder.prim_loc.setText(item.getLocation().split("of")[1]);
            holder.sec_loc.setVisibility(View.VISIBLE);
        }else  holder.prim_loc.setText(item.getLocation());

        holder.time.setText(item.getTime());
        holder.date.setText(item.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null)
                    onClickListener.onClick(item.getUrl());
            }
        });

    }

    private int getMagnitudeColor(float magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return context.getResources().getColor(magnitudeColorResourceId);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VH extends RecyclerView.ViewHolder{
        TextView prim_loc,sec_loc,mag,time,date;
        public VH(@NonNull View itemView) {
            super(itemView);
            prim_loc =itemView.findViewById(R.id.it_prim_loc);
            sec_loc =itemView.findViewById(R.id.it_sec_loc);
            mag=itemView.findViewById(R.id.mag);
            date=itemView.findViewById(R.id.it_date);
            time=itemView.findViewById(R.id.it_time);
        }
    }

}
