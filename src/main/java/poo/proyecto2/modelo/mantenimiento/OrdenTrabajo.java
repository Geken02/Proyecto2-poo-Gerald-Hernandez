package poo.proyecto2.modelo.mantenimiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

/**
 * Clase abstracta que representa una orden de trabajo asociada a un equipo.
 * Gestiona el ciclo de vida de una orden (pendiente, en progreso, completada o cancelada),
 * incluye fechas clave, costos, observaciones y permite registrar dos tipos de fallas:
 * <ul>
 *   <li><b>Fallas reportadas</b>: proporcionadas al crear la orden, sin identificador único.</li>
 *   <li><b>Fallas encontradas</b>: detectadas durante la ejecución, vinculadas a una falla maestra por su ID.</li>
 * </ul>
 * 
 * <p>Esta clase está diseñada para ser serializable a JSON mediante Gson, gracias a la anotación
 * {@code @Expose} en todos sus campos relevantes, incluyendo las listas de fallas y sus clases internas.</p>
 */
public abstract class OrdenTrabajo {
    @Expose protected int id;
    @Expose protected int idEquipo;
    @Expose protected LocalDate fechaOrden; // Fecha en que se generó la orden
    @Expose protected LocalDate fechaEjecucion; // Fecha en que debe ejecutarse
    @Expose protected String observaciones;
    @Expose protected LocalDate fechaInicioReal;
    @Expose protected LocalDate fechaFinReal;
    @Expose protected float horasTrabajo;
    @Expose protected int costoManoObra;
    @Expose protected int costoMateriales;
    @Expose protected String observacionesEjecucion; // Observaciones al finalizar
    @Expose protected EstadoOrden estado;
    @Expose protected LocalDate fechaCancelacion;
    @Expose protected String motivoCancelacion;

    // --- Cambio aquí ---
    // Dos listas: una para fallas reportadas (sin ID), otra para nuevas encontradas (con ID)
    @Expose protected List<FallaReportada> fallasReportadas; // Fallas reportadas inicialmente (sin ID)
    @Expose protected List<FallaEncontrada> fallasEncontradas; // Fallas nuevas encontradas durante el trabajo (con ID)
    // --- Fin cambio ---

    /**
     * Enumeración que representa los estados posibles de una orden de trabajo.
     */
    public enum EstadoOrden {
        PENDIENTE,
        EN_PROGRESO,
        COMPLETADA,
        CANCELADA
    }

    /**
     * Clase interna estática que representa una falla reportada al momento de crear la orden.
     * No tiene identificador único; solo incluye descripciones de causas y acciones tomadas.
     * 
     * <p>Está preparada para serialización JSON mediante Gson.</p>
     */
    public static class FallaReportada {
        @Expose private String causas;
        @Expose private String accionesTomadas;

        /**
         * Constructor que inicializa una falla reportada con causas y acciones tomadas.
         *
         * @param causas descripción de las posibles causas de la falla.
         * @param accionesTomadas acciones ya realizadas o propuestas para abordar la falla.
         */
        public FallaReportada(String causas, String accionesTomadas) {
            this.causas = causas;
            this.accionesTomadas = accionesTomadas;
        }

        // Getters

        /**
         * Obtiene la descripción de las causas de la falla reportada.
         *
         * @return las causas.
         */
        public String getCausas() { return causas; }

        /**
         * Obtiene la descripción de las acciones tomadas respecto a la falla.
         *
         * @return las acciones tomadas.
         */
        public String getAccionesTomadas() { return accionesTomadas; }

        /**
         * Devuelve una representación en cadena de la falla reportada.
         *
         * @return una cadena con el formato "FallaReportada{causas='...', accionesTomadas='...'}".
         */
        @Override
        public String toString() {
            return "FallaReportada{" +
                    "causas='" + causas + '\'' +
                    ", accionesTomadas='" + accionesTomadas + '\'' +
                    '}';
        }
    }

    /**
     * Clase interna estática que representa una falla descubierta durante la ejecución de la orden.
     * Incluye un identificador que la vincula a una falla maestra predefinida, además de su descripción,
     * causas y acciones tomadas.
     * 
     * <p>Está preparada para serialización JSON mediante Gson.</p>
     */
    public static class FallaEncontrada {
        @Expose private int idFalla; // Identificación de la falla según lista de fallas
        @Expose private String descripcionFalla; // Descripción de la falla (la identificación en texto)
        @Expose private String causas;
        @Expose private String accionesTomadas;

