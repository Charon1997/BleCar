package com.example.g150s.blecarnmid.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.g150s.blecarnmid.R;
import com.example.g150s.blecarnmid.others.Car;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G150S on 2017/3/15.
 */

public class SearchingRlAdapter extends RecyclerView.Adapter<SearchingRlAdapter.ViewHolder>  implements View.OnClickListener{

    Context mContext;
    List<Car> mSearchCars = new ArrayList<>();
    private OnSearchingItemClickListener mOnItemClickListener = null;

    public SearchingRlAdapter(Context context)
    {
        this.mContext =context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searching_adapter_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      /*  holder.searchingDeviceName.setText(mSearchCars.get(position).getCarName());
        holder.itemView.setTag(mSearchCars.get(position));*/
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null)
        {
            mOnItemClickListener.onItemClick(v,(Car) v.getTag());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView searchingDeviceName;
        public ViewHolder(View view)
        {
            super(view);
            searchingDeviceName =(TextView)view.findViewById(R.id.searching_device_name);
        }
    }
    public void setmOnItemClickListener(OnSearchingItemClickListener onItemClickListener)
    {
        this.mOnItemClickListener = onItemClickListener;
    }
    /* 添加搜索到设备小车 */
    public void addItem(Car car,int position)
    {
        mSearchCars.add(position,car);
        notifyItemInserted(position);
    }
    /* 移除搜索到的设备小车 */
    public void removeItem(Car car)
    {
        int position = mSearchCars.indexOf(car);
        mSearchCars.remove(position);
        notifyItemRemoved(position);
    }
    public static interface OnSearchingItemClickListener{
        void onItemClick(View view,Car car);
    }
}
