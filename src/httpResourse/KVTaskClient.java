package httpResourse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    URI uri;
    String apiToken;
    String port;

    public KVTaskClient(String port) {
        this.port = port;
        this.uri = URI.create("http://localhost:" + port + "/register");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET().uri(uri).build();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            apiToken = response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время запроса: " + uri + " произошла ошибка. Проверьте правильность введенного URL");
        }
    }

    //Метод по добавлению задач в мапу сервера в зависимости от ключа
    public void put(String key, String json) {
        uri = URI.create("http://localhost:" + port + "/save/" + key + "?API_TOKEN=" + apiToken);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json)).uri(uri).build();

        try {
            client.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время запроса: " + uri
                    + " произошла ошибка. Проверьте правильность введенного URL");
        }
    }

    //Метод для получению значений из мапы сервера в зависимости от ключа
    public String load(String key) {
        uri = URI.create("http://localhost:" + port + "/load/" + key + "?API_TOKEN=" + apiToken);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET().uri(uri).build();

        HttpResponse<String> response = null;

        try {
            response = client.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время запроса: " + uri
                    + " произошла ошибка. Проверьте правильность введенного URL");
        }

        assert response != null;
        return response.body();
    }


}
