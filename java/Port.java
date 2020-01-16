package plotter;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;

public class Port {

    private SerialPort port;

    //Serial port class
    public Port(String comPort) {
        port = SerialPort.getCommPort(comPort);
        port.setBaudRate(115200);
        port.openPort();
    }

    //Sends data to arduino
    public void send(String msg){
        try {
            port.getOutputStream().write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //checks if data is available
    int bytesAvailable(){
        return port.bytesAvailable();
    }

    //Reads data
    char read() throws IOException {
        return (char) port.getInputStream().read();
    }

    //closes port
    void close(){
        port.closePort();
    }
}
