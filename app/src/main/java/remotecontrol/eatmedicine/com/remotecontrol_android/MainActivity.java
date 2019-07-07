package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private List<Map<String,Object>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置跳转添加页面
        findViewById(R.id.btn_titleAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.setClass(MainActivity.this,AddClientActivity.class);
                startActivity(i);
            }
        });

        ListView listView = findViewById(R.id.listView_Client);
        //添加单击跳转对应IP的控制界面命令
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                //获取在Item里的Host和Port
                HashMap<String,Object> data = (HashMap<String,Object>) listView.getItemAtPosition(position);
                String host = data.get("host").toString();
                int port = (int)data.get("port");
                Intent i = new Intent();
                i.setClass(MainActivity.this,ClientControlActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("host",host);
                bundle.putInt("port",port);
                i.putExtras(bundle);
                startActivity(i);

            }
        });
        //添加长按删除IP地址的监听器
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                HashMap<String,Object> data = (HashMap<String,Object>) listView.getItemAtPosition(position);
                final String listId = (int)data.get("id")+"";
                AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("提示")
                        .setMessage("是否删除该IP")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SqliteHelper sqlite = new SqliteHelper(MainActivity.this);
                                SQLiteDatabase db = sqlite.getWritableDatabase();
                                db.delete("HostList","id=?",new String[]{listId});
                                UpdateList();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                alertDialog2.show();
                return true;
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
