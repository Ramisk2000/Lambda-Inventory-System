package tn.enit.tp4.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileReader {

    public static Properties readPropertyFile(String fileName) throws Exception {
        Properties prop = new Properties();
        try (InputStream input = PropertyFileReader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new IOException("Unable to find " + fileName);
            }
            prop.load(input);
        }
        return prop;
    }
}
