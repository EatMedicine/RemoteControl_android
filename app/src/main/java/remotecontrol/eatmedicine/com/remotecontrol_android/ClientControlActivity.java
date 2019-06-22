package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    private String[] FuncName = {"结束进程"};
    private int[] CommandIds = {1};

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
}
