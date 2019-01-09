
import static spark.Spark.*;


public class Main {

    public static void main(String[] args) {
        get("/hello", (req, res) -> {
            Connection connection = new Connection("Lomma", 0, 0);
            new GetWeather().getValues(connection.getWeather());
            return connection.getWeather();

        });
    }
}
