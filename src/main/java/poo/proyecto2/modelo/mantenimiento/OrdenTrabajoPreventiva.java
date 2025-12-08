package poo.proyecto2.modelo.mantenimiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una orden de trabajo preventiva, generada a partir de un programa
 * de mantenimiento planificado. Está asociada a una fase específica del programa
 * y contiene una lista de tareas detalladas a ejecutar durante la intervención.
 * 
 * <p>Esta clase extiende {@link OrdenTrabajo} y añade información específica del
 * mantenimiento preventivo, como el identificador de la fase y las tareas derivadas
 * de ella.</p>
 */
public class OrdenTrabajoPreventiva extends OrdenTrabajo {
    @com.google.gson.annotations.Expose private int idFase; // Referencia a la fase del programa
    @com.google.gson.annotations.Expose private List<TareaMantenimiento> tareas; // Tareas específicas de la fase

    /**
     * Constructor que inicializa una orden de trabajo preventiva con sus datos esenciales.
     * Llama al constructor de la clase base y configura los campos específicos del mantenimiento preventivo.
     *
     * @param id identificador único de la orden.
     * @param idEquipo identificador del equipo asociado.
     * @param idFase identificador de la fase del programa de mantenimiento que originó esta orden.
     * @param fechaOrden fecha en que se generó la orden.
     * @param fechaEjecucion fecha programada para su ejecución.
     */
    public OrdenTrabajoPreventiva(int id, int idEquipo, int idFase, LocalDate fechaOrden, LocalDate fechaEjecucion) {
        super(id, idEquipo, fechaOrden, fechaEjecucion);
        this.idFase = idFase;
        this.tareas = new ArrayList<>();
    }

    // Getters

    /**
     * Obtiene el identificador de la fase del programa de mantenimiento
     * a la que pertenece esta orden preventiva.
     *
     * @return el ID de la fase.
     */
    public int getIdFase() { return idFase; }

    /**
     * Obtiene una copia de la lista de tareas de mantenimiento asociadas a esta orden.
     * Se devuelve una copia para preservar la encapsulación y evitar modificaciones
     * directas a la lista interna.
     *
     * @return una nueva lista con las tareas de mantenimiento.
     */
    public List<TareaMantenimiento> getTareas() { return new ArrayList<>(tareas); }

    // Método específico para añadir tareas

    /**
     * Agrega una tarea de mantenimiento a la lista de tareas de esta orden preventiva.
     *
     * @param tarea la tarea a agregar; no debe ser {@code null}.
     */
    public void agregarTarea(TareaMantenimiento tarea) {
        tareas.add(tarea);
    }
    // ... (otros métodos) ...

    /**
     * Devuelve una representación en cadena resumida de la orden preventiva,
     * mostrando su identificador y estado actual.
     *
     * @return una cadena con el formato "Orden #ID - ESTADO", por ejemplo: "Orden #123 - Pendiente".
     */
    @Override
    public String toString() {
        return "Orden #" + getId() + " - " + getEstado(); // Ejemplo: "Orden #123 - Pendiente"
    }
}