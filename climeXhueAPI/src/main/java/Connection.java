import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Connects to OpenWeatherMapAPI and handles requests to OpenWeatherMap API
 */
public class Connection {

    //API key: 92d45b077fa249614bfc79c61cf8b50f
    private String apiKey = "&APPID=92d45b077fa249614bfc79c61cf8b50f";
    private String httpPath = "http://api.openweathermap.org/data/2.5/weather?q=";
    private String cordHttpPath = "http://api.openweathermap.org/data/2.5/weather?";
    private String language = "&lang=se";
    private String unit = "&units=metric";

    public Connection() {
        
    }

    public String getWeather(String city, double longitude, double latitude) {
        HttpURLConnection connection;
        InputStream inputStream;
        if (!city.equals("")) {
            try {
                if (!city.equals("Home")) {
                    connection = (HttpURLConnection) (new URL(httpPath + city + apiKey + language + unit))
                            .openConnection();
                } else {
                    connection = (HttpURLConnection) (new URL(cordHttpPath + "lat=" + latitude + "&lon=" + longitude
                            + apiKey + unit)).openConnection();
                }
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();

                StringBuilder buffer = new StringBuilder();

                if (connection.getResponseCode() == 200) {

                    System.out.println("HTTP response: " + connection.getResponseCode());
                    inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                            (inputStream));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        buffer.append(line).append("\r\n");
                    }
                    inputStream.close();
                    connection.disconnect();
                    return buffer.toString();
                } else {
                    System.out.println("HTTP response: " + connection.getResponseCode());
                    System.out.println(buffer.toString());
                    return String.valueOf(connection.getResponseCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "Unexplained server error";
    }
}
