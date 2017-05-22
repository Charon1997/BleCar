package com.example.g150s.blecarnmid.others;

import android.bluetooth.BluetoothDevice;
import android.view.View;

/**
 * Created by Administrator on 2017/5/22.
 */

public interface OnSearchingItemClickListener {
    void onItemClick(View view, BluetoothDevice device, int position);
}
