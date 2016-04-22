
import java.io.*;

public class FileHelper {

    public void testRead(String fileName) throws IOException {
        File file = new File(fileName);
        Reader reader = new FileReader(file);
        BufferedReader bReader = new BufferedReader(reader);
        String line;

        while ((line = bReader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
