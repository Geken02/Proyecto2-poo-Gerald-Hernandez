package poo.proyecto2.modelo.mantenimiento;

import java.time.LocalDate;

/**
 * Representa una orden de trabajo correctiva, es decir, una intervención realizada
 * para corregir fallas o problemas no planificados en un equipo.
 * 
 * <p>Esta clase hereda de {@link OrdenTrabajo} y no introduce campos adicionales,
 * pero puede extenderse en el futuro para incluir comportamientos específicos
 * del mantenimiento correctivo.</p>
 */
public class OrdenTrabajoCorrectiva extends OrdenTrabajo {

    /**
     * Constructor que inicializa una orden de trabajo correctiva con los datos esenciales.
     * Llama al constructor de la clase base {@link OrdenTrabajo}.
     *
     * @param id identificador único de la orden.
     * @param idEquipo identificador del equipo asociado.
     * @param fechaOrden fecha en que se generó la orden.
     * @param fechaEjecucion fecha programada para su ejecución.
     */
    public OrdenTrabajoCorrectiva(int id, int idEquipo, LocalDate fechaOrden, LocalDate fechaEjecucion) {
        super(id, idEquipo, fechaOrden, fechaEjecucion);
    }


    /**
     * Devuelve una representación en cadena de la orden correctiva,
     * mostrando su identificador y estado actual.
     *
     * @return una cadena con el formato "Orden Correctiva #ID - ESTADO".
     */
    @Override
    public String toString() {
        return "Orden Correctiva #" + getId() + " - " + getEstado();
    }
}