        /**
         * Constructor que inicializa una falla encontrada con todos sus atributos.
         *
         * @param idFalla el identificador de la falla en la lista maestra de fallas.
         * @param descripcionFalla la descripción textual de la falla.
         * @param causas las causas identificadas durante la intervención.
         * @param accionesTomadas las acciones realizadas para corregirla.
         */
        public FallaEncontrada(int idFalla, String descripcionFalla, String causas, String accionesTomadas) {
            this.idFalla = idFalla;
            this.descripcionFalla = descripcionFalla;
            this.causas = causas;
            this.accionesTomadas = accionesTomadas;
        }

        // Getters

        /**
         * Obtiene el identificador de la falla (relacionado con una lista maestra).
         *
         * @return el ID de la falla.
         */
        public int getIdFalla() { return idFalla; }

        /**
         * Obtiene la descripción textual de la falla encontrada.
         *
         * @return la descripción de la falla.
         */
        public String getDescripcionFalla() { return descripcionFalla; }

        /**
         * Obtiene las causas identificadas de la falla encontrada.
         *
         * @return las causas.
         */
        public String getCausas() { return causas; }

        /**
         * Obtiene las acciones tomadas para resolver la falla encontrada.
         *
         * @return las acciones tomadas.
         */
        public String getAccionesTomadas() { return accionesTomadas; }

        /**
         * Devuelve una representación en cadena de la falla encontrada.
         *
         * @return una cadena con el formato "FallaEncontrada{idFalla=..., descripcionFalla='...', ...}".
         */
        @Override
        public String toString() {
            return "FallaEncontrada{" +
                    "idFalla=" + idFalla +
                    ", descripcionFalla='" + descripcionFalla + '\'' +
                    ", causas='" + causas + '\'' +
                    ", accionesTomadas='" + accionesTomadas + '\'' +
                    '}';
        }
    }

    // Constructor base

    /**
     * Constructor base que inicializa una orden de trabajo con sus datos esenciales.
     * El estado inicial es {@code PENDIENTE}, y las listas de fallas se inicializan vacías.
     *
     * @param id identificador único de la orden.
     * @param idEquipo identificador del equipo asociado.
     * @param fechaOrden fecha en que se generó la orden.
     * @param fechaEjecucion fecha programada para su ejecución.
     */
    public OrdenTrabajo(int id, int idEquipo, LocalDate fechaOrden, LocalDate fechaEjecucion) {
        this.id = id;
        this.idEquipo = idEquipo;
        this.fechaOrden = fechaOrden;
        this.fechaEjecucion = fechaEjecucion;
        this.estado = EstadoOrden.PENDIENTE;
        // --- Inicializar las dos listas ---
        this.fallasReportadas = new ArrayList<>();
        this.fallasEncontradas = new ArrayList<>();
        // ---
    }

    // Getters (viejos y nuevos)

    /**
     * Obtiene el identificador único de la orden.
     *
     * @return el ID de la orden.
     */
    public int getId() { return id; }

    /**
     * Obtiene el identificador del equipo asociado a esta orden.
     *
     * @return el ID del equipo.
     */
    public int getIdEquipo() { return idEquipo; }

    /**
     * Obtiene la fecha en que se generó la orden.
     *
     * @return la fecha de generación.
     */
    public LocalDate getFechaOrden() { return fechaOrden; }

    /**
     * Obtiene la fecha programada para la ejecución de la orden.
     *
     * @return la fecha de ejecución.
     */
    public LocalDate getFechaEjecucion() { return fechaEjecucion; }

    /**
     * Obtiene las observaciones iniciales asociadas a la orden.
     *
     * @return las observaciones.
     */
    public String getObservaciones() { return observaciones; }

    /**
     * Obtiene la fecha real en que comenzó la ejecución (puede ser {@code null} si no ha iniciado).
     *
     * @return la fecha de inicio real.
     */
    public LocalDate getFechaInicioReal() { return fechaInicioReal; }

    /**
     * Obtiene la fecha real en que finalizó la ejecución (puede ser {@code null} si no ha terminado).
     *
     * @return la fecha de fin real.
     */
    public LocalDate getFechaFinReal() { return fechaFinReal; }

