package poo.proyecto2.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import poo.proyecto2.mantenimiento.*;
import poo.proyecto2.equipos.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    // Gson especializado para entidades con herencia (como OrdenTrabajo)
    private static final Gson gsonConHerencia = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory.of(OrdenTrabajo.class, "tipo")
                .registerSubtype(OrdenTrabajoPreventiva.class, "preventiva")
                .registerSubtype(OrdenTrabajoCorrectiva.class, "correctiva")
        )
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();

    // Gson genérico para otros tipos (como EquipoArbol, TareaMantenimientoMaestra, etc.)
    private static final Gson gsonGenerico = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();

    // === MÉTODOS GENÉRICOS ===
    // Usamos gsonGenerico para tipos sin herencia
    public static <T> void guardarListaGenerico(List<T> lista, Type tipoLista, String rutaArchivo) throws IOException {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            gsonGenerico.toJson(lista, tipoLista, writer);
        }
        System.out.println("Lista guardada en: " + rutaArchivo);
    }

    public static <T> List<T> cargarListaGenerico(String rutaArchivo, Type tipoLista) throws IOException {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            List<T> lista = gsonGenerico.fromJson(reader, tipoLista);
            return lista != null ? lista : new ArrayList<>();
        }
    }

    // Usamos gsonConHerencia para tipos con herencia (solo Órdenes por ahora)
    public static <T> void guardarListaConHerencia(List<T> lista, Type tipoLista, String rutaArchivo) throws IOException {
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            gsonConHerencia.toJson(lista, tipoLista, writer);
        }
        System.out.println("Lista (con herencia) guardada en: " + rutaArchivo);
    }

    public static <T> List<T> cargarListaConHerencia(String rutaArchivo, Type tipoLista) throws IOException {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            List<T> lista = gsonConHerencia.fromJson(reader, tipoLista);
            return lista != null ? lista : new ArrayList<>();
        }
    }
}