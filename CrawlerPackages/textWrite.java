package CrawlerPackages;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class textWrite {
    public static void write(String dir, String name, String toWrite){
        byte[] bt = toWrite.getBytes(StandardCharsets.UTF_8);
        File file = new File(dir, name);
        if (!file.exists()){
            try {
                file.createNewFile();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fileOutputStream = new  FileOutputStream(file);
            fileOutputStream.write(bt);
            fileOutputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
