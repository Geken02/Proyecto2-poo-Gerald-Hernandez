package poo.proyecto2.mantenimiento;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class FaseMantenimiento {
    @Expose private TipoFrecuencia tipoFrecuencia;
    @Expose private int medidorFrecuencia;
    @Expose private int cantidadCiclos;
    @Expose private List<Integer> idsTareasMaestras; 
    @Expose private String partes;
    @Expose private String herramientas;
    @Expose private String personal;
    @Expose private float horasEstimadas;

    public FaseMantenimiento(TipoFrecuencia tipoFrecuencia, int medidorFrecuencia, int cantidadCiclos,
                             String partes, String herramientas, String personal, float horasEstimadas) {
        this.tipoFrecuencia = tipoFrecuencia;
        this.medidorFrecuencia = medidorFrecuencia;
        this.cantidadCiclos = cantidadCiclos;
        this.idsTareasMaestras = new ArrayList<>(); 
        this.partes = partes;
        this.herramientas = herramientas;
        this.personal = personal;
        this.horasEstimadas = horasEstimadas;
    }

    // Getters
    public TipoFrecuencia getTipoFrecuencia() { return tipoFrecuencia; }
    public int getMedidorFrecuencia() { return medidorFrecuencia; }
    public int getCantidadCiclos() { return cantidadCiclos; }
    public List<Integer> getIdsTareasMaestras() { return new ArrayList<>(idsTareasMaestras); } // Devolver copia
    public String getPartes() { return partes; }
    public String getHerramientas() { return herramientas; }
    public String getPersonal() { return personal; }
    public float getHorasEstimadas() { return horasEstimadas; }

    // Método para añadir una tarea (por ID)
    public void agregarTareaMaestra(int idTarea) {
        idsTareasMaestras.add(idTarea);
    }

    // ... (otros métodos) ...

    @Override
    public String toString() {
        return "Fase: " + tipoFrecuencia + " cada " + medidorFrecuencia + " " + tipoFrecuencia;
    }
}