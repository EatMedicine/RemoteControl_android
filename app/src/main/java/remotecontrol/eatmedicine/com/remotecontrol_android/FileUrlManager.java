package remotecontrol.eatmedicine.com.remotecontrol_android;

import java.util.ArrayList;
import java.util.List;

public class FileUrlManager {

    public List<String> urlList = null;
    private int Length = 0;
    public FileUrlManager(){
        urlList = new ArrayList<>();
    }

    public void AddDirectory(String dirName){
        urlList.add(dirName);
        Length++;
    }

    public void ReturnDirectory(){
        urlList.remove(Length-1);
        Length--;
    }

    public String getUrl(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int count=0;count<Length;count++){
            stringBuilder.append(urlList.get(count)+'\\');
        }
        return stringBuilder.toString();
    }

    public String getLast(){
        if(Length==0)
            return "";
        return urlList.get(Length-1);
    }

    public int getLength(){
        return Length;
    }

}
