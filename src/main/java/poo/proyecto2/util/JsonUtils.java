package poo.proyecto2.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import poo.proyecto2.equipos.Equipos;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JsonUtils {
    private static final Gson gson = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
        .setPrettyPrinting()
        .create();

    public static List<Equipos> cargarArboles(String ruta) throws IOException {
        try (FileReader r = new FileReader(ruta)) {
            Equipos[] array = gson.fromJson(r, Equipos[].class);
            return array != null ? new ArrayList<>(Arrays.asList(array)) : new ArrayList<>();
        }
    }

    public static void guardarArboles(List<Equipos> bosque, String ruta) throws IOException {
        try (FileWriter w = new FileWriter(ruta)) {
            gson.toJson(bosque, w);
        }
    }
}