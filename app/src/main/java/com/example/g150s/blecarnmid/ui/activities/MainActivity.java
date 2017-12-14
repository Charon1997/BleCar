package com.example.g150s.blecarnmid.ui.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g150s.blecarnmid.R;
import com.example.g150s.blecarnmid.others.Car;
import com.example.g150s.blecarnmid.others.OnConnectCreateContextMenu;
import com.example.g150s.blecarnmid.others.OnConnectItemClickListener;
import com.example.g150s.blecarnmid.others.OnSearchingItemClickListener;
import com.example.g150s.blecarnmid.ui.adapter.ConnectedRLAdapter;
import com.example.g150s.blecarnmid.ui.adapter.SearchingRlAdapter;
import com.example.g150s.blecarnmid.ui.base.BaseActivity;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends BaseActivity implements OnConnectCreateContextMenu, MenuItem.OnMenuItemClickListener {
    private List<BluetoothDevice> mSearchBluetoothList = new ArrayList<>();
    private List<Car> mConnectBluetoothList = new ArrayList<>();


    private static final int TIMEENABLED = 120;
    private static final int SEARCHTIME = 15 * 1000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 530;
    private static final int SCAN_PERIOD = 15000;
    private static final int REQUEST_CODE = 1;

    public final static String ADDRESS = "Address";
    public final static String ID = "Id";
    public final static String NAME = "Name";
    public final static String SPRE = "myPref";
    private final static String TAG = MainActivity.class.getSimpleName();


    final int msg1 = 101;
    final int msg2 = 102;
    final int msg3 = 103;
    final int msg4 = 104;
    public int bleId = 0;

    //private Timer searchTimer;
    private CountDownTimer searchTimer;

    private int timeenabled = TIMEENABLED;
    /**
     * 允许检测到的时间
     */
    private int searchTime = 15;
    /**
     * 搜索蓝牙设备的时间
     */

    //搜索状态的标示
    private boolean mScanning;

    private Toolbar toolbar;

    private ContentLoadingProgressBar progressBar;

    private LinearLayout searchLinear, scanLinear;

    private RecyclerView connectedRL;
    private RecyclerView.LayoutManager connectedLayoutManager;
    private ConnectedRLAdapter connectedRLAdapter;

    private RecyclerView searchingRL;
    private RecyclerView.LayoutManager searchingLayoutManager;
    private SearchingRlAdapter searchingRlAdapter;

    private TextView searchText, noConnect, noSearch, tvCountDown;

    SharedPreferences spre;

    private BluetoothAdapter mBluetoothAdapter;
    //请求启用蓝牙请求码
    private static final int REQUEST_ENABLE_BT = 1;


    /**
     * 1.在开机引导页判断用户手机是否支持蓝牙4.0
     * 2.加入蓝牙模块后 点击搜索设备时 应先检测蓝牙是否开启 如果没有开启蓝牙功能 提示用户开启蓝牙功能
     * 3.已配对设备列表有 添加 移除 已配对设备的方法
     * 4.可用设备搜索列表 有Item点击接口
     */

    @Override
    protected void onResume() {
        super.onResume();
        // 确保蓝牙在设备上可以开启
        if (!mBluetoothAdapter.isEnabled() || mBluetoothAdapter == null) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        mConnectBluetoothList.clear();
        spre = getSharedPreferences(SPRE, MODE_PRIVATE);
        bleId = spre.getInt(ID, 0);
        if (bleId != 0) {
            for (int i = 1; i <= bleId; i++) {
                Log.d(TAG, "bleId" + bleId);
                Car car = new Car(spre.getString(NAME + i, "未命名"), spre.getString(ADDRESS + i, "ErrorAddress"));
                mConnectBluetoothList.add(car);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAdapter();

        initViews();

        initPermission();
    }

    /**
     * 权限加载
     */
    private void initPermission() {
        PermissionGen.with(MainActivity.this)
                .addRequestCode(100)
                .permissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA
                ).request();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void doSomething() {
        Toast.makeText(this, "已经获取权限", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 100)
    public void doFailSomething() {
        Toast.makeText(MainActivity.this, "请开启位置权限", Toast.LENGTH_SHORT).show();
        Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");

        String pkg = "com.android.settings";
        String cls = "com.android.settings.applications.InstalledAppDetails";

        i.setComponent(new ComponentName(pkg, cls));
        i.setData(Uri.parse("package:" + getPackageName()));
        startActivity(i);
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        //初始化蓝牙适配器
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "您的设备不支持蓝牙BLE，将关闭", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void initPermission() {
//        if (ContextCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                    Manifest.permission.READ_CONTACTS)) {
//                showMessageOKCancel("你必须允许这个权限，否则无法搜索到BLE设备", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        ActivityCompat.requestPermissions(MainActivity.this,
//                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA},
//                                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
//                    }
//                });
//                return;
//            }
//            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
//                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
//        }
//    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("关闭", okListener)
                .create()
                .show();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //用户允许改权限，0表示允许，-1表示拒绝 PERMISSION_GRANTED = 0， PERMISSION_DENIED = -1
//                //这里进行授权被允许的处理
//            } else {
//                //这里进行权限被拒绝的处理
//                Toast.makeText(MainActivity.this, "请开启位置权限", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
//
//                String pkg = "com.android.settings";
//                String cls = "com.android.settings.applications.InstalledAppDetails";
//
//                i.setComponent(new ComponentName(pkg, cls));
//                i.setData(Uri.parse("package:" + getPackageName()));
//                startActivity(i);
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }


    /**
     * 初始化view
     */
    private void initViews() {

        initToolbar();

        findId();

        initProgressBar();

        initConnectRL();

        initSearchRL();

        if (connectedRLAdapter.getItemCount() != 0) {
            noConnect.setVisibility(View.GONE);
        }
        if (searchingRlAdapter.getItemCount() != 0) {
            noSearch.setVisibility(View.GONE);
        }

        initSearch();

        initScan();
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            Log.d(TAG, "显示" + mScanning);
            handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mScanning) {
                        Log.d(TAG, "结束搜索");
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        searchText.setText("开始搜索");
                        invalidateOptionsMenu();
                    }
                }
            }, SCAN_PERIOD);
            Log.d(TAG, "开始");
            mScanning = true;
            handle.sendEmptyMessage(msg3);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.d(TAG, "mLeScanCallback 搜索结果");
                            //获取到蓝牙设备
                            //可以判断是否添加
                            if (!mSearchBluetoothList.contains(device) && !checkAddress(device.getAddress(), 0)) {
                                mSearchBluetoothList.add(device);
                                Log.d(TAG, "mLeScanCallback 搜索结果   " + device.getAddress());
                            }
                            //List加载适配器
                            if (searchingRlAdapter.isEmpty()) {
                                Log.d(TAG, "mLeDeviceListAdapter为空");
                            } else {
                                Log.d(TAG, "mLeDeviceListAdapter设置");
                                searchingRL.setAdapter(searchingRlAdapter);
                            }
                            handle.sendEmptyMessage(msg3);
                        }
                    });
                }
            };

    final Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case msg3:
                    searchingRlAdapter.notifyDataSetChanged();
                    break;
                case msg4:
                    connectedRLAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    private void findId() {
        noConnect = (TextView) findViewById(R.id.noConnect_text);
        noSearch = (TextView) findViewById(R.id.noSearch_text);
        searchText = (TextView) findViewById(R.id.search_text);
        tvCountDown = (TextView) findViewById(R.id.tv_countdown_timer);
        searchLinear = (LinearLayout) findViewById(R.id.search_linear);
        scanLinear = (LinearLayout) findViewById(R.id.scan_linear);
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    private void initProgressBar() {
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.circle_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY);
        progressBar.hide();
    }

    private void initConnectRL() {
        connectedRL = (RecyclerView) findViewById(R.id.connectedRL);
        connectedLayoutManager = new LinearLayoutManager(this);
        connectedRL.setLayoutManager(connectedLayoutManager);
        connectedRL.setHasFixedSize(true);
        connectedRLAdapter = new ConnectedRLAdapter(getApplicationContext(), mConnectBluetoothList);
        connectedRL.setAdapter(connectedRLAdapter);
        connectedRLAdapter.setOnItemClickListener(new OnConnectItemClickListener() {
            @Override
            public void onItemClick(View view, Car car) {
                showConnectDialog(car.getCarName(), car.getCarAddress());
            }
        });
        connectedRLAdapter.setOnCreateContextMenuListener(this);
    }

    private void initSearchRL() {
        searchingRL = (RecyclerView) findViewById(R.id.searchingRL);
        searchingLayoutManager = new LinearLayoutManager(this);
        searchingRL.setLayoutManager(searchingLayoutManager);
        searchingRL.setHasFixedSize(true);
        searchingRlAdapter = new SearchingRlAdapter(getApplicationContext(), mSearchBluetoothList);
        searchingRlAdapter.setOnItemClickListener(new OnSearchingItemClickListener() {
            @Override
            public void onItemClick(View view, BluetoothDevice device, int position) {
                Toast.makeText(MainActivity.this, "名字：" + device.getName() + "地址：" + device.getAddress(), Toast.LENGTH_SHORT).show();
                showNormalDialog(device.getName(), device.getAddress(), position);
            }
        });

        searchingRL.setAdapter(searchingRlAdapter);
    }

    private void initSearch() {
        searchTimer = new CountDownTimer(SEARCHTIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountDown.setText(String.format(getResources().getString(R.string.count_time), millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                searchText.setText("开始搜索");
                scanLeDevice(false);
                progressBar.hide();
                tvCountDown.setText("");
            }
        };

        searchLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "请开启位置权限", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");

                    String pkg = "com.android.settings";
                    String cls = "com.android.settings.applications.InstalledAppDetails";

                    i.setComponent(new ComponentName(pkg, cls));
                    i.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(i);
                } else {
                    if (!mScanning) {
                        searchTimer.start();
                        mSearchBluetoothList.clear();
                        searchingRlAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Search!", Toast.LENGTH_SHORT).show();
                        searchText.setText("停止搜索");
                        progressBar.show();
                        scanLeDevice(true);
                    } else {
                        searchTimer.cancel();
                        searchText.setText("开始搜索");
                        scanLeDevice(false);
                        progressBar.hide();
                        tvCountDown.setText("");
                    }
                }
            }
        });
    }

    private void initScan() {
        scanLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    /**
     * 添加小车的提示框
     *
     * @param name     小车名
     * @param address  小车地址
     * @param position 该小车在搜索的位置
     */
    private void showNormalDialog(final String name, final String address, final int position) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.drawable.ble_weclome_img);
        normalDialog.setTitle("添加设备");
        normalDialog.setMessage("是否添加" + name);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do传入数据库，建立连接，退出
                        if (!checkAddress(address, 1)) {
                            bleId++;
                            Log.d("123", bleId + "scan");
                            SharedPreferences.Editor editor = spre.edit();
                            editor.putString(NAME + bleId, name);
                            editor.putString(ADDRESS + bleId, address);
                            editor.putInt(ID, bleId);
                            editor.apply();
                            connectedRLAdapter.addItem(name, address);
                            mSearchBluetoothList.remove(position);
                            searchingRlAdapter.notifyDataSetChanged();
                        }
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // 显示
        normalDialog.show();
    }

    /**
     * 小车连接 提示框
     *
     * @param name    小车名字
     * @param address 小车地址
     */
    private void showConnectDialog(final String name, final String address) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setIcon(R.drawable.car);
        normalDialog.setTitle("连接小车");
        normalDialog.setMessage("是否连接小车" + name);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...连接小车
                        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        } else {
                            Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(ADDRESS, address);
                            bundle.putString(NAME, name);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // 显示
        normalDialog.show();
    }

    /**
     * 编辑小车名字
     *
     * @param name     小车名
     * @param mContext 上下文
     * @param id       小车id
     */
    public void showEditDialog(final String name, Context mContext, final int id) {

        AlertDialog.Builder editDialog = new AlertDialog.Builder(mContext);
        final EditText editText = new EditText(mContext);
        editText.setText(name);

        editDialog.setView(editText);
        editDialog.setIcon(R.drawable.car);
        editDialog.setTitle("修改设备名字");
        editDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...修改小车名字，存入sp
                        if (!spre.getString(NAME + id, "ERROR").equals(editText.getText().toString())) {
                            SharedPreferences.Editor editor = spre.edit();
                            editor.putString(NAME + id, editText.getText().toString()).apply();

                            mConnectBluetoothList.get(id - 1).setCarName(editText.getText().toString());
                            connectedRLAdapter.notifyDataSetChanged();
                        }
                    }
                });
        editDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // 显示
        AlertDialog dlg = editDialog.create();
        dlg.show();
    }

    /**
     * 查重检查
     *
     * @param address 小车地址
     * @param id      有1则由提示，为0时，搜索的时候不添加
     * @return 是否有该设备
     */
    private boolean checkAddress(String address, int id) {
        if (bleId != 0) {
            for (int i = 0; i < bleId; i++) {
                Log.d(TAG, address + spre.getString(ADDRESS + 0, "none"));
                if (address.equals(mConnectBluetoothList.get(i).getCarAddress())) {
                    if (id == 1) {
                        Toast.makeText(this, "已经添加该设备", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onItemCreateContextMenu(ContextMenu menu, int position) {
        connectedRLAdapter.setPosition1(position);
        MenuItem edit = menu.add(0, 1, 0, "编辑");
        MenuItem delete = menu.add(0, 2, 0, "删除");
        edit.setOnMenuItemClickListener(this);
        delete.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int position = -1;
        position = connectedRLAdapter.getPosition1();
        Log.d("123", "点击了" + position);
        if (position >= 0) {
            switch (item.getItemId()) {
                case 1:
                    showEditDialog(connectedRLAdapter.getName(position), MainActivity.this, position + 1);
                    break;
                case 2:
                    deleteItem(position + 1);
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    /**
     * 删除某个已添加小车
     *
     * @param id 小车id
     */
    private void deleteItem(int id) {
        int size = mConnectBluetoothList.size();
        int oldId = id + 1;
        int newId = id;
        SharedPreferences.Editor editor = spre.edit();
        connectedRLAdapter.removeItem(id - 1);
        bleId--;
        for (; newId < size; newId++, oldId++) {
            editor.putString(NAME + newId, spre.getString(NAME + oldId, "error" + oldId));
            editor.putString(ADDRESS + newId, spre.getString(ADDRESS + oldId, "error" + oldId));
            editor.apply();
        }
        editor.putInt("Id", bleId).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
