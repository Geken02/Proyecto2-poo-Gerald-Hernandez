package poo.proyecto2.mantenimiento;

import com.google.gson.annotations.Expose;

public class TareaMantenimiento {
    @Expose private int id;
    @Expose private String descripcion;

    public TareaMantenimiento(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    // Getters
    public int getId() { return id; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return "TareaMantenimientoMaestra{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}