package poo.proyecto2.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue(); // Si el valor es null, escribe null en el JSON
        } else {
            out.value(formatter.format(value)); // Si no es null, escribe la cadena formateada
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        // Usamos peek() para ver el tipo de token sin consumirlo
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull(); // Consumimos el token NULL
            return null; // Devolvemos null como LocalDate
        }
        // Si no era NULL, entonces debe ser STRING
        String dateString = in.nextString(); // Consumimos el token STRING
        // Parseamos la cadena a LocalDate
        return dateString == null || dateString.trim().isEmpty() ? null : LocalDate.parse(dateString, formatter);
    }
}