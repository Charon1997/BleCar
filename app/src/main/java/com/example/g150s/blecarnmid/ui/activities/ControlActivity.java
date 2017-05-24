package com.example.g150s.blecarnmid.ui.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.g150s.blecarnmid.R;
import com.example.g150s.blecarnmid.ble.BluetoothLeService;
import com.example.g150s.blecarnmid.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/5/24.
 */

public class ControlActivity extends BaseActivity {
    private final static String TAG = ControlActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 530;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int DISCONNECTED = 0;
    private static final int CONNECTED = 1;
    private static final int CONNECTING = 2;



    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService mBluetoothLeService;
    private Handler mHandler;

    private String mAddress;
    private String mName;
    private int connectFlag;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        initService();

        initBle();

        initDate();

        initView();
    }

    private void initService() {
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        boolean bll = bindService(gattServiceIntent, mServiceConnection,//2
                BIND_AUTO_CREATE);
        if (bll) {
            Log.d("123", "绑定成功");
        } else {
            Log.d("123", "绑定失败");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initBle() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "您的设备不支持蓝牙BLE，将关闭", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                showMessageOKCancel("你必须允许这个权限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(ControlActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                    }
                });
                return;
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void initDate() {
        Intent intent = getIntent();
        mAddress = intent.getStringExtra(MainActivity.ADDRESS);
        mName = intent.getStringExtra(MainActivity.NAME);
    }

    private void initView() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.control_toolbar);
        setTitle(mName);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.control, menu);
        if (connectFlag == DISCONNECTED){
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else if (connectFlag == CONNECTING) {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_refresh);
        } else if (connectFlag == CONNECTED) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                //连接
                connectFlag = CONNECTING;
                mBluetoothLeService.connect(mAddress);
                break;
            case R.id.menu_disconnect:
                //断开
                connectFlag = DISCONNECTED;
                mBluetoothLeService.disconnect();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {//2

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {//3
                Log.e("123", "Unable to initialize Bluetooth");
                finish();
            } else Log.d("123", "能初始化");
            // 自动连接to the device upon successful start-up
            // 初始化.
            mBluetoothLeService.connect(mAddress);//4
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled() || mBluetoothAdapter == null) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        //7
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//8

        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mAddress);
            Log.d("123", "连接结果" + result);
        } else Log.d("123", "mBluetoothLeService为空");
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.READ_RSSI);
        return intentFilter;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();//接收广播
            Log.d("123", "action:" + action);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                connectFlag = CONNECTED;
                mBluetoothLeService.mBluetoothGatt.readRemoteRssi();

                writeDate(true);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                connectFlag = DISCONNECTED;
                //加入是否可以点击
                writeDate(false);
                //invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                // 搜索需要的uuid
                initGattCharacteristics(mBluetoothLeService
                        .getSupportedGattServices());

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

            } else if (BluetoothLeService.READ_RSSI.equals(action)) {

            }
        }
    };

    private void writeDate(boolean connect) {
        BluetoothGattCharacteristic characteristic ;
        if (mGattCharacteristics == null) {
            return;
        } else {
            if (connect) {
                for (int i = 0;i < mGattCharacteristics.size();i++) {
                    for (int j = 0; j < mGattCharacteristics.get(i).size(); j++) {
                        if (mGattCharacteristics.get(i).get(j).getUuid().toString().equals("0000fff1-0000-1000-8000-00805f9b34fb")) {//对应的uuid
                            characteristic = mGattCharacteristics.get(i).get(j);
                            write(characteristic,new byte[] {(byte) 0xff});//写入的数据
                            mBluetoothLeService.writeCharacteristic(characteristic);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                 writeDate(true);
                                }
                            },500);
                            Log.d("123", "发送点击报警成功");
                            //Toast.makeText(ControlActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

        }
    }

    private void write(BluetoothGattCharacteristic characteristic, byte byteArray[]) {
        characteristic.setValue(byteArray);
    }

    private void write(BluetoothGattCharacteristic characteristic, String string) {
        characteristic.setValue(string);
    }
    private void initGattCharacteristics(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        mGattCharacteristics = new ArrayList<>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
            }
            mGattCharacteristics.add(charas);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
}
