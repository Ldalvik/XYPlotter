package plotter;

import java.io.*;
import java.nio.charset.StandardCharsets;

class FileParser {
    private String data;
    private String txt = "text/plain";
    private String svg = "image/svg+xml";

    FileParser(String data){
        this.data = data.trim();
    }


    private String getBoundary(){
        return data.split("\n")[0];
    }

    String getFileName(){
        String a = Utils.removeTillWord(data, "filename=\"");
        return Utils.removeAllAfter(a, "\"");
    }

    String getContent(){
        String a = Utils.removeTillWord(data, svg);
        return Utils.removeAllAfter(a, getBoundary()).trim();
    }

    String getDirectory(){
        String a = Utils.removeTillWord(data, "name=\"path\"");
        return Utils.removeAllAfter(a, getBoundary()).trim();
    }

    void saveFile(String fileDirectory, String fileContents) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileDirectory), StandardCharsets.UTF_8))) {
            writer.write(fileContents);
        }
    }
}
