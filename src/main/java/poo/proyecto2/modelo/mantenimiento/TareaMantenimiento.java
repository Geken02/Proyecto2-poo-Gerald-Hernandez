package poo.proyecto2.modelo.mantenimiento;

import com.google.gson.annotations.Expose;

/**
 * Representa una tarea de mantenimiento básica, definida por un identificador único
 * y una descripción textual.
 * 
 * <p>Esta clase está diseñada para ser parte de planes o órdenes de mantenimiento,
 * y es serializable a JSON mediante la biblioteca Gson gracias a la anotación
 * {@code @Expose} en sus campos.</p>
 */
public class TareaMantenimiento {
    @Expose private int id;
    @Expose private String descripcion;

    /**
     * Constructor que inicializa una tarea de mantenimiento con un ID y una descripción.
     *
     * @param id identificador único de la tarea.
     * @param descripcion descripción textual de la acción a realizar.
     */
    public TareaMantenimiento(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    // Getters

    /**
     * Obtiene el identificador único de la tarea.
     *
     * @return el ID de la tarea.
     */
    public int getId() { return id; }

    /**
     * Obtiene la descripción textual de la tarea.
     *
     * @return la descripción de la tarea.
     */
    public String getDescripcion() { return descripcion; }

    /**
     * Devuelve una representación en cadena de la tarea, mostrando su ID y descripción
     * separados por un guion.
     *
     * @return una cadena con el formato "{id} - {descripcion}".
     */
    @Override
    public String toString() {
        return id + " - " + descripcion; 
        
    }
}