    /**
     * Obtiene el número de horas de trabajo registradas al finalizar.
     *
     * @return las horas de trabajo (puede incluir fracciones).
     */
    public float getHorasTrabajo() { return horasTrabajo; }

    /**
     * Obtiene el costo asociado a la mano de obra.
     *
     * @return el costo de mano de obra.
     */
    public int getCostoManoObra() { return costoManoObra; }

    /**
     * Obtiene el costo asociado a los materiales utilizados.
     *
     * @return el costo de materiales.
     */
    public int getCostoMateriales() { return costoMateriales; }

    /**
     * Obtiene las observaciones registradas al finalizar la orden.
     *
     * @return las observaciones de ejecución.
     */
    public String getObservacionesEjecucion() { return observacionesEjecucion; }

    /**
     * Obtiene el estado actual de la orden.
     *
     * @return el estado (PENDIENTE, EN_PROGRESO, COMPLETADA o CANCELADA).
     */
    public EstadoOrden getEstado() { return estado; }

    /**
     * Obtiene la fecha en que se canceló la orden (puede ser {@code null} si no fue cancelada).
     *
     * @return la fecha de cancelación.
     */
    public LocalDate getFechaCancelacion() { return fechaCancelacion; }

    /**
     * Obtiene el motivo por el cual se canceló la orden.
     *
     * @return el motivo de cancelación, o {@code null} si no aplica.
     */
    public String getMotivoCancelacion() { return motivoCancelacion; }

    // --- Getters para las nuevas listas ---

    /**
     * Obtiene una copia de la lista de fallas reportadas inicialmente.
     * Se devuelve una copia para proteger la encapsulación del estado interno.
     *
     * @return una nueva lista con las fallas reportadas.
     */
    public List<FallaReportada> getFallasReportadas() { return new ArrayList<>(fallasReportadas); }

    /**
     * Obtiene una copia de la lista de fallas encontradas durante la ejecución.
     * Se devuelve una copia para proteger la encapsulación del estado interno.
     *
     * @return una nueva lista con las fallas encontradas.
     */
    public List<FallaEncontrada> getFallasEncontradas() { return new ArrayList<>(fallasEncontradas); }


    /**
     * Establece la lista de fallas encontradas. Si se pasa {@code null}, se inicializa una lista vacía.
     *
     * @param nuevasFallas la nueva lista de fallas encontradas.
     */
    public void setFallasEncontradas(List<FallaEncontrada> nuevasFallas) {
        this.fallasEncontradas = nuevasFallas != null ? nuevasFallas : new ArrayList<>();
    }

    /**
     * Establece el estado actual de la orden.
     *
     * @param estado el nuevo estado.
     */
    public void setEstado(EstadoOrden estado)
    {
        this.estado = estado;
    }

    /**
     * Establece la fecha real de inicio de la ejecución.
     *
     * @param fechaInicioReal la fecha en que realmente comenzó el trabajo.
     */
    public void setFechaInicioReal(LocalDate fechaInicioReal) {
        this.fechaInicioReal = fechaInicioReal;
    }

    /**
     * Establece la fecha real de finalización de la ejecución.
     *
     * @param fechaFinReal la fecha en que realmente terminó el trabajo.
     */
    public void setFechaFinReal(LocalDate fechaFinReal) {
        this.fechaFinReal = fechaFinReal;
    }

    /**
     * Establece el número de horas de trabajo dedicadas a la orden.
     *
     * @param horasTrabajo las horas trabajadas (puede incluir decimales).
     */
    public void setHorasTrabajo(float horasTrabajo) {
        this.horasTrabajo = horasTrabajo;
    }

    /**
     * Establece el costo asociado a la mano de obra.
     *
     * @param costoManoObra el nuevo costo de mano de obra.
     */
    public void setCostoManoObra(int costoManoObra) {
        this.costoManoObra = costoManoObra;
    }

    /**
     * Establece el costo asociado a los materiales utilizados.
     *
     * @param costoMateriales el nuevo costo de materiales.
     */
    public void setCostoMateriales(int costoMateriales) {
        this.costoMateriales = costoMateriales;
    }

    /**
     * Establece las observaciones registradas al finalizar la orden.
     *
     * @param observacionesEjecucion las observaciones finales.
     */
    public void setObservacionesEjecucion(String observacionesEjecucion) {
        this.observacionesEjecucion = observacionesEjecucion;
    }

