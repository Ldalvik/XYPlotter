import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class SvgParser {
    private long[] xCoords;
    private long[] yCoords;
    private long[] xCoordsSorted;
    private long[] yCoordsSorted;

    public SvgParser(String fileName) {
        try {
            File svg = new File(fileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document d = dbf.newDocumentBuilder().parse(svg);
            d.getDocumentElement().normalize();
            NodeList list = d.getElementsByTagName("circle");
            xCoordsSorted = new long[list.getLength()];
            yCoordsSorted = new long[list.getLength()];

            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    xCoordsSorted[i] = (long) (Float.valueOf(e.getAttribute("cx")) * 1000000);
                    yCoordsSorted[i] = (long) (Float.valueOf(e.getAttribute("cy")) * 1000000);
                }
            }
            Arrays.sort(xCoordsSorted);
            Arrays.sort(yCoordsSorted);

            xCoords = new long[list.getLength()];
            yCoords = new long[list.getLength()];
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element ee = (Element) node;
                    xCoords[i] = (long) (Float.valueOf(ee.getAttribute("cx")) * 1000000) - xCoordsSorted[0];
                    yCoords[i] = (long) (Float.valueOf(ee.getAttribute("cy")) * 1000000) - yCoordsSorted[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    long getX(int count) {
        return xCoords[count];
    }

    long getY(int count) {
        return yCoords[count];
    }

    long getLowestX() {
        return 0;
    }

    long getHighestX() {
        return (xCoordsSorted[xCoordsSorted.length-1]) - xCoordsSorted[0];
    }

    long getLowestY() {
        return 0;
    }

    long getHighestY() {
        return (yCoordsSorted[yCoordsSorted.length-1]) - yCoordsSorted[0];
    }

    int getLength() {
        return xCoords.length;
    }
}