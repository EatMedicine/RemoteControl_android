package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KillProcessActivity extends AppCompatActivity {

    public static final int MSG_CODE_UPDATE_LIST = 0;
    public static final int MSG_CODE_UPDATE_STATUS = 1;

    public String Host = "";
    public int Port = 0;
    public Socket socket = null;
    public List<Map<String,Object>> result = null;
    private String status = "";
    //用于异步对列表和状态的更新
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    UpdateList(result);
                    break;
                case 1:
                    TextView txt = findViewById(R.id.kill_process_status);
                    txt.setText(status);
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_process);
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
                        InputStream inputStream = socket.getInputStream();
                        OutputStream outputStream = socket.getOutputStream();
                        status = "Socket连接成功，获取进程列表请求发送中";
                        handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                        JSONObject obj = new JSONObject();
                        obj.put("CommandId",Tools.COMMAND_ID_GET_PROCESS_LIST);
                        String json = obj.toString();
                        outputStream.write(json.getBytes());
                        outputStream.flush();
                        status = "发送请求成功，等待数据";
                        handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                        byte[] buffer = new byte[1024*1024];
                        while(true){
                            int count = inputStream.read(buffer);
                            if(count == 0){
                                Thread.sleep(100);
                                continue;
                            }
                            status = "成功接收数据，数据处理中";
                            handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                            String str = new String(buffer,0,count);
                            Log.i("KILL_PROCESS",str+count);
                            JSONObject jobj = new JSONObject(str);
                            if (jobj.getJSONArray("data")==null)
                                continue;
                            JSONArray array = jobj.getJSONArray("data");
                            result = new ArrayList<>();
                            for(count =0;count<array.length();count++){
                                Map<String,Object> map = new HashMap<>();
                                map.put("name",array.get(count));
                                result.add(map);
                            }
                            Collections.sort(result, new Comparator<Map<String, Object>>() {
                                @Override
                                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                                    String str1 = o1.get("name").toString().toLowerCase();
                                    String str2 = o2.get("name").toString().toLowerCase();
                                    return str1.compareTo(str2);
                                }
                            });
                            handler.sendEmptyMessage(MSG_CODE_UPDATE_LIST);
                            break;
                        }
                    }

                }
                catch (Exception e){
                    status = "数据接受失败";
                    handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                    finish();
                }
            }
        }.start();

        ListView listView = findViewById(R.id.kill_process_listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ListView listView = (ListView) parent;
                HashMap<String,Object> data = (HashMap<String,Object>) listView.getItemAtPosition(position);
                final  String processName = data.get("name").toString();
                final int index = position;
                AlertDialog alertDialog2 = new AlertDialog.Builder(KillProcessActivity.this)
                        .setTitle("提示")
                        .setMessage("是否结束该进程："+processName)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try{
                                    JSONObject obj = new JSONObject();
                                    obj.put("CommandId", Tools.COMMAND_ID_KILL_PROCESS);
                                    obj.put("ProcessName",processName);
                                    final String json = obj.toString();
                                    final OutputStream outputStream = socket.getOutputStream();
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
                                    Log.i("KILL_PROCESS",result.get(index).get("name").toString());
                                    result.remove(index);
                                    handler.sendEmptyMessage(MSG_CODE_UPDATE_LIST);
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                alertDialog2.show();


            }
        });

    }

    public void UpdateList(List<Map<String,Object>> list){
        ListView listView = findViewById(R.id.kill_process_listView);
        listView.setFastScrollEnabled(false);
        listView.setVerticalScrollBarEnabled(false);
        SimpleAdapter sa = new SimpleAdapter(KillProcessActivity.this,list,R.layout.function_item,
                new String[]{"name"},
                new int[]{R.id.function_item_txt1});
        listView.setAdapter(sa);
        status = "列表显示成功，轻按结束进程";
        handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
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
