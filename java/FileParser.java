package plotter;

public class FileParser {
    private String data;

    public FileParser(String data){
        this.data = data.trim();
    }

    public String getChunk(int i){
        String webkit = data.split("\n")[0];
        return data.split(webkit)[i];
    }

    public String getWebKit(){
        return data.split("\n")[0];
    }

    public String getFileName(){
        String a = Utils.removeTillWord(data, "filename=\"");
        return Utils.removeAllAfter(a, "\"");
    }

    public String getContent(){
        String a = Utils.removeTillWord(data, "image/svg+xml");
        return Utils.removeAllAfter(a, getWebKit()).trim();
    }

    public String getDirectory(){
        String a = Utils.removeTillWord(data, "name=\"path\"");
        return Utils.removeAllAfter(a, getWebKit()).trim();
    }
}
