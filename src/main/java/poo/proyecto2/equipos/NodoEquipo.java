package poo.proyecto2.equipos;


import java.time.LocalDate;
import com.google.gson.annotations.Expose;
import poo.proyecto2.EstadoEquipo;

public class NodoEquipo {
    @Expose private int id;
    @Expose private int equipoPrincipal; // 0 = raíz
    @Expose private String descripcion;
    @Expose private String tipo;
    @Expose private String ubicacion;
    @Expose private String fabricante;
    @Expose private String serie;
    @Expose private LocalDate fechaAdquisicion;
    @Expose private LocalDate fechaPuestaEnServicio;
    @Expose private int mesesVidaUtil;
    @Expose private EstadoEquipo estado;
    @Expose private double costoInicial;
    @Expose private String especificacionesTecnicas;
    @Expose private String informacionGarantia;

    @Expose private NodoEquipo primerHijo;
    @Expose private NodoEquipo siguienteHermano;

    // Constructor vacío (para Gson)
    public NodoEquipo() {}

    // Constructor para creación en memoria
    public NodoEquipo(int id, int equipoPrincipal, String descripcion, String tipo, String ubicacion,
                      String fabricante, String serie, LocalDate fechaAdquisicion,
                      LocalDate fechaPuestaEnServicio, int mesesVidaUtil,
                      EstadoEquipo estado, double costoInicial) {
        this.id = id;
        this.equipoPrincipal = equipoPrincipal;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.ubicacion = ubicacion;
        this.fabricante = fabricante;
        this.serie = serie;
        this.fechaAdquisicion = fechaAdquisicion;
        this.fechaPuestaEnServicio = fechaPuestaEnServicio;
        this.mesesVidaUtil = mesesVidaUtil;
        this.estado = estado;
        this.costoInicial = costoInicial;
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public int getEquipoPrincipal() { return equipoPrincipal; }
    public String getDescripcion() { return descripcion; }
    public String getTipo() { return tipo; }
    public String getUbicacion() { return ubicacion; }
    public String getFabricante() { return fabricante; }
    public String getSerie() { return serie; }
    public LocalDate getFechaAdquisicion() { return fechaAdquisicion; }
    public LocalDate getFechaPuestaEnServicio() { return fechaPuestaEnServicio; }
    public int getMesesVidaUtil() { return mesesVidaUtil; }
    public EstadoEquipo getEstado() { return estado; }
    public double getCostoInicial() { return costoInicial; }
    public String getEspecificacionesTecnicas() { return especificacionesTecnicas; }
    public String getInformacionGarantia() { return informacionGarantia; }
    public NodoEquipo getPrimerHijo() { return primerHijo; }
    public NodoEquipo getSiguienteHermano() { return siguienteHermano; }

    // --- SETTERS (todos los necesarios para el main) ---
    public void setEspecificacionesTecnicas(String especificacionesTecnicas) {
        this.especificacionesTecnicas = especificacionesTecnicas;
    }

    public void setInformacionGarantia(String informacionGarantia) {
        this.informacionGarantia = informacionGarantia;
    }

    public void setPrimerHijo(NodoEquipo primerHijo) {
        this.primerHijo = primerHijo;
    }

    public void setSiguienteHermano(NodoEquipo siguienteHermano) {
        this.siguienteHermano = siguienteHermano;
    }

    // (Los demás campos rara vez se modifican tras la creación, pero si los necesitas, aquí van)
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    public void setSerie(String serie) { this.serie = serie; }
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) { this.fechaAdquisicion = fechaAdquisicion; }
    public void setFechaPuestaEnServicio(LocalDate fechaPuestaEnServicio) { this.fechaPuestaEnServicio = fechaPuestaEnServicio; }
    public void setMesesVidaUtil(int mesesVidaUtil) { this.mesesVidaUtil = mesesVidaUtil; }
    public void setEstado(EstadoEquipo estado) { this.estado = estado; }
    public void setCostoInicial(double costoInicial) { this.costoInicial = costoInicial; }
}