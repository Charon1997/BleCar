package com.example.g150s.blecarnmid.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.g150s.blecarnmid.ui.activities.ControlActivity;
import com.example.g150s.blecarnmid.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2017/5/21.
 */

public class BluetoothLeService extends Service {

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;//蓝牙设备地址
    public BluetoothGatt mBluetoothGatt ;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.charon.www.NewBluetooth.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.charon.www.NewBluetooth.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.charon.www.NewBluetooth.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.charon.www.NewBluetooth.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.charon.www.NewBluetooth.EXTRA_DATA";
    public final static String READ_RSSI = "com.charon.www.NewBluetooth.READ_RSSI";

    public List<UUID> writeUuid = new ArrayList<>();
    public List<UUID> readUuid =new ArrayList<>();
    public List<UUID> notifyUuid =new ArrayList<>();

    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private final IBinder mBinder = new LocalBinder();

    private ControlActivity controlActivity = new ControlActivity();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    public boolean initialize() {//3
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.d("123", "不能初始化BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.d("123", "不能获取a BluetoothAdapter.");
            return false;
        }
        return true;
    }


    public boolean connect(final String address) {//4
        Log.d("123", "连接" + mBluetoothDeviceAddress);
        //controlActivity.invalidateOptionsMenu();
        if (mBluetoothAdapter == null || address == null) {
            Log.d("123",
                    "BluetoothAdapter不能初始化 or 未知 address.");
            return false;
        }

        // 以前连接过的设备，重新连接. (��ǰ���ӵ��豸�� ������������)
        if (mBluetoothDeviceAddress != null
                && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d("123",
                    "尝试使用现在的 mBluetoothGatt连接.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            Log.d("123", "设备没找到，不能连接");
            return false;
        }
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);//5这里改进成 List
        //这个方法需要三个参数：一个Context对象，自动连接（boolean值,表示只要BLE设备可用是否自动连接到它），和BluetoothGattCallback调用。
        Log.d("123", "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        //mBluetoothGatt.readRemoteRssi();
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        //controlActivity.invalidateOptionsMenu();
        mBluetoothGatt.disconnect();

        //mBluetoothGatt = null;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {//5
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            String intentAction;
            Log.d("123", "status" + status);
            if (newState == BluetoothProfile.STATE_CONNECTED) {//当连接状态发生改变
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);//6
                Log.d(TAG, "连接GATT server");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:"
                        + mBluetoothGatt.discoverServices());
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//当设备无法连接
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);   //发送广播
            }
        }



        @Override
        // 发现新服务端
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> supportedGattServices = gatt.getServices();

                for (BluetoothGattService gattService : supportedGattServices) {
                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        int charaProp = gattCharacteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            Log.e("nihao", "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid());
                            Log.e("nihao", "gattCharacteristic的属性为:  可读");
                            readUuid.add(gattCharacteristic.getUuid());
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                            Log.e("nihao", "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid());
                            Log.e("nihao", "gattCharacteristic的属性为:  可写");
                            writeUuid.add(gattCharacteristic.getUuid());
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            Log.e("nihao", "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid() + gattCharacteristic);
                            Log.e("nihao", "gattCharacteristic的属性为:  具备通知属性");
                            notifyUuid.add(gattCharacteristic.getUuid());
                        }
                    }
                }

                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        // 读写特性
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            System.out.println("onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {

            System.out.println("onDescriptorWriteonDescriptorWrite = " + status
                    + ", descriptor =" + descriptor.getUuid().toString());
        }

        //如果对一个特性启用通知,当远程蓝牙设备特性发送变化，回调函数onCharacteristicChanged( ))被触发。
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

        }
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            controlActivity.rssi = rssi;
            broadcastUpdate(READ_RSSI);
        }

        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            System.out.println("--------write success----- status:" + status);
        };
    };

    private void broadcastUpdate(final String action) {//9发送广播
        final Intent intent = new Intent(action);
        sendBroadcast(intent);//广播
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;

        return mBluetoothGatt.getServices();
    }
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        } else mBluetoothGatt.writeCharacteristic(characteristic);

    }
}
