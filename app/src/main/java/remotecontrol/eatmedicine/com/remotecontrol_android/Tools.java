package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public  class Tools {

    public static final int COMMAND_ID_KILL_PROCESS = 1;
    public static final int COMMAND_ID_GET_PROCESS_LIST = 2;
    public static final int COMMAND_ID_SEND_MESSAGE = 3;
    public static final int COMMAND_ID_GET_FILE_LIST = 4;
    public static final int COMMAND_ID_OPEN_FILE = 5;
    public static final int COMMAND_ID_SEND_CMD = 6;

    //判断是否是一个合法的IP
    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            if (text.matches(regex)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean portCheck(int port){
        if(!(0<=port&&port<=65535))
            return false;
        else
            return true;
    }

    public static List<HostData> ConvertCursor(Cursor cursor) {
        List<HostData> list = new ArrayList<HostData>();
        if (cursor == null)
            return list;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String host = cursor.getString(1);
            int port = cursor.getInt(2);
            HostData tmp = new HostData(id, host, port);
            list.add(tmp);
        }
        return list;
    }
}
