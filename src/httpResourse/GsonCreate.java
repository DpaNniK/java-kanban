package httpResourse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonCreate {
    //Класс, в котором описана логика сериализации и десериализации полей LocalDateTime в объектах
    static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    public static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {

                    @Override
                    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
                        if (localDateTime != null) {
                            jsonWriter.value(localDateTime.format(DATE_TIME_FORMATTER));
                        }
                    }

                    @Override
                    public LocalDateTime read(JsonReader jsonReader) throws IOException {
                        return LocalDateTime.parse(jsonReader.nextString(), DATE_TIME_FORMATTER);
                    }
                }
                        .nullSafe())
                .registerTypeAdapter(Duration.class, new TypeAdapter<Duration>() {

                    @Override
                    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
                        if (duration != null) {
                            jsonWriter.value(duration.toMinutes());
                        }
                    }

                    @Override
                    public Duration read(JsonReader jsonReader) throws IOException {
                        return Duration.ofMinutes(jsonReader.nextLong());
                    }
                }
                        .nullSafe())
                .create();
    }
}
