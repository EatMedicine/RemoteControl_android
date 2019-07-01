package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.Socket;

public class CmdActivity extends AppCompatActivity {

    public static final int MSG_CODE_UPDATE_STATUS = 0;

    public String Host = "";
    public int Port = 0;
    public Socket socket = null;
    private String status = "";

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CODE_UPDATE_STATUS:
                    TextView txt = findViewById(R.id.send_cmd_status);
                    txt.setText(status);
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmd);
        Bundle bundle = this.getIntent().getExtras();
        Host = bundle.getString("host");
        Port = bundle.getInt("port");
        status = "初始化中";
        handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    socket = new Socket(Host,Port);
                    status = "建立Socket连接中";
                    handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                    if(socket != null){
                        status = "Socket连接成功";
                        handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                    }
                    else{
                        status = "Socket连接失败";
                        handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                    }
                }catch (Exception ex){
                    status = "Socket断开连接";
                    handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                    finish();
                }
            }
        }.start();
        //添加监听发送
        findViewById(R.id.send_cmd_btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.send_cmd_editMsg);
                String str = editText.getText().toString();
                if(str == null || str.length()<=0){
                    Toast.makeText(CmdActivity.this,"请输入要发送的命令",Toast.LENGTH_LONG).show();
                    return;
                }
                if(socket==null){
                    finish();
                    return;
                }
                try{
                    JSONObject obj = new JSONObject();
                    obj.put("CommandId",Tools.COMMAND_ID_SEND_CMD);
                    obj.put("Command",str);
                    final String json = obj.toString();
                    final OutputStream outputStream = socket.getOutputStream();
                    //发送
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try{
                                outputStream.write(json.getBytes());
                                outputStream.flush();
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                        }
                    }.start();
                    Toast.makeText(CmdActivity.this,"发送成功",Toast.LENGTH_LONG).show();
                    editText.setText("");
                }catch (Exception ex){
                    Toast.makeText(CmdActivity.this,"发送命令出错：1",Toast.LENGTH_LONG).show();
                    return;
                }


            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(socket!=null){
            try{
                socket.close();
            }catch(Exception ex) {

            }

        }
    }
}
