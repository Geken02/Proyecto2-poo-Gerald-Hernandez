package poo.proyecto2.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    // Cambia el formatter para usar espacio en lugar de "T"
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue(); // Si el valor es null, escribe null en el JSON
        } else {
            out.value(formatter.format(value)); // Si no es null, escribe la cadena formateada
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        // Usamos peek() para ver el tipo de token sin consumirlo
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull(); // Consumimos el token NULL
            return null; // Devolvemos null como LocalDateTime
        }
        // Si no era NULL, entonces debe ser STRING
        String dateString = in.nextString(); // Consumimos el token STRING
        // Parseamos la cadena a LocalDateTime
        return dateString == null || dateString.trim().isEmpty() ? null : LocalDateTime.parse(dateString, formatter);
    }
}