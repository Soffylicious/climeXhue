import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONObject;


public class ParseJSON {


    public ParseJSON() {
    }

    //TODO weater:main, weather:description, weather:icon (d or n), main:humidity, main:temp, wind:speed


    public JSONObject getValues(String weatherAnswer){
        JSONObject o = new JSONObject(weatherAnswer);

        JSONArray array = o.getJSONArray("weather");
        JSONObject weather = array.getJSONObject(0);
        String mainWeather = weather.getString("main");
        String details = weather.getString("description");
        boolean day = weather.getString("icon").contains("d");

        JSONObject main = o.getJSONObject("main");
        int temp = (int) main.getDouble("temp");
        int humidity = main.getInt("humidity");

        JSONObject wind = o.getJSONObject("wind");
        int speed = (int) wind.getDouble("speed");
        String cityName = o.getString("name");

        String color = "";
        if (mainWeather.equals("Thunderstorm")) color = "0"; //Red
        else if (mainWeather.equals("Drizzle")) color = "46920"; //Blue
        else if (mainWeather.equals("Rain")) color = "rain";
        else if (mainWeather.equals("Snow")) color = "snow";
        else if (mainWeather.equals("Atmosphere")) color = "atmosphere";
        else if (mainWeather.equals("Clear")) color = "clear";
        else if (mainWeather.equals("Clouds")) color = ""; //Blue


        JSONObject reply = new JSONObject();
        reply.put("cityName", cityName).put("mainWeather", mainWeather).put("details", details).put("day", day)
                .put("temperature", temp).put("humidity", humidity).put("windSpeed", speed).put("color",color);
        System.out.println(mainWeather);
        return reply;
    }
}
