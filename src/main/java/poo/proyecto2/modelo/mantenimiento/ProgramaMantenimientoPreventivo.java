package poo.proyecto2.modelo.mantenimiento;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un programa de mantenimiento preventivo asociado a un equipo específico.
 * Contiene una lista de fases, donde cada fase define un conjunto de tareas, frecuencia,
 * recursos y tiempo estimado para la ejecución del mantenimiento planificado.
 * 
 * <p>Esta clase está preparada para serialización y deserialización JSON mediante la
 * biblioteca Gson, gracias a la anotación {@code @Expose} en sus campos.</p>
 */
public class ProgramaMantenimientoPreventivo {
    @Expose private int idEquipo;
    @Expose private List<FaseMantenimiento> fases; 

    // Constructor que recibe idEquipo

    /**
     * Constructor que inicializa un programa de mantenimiento preventivo para un equipo dado.
     * La lista de fases se inicializa como vacía y puede poblarse posteriormente.
     *
     * @param idEquipo el identificador del equipo al que pertenece este programa.
     */
    public ProgramaMantenimientoPreventivo(int idEquipo) {
        this.idEquipo = idEquipo;
        this.fases = new ArrayList<>();
    }

    // Constructor vacío necesario para Gson

    /**
     * Constructor por defecto requerido por la biblioteca Gson para la deserialización.
     * Inicializa la lista de fases como vacía. El {@code idEquipo} debe establecerse posteriormente.
     */
    public ProgramaMantenimientoPreventivo() {
        this.fases = new ArrayList<>();
    }

    /**
     * Agrega una fase al programa de mantenimiento preventivo.
     *
     * @param fase la fase a agregar; no debe ser {@code null}.
     */
    public void agregarFase(FaseMantenimiento fase) {
        fases.add(fase);
    }

    // Getters

    /**
     * Obtiene el identificador del equipo asociado a este programa de mantenimiento.
     *
     * @return el ID del equipo.
     */
    public int getIdEquipo() { return idEquipo; }

    /**
     * Obtiene una copia de la lista de fases del programa.
     * Se devuelve una copia para proteger la encapsulación del estado interno.
     *
     * @return una nueva lista con las fases del programa.
     */
    public List<FaseMantenimiento> getFases() { return new ArrayList<>(fases); }

    /**
     * Devuelve una representación en cadena del programa de mantenimiento,
     * incluyendo el ID del equipo y la lista de fases.
     *
     * @return una cadena con el formato "ProgramaMantenimientoPreventivo{idEquipo=..., fases=[...]}". 
     */
    @Override
    public String toString() {
        return "ProgramaMantenimientoPreventivo{" +
                "idEquipo=" + idEquipo +
                ", fases=" + fases +
                '}';
    }
}