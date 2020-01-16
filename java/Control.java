package plotter;

import plotter.controller.Send;

//Serial port data connection loop

class Control {
    private Port port;
    private static int count = 0;
    private Send s;

    Control(Port port) {
        this.port = port;
        s = new Send(port);
    }

    void plot(String fileDirectory, int svg_size) {
        SvgParser svg = new SvgParser(fileDirectory); //Load and parse the SVG file
        Plotter p = new Plotter(svg, svg_size); //Parse and calculate coordinate list
        p.calculate();
        System.out.println(p.toString());
        s.home(); //Send pen to starting position

        s.setX((int) p.getStartX()).setY((int)-(p.ySize-p.getStartY())).sendMessage(false); //Sends pen to first coordinate
        PlotterServer.print(p.getStartX() + "," + p.getStartY());

        //Iterates through all coordinates and sends them to the plotter
        int i = 0;
        try {
            while (true) {
                while (port.bytesAvailable() == 0)
                    Thread.sleep(20);
                char charRead = port.read();
                if (charRead == '5' && count < svg.getLength() - 1) { //Waits for confirmation that the previous point was plotted before sending another
                    System.out.println(i + "/" + svg.getLength());
                    i++;
                    s.setX(p.getX(count)).setY(p.getY(count)).sendMessage(false); //Sends coordinates to plotter
                    count++;
                } else {
                    s.box(); //Draws box and scrolls to new image
                    PlotterServer.print("Done!");
                    System.exit(0);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        port.close();
    }
}
