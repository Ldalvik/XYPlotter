import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.util.Scanner;

public class Port {

    private SerialPort port;

    public Port(String comPort) {
        port = SerialPort.getCommPort(comPort);
        port.openPort();
    }

    public void send(String msg){
        try {
            port.getOutputStream().write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int bytesAvailable(){
        return port.bytesAvailable();
    }

    public char read() throws IOException {
        return (char) port.getInputStream().read();
    }

    public void close(){
        port.closePort();
    }
}
