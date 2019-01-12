
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) throws MqttException, URISyntaxException {
        ParseJSON parseJSON = new ParseJSON();
        Connection connection = new Connection();
        MqttConnection mqttConnection = new MqttConnection("tcp://m20.cloudmqtt.com:14778");

        get("/city/:city", (request, response) -> {
            System.out.println("get: /city/:city");
            String answer = connection.getWeather(request.params(":city"), 0, 0);
            System.out.println(answer);
            JSONObject reply = parseJSON.getValues(answer);
            mqttConnection.sendMessage("1,hue," + reply.get("color"));
            return reply;
        });

        get("/local", (request, response) -> {
            System.out.println("get: /local");
            JSONObject latlang = new JSONObject(request.body().toString());
            String answer = connection.getWeather("Home", latlang.getDouble("longitude"), latlang.getDouble("latitude"));
            System.out.println(answer);
            JSONObject reply = parseJSON.getValues(answer);
            mqttConnection.sendMessage("1,hue," + reply.get("color"));
            return reply;
        });

        put("/power", (request, response) -> {
            System.out.println("put: /power");
            JSONObject light = new JSONObject(request.body().toString());
            mqttConnection.sendMessage("1,power," + light.getBoolean("power"));
            return "Power ON= " + light.getBoolean("power");
        });

        put("/brightness", (request, response) -> {
            System.out.println("put: /brightness");
            JSONObject brightness = new JSONObject(request.body().toString());
            mqttConnection.sendMessage("1,brightness," + brightness.getInt("brightness"));
            return "Brightness set to: " + brightness.getInt("brightness");
        });

        put("/hue", ((request, response) -> {
            System.out.println("put: /hue");
            JSONObject hue = new JSONObject(request.body().toString());
            mqttConnection.sendMessage("1,hue," + hue.getInt("hue"));
            return "Hue is set to: " + hue.getInt("hue");
        }));
    }
}
