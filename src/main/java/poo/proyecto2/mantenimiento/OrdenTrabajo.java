package poo.proyecto2.mantenimiento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

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

    public enum EstadoOrden {
        PENDIENTE,
        EN_PROGRESO,
        COMPLETADA,
        CANCELADA
    }

    // Clase interna para fallas reportadas inicialmente (solo causas y acciones)
    public static class FallaReportada {
        @Expose private String causas;
        @Expose private String accionesTomadas;

        public FallaReportada(String causas, String accionesTomadas) {
            this.causas = causas;
            this.accionesTomadas = accionesTomadas;
        }

        // Getters
        public String getCausas() { return causas; }
        public String getAccionesTomadas() { return accionesTomadas; }

        @Override
        public String toString() {
            return "FallaReportada{" +
                    "causas='" + causas + '\'' +
                    ", accionesTomadas='" + accionesTomadas + '\'' +
                    '}';
        }
    }

    // Clase interna para fallas nuevas encontradas durante el trabajo (con ID de la falla maestra)
    public static class FallaEncontrada {
        @Expose private int idFalla; // Identificación de la falla según lista de fallas
        @Expose private String descripcionFalla; // Descripción de la falla (la identificación en texto)
        @Expose private String causas;
        @Expose private String accionesTomadas;

        public FallaEncontrada(int idFalla, String descripcionFalla, String causas, String accionesTomadas) {
            this.idFalla = idFalla;
            this.descripcionFalla = descripcionFalla;
            this.causas = causas;
            this.accionesTomadas = accionesTomadas;
        }

        // Getters
        public int getIdFalla() { return idFalla; }
        public String getDescripcionFalla() { return descripcionFalla; }
        public String getCausas() { return causas; }
        public String getAccionesTomadas() { return accionesTomadas; }

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
    public int getId() { return id; }
    public int getIdEquipo() { return idEquipo; }
    public LocalDate getFechaOrden() { return fechaOrden; }
    public LocalDate getFechaEjecucion() { return fechaEjecucion; }
    public String getObservaciones() { return observaciones; }
    public LocalDate getFechaInicioReal() { return fechaInicioReal; }
    public LocalDate getFechaFinReal() { return fechaFinReal; }
    public float getHorasTrabajo() { return horasTrabajo; }
    public int getCostoManoObra() { return costoManoObra; }
    public int getCostoMateriales() { return costoMateriales; }
    public String getObservacionesEjecucion() { return observacionesEjecucion; }
    public EstadoOrden getEstado() { return estado; }
    public LocalDate getFechaCancelacion() { return fechaCancelacion; }
    public String getMotivoCancelacion() { return motivoCancelacion; }

    // --- Getters para las nuevas listas ---
    public List<FallaReportada> getFallasReportadas() { return new ArrayList<>(fallasReportadas); }
    public List<FallaEncontrada> getFallasEncontradas() { return new ArrayList<>(fallasEncontradas); }


    public void setFallasEncontradas(List<FallaEncontrada> nuevasFallas) {
        this.fallasEncontradas = nuevasFallas != null ? nuevasFallas : new ArrayList<>();
    }
    public void setEstado(EstadoOrden estado)
    {
        this.estado = estado;
    }

    public void setFechaInicioReal(LocalDate fechaInicioReal) {
        this.fechaInicioReal = fechaInicioReal;
    }

    public void setFechaFinReal(LocalDate fechaFinReal) {
        this.fechaFinReal = fechaFinReal;
    }

    public void setHorasTrabajo(float horasTrabajo) {
        this.horasTrabajo = horasTrabajo;
    }

    public void setCostoManoObra(int costoManoObra) {
        this.costoManoObra = costoManoObra;
    }

    public void setCostoMateriales(int costoMateriales) {
        this.costoMateriales = costoMateriales;
    }

    public void setObservacionesEjecucion(String observacionesEjecucion) {
        this.observacionesEjecucion = observacionesEjecucion;
    }

    public void setFechaCancelacion(LocalDate fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    // --- Setter para la lista de fallas reportadas (por si acaso) ---
    public void setFallasReportadas(List<FallaReportada> nuevasFallas) {
        this.fallasReportadas = nuevasFallas != null ? nuevasFallas : new ArrayList<>();
    }


    // Setters para observaciones
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // Métodos comunes
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
            this.observacionesEjecucion = observaciones;
            this.estado = EstadoOrden.COMPLETADA;
        }
    }

    // --- Nuevos métodos para registrar los dos tipos de fallas ---
    public void registrarFallaReportada(String causas, String accionesTomadas) {
        if (estado != EstadoOrden.CANCELADA) {
            fallasReportadas.add(new FallaReportada(causas, accionesTomadas));
        }
    }

    public void registrarFallaEncontrada(int idFalla, String descripcionFalla, String causas, String accionesTomadas) {
        if (estado != EstadoOrden.CANCELADA) {
            fallasEncontradas.add(new FallaEncontrada(idFalla, descripcionFalla, causas, accionesTomadas));
        }
    }
    // ---


    public void cancelar(LocalDate fecha, String motivo) {
        this.fechaCancelacion = fecha;
        this.motivoCancelacion = motivo;
        this.estado = EstadoOrden.CANCELADA;
    }
}