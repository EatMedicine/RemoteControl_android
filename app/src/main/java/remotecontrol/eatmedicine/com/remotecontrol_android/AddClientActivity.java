package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.ConditionVariable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        final SqliteHelper sqlite = new SqliteHelper(this);
        findViewById(R.id.add_btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txt = findViewById(R.id.add_editIP);
                String tmp = txt.getText().toString();
                if(tmp == null||tmp == "") {
                    Toast.makeText(getApplicationContext(),"IP格式错误",Toast.LENGTH_LONG).show();
                    return;
                }
                String[] data = tmp.split(":");
                if(data.length<2) {
                    Toast.makeText(getApplicationContext(),"IP格式错误",Toast.LENGTH_LONG).show();
                    return;
                }
                String host = data[0];
                if(Tools.ipCheck(host)==false){
                    Toast.makeText(getApplicationContext(),"IP格式错误",Toast.LENGTH_LONG).show();
                    return;
                }
                int port = 0;
                try{
                    port = Integer.parseInt(data[1]);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"IP格式错误",Toast.LENGTH_LONG).show();
                    return;
                }

                //获取数据
                SQLiteDatabase db = sqlite.getWritableDatabase();
                ContentValues value = new ContentValues();
                value.put("host",host);
                value.put("port",port);
                db.insert("HostList",null,value);
                db.close();
                Toast.makeText(getApplicationContext(),"成功添加",Toast.LENGTH_LONG).show();
                AddClientActivity.this.finish();
            }
        });
    }
}
