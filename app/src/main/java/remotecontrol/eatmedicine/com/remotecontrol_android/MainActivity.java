package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private List<Map<String,Object>> list;
    private String[] host={"127.0.0.1","127.0.0.1","127.0.0.1","127.0.0.1","127.0.0.1","127.0.0.1","127.0.0.1","127.0.0.1","127.0.0.1"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_titleAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.setClass(MainActivity.this,AddClientActivity.class);
                startActivity(i);
            }
        });
        //更新数据
        UpdateList();

    }

    public List<Map<String,Object>> getList(List<HostData> list){
        List<Map<String,Object>> result = new ArrayList<>();
        for(HostData data:list){
            if(data.CheckVaild()==false)
                continue;
            Map<String,Object> map = new HashMap<>();
            map.put("id",data.get_id());
            map.put("host",data.get_host());
            map.put("port",data.get_port());
            result.add(map);
        }
        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();
        UpdateList();

    }

    public void UpdateList(){
        //获取数据
        SqliteHelper sqlite = new SqliteHelper(this);
        SQLiteDatabase dbRead = sqlite.getReadableDatabase();
        Cursor cursor = dbRead.query("HostList",
                new String[]{"id","host","port"},null,null,null,null,null);
        List<HostData> hostList = Tools.ConvertCursor(cursor);
        dbRead.close();

        //设置listView
        ListView listView = findViewById(R.id.listView_Client);
        listView.setFastScrollEnabled(false);
        listView.setVerticalScrollBarEnabled(false);
        list = getList(hostList);
        SimpleAdapter sa = new SimpleAdapter(this,list,R.layout.list_item,
                new String[]{"host"},
                new int[]{R.id.item_host});
        listView.setAdapter(sa);
    }
}
