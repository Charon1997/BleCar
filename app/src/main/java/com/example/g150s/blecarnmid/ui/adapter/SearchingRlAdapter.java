package com.example.g150s.blecarnmid.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.g150s.blecarnmid.R;
import com.example.g150s.blecarnmid.others.Car;
import com.example.g150s.blecarnmid.others.OnSearchingItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G150S on 2017/3/15.
 */

public class SearchingRlAdapter extends RecyclerView.Adapter<SearchingRlAdapter.ViewHolder>  implements OnSearchingItemClickListener{
    private List<BluetoothDevice> mBluelist;
    private LayoutInflater layoutInflater;
    private OnSearchingItemClickListener mOnItemClickListener = null;

    public SearchingRlAdapter(Context context,List<BluetoothDevice> list)
    {
        this.mBluelist = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searching_adapter_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
      /*  holder.searchingDeviceName.setText(mSearchCars.get(position).getCarName());
        holder.itemView.setTag(mSearchCars.get(position));*/

        holder.searchingDeviceName.setText(mBluelist.get(position).getName());
        holder.searchingDeviceAddress.setText(mBluelist.get(position).getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v,mBluelist.get(position),position);
            }
        });
    }

    private String modifyName(final String oldName) {
        //Log.d("123setName",oldName.length()+"length");
        int oldNameLength = oldName.length();
        String newName = oldName;
        for (int i = 0;i < 14-oldNameLength;i++) {
            newName = newName + " ";
            Log.d("123setName",newName.length()+"newlength");
        }
        Log.d("123setName",newName.length()+"newlength");
        return newName;
    }

    @Override
    public int getItemCount() {
        return mBluelist.size();
    }

    public void clear() {
        mBluelist.clear();
    }



    @Override
    public void onItemClick(View view, BluetoothDevice device, int position) {
        if (mOnItemClickListener != null)
        {
            //bug!!!得不到position
            mOnItemClickListener.onItemClick(view, device, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView searchingDeviceName,searchingDeviceAddress;
        ViewHolder(View view)
        {
            super(view);
            searchingDeviceName =(TextView)view.findViewById(R.id.searching_device_name);
            searchingDeviceAddress = (TextView) view.findViewById(R.id.searching_device_address);
        }
    }
    public void setOnItemClickListener(OnSearchingItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
//    /* 添加搜索到设备小车 */
//    public void addItem(Car car,int position)
//    {
//        mSearchCars.add(position,car);
//        notifyItemInserted(position);
//    }
//    /* 移除搜索到的设备小车 */
//    public void removeItem(Car car)
//    {
//        int position = mSearchCars.indexOf(car);
//        mSearchCars.remove(position);
//        notifyItemRemoved(position);
//    }



    public boolean isEmpty() {
        return mBluelist.isEmpty();
    }
}
