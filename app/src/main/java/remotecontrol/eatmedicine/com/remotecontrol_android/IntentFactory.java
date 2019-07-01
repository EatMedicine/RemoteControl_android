package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.content.Context;
import android.content.Intent;

public class IntentFactory {

    public static Intent getIntent(int CommandId,Context current){
        Intent i = new Intent();
        switch(CommandId){
            //结束进程
            case Tools.COMMAND_ID_KILL_PROCESS:
                i.setClass(current,KillProcessActivity.class);
                break;
            //发送消息
            case Tools.COMMAND_ID_SEND_MESSAGE:
                i.setClass(current,SendMessageActivity.class);
                break;
            default:i = null;break;
        }
        return i;
    }

}
