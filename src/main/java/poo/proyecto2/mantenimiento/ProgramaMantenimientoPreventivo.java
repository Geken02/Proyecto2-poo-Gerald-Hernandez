package poo.proyecto2.mantenimiento;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;

public class ProgramaMantenimientoPreventivo {
    @Expose private int idEquipo; // <-- Añadido @Expose
    @Expose private List<FaseMantenimiento> fases; // <-- Añadido @Expose

    // Constructor que recibe idEquipo
    public ProgramaMantenimientoPreventivo(int idEquipo) {
        this.idEquipo = idEquipo;
        this.fases = new ArrayList<>();
    }

    // Constructor vacío necesario para Gson
    public ProgramaMantenimientoPreventivo() {
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