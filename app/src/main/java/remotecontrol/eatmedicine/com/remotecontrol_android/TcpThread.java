package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpThread extends Thread {

    private Socket _socket;
    private boolean isConnect=false;
    private boolean stopFlag = false;

    public InputStream inputStream;
    public OutputStream outputStream;
    public String IP=null;
    public int Port=0;
    public TextView txtStatus;
    public Context Context = null;

    public TcpThread(String ip, int port,TextView txtStatus,Context context){
        if(!(0<=port&&port<=65535)){
            txtStatus.setText("IP端口输入错误，格式:xxx.xxx.xxx.xxx:xxx");
            return;
        }
        if(isIPAddressByRegex(ip)==false){
            txtStatus.setText("IP端口输入错误，格式:xxx.xxx.xxx.xxx:xxx");
            return;
        }
        IP=ip;
        Port=port;
        Context = context;
        this.txtStatus = txtStatus;
    }

    @Override
    public void run(){
        super.run();
        if(isConnect==true)
            return;
        try{
            if(IP==null){
                return;
            }
            _socket = new Socket(IP,Port);
            if(_socket!=null){
                inputStream = _socket.getInputStream();
                outputStream = _socket.getOutputStream();
                isConnect = true;
            }
            else {
                txtStatus.setText("未连接");
                return;
            }
            //监听消息
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try{
                        while(true){
                            byte[] buffer = new byte[1024*1024];
                            int count = inputStream.read(buffer);
                            if(count == 0)
                                continue;
                            String str = new String(buffer);
                            Toast.makeText(Context,str,Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (Exception e){
                        isConnect = false;
                        e.printStackTrace();
                        return;
                    }
                }
            }.start();
            txtStatus.setText("已连接");

        }catch (Exception ex){
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