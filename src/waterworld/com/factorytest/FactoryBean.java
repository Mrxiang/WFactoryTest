package waterworld.com.factorytest;

import android.util.Log;

public class FactoryBean {
    private static final String TAG =Utils.TAG +"FactoryBean" ;
    private String name;

    private String title;

    private String action;

    private int status = Utils.NONE;

    public FactoryBean(String name, String title, String action, int status) {
        this.name = name;
        this.title = title;
        this.action = action;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle( ){
        return  title;
    }
    public void setTitle(String title ){
        this.title = title;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getStatus( ){
        return  status;
    }
    public void setStatus(int status ){
        Log.d(TAG, "setStatus: "+status);
        this.status = status;
    }

    public String toString( ){
        String s = "name :"+name+"-title:"+title+"-action:"+action+"-status:"+status+"\n";
        return s;
    }
}
