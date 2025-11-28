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
    @Expose protected List<FallaObservada> fallasObservadas;

    public enum EstadoOrden {
        PENDIENTE,
        EN_PROGRESO,
        COMPLETADA,
        CANCELADA
    }

    public static class FallaObservada {
        @Expose private int idFalla;
        @Expose private String descripcionFalla;
        @Expose private String causas;
        @Expose private String accionesTomadas;

        public FallaObservada(int idFalla, String descripcionFalla, String causas, String accionesTomadas) {
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
    }

    // Constructor base
    public OrdenTrabajo(int id, int idEquipo, LocalDate fechaOrden, LocalDate fechaEjecucion) {
        this.id = id;
        this.idEquipo = idEquipo;
        this.fechaOrden = fechaOrden;
        this.fechaEjecucion = fechaEjecucion;
        this.estado = EstadoOrden.PENDIENTE;
        this.fallasObservadas = new ArrayList<>();
    }

    // Getters
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
    public List<FallaObservada> getFallasObservadas() { return new ArrayList<>(fallasObservadas); }

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
}