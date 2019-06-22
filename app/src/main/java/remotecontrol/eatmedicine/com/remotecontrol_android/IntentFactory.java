package remotecontrol.eatmedicine.com.remotecontrol_android;

import android.content.Context;
import android.content.Intent;

public class IntentFactory {

    public static Intent getIntent(int CommandId,Context current){
        Intent i = new Intent();
        switch(CommandId){
            //结束进程
            case 1:
                i.setClass(current,KillProcessActivity.class);
                break;
            default:i = null;break;
        }
        return i;
    }

}
