package remotecontrol.eatmedicine.com.remotecontrol_android;

public class HostData {
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_host() {
        return _host;
    }

    public void set_host(String _host) {
        this._host = _host;
    }

    public int get_port() {
        return _port;
    }

    public void set_port(int _port) {
        this._port = _port;
    }

    //判断ip和port是否合法
    public boolean CheckVaild(){
        if(Tools.ipCheck(_host)==false||Tools.portCheck(_port)==false)
            return false;
        else
            return true;
    }

    private int _id;
    private String _host;
    private int _port;
    public HostData(int id,String host,int port ){
        set_id(id);
        set_host(host);
        set_port(port);
    }


}
