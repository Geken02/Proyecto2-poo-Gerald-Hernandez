package poo.proyecto2.equipos;

import com.google.gson.annotations.Expose;

public class FallaEquipo {
    @Expose private int id;
    @Expose private String descripcion; // Ej: "Cortes en la banda", "Fallo en el motor"

    public FallaEquipo(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    // Getters
    public int getId() { return id; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return "FallaEquipo{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}