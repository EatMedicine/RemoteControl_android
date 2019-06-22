package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "database.db";
    public static final int DB_VERSION = 1;
    public static final String CREATE_TABLE = "create table HostList" + "("
            + "id integer primary key autoincrement,"
            + "host varchar(20) not null,"
            + "port integer not null"
            + ");";

    public SqliteHelper(Context context) {
        // 传递数据库名与版本号给父类
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
