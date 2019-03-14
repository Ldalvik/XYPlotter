package plotter.controller;

import plotter.Port;

public class Send {
    private Port port;
    private int x;
    private int y;

    public Send(Port port){
        this.port = port;
    }

    public Send setX(int x){
        this.x = x;
        return this;
    }

    public Send setX(long x){
        this.x = Math.toIntExact(x);
        return this;
    }

    public Send setY(int y){
        this.y = y;
        return this;
    }

    public Send setY(long y){
        this.y = Math.toIntExact(y);
        return this;
    }

    public Send tap(){
        port.send("tap/");
        return this;
    }

    public void sendMessage(boolean isManual){
        if(isManual){
            port.send("MANUAL:" + x + "," + y + "/");
        } else {
            port.send("AUTO:" + x + "," + y + "/");
        }
    }
}
