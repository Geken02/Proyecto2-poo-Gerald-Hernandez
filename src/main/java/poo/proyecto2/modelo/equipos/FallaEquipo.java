package poo.proyecto2.modelo.equipos;

import com.google.gson.annotations.Expose;

/**
 * Representa una falla asociada a un equipo en el sistema de mantenimiento.
 * Cada instancia contiene un identificador único y una descripción textual
 * que detalla la naturaleza de la falla.
 * 
 * <p>Esta clase está preparada para serialización JSON mediante la biblioteca Gson,
 * ya que sus campos relevantes están anotados con {@code @Expose}.</p>
 */
public class FallaEquipo {
    @Expose private int id;
    @Expose private String descripcion;

    /**
     * Constructor que inicializa una falla con un identificador y una descripción.
     *
     * @param id el identificador único de la falla.
     * @param descripcion la descripción textual de la falla.
     */
    public FallaEquipo(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    // Getters

    /**
     * Obtiene el identificador único de la falla.
     *
     * @return el ID de la falla.
     */
    public int getId() { return id; }

    /**
     * Obtiene la descripción textual de la falla.
     *
     * @return la descripción de la falla.
     */
    public String getDescripcion() { return descripcion; }

    /**
     * Devuelve una representación en cadena de la falla, compuesta por su ID
     * seguido de un espacio y su descripción.
     *
     * @return una cadena en el formato "{id} {descripcion}".
     */
    @Override
    public String toString() {
        return "" + id +
                " " + descripcion;
    }
}