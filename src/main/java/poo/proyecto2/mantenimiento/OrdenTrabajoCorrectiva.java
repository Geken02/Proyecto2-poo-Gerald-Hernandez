package poo.proyecto2.mantenimiento;

import java.time.LocalDate;

public class OrdenTrabajoCorrectiva extends OrdenTrabajo {
    // No requiere campos adicionales según la descripción.
    // Todos los campos necesarios ya están en la clase base: id, idEquipo, fechas, fallas, etc.

    public OrdenTrabajoCorrectiva(int id, int idEquipo, LocalDate fechaOrden, LocalDate fechaEjecucion) {
        super(id, idEquipo, fechaOrden, fechaEjecucion);
    }
}