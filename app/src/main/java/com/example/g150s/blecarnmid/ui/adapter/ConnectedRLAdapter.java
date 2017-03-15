package com.example.g150s.blecarnmid.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g150s.blecarnmid.R;
import com.example.g150s.blecarnmid.others.Car;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G150S on 2017/3/14.
 */

public class ConnectedRLAdapter extends RecyclerView.Adapter<ConnectedRLAdapter.ViewHolder> {
    Context mContext;
    List<Car> mConnectedCars = new ArrayList<>();
    public ConnectedRLAdapter(Context context)
    {
        this.mContext = context;
    }

    @Override
    public ConnectedRLAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connected_adapter_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ConnectedRLAdapter.ViewHolder holder, final int position) {
        holder.detailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,position+"",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView deviceName;
        public ImageView detailImg;
        public ViewHolder(View view)
        {
            super(view);
            deviceName = (TextView)view.findViewById(R.id.device_name);
            detailImg = (ImageView)view.findViewById(R.id.item_detail);

        }
    }
    /* 添加配对设备小车 */
    public void addItem(Car car,int position)
    {
        mConnectedCars.add(position,car);
        notifyItemInserted(position);
    }
    /* 移除已配对的设备小车 */
    public void removeItem(Car car)
    {
        int position = mConnectedCars.indexOf(car);
        mConnectedCars.remove(position);
        notifyItemRemoved(position);
    }

}
