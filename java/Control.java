package plotter;

import plotter.controller.Send;

class Control {
    private Port port;
    private static int count = 0;
    private Send s;

    Control(Port port) {
        this.port = port;
        s = new Send(port);
    }

    void plot(String fileDirectory, int svg_size) {
        SvgParser svg = new SvgParser(fileDirectory);                           //Class file for parsing svg file into X/Y coordinates
        Plotter p = new Plotter(svg, svg_size);                                 //Class file for parsing X/Y coordinates into points to plot
        p.calculate();                                                          //Calculate coordinates
        s.setX(p.getStartX()).setY(-p.getStartY()).sendMessage(false); //Move plotter to first point that is being plotted
        PlotterServer.print(p.getStartX() + "," + p.getStartY());               //Print start X and start Y

        try {
            while (true) {
                while (port.bytesAvailable() == 0)
                    Thread.sleep(20);
                char charRead = port.read();                                               //Read char sent from arduino
                if (charRead == '5' && count < svg.getLength() - 1) {                      //While received char is '5' (last coordinates sent finished being plotted) and svg coordinates are not completed, continue to loop
                    s.setX(p.getX(count)).setY(p.getY(count)).sendMessage(false); //Send X/Y coordinates of the next point to plot
                    count++;                                                               //Keep track of iterations
                } else {
                    System.exit(0);                                                 //Terminates the .jar on completion
                    PlotterServer.print("Done!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        port.close();                                                                       //Close COM port on completion
    }
}
