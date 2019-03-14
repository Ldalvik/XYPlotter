package plotter;

import plotter.controller.Send;

public class Control {
    private Port port;
    private static int count = 0;
    private Send s;

    public Control(Port port) {
        this.port = port;
        s = new Send(port);
    }

    public void plot(String fileContents, int svg_size) {
        SvgParser svg = new SvgParser(fileContents);
        Plotter p = new Plotter(svg, svg_size);
        p.calculate();
        /*for (int i = 0; i < svg.getLength(); i++) {
            System.out.println(i + ": " + p.getX(i) + " | " + p.getY(i));
        }*/
        s.setX(p.getStartX()).setY(p.getStartY()).sendMessage(true);

        try {
            while (true) {
                while (port.bytesAvailable() == 0)
                    Thread.sleep(20);
                char charRead = port.read();
                if (charRead == '5' && count < svg.getLength() - 1) {
                    PlotterServer.print("(" + count + "/" + svg.getLength() + ")");
                    s.setX(svg.getX(count)).setY(svg.getY(count)).sendMessage(false);
                    count++;
                } else {
                    System.exit(0);
                    PlotterServer.print("Done!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        port.close();
    }


}
