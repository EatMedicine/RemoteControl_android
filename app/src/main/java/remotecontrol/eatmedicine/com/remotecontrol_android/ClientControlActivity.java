package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientControlActivity extends AppCompatActivity {

    public String Host = "";
    public int Port = 0;
    public TcpThread socket = null;

    private String[] FuncName = {"结束进程","发送消息","获取电脑共享文件"};
    private int[] CommandIds = {Tools.COMMAND_ID_KILL_PROCESS,Tools.COMMAND_ID_SEND_MESSAGE,Tools.COMMAND_ID_GET_FILE_LIST};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_control);
        Bundle bundle = this.getIntent().getExtras();
        Host = bundle.getString("host");
        Port = bundle.getInt("port");
        //设置IP显示
        ((TextView)findViewById(R.id.client_IP )).setText(Host+":"+Port);
        socket = new TcpThread(Host,Port,(TextView) findViewById(R.id.client_Status),this);
        socket.start();
        ListView list = findViewById(R.id.client_listView);
        list.setFastScrollEnabled(false);
        list.setVerticalScrollBarEnabled(false);
        SimpleAdapter sa = new SimpleAdapter(this,getList(),R.layout.function_item,
                new String[]{"name"},
                new int[]{R.id.function_item_txt1});
        list.setAdapter(sa);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                HashMap<String,Object> data = (HashMap<String,Object>) listView.getItemAtPosition(position);
                int commandId = (int)data.get("commandId");
                //用工厂类来生成跳转的Intent
                Intent i = IntentFactory.getIntent(commandId,ClientControlActivity.this);
                Bundle bundle = new Bundle();
                bundle.putString("host",Host);
                bundle.putInt("port",Port);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    public List<Map<String,Object>> getList(){
        List<Map<String,Object>> result = new ArrayList<>();
        for(int count=0;count<FuncName.length;count++){
            Map<String,Object> map = new HashMap<>();
            map.put("name",FuncName[count]);
            map.put("commandId",CommandIds[count]);
            result.add(map);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.exit();
    }
}
