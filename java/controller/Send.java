package plotter.controller;

import plotter.Port;

public class Send {
    private Port port;
    private int x;
    private int y;

    public Send(Port port){
        this.port = port;
    }

    //Sets int X coordinate
    public Send setX(int x){
        this.x = x;
        return this;
    }

    //Sets long X coordinate
    public Send setX(long x){
        this.x = Math.toIntExact(x);
        return this;
    }

    //Sets int Y coordinate
    public Send setY(int y){
        this.y = y;
        return this;
    }

    //Sets long Y coordinate
    public Send setY(long y){
        this.y = Math.toIntExact(y);
        return this;
    }

    //Sends tap request
    public void tap(){
        port.send("tap/");
    }

    /*
      Sends request to move the given X and Y value (if isManual == false, then the arduino will
      confirm when its done plotting. If true, it will not confirm that its done, which is used for manual control.
    */
    public void sendMessage(boolean isManual){
        if(isManual){
            port.send("MANUAL:" + x + "," + y + "/");
        } else {
            port.send("AUTO:" + x + "," + y + "/");
        }
    }
}
