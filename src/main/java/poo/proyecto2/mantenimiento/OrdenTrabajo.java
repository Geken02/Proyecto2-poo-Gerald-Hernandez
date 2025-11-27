package poo.proyecto2.mantenimiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class OrdenTrabajo {
    @Expose private int id;
    @Expose private int idEquipo;
    @Expose private int idFase;
    @Expose private LocalDate fechaEjecucion;
    @Expose private LocalDate fechaInicioReal;
    @Expose private LocalDate fechaFinReal;
    @Expose private float horasTrabajo;
    @Expose private int costoManoObra;
    @Expose private int costoMateriales;
    @Expose private String observaciones;
    @Expose private List<TareaMantenimiento> tareas;
    @Expose private EstadoOrden estado;

    // --- Nuevos campos ---
    // Cambio: Ahora cada falla observada incluye la descripción de la falla
    @Expose private List<FallaObservada> fallasObservadas; // Lista de fallas registradas en esta orden
    @Expose private LocalDate fechaCancelacion;
    @Expose private String motivoCancelacion;
    // --- Fin nuevos campos ---

    public enum EstadoOrden {
        PENDIENTE,
        EN_PROGRESO,
        COMPLETADA,
        CANCELADA
    }

    // Clase interna para encapsular la falla observada
    // Cumple con: "cada falla debe tener: Falla (identificación de la falla según lista de fallas), Causas, Acciones tomadas"
    public static class FallaObservada {
        @Expose private int idFalla; // Identificación de la falla según lista de fallas (como solicitaste)
        @Expose private String descripcionFalla; // Descripción de la falla (la identificación en texto)
        @Expose private String causas; // Causas observadas
        @Expose private String accionesTomadas; // Acciones tomadas

        public FallaObservada(int idFalla, String descripcionFalla, String causas, String accionesTomadas) {
            this.idFalla = idFalla;
            this.descripcionFalla = descripcionFalla;
            this.causas = causas;
            this.accionesTomadas = accionesTomadas;
        }

        // Getters
        public int getIdFalla() { return idFalla; }
        public String getDescripcionFalla() { return descripcionFalla; } // <-- Nuevo getter
        public String getCausas() { return causas; }
        public String getAccionesTomadas() { return accionesTomadas; }

        @Override
        public String toString() {
            return "FallaObservada{" +
                    "idFalla=" + idFalla +
                    ", descripcionFalla='" + descripcionFalla + '\'' +
                    ", causas='" + causas + '\'' +
                    ", accionesTomadas='" + accionesTomadas + '\'' +
                    '}';
        }
    }

    public OrdenTrabajo(int id, int idEquipo, int idFase, LocalDate fechaEjecucion) {
        this.id = id;
        this.idEquipo = idEquipo;
        this.idFase = idFase;
        this.fechaEjecucion = fechaEjecucion;
        this.tareas = new ArrayList<>();
        this.fallasObservadas = new ArrayList<>(); // Inicializar lista
        this.estado = EstadoOrden.PENDIENTE;
    }

    // Getters (viejos y nuevos)
    public int getId() { return id; }
    public int getIdEquipo() { return idEquipo; }
    public int getIdFase() { return idFase; }
    public LocalDate getFechaEjecucion() { return fechaEjecucion; }
    public LocalDate getFechaInicioReal() { return fechaInicioReal; }
    public LocalDate getFechaFinReal() { return fechaFinReal; }
    public float getHorasTrabajo() { return horasTrabajo; }
    public int getCostoManoObra() { return costoManoObra; }
    public int getCostoMateriales() { return costoMateriales; }
    public String getObservaciones() { return observaciones; }
    public List<TareaMantenimiento> getTareas() { return new ArrayList<>(tareas); }
    public EstadoOrden getEstado() { return estado; }
    // --- Getters nuevos ---
    public List<FallaObservada> getFallasObservadas() { return new ArrayList<>(fallasObservadas); }
    public LocalDate getFechaCancelacion() { return fechaCancelacion; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    // ---

    // Métodos para registrar ejecución
    public void iniciar(LocalDate fechaInicio) {
        if (estado == EstadoOrden.PENDIENTE) {
            this.fechaInicioReal = fechaInicio;
            this.estado = EstadoOrden.EN_PROGRESO;
        }
    }

    public void finalizar(LocalDate fechaFin, float horasTrabajo, int costoManoObra, int costoMateriales, String observaciones) {
        if (estado == EstadoOrden.EN_PROGRESO) {
            this.fechaFinReal = fechaFin;
            this.horasTrabajo = horasTrabajo;
            this.costoManoObra = costoManoObra;
            this.costoMateriales = costoMateriales;
            this.observaciones = observaciones;
            this.estado = EstadoOrden.COMPLETADA;
        }
    }

    public void agregarTarea(TareaMantenimiento tarea) {
        tareas.add(tarea);
    }

    // --- Nuevos métodos para manejo de fallas y cancelación ---
    // Ahora el constructor de FallaObservada incluye la descripción, cumpliendo con el requisito.
    public void registrarFalla(int idFalla, String descripcionFalla, String causas, String accionesTomadas) {
        if (estado != EstadoOrden.CANCELADA) {
            fallasObservadas.add(new FallaObservada(idFalla, descripcionFalla, causas, accionesTomadas));
        }
    }

    public void cancelar(LocalDate fecha, String motivo) {
        this.fechaCancelacion = fecha;
        this.motivoCancelacion = motivo;
        this.estado = EstadoOrden.CANCELADA;
    }
    // ---


    @Override
    public String toString() {
        return "OrdenTrabajo{" +
                "id=" + id +
                ", idEquipo=" + idEquipo +
                ", idFase=" + idFase +
                ", fechaEjecucion=" + fechaEjecucion +
                ", estado=" + estado +
                ", tareas=" + tareas +
                ", fallasObservadas=" + fallasObservadas +
                ", fechaCancelacion=" + fechaCancelacion +
                ", motivoCancelacion='" + motivoCancelacion + '\'' +
                '}';
    }
}