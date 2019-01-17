import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Parse the JSON answer from OpenWeatherMap API and creates a new JSON with the values requested by the webb-client
 */
public class ParseJSON {


    public ParseJSON() {
    }



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
        if (mainWeather.equals("Thunderstorm")) color = "0"; //Red KLAR
        else if (mainWeather.equals("Drizzle")) color = "46000"; //Light Blue KLAR
        else if (mainWeather.equals("Rain")) color = "52500"; //Purple KLAR
        else if (mainWeather.equals("Snow")) color = "47563"; //White KLAR
        else if (mainWeather.equals("Atmosphere")) color = "25500"; //Green KLAR
        else if (mainWeather.equals("Clear")) color = "13000"; //Orange KLAR
        else if (mainWeather.equals("Clouds")) color = "25000"; //Dim KLAR

        JSONObject reply = new JSONObject();
        reply.put("cityName", cityName).put("mainWeather", mainWeather).put("details", details).put("day", day)
                .put("temperature", temp).put("humidity", humidity).put("windSpeed", speed).put("color",color);
        System.out.println(mainWeather);
        return reply;
    }
}
