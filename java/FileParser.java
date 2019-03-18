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

    //Get boundary of request, seperates the form data (file name, file contents, file directory)
    private String getBoundary(){
        return data.split("\n")[0];
    }

    //Gets file name
    String getFileName(){
        String a = Utils.removeTillWord(data, "filename=\"");
        return Utils.removeAllAfter(a, "\"");
    }

    //Gets file content
    String getContent(){
        String a = Utils.removeTillWord(data, svg);
        return Utils.removeAllAfter(a, getBoundary()).trim();
    }

    //Gets file directory
    String getDirectory(){
        String a = Utils.removeTillWord(data, "name=\"path\"");
        return Utils.removeAllAfter(a, getBoundary()).trim();
    }

    //Saves file
    void saveFile(String fileDirectory, String fileContents) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileDirectory), StandardCharsets.UTF_8))) {
            writer.write(fileContents);
        }
    }
}
