
import org.eclipse.paho.client.mqttv3.MqttException;
import java.net.URISyntaxException;
import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws MqttException, URISyntaxException {
        ParseJSON parseJSON = new ParseJSON();
        Connection connection = new Connection();

        get("/city/:city", (req, res) -> {
            String answer = connection.getWeather(req.params(":city"), 0, 0);
            System.out.println(answer);
            return parseJSON.getValues(answer);
        });
    }
}
