package com.example.g150s.blecarnmid.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g150s.blecarnmid.R;
import com.example.g150s.blecarnmid.others.Car;
import com.example.g150s.blecarnmid.others.OnConnectCreateContextMenu;
import com.example.g150s.blecarnmid.others.OnConnectItemClickListener;
import com.example.g150s.blecarnmid.others.OnSearchingItemClickListener;
import com.example.g150s.blecarnmid.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G150S on 2017/3/14.
 */

public class ConnectedRLAdapter extends RecyclerView.Adapter<ConnectedRLAdapter.ViewHolder> implements OnConnectItemClickListener,OnConnectCreateContextMenu{
    Context mContext;
    private List<Car> mBluelist = new ArrayList<>();
    private OnConnectItemClickListener mOnItemClickListener = null;
    private OnConnectCreateContextMenu mOnConnectCreateContextMenuListener = null;
    private int position;
    private MainActivity mainActivity;


    public ConnectedRLAdapter() {

    }

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, mBluelist.get(position));
            }
        });
        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                onItemCreateContextMenu(menu,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBluelist.size();
    }

    @Override
    public void onItemClick(View view, Car car) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, car);
        }
    }

    public int getPosition1() {
        return position;
    }

    public void setPosition1(int position) {
        this.position = position;
    }

    @Override
    public void onItemCreateContextMenu(ContextMenu menu,int position) {
        if (mOnConnectCreateContextMenuListener != null) {
            mOnConnectCreateContextMenuListener.onItemCreateContextMenu(menu,position);
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView deviceName;
        private ImageView detailImg;

        ViewHolder(View view) {
            super(view);
            deviceName = (TextView) view.findViewById(R.id.device_name);
            detailImg = (ImageView) view.findViewById(R.id.item_detail);
        }

    }

    /* 添加配对设备小车 */
    public void addItem(String name, String address) {
        Car car = new Car(name, address);
        mBluelist.add(car);
        notifyDataSetChanged();
    }

        /* 移除已配对的设备小车 */
    public void removeItem(int position)
    {
        mBluelist.remove(position);
        notifyDataSetChanged();
    }
    public boolean isEmpty() {
        return mBluelist.isEmpty();
    }


    public void setOnItemClickListener(OnConnectItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnCreateContextMenuListener(OnConnectCreateContextMenu onCreateContextMenuListener) {
        this.mOnConnectCreateContextMenuListener = onCreateContextMenuListener;
    }

    public String getAddress(int position) {
        return mBluelist.get(position).getCarAddress();
    }

    public String getName(int position) {
        return mBluelist.get(position).getCarName();
    }

}
