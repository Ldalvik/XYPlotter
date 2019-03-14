package plotter;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;

public class Port {

    private SerialPort port;

    Port(String comPort) {
        port = SerialPort.getCommPort(comPort);
        port.setBaudRate(115200);
        port.openPort();
    }

    public void send(String msg){
        try {
            port.getOutputStream().write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int bytesAvailable(){
        return port.bytesAvailable();
    }

    char read() throws IOException {
        return (char) port.getInputStream().read();
    }

    void close(){
        port.closePort();
    }
}
