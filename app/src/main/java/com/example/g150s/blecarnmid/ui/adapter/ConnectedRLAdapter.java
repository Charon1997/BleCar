package com.example.g150s.blecarnmid.ui.adapter;

import android.bluetooth.BluetoothDevice;
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

public class ConnectedRLAdapter extends RecyclerView.Adapter<ConnectedRLAdapter.ViewHolder> implements View.OnClickListener {
    Context mContext;
    private List<Car> mBluelist = new ArrayList<>();
    private OnConnectItemClickListener mOnItemClickListener = null;

    public ConnectedRLAdapter(Context context, List<Car> list) {
        this.mContext = context;
        mBluelist = list;
    }

    @Override
    public ConnectedRLAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.connected_adapter_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConnectedRLAdapter.ViewHolder holder, final int position) {
        holder.detailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "设备地址：" + mBluelist.get(position).getCarAddress(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.deviceName.setText(mBluelist.get(position).getCarName());
    }

    @Override
    public int getItemCount() {
        return mBluelist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView deviceName;
        private ImageView detailImg;

        ViewHolder(View view) {
            super(view);
            deviceName = (TextView) view.findViewById(R.id.device_name);
            detailImg = (ImageView) view.findViewById(R.id.item_detail);
        }
    }

    /* 添加配对设备小车 */
    public void addItem(String name,String address) {
        Car car = new Car(name,address);
        mBluelist.add(car);
        notifyDataSetChanged();
    }

    //    /* 移除已配对的设备小车 */
//    public void removeItem(Car car)
//    {
//        int position = mConnectedCars.indexOf(car);
//        mConnectedCars.remove(position);
//        notifyItemRemoved(position);
//    }
    public boolean isEmpty() {
        return mBluelist.isEmpty();
    }

    public static interface OnConnectItemClickListener {
        void onItemClick(View view, Car device);
    }

    public void setOnItemClickListener(ConnectedRLAdapter.OnConnectItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, mBluelist.get(v.getVerticalScrollbarPosition()));
        }
    }
}
