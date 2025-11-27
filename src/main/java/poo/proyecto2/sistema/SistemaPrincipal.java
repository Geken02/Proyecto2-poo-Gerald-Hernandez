package poo.proyecto2.sistema;

import poo.proyecto2.equipos.*;
import poo.proyecto2.EstadoEquipo;
import poo.proyecto2.util.JsonUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;



public class SistemaPrincipal {
    private List<Equipos> bosque;
    private int siguienteId;
    private static final String RUTA = "datos/ME_Equipos.json";

    public SistemaPrincipal() {
        bosque = new ArrayList<>();
        try {
            new java.io.File("datos").mkdirs();
            bosque = JsonUtils.cargarArboles(RUTA);
            siguienteId = calcularSiguienteId();
        } catch (IOException e) {
            siguienteId = 1;
        }
    }

    private int calcularSiguienteId() {
        int max = 0;
        for (Equipos arbol : bosque) {
            max = Math.max(max, buscarMaxId(arbol.getRaiz()));
        }
        return max + 1;
    }

    private int buscarMaxId(NodoEquipo nodo) {
        if (nodo == null) return 0;
        int max = nodo.getId();
        max = Math.max(max, buscarMaxId(nodo.getPrimerHijo()));
        max = Math.max(max, buscarMaxId(nodo.getSiguienteHermano()));
        return max;
    }

    // === CREAR RAÍZ (nuevo árbol) ===
    public NodoEquipo crearRaiz(String descripcion, String tipo, String ubicacion,
                                String fabricante, String serie, LocalDate fechaAdquisicion,
                                LocalDate fechaPuestaEnServicio, int mesesVidaUtil,
                                EstadoEquipo estado, double costoInicial) {
        NodoEquipo raiz = new NodoEquipo(siguienteId++, 0, descripcion, tipo, ubicacion,
                fabricante, serie, fechaAdquisicion, fechaPuestaEnServicio,
                mesesVidaUtil, estado, costoInicial);
        bosque.add(new Equipos(raiz));
        return raiz;
    }

    // === CREAR HIJO (bajo cualquier nodo existente) ===
    public NodoEquipo crearHijo(int idPadre, String descripcion, String tipo, String ubicacion,
                                String fabricante, String serie, LocalDate fechaAdquisicion,
                                LocalDate fechaPuestaEnServicio, int mesesVidaUtil,
                                EstadoEquipo estado, double costoInicial) {
        NodoEquipo padre = buscarNodoGlobal(idPadre);
        if (padre == null) {
            throw new IllegalArgumentException("Padre no encontrado: " + idPadre);
        }
        NodoEquipo hijo = new NodoEquipo(siguienteId++, idPadre, descripcion, tipo, ubicacion,
                fabricante, serie, fechaAdquisicion, fechaPuestaEnServicio,
                mesesVidaUtil, estado, costoInicial);

        if (padre.getPrimerHijo() == null) {
            padre.setPrimerHijo(hijo);
        } else {
            NodoEquipo hermano = padre.getPrimerHijo();
            while (hermano.getSiguienteHermano() != null) {
                hermano = hermano.getSiguienteHermano();
            }
            hermano.setSiguienteHermano(hijo);
        }
        return hijo;
    }

    // Busca en todos los árboles
    private NodoEquipo buscarNodoGlobal(int id) {
        for (Equipos arbol : bosque) {
            NodoEquipo encontrado = buscarEnArbol(arbol.getRaiz(), id);
            if (encontrado != null) return encontrado;
        }
        return null;
    }

    private NodoEquipo buscarEnArbol(NodoEquipo nodo, int id) {
        if (nodo == null) return null;
        if (nodo.getId() == id) return nodo;
        NodoEquipo enHijo = buscarEnArbol(nodo.getPrimerHijo(), id);
        if (enHijo != null) return enHijo;
        return buscarEnArbol(nodo.getSiguienteHermano(), id);
    }

    public void guardar() throws IOException {
        JsonUtils.guardarArboles(bosque, RUTA);
    }

    public List<Equipos> getBosque() { return new ArrayList<>(bosque); }
}