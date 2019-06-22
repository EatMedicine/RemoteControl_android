package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.content.Context;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpCustomThread extends Thread{

    public Socket _socket;
    private boolean isConnect=false;
    private boolean stopFlag = false;

    public InputStream inputStream;
    public OutputStream outputStream;
    public String IP=null;
    public int Port=0;
    public TextView txtStatus;
    public Context Context = null;
    public Thread ListenFunc = null;

    public TcpCustomThread(String ip, int port, TextView txtStatus, android.content.Context context,Thread listen) {
        if(!(0<=port&&port<=65535)){
            if(txtStatus!=null){
                txtStatus.setText("已连接");
            }
            return;
        }
        if(isIPAddressByRegex(ip)==false){
            if(txtStatus!=null){
                txtStatus.setText("已连接");
            }
            return;
        }
        IP=ip;
        Port=port;
        Context = context;
        this.txtStatus = txtStatus;
        this.ListenFunc = listen;
    }

    @Override
    public void run(){
        super.run();
        if(isConnect==true)
            return;
        try {
            if (IP == null) {
                return;
            }
            _socket = new Socket(IP, Port);
            if (_socket != null) {
                inputStream = _socket.getInputStream();
                outputStream = _socket.getOutputStream();
                isConnect = true;
            } else {
                if (txtStatus != null) {
                    txtStatus.setText("已连接");
                }
                return;
            }

            //监听方法
            ListenFunc.start();

            if(txtStatus!=null){
                txtStatus.setText("已连接");
            }
        }
        catch (Exception ex){
            isConnect=false;
            ex.printStackTrace();
            return;
        }
    }

    public void writeMsg(String msg){
        if(isConnect==false)
            return;
        final String str = msg;
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    outputStream.write(str.getBytes());
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }.start();


    }

    public void exit(){
        try{
            _socket.close();
        }catch (Exception e){

        }

        stopFlag=true;
    }


    public boolean isIPAddressByRegex(String str) {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        // 判断ip地址是否与正则表达式匹配
        if (str.matches(regex)) {
            String[] arr = str.split("\\.");
            for (int i = 0; i < 4; i++) {
                int temp = Integer.parseInt(arr[i]);
                //如果某个数字不是0到255之间的数 就返回false
                if (temp < 0 || temp > 255) return false;
            }
            return true;
        } else return false;
    }
}
