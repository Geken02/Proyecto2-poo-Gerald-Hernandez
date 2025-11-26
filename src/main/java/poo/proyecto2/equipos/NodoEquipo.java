package poo.proyecto2.equipos;

import java.time.LocalDate;
import com.google.gson.annotations.Expose;
import poo.proyecto2.EstadoEquipo;

public class NodoEquipo {

    @Expose private int id;
    @Expose private int equipoPrincipal; 
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

    public NodoEquipo() {}

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

    // Getters
    public int getId() { return id; }
    public int getEquipoPrincipal() { return equipoPrincipal; }
    public boolean esRaiz() { return equipoPrincipal == 0; } 

    public NodoEquipo getPrimerHijo() { return primerHijo; }
    public NodoEquipo getSiguienteHermano() { return siguienteHermano; }

    public void setPrimerHijo(NodoEquipo h) { this.primerHijo = h; }
    public void setSiguienteHermano(NodoEquipo h) { this.siguienteHermano = h; }
}