    /**
     * Establece la fecha de cancelación de la orden.
     *
     * @param fechaCancelacion la fecha en que se canceló.
     */
    public void setFechaCancelacion(LocalDate fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    /**
     * Establece el motivo de la cancelación de la orden.
     *
     * @param motivoCancelacion la razón por la cual se canceló.
     */
    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    // --- Setter para la lista de fallas reportadas (por si acaso) ---

    /**
     * Establece la lista de fallas reportadas. Si se pasa {@code null}, se inicializa una lista vacía.
     *
     * @param nuevasFallas la nueva lista de fallas reportadas.
     */
    public void setFallasReportadas(List<FallaReportada> nuevasFallas) {
        this.fallasReportadas = nuevasFallas != null ? nuevasFallas : new ArrayList<>();
    }


    // Setters para observaciones

    /**
     * Establece las observaciones iniciales de la orden.
     *
     * @param observaciones las observaciones a registrar.
     */
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // Métodos comunes

    /**
     * Inicia la orden de trabajo, cambiando su estado a {@code EN_PROGRESO} y registrando
     * la fecha de inicio real, siempre que su estado actual sea {@code PENDIENTE}.
     *
     * @param fechaInicio la fecha en que comienza la ejecución.
     */
    public void iniciar(LocalDate fechaInicio) {
        if (estado == EstadoOrden.PENDIENTE) {
            this.fechaInicioReal = fechaInicio;
            this.estado = EstadoOrden.EN_PROGRESO;
        }
    }

    /**
     * Finaliza la orden de trabajo, actualizando todos los datos de cierre y cambiando
     * su estado a {@code COMPLETADA}, siempre que esté en estado {@code EN_PROGRESO}.
     *
     * @param fechaFin fecha real de finalización.
     * @param horasTrabajo horas dedicadas al trabajo.
     * @param costoManoObra costo de la mano de obra.
     * @param costoMateriales costo de los materiales usados.
     * @param observaciones observaciones finales sobre la ejecución.
     */
    public void finalizar(LocalDate fechaFin, float horasTrabajo, int costoManoObra, int costoMateriales, String observaciones) {
        if (estado == EstadoOrden.EN_PROGRESO) {
            this.fechaFinReal = fechaFin;
            this.horasTrabajo = horasTrabajo;
            this.costoManoObra = costoManoObra;
            this.costoMateriales = costoMateriales;
            this.observacionesEjecucion = observaciones;
            this.estado = EstadoOrden.COMPLETADA;
        }
    }

    // --- Nuevos métodos para registrar los dos tipos de fallas ---

    /**
     * Registra una falla reportada (sin ID) en la orden, siempre que no esté cancelada.
     *
     * @param causas descripción de las causas de la falla.
     * @param accionesTomadas acciones ya tomadas o planeadas.
     */
    public void registrarFallaReportada(String causas, String accionesTomadas) {
        if (estado != EstadoOrden.CANCELADA) {
            fallasReportadas.add(new FallaReportada(causas, accionesTomadas));
        }
    }

    /**
     * Registra una falla encontrada durante la ejecución, asociada a una falla maestra por su ID,
     * siempre que la orden no esté cancelada.
     *
     * @param idFalla identificador de la falla en la lista maestra.
     * @param descripcionFalla descripción textual de la falla.
     * @param causas causas identificadas durante la intervención.
     * @param accionesTomadas acciones realizadas para corregirla.
     */
    public void registrarFallaEncontrada(int idFalla, String descripcionFalla, String causas, String accionesTomadas) {
        if (estado != EstadoOrden.CANCELADA) {
            fallasEncontradas.add(new FallaEncontrada(idFalla, descripcionFalla, causas, accionesTomadas));
        }
    }
    // ---


    /**
     * Cancela la orden de trabajo, estableciendo la fecha y el motivo, y cambiando su estado
     * a {@code CANCELADA}.
     *
     * @param fecha fecha en que se cancela la orden.
     * @param motivo razón por la cual se cancela.
     */
    public void cancelar(LocalDate fecha, String motivo) {
        this.fechaCancelacion = fecha;
        this.motivoCancelacion = motivo;
        this.estado = EstadoOrden.CANCELADA;
    }
}