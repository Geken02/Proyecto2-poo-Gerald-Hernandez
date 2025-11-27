package poo.proyecto2.mantenimiento;

import java.util.ArrayList;
import java.util.List;

public class ProgramaMantenimientoPreventivo {
    private int idEquipo;
    private List<FaseMantenimiento> fases;

    public ProgramaMantenimientoPreventivo(int idEquipo) {
        this.idEquipo = idEquipo;
        this.fases = new ArrayList<>();
    }

    public void agregarFase(FaseMantenimiento fase) {
        fases.add(fase);
    }

    // Getters
    public int getIdEquipo() { return idEquipo; }
    public List<FaseMantenimiento> getFases() { return new ArrayList<>(fases); }

    @Override
    public String toString() {
        return "ProgramaMantenimientoPreventivo{" +
                "idEquipo=" + idEquipo +
                ", fases=" + fases +
                '}';
    }
}