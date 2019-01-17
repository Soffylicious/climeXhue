
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import static spark.Spark.*;

/**
 * Main class and webb interface
 */
public class Main {

    public static void main(String[] args) throws MqttException, URISyntaxException {
        System.setProperty("file.encoding", "UTF-8");
        ParseJSON parseJSON = new ParseJSON();
        Connection connection = new Connection();
        MqttConnection mqttConnection = new MqttConnection("tcp://m20.cloudmqtt.com:14778");

        get("/city/:city", (request, response) -> {
            String answer = connection.getWeather(request.params(":city"), 0, 0);
            JSONObject reply = parseJSON.getValues(answer);
            mqttConnection.sendMessage("1,hue," + reply.get("color"));
            return reply;
        });

        put("/local", (request, response) -> {
            JSONObject latlang = new JSONObject(request.body());
            String answer = connection.getWeather("Home", latlang.getDouble("longitude"), latlang.getDouble("latitude"));
            JSONObject reply = parseJSON.getValues(answer);
            mqttConnection.sendMessage("1,hue," + reply.get("color"));
            return reply;
        });

        put("/power", (request, response) -> {
            JSONObject light = new JSONObject(request.body());
            mqttConnection.sendMessage("1,power," + light.getBoolean("power"));
            return "Power ON= " + light.getBoolean("power");
        });

        put("/brightness", (request, response) -> {
            JSONObject brightness = new JSONObject(request.body());
            mqttConnection.sendMessage("1,brightness," + brightness.getInt("brightness"));
            return "Brightness set to: " + brightness.getInt("brightness");
        });

        put("/hue", ((request, response) -> {
            JSONObject hue = new JSONObject(request.body());
            mqttConnection.sendMessage("1,hue," + hue.getInt("hue"));
            return "Hue is set to: " + hue.getInt("hue");
        }));
    }
}
