package poo.proyecto2.mantenimiento;

import java.time.LocalDate;

public class OrdenTrabajoCorrectiva extends OrdenTrabajo {


    public OrdenTrabajoCorrectiva(int id, int idEquipo, LocalDate fechaOrden, LocalDate fechaEjecucion) {
        super(id, idEquipo, fechaOrden, fechaEjecucion);
    }

    // ... (otros métodos) ...

    @Override
    public String toString() {
        return "Orden Correctiva #" + getId() + " - " + getEstado();
    }
}