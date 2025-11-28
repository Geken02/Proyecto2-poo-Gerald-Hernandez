package poo.proyecto2.mantenimiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdenTrabajoPreventiva extends OrdenTrabajo {
    @com.google.gson.annotations.Expose private int idFase; // Referencia a la fase del programa
    @com.google.gson.annotations.Expose private List<TareaMantenimiento> tareas; // Tareas específicas de la fase

    public OrdenTrabajoPreventiva(int id, int idEquipo, int idFase, LocalDate fechaOrden, LocalDate fechaEjecucion) {
        super(id, idEquipo, fechaOrden, fechaEjecucion);
        this.idFase = idFase;
        this.tareas = new ArrayList<>();
    }

    // Getters
    public int getIdFase() { return idFase; }
    public List<TareaMantenimiento> getTareas() { return new ArrayList<>(tareas); }

    // Método específico para añadir tareas
    public void agregarTarea(TareaMantenimiento tarea) {
        tareas.add(tarea);
    }
}