package useGradleAndMaps;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class ApiKey {
    private String key = "Default";
    private static final String FILE_NAME = "D:\\Stefano\\Documents\\"
                                            + "Universita\\Anni-Scolastici\\"
                                            + "SECONDO ANNO\\Corsi\\"
                                            + "Programmazione ad Oggetti\\"
                                            + "EclipseWorkSpace\\useGradleAndMaps\\"
                                            + "ApiKey\\key.txt";
    
    public ApiKey() throws IOException {
        try(final BufferedReader r = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(ApiKey.FILE_NAME), "UTF-8"))){
            this.key = r.readLine();
        } catch(IOException exc) {
            System.out.println(exc.getMessage());
        }
    }
    
    public String getApiKey() {
        return this.key;
    }
}

