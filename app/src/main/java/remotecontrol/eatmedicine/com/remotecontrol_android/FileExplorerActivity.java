package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

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

public class FileExplorerActivity extends AppCompatActivity {

    public static final int MSG_CODE_UPDATE_LIST = 0;
    public static final int MSG_CODE_UPDATE_STATUS = 1;
    public static final int MSG_CODE_UPDATE_DIRECTORY = 2;

    public String Host = "";
    public int Port = 0;
    public Socket socket = null;
    public List<Map<String,Object>> dirResult = null;
    public List<Map<String,Object>> fileResult = null;
    private String status = "";
    public FileUrlManager url = null;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CODE_UPDATE_LIST:
                    UpdateList((ListView) findViewById(R.id.file_explorer_listView_dir),dirResult);
                    UpdateList((ListView) findViewById(R.id.file_explorer_listView_file),fileResult);
                    break;
                case MSG_CODE_UPDATE_STATUS:
                    TextView txt = findViewById(R.id.file_explorer_status);
                    txt.setText(status);
                    break;
                case MSG_CODE_UPDATE_DIRECTORY:
                    txt = findViewById(R.id.file_explorer_txt_dir);
                    txt.setText("目录："+url.getLast());
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);
        Bundle bundle = this.getIntent().getExtras();
        Host = bundle.getString("host");
        Port = bundle.getInt("port");
        status = "初始化中";
        url = new FileUrlManager();
        handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    socket = new Socket(Host,Port);
                    status = "建立Socket连接中";
                    handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                    if(socket!=null){
                        InputStream inputStream = socket.getInputStream();
                        OutputStream outputStream = socket.getOutputStream();
                        status = "Socket连接成功，获取共享列表中";
                        handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                        JSONObject obj = new JSONObject();
                        obj.put("CommandId",Tools.COMMAND_ID_GET_FILE_LIST);
                        obj.put("Path",url.getUrl());
                        String json = obj.toString();
                        outputStream.write(json.getBytes());
                        outputStream.flush();
                        status = "发送请求成功，等待数据";
                        handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                        WaitData();
                    }
                }catch (Exception ex){
                    status = "数据接受失败";
                    handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                    Log.i("RemoteControl",ex.toString());
                    finish();
                }
            }
        }.start();
        //注册目录item操作
        ListView listView = findViewById(R.id.file_explorer_listView_dir);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ListView listView = (ListView) parent;
                HashMap<String,Object> data = (HashMap<String,Object>) listView.getItemAtPosition(position);
                final  String dirName = data.get("name").toString();
                try{
                    //区分上一级还是下一级目录
                    if(position == 0){
                        url.ReturnDirectory();
                    }
                    else{
                        url.AddDirectory(dirName);
                    }
                    status = "目录获取中";
                    handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                    ((ListView)findViewById(R.id.file_explorer_listView_dir)).setAdapter(null);
                    ((ListView)findViewById(R.id.file_explorer_listView_file)).setAdapter(null);
                    final OutputStream outputStream = socket.getOutputStream();
                    JSONObject obj = new JSONObject();
                    obj.put("CommandId",Tools.COMMAND_ID_GET_FILE_LIST);
                    obj.put("Path",url.getUrl());
                    final String json = obj.toString();
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try{
                                outputStream.write(json.getBytes());
                                outputStream.flush();
                                status = "发送请求成功，等待数据";
                                handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                                WaitData();
                            }catch (Exception ex){
                                Log.i("RemoteControl",ex.toString());
                                finish();
                            }

                        }
                    }.start();

                }catch (Exception ex){
                    Log.i("RemoteControl",ex.toString());
                    finish();
                }

            }
        });
        //注册文件操作
        listView  = findViewById(R.id.file_explorer_listView_file);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                HashMap<String,Object> data = (HashMap<String,Object>) listView.getItemAtPosition(position);
                final  String fileName = data.get("name").toString();
                final String[] items = { "打开文件","取消" };
                AlertDialog.Builder listDialog =
                        new AlertDialog.Builder(FileExplorerActivity.this);
                listDialog.setTitle("请选择操作");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // which 下标从0开始
                        // ...To-do
                        try{
                            switch (which){
                                case 0:
                                    JSONObject obj = new JSONObject();
                                    obj.put("CommandId",Tools.COMMAND_ID_OPEN_FILE);
                                    obj.put("FileName",url.getUrl()+fileName);
                                    String str = obj.toString();
                                    SocketSendMsg(str);
                                    break;
                                default:break;
                            }
                        }
                        catch (Exception ex){
                            Log.i("RemoteControl",ex.toString());
                        }

                    }
                });
                listDialog.show();
            }
        });
    }


    public void UpdateList(ListView listView,List<Map<String,Object>> list){
        listView.setFastScrollEnabled(false);
        listView.setVerticalScrollBarEnabled(false);
        SimpleAdapter sa = new SimpleAdapter(FileExplorerActivity.this,list,R.layout.function_item,
                new String[]{"name"},
                new int[]{R.id.function_item_txt1});
        listView.setAdapter(sa);
        status = "列表显示成功";
        handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
    }
    public void SocketSendMsg(final String msg){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    if(socket!=null){
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(msg.getBytes());
                        outputStream.flush();
                    }
                }catch (Exception ex){
                    Log.i("RemoteControl",ex.toString());
                }
            }
        }.start();
    }

    public void WaitData(){
        byte[] buffer = new byte[1024*1024];
        try{
            InputStream inputStream = socket.getInputStream();
            while(true){
                int count = inputStream.read(buffer);
                if(count == 0){
                    Thread.sleep(100);
                    continue;
                }
                status = "成功接收数据，数据处理中";
                handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
                String str = new String(buffer,0,count);
                JSONObject jobj = new JSONObject(str);
                if (jobj.getJSONArray("dirData")==null||
                        jobj.getJSONArray("fileData")==null)
                    continue;
                //目录数据
                JSONArray array = jobj.getJSONArray("dirData");
                dirResult = new ArrayList<>();
                for(count =0;count<array.length();count++){
                    Map<String,Object> map = new HashMap<>();
                    map.put("name",array.get(count));
                    dirResult.add(map);
                }
                //文件数据
                array = jobj.getJSONArray("fileData");
                fileResult = new ArrayList<>();
                for(count =0;count<array.length();count++){
                    Map<String,Object> map = new HashMap<>();
                    map.put("name",array.get(count));
                    fileResult.add(map);
                }
                //比较器
                Comparator<Map<String,Object>> comparator =
                        new Comparator<Map<String, Object>>() {
                            @Override
                            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                                String str1 = o1.get("name").toString().toLowerCase();
                                String str2 = o2.get("name").toString().toLowerCase();
                                return str1.compareTo(str2);
                            }
                        };
                //排序
                Collections.sort(dirResult,comparator );
                //插入返回上一级
                if(url.getLength()!=0){
                    Map<String,Object> map = new HashMap<>();
                    map.put("name","返回上一级目录");
                    dirResult.add(0,map);
                }
                Collections.sort(fileResult,comparator);
                handler.sendEmptyMessage(MSG_CODE_UPDATE_DIRECTORY);
                handler.sendEmptyMessage(MSG_CODE_UPDATE_LIST);
                break;
            }
        }catch (Exception ex){
            status = "数据接受失败";
            handler.sendEmptyMessage(MSG_CODE_UPDATE_STATUS);
            finish();
        }
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
