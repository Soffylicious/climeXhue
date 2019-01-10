import org.json.JSONArray;
import org.json.JSONObject;

public class GetWeather {

    public GetWeather() {
    }

    //TODO weater:main, weather:description, weather:icon (d or n), main:humidity, main:temp, wind:speed


    public JSONObject getValues(String weatherAnswer) {
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

        JSONObject reply = new JSONObject();
        reply.put("cityName", cityName).put("mainWeather", mainWeather).put("details", details).put("day", day)
                .put("temperature", temp).put("humidity", humidity).put("windSpeed", speed);

        return reply;
    }
}
