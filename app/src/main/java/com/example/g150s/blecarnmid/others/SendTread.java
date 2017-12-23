package com.example.g150s.blecarnmid.others;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author 张兴锐
 * @date 2017/12/13
 */

public class SendTread extends Thread {
    private String sendData;
    private SendResultListener listener;
    private String ip;
    private int port;

    public SendTread setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public SendTread setPort(int port) {
        this.port = port;
        return this;
    }

    public SendTread setSendData(String sendData) {
        this.sendData = sendData;
        return this;
    }

    public interface SendResultListener {
        void onSuccess();

        void onError(String msg);
    }

    public SendTread setSendResultListener(SendResultListener listener) {
        this.listener = listener;
        return this;
    }

    public SendTread() {
        super();
        this.port = 5566;
    }

    public SendTread(String data, String ip) {
        this.sendData = data;
        this.ip = ip;
        this.port = 5566;
    }

    @Override
    public void run() {
        super.run();
        //进行发送
        Socket socket;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 1500);
            OutputStream os = socket.getOutputStream();
            StringBuilder builder = new StringBuilder();
            builder.append(sendData);
            Log.i("Service", builder.toString());
            os.write((builder.toString()).getBytes("utf-8"));
            listener.onSuccess();
        } catch (IOException e) {
            e.printStackTrace();
            listener.onError(e.getMessage());
        }
    }

}
    