
import static spark.Spark.*;


public class Main {

    public static void main(String[] args) {

        get("/city/:city", (req, res) -> {
            Connection connection = new Connection(req.params(":city"), 0, 0);
            System.out.println("line 10");
            String answer = connection.getWeather();
            System.out.println("line 12");
            return new GetWeather().getValues(answer);

        });
    }
}
