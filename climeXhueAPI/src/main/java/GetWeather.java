import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetWeather {

    public GetWeather() {}

    public String getValues(String weatherAnswer) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject o = (JSONObject) parser.parse(weatherAnswer);
        System.out.println(o);


        return null;
    }
}
