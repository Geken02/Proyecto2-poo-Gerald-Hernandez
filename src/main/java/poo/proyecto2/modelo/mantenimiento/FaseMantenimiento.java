package poo.proyecto2.modelo.mantenimiento;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

/**
 * Representa una fase dentro de un plan de mantenimiento.
 * Cada fase define la frecuencia con la que se debe ejecutar, los recursos necesarios
 * (partes, herramientas, personal), el tiempo estimado y las tareas maestras asociadas
 * mediante sus identificadores.
 * 
 * <p>La frecuencia se especifica mediante un tipo ({@link TipoFrecuencia}) y un valor numérico
 * ({@code medidorFrecuencia}), y la fase se repite un número determinado de ciclos.</p>
 * 
 * <p>Esta clase está preparada para serialización JSON usando la biblioteca Gson,
 * gracias a la anotación {@code @Expose} en todos sus campos relevantes.</p>
 */
public class FaseMantenimiento {
    @Expose private TipoFrecuencia tipoFrecuencia;
    @Expose private int medidorFrecuencia;
    @Expose private int cantidadCiclos;
    @Expose private List<Integer> idsTareasMaestras; 
    @Expose private String partes;
    @Expose private String herramientas;
    @Expose private String personal;
    @Expose private float horasEstimadas;

    /**
     * Constructor que inicializa una fase de mantenimiento con sus parámetros básicos.
     * La lista de tareas maestras se inicializa como vacía y puede poblarse posteriormente.
     *
     * @param tipoFrecuencia el tipo de frecuencia (por ejemplo: diaria, semanal, por horas de operación).
     * @param medidorFrecuencia el valor numérico asociado a la frecuencia (ej. cada 30 días).
     * @param cantidadCiclos número de veces que se repetirá esta fase en el plan.
     * @param partes materiales o repuestos requeridos para la fase.
     * @param herramientas herramientas necesarias para ejecutar la fase.
     * @param personal personal requerido (puede incluir roles o cantidades).
     * @param horasEstimadas tiempo estimado en horas para completar la fase.
     */
    public FaseMantenimiento(TipoFrecuencia tipoFrecuencia, int medidorFrecuencia, int cantidadCiclos,
                             String partes, String herramientas, String personal, float horasEstimadas) {
        this.tipoFrecuencia = tipoFrecuencia;
        this.medidorFrecuencia = medidorFrecuencia;
        this.cantidadCiclos = cantidadCiclos;
        this.idsTareasMaestras = new ArrayList<>(); 
        this.partes = partes;
        this.herramientas = herramientas;
        this.personal = personal;
        this.horasEstimadas = horasEstimadas;
    }

    // Getters

    /**
     * Obtiene el tipo de frecuencia con la que se programa esta fase.
     *
     * @return el tipo de frecuencia.
     */
    public TipoFrecuencia getTipoFrecuencia() { return tipoFrecuencia; }

    /**
     * Obtiene el valor numérico asociado a la frecuencia (por ejemplo, "cada 15" días).
     *
     * @return el medidor de frecuencia.
     */
    public int getMedidorFrecuencia() { return medidorFrecuencia; }

    /**
     * Obtiene el número de ciclos que se ejecutará esta fase.
     *
     * @return la cantidad de ciclos.
     */
    public int getCantidadCiclos() { return cantidadCiclos; }

    /**
     * Obtiene una copia de la lista de identificadores de tareas maestras asociadas a esta fase.
     * Se devuelve una copia para preservar la encapsulación y evitar modificaciones externas
     * a la lista interna.
     *
     * @return una nueva lista con los IDs de las tareas maestras.
     */
    public List<Integer> getIdsTareasMaestras() { return new ArrayList<>(idsTareasMaestras); } // Devolver copia

    /**
     * Obtiene la descripción de las partes o repuestos necesarios para la fase.
     *
     * @return las partes requeridas.
     */
    public String getPartes() { return partes; }

    /**
     * Obtiene la descripción de las herramientas necesarias para ejecutar la fase.
     *
     * @return las herramientas requeridas.
     */
    public String getHerramientas() { return herramientas; }

    /**
     * Obtiene la descripción del personal requerido (roles, cantidad, especialidad, etc.).
     *
     * @return la información del personal.
     */
    public String getPersonal() { return personal; }

    /**
     * Obtiene el tiempo estimado en horas para completar esta fase.
     *
     * @return las horas estimadas (puede incluir fracciones).
     */
    public float getHorasEstimadas() { return horasEstimadas; }

    // Método para añadir una tarea (por ID)

    /**
     * Agrega el identificador de una tarea maestra a la lista de tareas asociadas a esta fase.
     *
     * @param idTarea el ID de la tarea maestra a agregar.
     */
    public void agregarTareaMaestra(int idTarea) {
        idsTareasMaestras.add(idTarea);
    }

    /**
     * Devuelve una representación en cadena resumida de la fase, indicando su tipo de frecuencia
     * y el intervalo correspondiente.
     *
     * @return una cadena en el formato "Fase: [tipoFrecuencia] cada [medidorFrecuencia] [tipoFrecuencia]".
     */
    @Override
    public String toString() {
        return "Fase: " + tipoFrecuencia + " cada " + medidorFrecuencia + " " + tipoFrecuencia;
    }
}