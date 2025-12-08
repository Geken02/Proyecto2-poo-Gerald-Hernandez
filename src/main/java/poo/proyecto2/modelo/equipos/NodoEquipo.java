package poo.proyecto2.modelo.equipos;


import java.time.LocalDate;
import com.google.gson.annotations.Expose;
import poo.proyecto2.modelo.EstadoEquipo;

/**
 * Representa un nodo en una estructura de árbol jerárquico de equipos.
 * Cada nodo contiene información detallada sobre un equipo técnico o de mantenimiento,
 * incluyendo datos como identificación, descripción, ubicación, fechas relevantes,
 * estado, costo y especificaciones técnicas.
 * 
 * <p>La estructura del árbol se implementa mediante la representación "primer hijo / siguiente hermano",
 * donde {@code primerHijo} apunta al primer subequipo y {@code siguienteHermano} al siguiente equipo
 * al mismo nivel jerárquico.</p>
 * 
 * <p>Esta clase está diseñada para ser serializable a JSON mediante la biblioteca Gson,
 * gracias a la anotación {@code @Expose} en todos sus campos relevantes.</p>
 */
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

    /**
     * Constructor por defecto necesario para la deserialización con Gson.
     */
    public NodoEquipo() {}

    /**
     * Constructor completo para la creación manual de nodos en memoria.
     * Inicializa los campos esenciales del equipo, excluyendo especificaciones técnicas
     * y garantía, que pueden establecerse posteriormente.
     *
     * @param id identificador único del equipo.
     * @param equipoPrincipal ID del equipo padre; 0 indica que es la raíz.
     * @param descripcion descripción breve del equipo.
     * @param tipo tipo o categoría del equipo.
     * @param ubicacion ubicación física del equipo.
     * @param fabricante nombre del fabricante.
     * @param serie número de serie del equipo.
     * @param fechaAdquisicion fecha en que se adquirió el equipo.
     * @param fechaPuestaEnServicio fecha en que se puso en operación.
     * @param mesesVidaUtil vida útil estimada en meses.
     * @param estado estado actual del equipo.
     * @param costoInicial costo de adquisición del equipo.
     */
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

    /**
     * Obtiene el identificador único del equipo.
     *
     * @return el ID del equipo.
     */
    public int getId() { return id; }

    /**
     * Obtiene el ID del equipo padre. Un valor de 0 indica que este nodo es la raíz.
     *
     * @return el ID del equipo principal (padre).
     */
    public int getEquipoPrincipal() { return equipoPrincipal; }

    /**
     * Obtiene la descripción del equipo.
     *
     * @return la descripción del equipo.
     */
    public String getDescripcion() { return descripcion; }

    /**
     * Obtiene el tipo o categoría del equipo.
     *
     * @return el tipo del equipo.
     */
    public String getTipo() { return tipo; }

    /**
     * Obtiene la ubicación física del equipo.
     *
     * @return la ubicación del equipo.
     */
    public String getUbicacion() { return ubicacion; }

    /**
     * Obtiene el nombre del fabricante del equipo.
     *
     * @return el fabricante del equipo.
     */
    public String getFabricante() { return fabricante; }

    /**
     * Obtiene el número de serie del equipo.
     *
     * @return el número de serie.
     */
    public String getSerie() { return serie; }

    /**
     * Obtiene la fecha de adquisición del equipo.
     *
     * @return la fecha de adquisición.
     */
    public LocalDate getFechaAdquisicion() { return fechaAdquisicion; }

    /**
     * Obtiene la fecha en que el equipo fue puesto en servicio.
     *
     * @return la fecha de puesta en servicio.
     */
    public LocalDate getFechaPuestaEnServicio() { return fechaPuestaEnServicio; }

    /**
     * Obtiene la vida útil estimada del equipo en meses.
     *
     * @return la vida útil en meses.
     */
    public int getMesesVidaUtil() { return mesesVidaUtil; }

    /**
     * Obtiene el estado actual del equipo.
     *
     * @return el estado del equipo.
     */
    public EstadoEquipo getEstado() { return estado; }

    /**
     * Obtiene el costo inicial (de adquisición) del equipo.
     *
     * @return el costo inicial.
     */
    public double getCostoInicial() { return costoInicial; }

    /**
     * Obtiene las especificaciones técnicas del equipo.
     *
     * @return las especificaciones técnicas, o {@code null} si no se han definido.
     */
    public String getEspecificacionesTecnicas() { return especificacionesTecnicas; }

    /**
     * Obtiene la información de garantía del equipo.
     *
     * @return la información de garantía, o {@code null} si no se ha definido.
     */
    public String getInformacionGarantia() { return informacionGarantia; }

    /**
     * Obtiene el primer hijo (primer subequipo directo) de este nodo.
     *
     * @return el primer hijo, o {@code null} si no tiene subequipos.
     */
    public NodoEquipo getPrimerHijo() { return primerHijo; }

    /**
     * Obtiene el siguiente hermano (siguiente equipo al mismo nivel jerárquico).
     *
     * @return el siguiente hermano, o {@code null} si es el último en su nivel.
     */
    public NodoEquipo getSiguienteHermano() { return siguienteHermano; }

    // --- SETTERS (todos los necesarios para el main) ---

    /**
     * Establece las especificaciones técnicas del equipo.
     *
     * @param especificacionesTecnicas las especificaciones técnicas a asignar.
     */
    public void setEspecificacionesTecnicas(String especificacionesTecnicas) {
        this.especificacionesTecnicas = especificacionesTecnicas;
    }

    /**
     * Establece la información de garantía del equipo.
     *
     * @param informacionGarantia la información de garantía a asignar.
     */
    public void setInformacionGarantia(String informacionGarantia) {
        this.informacionGarantia = informacionGarantia;
    }

    /**
     * Establece el primer hijo (primer subequipo) de este nodo.
     *
     * @param primerHijo el nodo que será el primer hijo.
     */
    public void setPrimerHijo(NodoEquipo primerHijo) {
        this.primerHijo = primerHijo;
    }

    /**
     * Establece el siguiente hermano de este nodo en la estructura de árbol.
     *
     * @param siguienteHermano el nodo que será el siguiente hermano.
     */
    public void setSiguienteHermano(NodoEquipo siguienteHermano) {
        this.siguienteHermano = siguienteHermano;
    }

    // (Los demás campos rara vez se modifican tras la creación, pero si los necesitas, aquí van)

    /**
     * Modifica la descripción del equipo.
     *
     * @param descripcion la nueva descripción.
     */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * Modifica el tipo del equipo.
     *
     * @param tipo el nuevo tipo.
     */
    public void setTipo(String tipo) { this.tipo = tipo; }

    /**
     * Modifica la ubicación del equipo.
     *
     * @param ubicacion la nueva ubicación.
     */
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    /**
     * Modifica el fabricante del equipo.
     *
     * @param fabricante el nuevo fabricante.
     */
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    /**
     * Modifica el número de serie del equipo.
     *
     * @param serie el nuevo número de serie.
     */
    public void setSerie(String serie) { this.serie = serie; }

    /**
     * Modifica la fecha de adquisición del equipo.
     *
     * @param fechaAdquisicion la nueva fecha de adquisición.
     */
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) { this.fechaAdquisicion = fechaAdquisicion; }

    /**
     * Modifica la fecha de puesta en servicio del equipo.
     *
     * @param fechaPuestaEnServicio la nueva fecha de puesta en servicio.
     */
    public void setFechaPuestaEnServicio(LocalDate fechaPuestaEnServicio) { this.fechaPuestaEnServicio = fechaPuestaEnServicio; }

    /**
     * Modifica la vida útil estimada del equipo en meses.
     *
     * @param mesesVidaUtil la nueva vida útil en meses.
     */
    public void setMesesVidaUtil(int mesesVidaUtil) { this.mesesVidaUtil = mesesVidaUtil; }

    /**
     * Modifica el estado actual del equipo.
     *
     * @param estado el nuevo estado.
     */
    public void setEstado(EstadoEquipo estado) { this.estado = estado; }

    /**
     * Modifica el costo inicial del equipo.
     *
     * @param costoInicial el nuevo costo inicial.
     */
    public void setCostoInicial(double costoInicial) { this.costoInicial = costoInicial; }

  

    /**
     * Devuelve una representación en cadena del nodo, mostrando únicamente
     * su identificador y descripción.
     *
     * @return una cadena con el formato "{id} {descripcion}".
     */
    @Override
    public String toString() {
        return id + " " + descripcion; // Muestra ID y Descripción
    }
}