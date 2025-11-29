package poo.proyecto2.sistema;

import com.google.gson.reflect.TypeToken;
import poo.proyecto2.equipos.*;
import poo.proyecto2.mantenimiento.*;
import poo.proyecto2.util.JsonUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.lang.reflect.Type;
import poo.proyecto2.EstadoEquipo;

public class SistemaPrincipal {
    private List<Equipos> bosque;
    private Map<Integer, ProgramaMantenimientoPreventivo> programasMantenimiento;
    private Map<Integer, TareaMantenimiento> tareasMaestras;
    private Map<Integer, FallaEquipo> fallasMaestras;
    private List<OrdenTrabajo> listaOrdenes;
    private int siguienteIdEquipo;
    private int siguienteIdTarea;
    private int siguienteIdFalla;
    private int siguienteIdOrden;
    private static final String RUTA_EQUIPOS = "datos/ME_Equipos.json";
    private static final String RUTA_PROGRAMAS = "datos/ME_Programas.json";
    private static final String RUTA_TAREAS = "datos/ME_Tareas.json";
    private static final String RUTA_FALLAS = "datos/ME_Fallas.json";
    private static final String RUTA_ORDENES = "datos/ME_Ordenes.json";

    public SistemaPrincipal() {
        bosque = new ArrayList<>();
        programasMantenimiento = new HashMap<>();
        tareasMaestras = new HashMap<>();
        fallasMaestras = new HashMap<>();
        listaOrdenes = new ArrayList<>();

        try {
            new java.io.File("datos").mkdirs();

            // Cargar con métodos genéricos de JsonUtils
            Type tipoArboles = new TypeToken<List<Equipos>>(){}.getType();
            bosque = JsonUtils.cargarListaGenerico(RUTA_EQUIPOS, tipoArboles);
            siguienteIdEquipo = calcularSiguienteIdEquipo();

            Type tipoProgramas = new TypeToken<List<ProgramaMantenimientoPreventivo>>(){}.getType();
            List<ProgramaMantenimientoPreventivo> programas = JsonUtils.cargarListaGenerico(RUTA_PROGRAMAS, tipoProgramas);
            for (ProgramaMantenimientoPreventivo p : programas) {
                programasMantenimiento.put(p.getIdEquipo(), p);
            }

            Type tipoTareas = new TypeToken<List<TareaMantenimiento>>(){}.getType();
            List<TareaMantenimiento> tareas = JsonUtils.cargarListaGenerico(RUTA_TAREAS, tipoTareas);
            for (TareaMantenimiento t : tareas) {
                tareasMaestras.put(t.getId(), t);
            }
            siguienteIdTarea = calcularSiguienteIdTarea();

            Type tipoFallas = new TypeToken<List<FallaEquipo>>(){}.getType();
            List<FallaEquipo> fallas = JsonUtils.cargarListaGenerico(RUTA_FALLAS, tipoFallas);
            for (FallaEquipo f : fallas) {
                fallasMaestras.put(f.getId(), f);
            }
            siguienteIdFalla = calcularSiguienteIdFalla();

            Type tipoOrdenes = new TypeToken<List<OrdenTrabajo>>(){}.getType();
            listaOrdenes = JsonUtils.cargarListaConHerencia(RUTA_ORDENES, tipoOrdenes); // Usa el Gson con herencia
            siguienteIdOrden = calcularSiguienteIdOrden();

        } catch (IOException e) {
            siguienteIdEquipo = 1;
            siguienteIdTarea = 1;
            siguienteIdFalla = 1;
            siguienteIdOrden = 1;
        }
    }

    // --- MÉTODOS PRIVADOS DE APOYO ---
    private int calcularSiguienteIdEquipo() {
        int max = 0;
        for (Equipos arbol : bosque) {
            max = Math.max(max, buscarMaxIdEquipo(arbol.getRaiz()));
        }
        return max + 1;
    }

    private int buscarMaxIdEquipo(NodoEquipo nodo) {
        if (nodo == null) return 0;
        int max = nodo.getId();
        max = Math.max(max, buscarMaxIdEquipo(nodo.getPrimerHijo()));
        max = Math.max(max, buscarMaxIdEquipo(nodo.getSiguienteHermano()));
        return max;
    }

    private int calcularSiguienteIdTarea() {
        return tareasMaestras.isEmpty() ? 1 : tareasMaestras.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }

    private int calcularSiguienteIdFalla() {
        return fallasMaestras.isEmpty() ? 1 : fallasMaestras.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }

    private int calcularSiguienteIdOrden() {
        return listaOrdenes.isEmpty() ? 1 : listaOrdenes.stream().mapToInt(OrdenTrabajo::getId).max().orElse(0) + 1;
    }

    // --- MÉTODOS GUI: CRUD EQUIPOS ---
    /**
     * Crea un nuevo equipo. Si equipoPrincipal es 0, se crea un nuevo árbol.
     * Si equipoPrincipal != 0, se crea como hijo del equipo con ese ID.
     */
    public NodoEquipo crearEquipo(int equipoPrincipal, String descripcion, String tipo, String ubicacion,
                                  String fabricante, String serie, LocalDate fechaAdquisicion,
                                  LocalDate fechaPuestaEnServicio, int mesesVidaUtil,
                                  EstadoEquipo estado, double costoInicial) {
        NodoEquipo nuevo = new NodoEquipo(siguienteIdEquipo++, equipoPrincipal, descripcion, tipo, ubicacion,
                fabricante, serie, fechaAdquisicion, fechaPuestaEnServicio,
                mesesVidaUtil, estado, costoInicial);

        if (equipoPrincipal == 0) {
            // Es raíz de un nuevo árbol
            bosque.add(new Equipos(nuevo));
        } else {
            // Es hijo de un equipo existente
            NodoEquipo padre = buscarEquipoPorId(equipoPrincipal);
            if (padre == null) {
                throw new IllegalArgumentException("Equipo padre no encontrado: " + equipoPrincipal);
            }
            if (padre.getPrimerHijo() == null) {
                padre.setPrimerHijo(nuevo);
            } else {
                NodoEquipo hermano = padre.getPrimerHijo();
                while (hermano.getSiguienteHermano() != null) {
                    hermano = hermano.getSiguienteHermano();
                }
                hermano.setSiguienteHermano(nuevo);
            }
        }
        return nuevo;
    }

    /**
     * Actualiza los datos de un equipo existente.
     */
    public boolean actualizarEquipo(int id, String descripcion, String tipo, String ubicacion,
                                    String fabricante, String serie, LocalDate fechaAdquisicion,
                                    LocalDate fechaPuestaEnServicio, int mesesVidaUtil,
                                    EstadoEquipo estado, double costoInicial,
                                    String especificacionesTecnicas, String informacionGarantia) {
        NodoEquipo equipo = buscarEquipoPorId(id);
        if (equipo == null) return false;

        equipo.setDescripcion(descripcion);
        equipo.setTipo(tipo);
        equipo.setUbicacion(ubicacion);
        equipo.setFabricante(fabricante);
        equipo.setSerie(serie);
        equipo.setFechaAdquisicion(fechaAdquisicion);
        equipo.setFechaPuestaEnServicio(fechaPuestaEnServicio);
        equipo.setMesesVidaUtil(mesesVidaUtil);
        equipo.setEstado(estado);
        equipo.setCostoInicial(costoInicial);
        equipo.setEspecificacionesTecnicas(especificacionesTecnicas);
        equipo.setInformacionGarantia(informacionGarantia);

        return true;
    }

    /**
     * Elimina un equipo y todos sus subequipos (eliminación recursiva).
     */
    public boolean eliminarEquipo(int id) {
        for (Equipos arbol : bosque) {
            if (arbol.getRaiz().getId() == id) {
                bosque.remove(arbol);
                // Opcional: Eliminar también su programa y órdenes asociadas
                eliminarProgramaYOrdenes(id);
                return true;
            }
            if (eliminarEnArbol(arbol.getRaiz(), id)) {
                // Opcional: Eliminar también su programa y órdenes asociadas
                eliminarProgramaYOrdenes(id);
                return true;
            }
        }
        return false;
    }

    private boolean eliminarEnArbol(NodoEquipo nodo, int id) {
        if (nodo == null) return false;

        if (nodo.getPrimerHijo() != null && nodo.getPrimerHijo().getId() == id) {
            nodo.setPrimerHijo(eliminarSubarbol(nodo.getPrimerHijo()));
            return true;
        }

        NodoEquipo hermano = nodo.getPrimerHijo();
        while (hermano != null) {
            if (hermano.getSiguienteHermano() != null && hermano.getSiguienteHermano().getId() == id) {
                hermano.setSiguienteHermano(eliminarSubarbol(hermano.getSiguienteHermano()));
                return true;
            }
            hermano = hermano.getSiguienteHermano();
        }

        return eliminarEnArbol(nodo.getPrimerHijo(), id) || eliminarEnArbol(nodo.getSiguienteHermano(), id);
    }

    private NodoEquipo eliminarSubarbol(NodoEquipo nodo) {
        // Este método "desconecta" el subárbol
        return null;
    }

    private void eliminarProgramaYOrdenes(int idEquipo) {
        // Eliminar programa asociado
        programasMantenimiento.remove(idEquipo);
        // Eliminar órdenes asociadas
        listaOrdenes.removeIf(orden -> orden.getIdEquipo() == idEquipo);
    }

    /**
     * Obtiene todos los equipos en una lista plana.
     */
    public List<NodoEquipo> obtenerTodosLosEquipos() {
        List<NodoEquipo> todos = new ArrayList<>();
        for (Equipos arbol : bosque) {
            recolectarNodos(arbol.getRaiz(), todos);
        }
        return todos;
    }

    private void recolectarNodos(NodoEquipo nodo, List<NodoEquipo> lista) {
        if (nodo == null) return;
        lista.add(nodo);
        recolectarNodos(nodo.getPrimerHijo(), lista);
        recolectarNodos(nodo.getSiguienteHermano(), lista);
    }

    /**
     * Busca un equipo por ID.
     */
    public NodoEquipo buscarEquipoPorId(int id) {
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

    // --- MÉTODOS GUI: LISTA DE TAREAS DE UN EQUIPO ---
    /**
     * Obtiene una lista plana de tareas maestras asociadas a un equipo a través de su programa.
     */
    public List<TareaMantenimiento> obtenerTareasDeEquipo(int idEquipo) {
        ProgramaMantenimientoPreventivo programa = obtenerProgramaDeEquipo(idEquipo);
        if (programa == null) return new ArrayList<>();

        List<TareaMantenimiento> tareas = new ArrayList<>();
        for (FaseMantenimiento fase : programa.getFases()) {
            for (int idTarea : fase.getIdsTareasMaestras()) {
                TareaMantenimiento tarea = buscarTareaMaestraPorId(idTarea);
                if (tarea != null) {
                    tareas.add(tarea);
                }
            }
        }
        return tareas;
    }

    // --- MÉTODOS GUI: PROGRAMAS DE MANTENIMIENTO ---
    public ProgramaMantenimientoPreventivo obtenerProgramaDeEquipo(int idEquipo) {
        return programasMantenimiento.get(idEquipo);
    }

    // --- MÉTODOS GUI: TAREAS MAESTRAS ---
    public List<TareaMantenimiento> obtenerTodasLasTareasMaestras() {
        return new ArrayList<>(tareasMaestras.values());
    }

    public TareaMantenimiento buscarTareaMaestraPorId(int id) {
        return tareasMaestras.get(id);
    }

    // --- MÉTODOS GUI: FALLAS MAESTRAS ---
    public List<FallaEquipo> obtenerTodasLasFallasMaestras() {
        return new ArrayList<>(fallasMaestras.values());
    }

    public FallaEquipo buscarFallaMaestraPorId(int id) {
        return fallasMaestras.get(id);
    }

    // --- MÉTODOS GUI: ÓRDENES DE TRABAJO ---

    // --- ORDENES PREVENTIVAS ---
    /**
     * Genera una nueva orden de trabajo preventiva basada en una fase del programa de un equipo.
     * (Lógica de generación futura: calcular fechaEjecucion según programa)
     */
    public OrdenTrabajoPreventiva generarOrdenPreventiva(int idEquipo, int idFase, LocalDate fechaOrden, LocalDate fechaEjecucion) {
        // Por ahora, creamos una orden vacía o con tareas copiadas del programa.
        OrdenTrabajoPreventiva orden = new OrdenTrabajoPreventiva(siguienteIdOrden++, idEquipo, idFase, fechaOrden, fechaEjecucion);

        ProgramaMantenimientoPreventivo prog = obtenerProgramaDeEquipo(idEquipo);
        if (prog != null) {
            FaseMantenimiento fase = null;
            for (FaseMantenimiento f : prog.getFases()) {
                // Suponemos que idFase es el índice o ID interno de la fase, o se busca por otro criterio
                // Por simplicidad, asumiremos que idFase es el índice (0, 1, 2...)
                if (prog.getFases().indexOf(f) == idFase) {
                    fase = f;
                    break;
                }
            }
            if (fase != null) {
                for (int idTarea : fase.getIdsTareasMaestras()) {
                    TareaMantenimiento tarea = buscarTareaMaestraPorId(idTarea);
                    if (tarea != null) {
                        orden.agregarTarea(tarea); // Asumiendo que OrdenTrabajoPreventiva tiene este método
                    }
                }
            }
        }
        listaOrdenes.add(orden);
        return orden;
    }

    /**
     * Consulta todas las órdenes preventivas (o filtra por equipo, estado, etc.)
     */
    public List<OrdenTrabajoPreventiva> consultarOrdenesPreventivas() {
        return listaOrdenes.stream()
                .filter(orden -> orden instanceof OrdenTrabajoPreventiva)
                .map(orden -> (OrdenTrabajoPreventiva) orden)
                .collect(Collectors.toList());
    }

    /**
     * Consulta las órdenes preventivas de un equipo específico.
     */
    public List<OrdenTrabajoPreventiva> consultarOrdenesPreventivasPorEquipo(int idEquipo) {
        return listaOrdenes.stream()
                .filter(orden -> orden instanceof OrdenTrabajoPreventiva && orden.getIdEquipo() == idEquipo)
                .map(orden -> (OrdenTrabajoPreventiva) orden)
                .collect(Collectors.toList());
    }

    /**
     * Modifica los datos generales de una orden preventiva (antes de iniciar).
     * (No se permite modificar tareas directamente aquí, eso es parte de la lógica de generación).
     */
    public boolean modificarOrdenPreventiva(int idOrden, String observaciones) {
        OrdenTrabajoPreventiva orden = (OrdenTrabajoPreventiva) buscarOrdenPorId(idOrden);
        if (orden != null && orden.getEstado() == OrdenTrabajo.EstadoOrden.PENDIENTE) {
            orden.setObservaciones(observaciones);
            return true;
        }
        return false;
    }

    /**
     * Registra el inicio de una orden preventiva.
     */
    public boolean iniciarOrdenPreventiva(int idOrden, LocalDate fechaInicio) {
        OrdenTrabajoPreventiva orden = (OrdenTrabajoPreventiva) buscarOrdenPorId(idOrden);
        if (orden != null) {
            orden.iniciar(fechaInicio);
            return true;
        }
        return false;
    }

    /**
     * Registra la finalización de una orden preventiva.
     */
    public boolean finalizarOrdenPreventiva(int idOrden, LocalDate fechaFin, float horasTrabajo, int costoManoObra, int costoMateriales, String observaciones) {
        OrdenTrabajoPreventiva orden = (OrdenTrabajoPreventiva) buscarOrdenPorId(idOrden);
        if (orden != null) {
            orden.finalizar(fechaFin, horasTrabajo, costoManoObra, costoMateriales, observaciones);
            return true;
        }
        return false;
    }

    /**
     * Cancela una orden preventiva.
     */
    public boolean cancelarOrdenPreventiva(int idOrden, LocalDate fechaCancelacion, String motivo) {
        OrdenTrabajoPreventiva orden = (OrdenTrabajoPreventiva) buscarOrdenPorId(idOrden);
        if (orden != null) {
            orden.cancelar(fechaCancelacion, motivo);
            return true;
        }
        return false;
    }

    // --- REVISIÓN DE FALLAS (Consultar fallas en órdenes) ---
    /**
     * Obtiene todas las fallas reportadas y encontradas en todas las órdenes.
     */
    public List<OrdenTrabajo.FallaReportada> obtenerFallasReportadasEnOrdenes() {
        List<OrdenTrabajo.FallaReportada> todas = new ArrayList<>();
        for (OrdenTrabajo orden : listaOrdenes) {
            todas.addAll(orden.getFallasReportadas());
        }
        return todas;
    }

    /**
     * Obtiene todas las fallas nuevas encontradas en todas las órdenes.
     */
    public List<OrdenTrabajo.FallaEncontrada> obtenerFallasEncontradasEnOrdenes() {
        List<OrdenTrabajo.FallaEncontrada> todas = new ArrayList<>();
        for (OrdenTrabajo orden : listaOrdenes) {
            todas.addAll(orden.getFallasEncontradas());
        }
        return todas;
    }

    // --- ORDENES CORRECTIVAS ---
    /**
     * Registra una nueva orden de trabajo correctiva.
     */
    public OrdenTrabajoCorrectiva registrarOrdenCorrectiva(int idEquipo, LocalDate fechaOrden, LocalDate fechaEjecucion, String observaciones) {
        OrdenTrabajoCorrectiva orden = new OrdenTrabajoCorrectiva(siguienteIdOrden++, idEquipo, fechaOrden, fechaEjecucion);
        orden.setObservaciones(observaciones);
        listaOrdenes.add(orden);
        return orden;
    }

    /**
     * Consulta todas las órdenes correctivas (o filtra por equipo, estado, etc.)
     */
    public List<OrdenTrabajoCorrectiva> consultarOrdenesCorrectivas() {
        return listaOrdenes.stream()
                .filter(orden -> orden instanceof OrdenTrabajoCorrectiva)
                .map(orden -> (OrdenTrabajoCorrectiva) orden)
                .collect(Collectors.toList());
    }

    /**
     * Consulta las órdenes correctivas de un equipo específico.
     */
    public List<OrdenTrabajoCorrectiva> consultarOrdenesCorrectivasPorEquipo(int idEquipo) {
        return listaOrdenes.stream()
                .filter(orden -> orden instanceof OrdenTrabajoCorrectiva && orden.getIdEquipo() == idEquipo)
                .map(orden -> (OrdenTrabajoCorrectiva) orden)
                .collect(Collectors.toList());
    }

    /**
     * Registra el inicio de una orden correctiva.
     */
    public boolean iniciarOrdenCorrectiva(int idOrden, LocalDate fechaInicio) {
        OrdenTrabajoCorrectiva orden = (OrdenTrabajoCorrectiva) buscarOrdenPorId(idOrden);
        if (orden != null) {
            orden.iniciar(fechaInicio);
            return true;
        }
        return false;
    }

    /**
     * Registra la finalización de una orden correctiva.
     */
    public boolean finalizarOrdenCorrectiva(int idOrden, LocalDate fechaFin, float horasTrabajo, int costoManoObra, int costoMateriales, String observaciones) {
        OrdenTrabajoCorrectiva orden = (OrdenTrabajoCorrectiva) buscarOrdenPorId(idOrden);
        if (orden != null) {
            orden.finalizar(fechaFin, horasTrabajo, costoManoObra, costoMateriales, observaciones);
            return true;
        }
        return false;
    }

    /**
     * Cancela una orden correctiva.
     */
    public boolean cancelarOrdenCorrectiva(int idOrden, LocalDate fechaCancelacion, String motivo) {
        OrdenTrabajoCorrectiva orden = (OrdenTrabajoCorrectiva) buscarOrdenPorId(idOrden);
        if (orden != null) {
            orden.cancelar(fechaCancelacion, motivo);
            return true;
        }
        return false;
    }

    // --- MÉTODOS GENERALES PARA ÓRDENES ---
    /**
     * Busca una orden de trabajo (preventiva o correctiva) por ID.
     */
    public OrdenTrabajo buscarOrdenPorId(int id) {
        for (OrdenTrabajo orden : listaOrdenes) {
            if (orden.getId() == id) {
                return orden;
            }
        }
        return null;
    }

    /**
     * Obtiene todas las órdenes de trabajo (preventivas y correctivas).
     */
    public List<OrdenTrabajo> obtenerTodasLasOrdenes() {
        return new ArrayList<>(listaOrdenes);
    }

    /**
     * Obtiene las órdenes de trabajo asociadas a un equipo específico.
     */
    public List<OrdenTrabajo> obtenerOrdenesPorEquipo(int idEquipo) {
        return listaOrdenes.stream()
                .filter(orden -> orden.getIdEquipo() == idEquipo)
                .collect(Collectors.toList());
    }


    // --- MÉTODOS CRUD: TAREAS MAESTRAS ---
    /**
     * Crea una nueva tarea maestra y la agrega al sistema.
     */
    public TareaMantenimiento crearTareaMaestra(String descripcion) {
        TareaMantenimiento nuevaTarea = new TareaMantenimiento(siguienteIdTarea++, descripcion);
        tareasMaestras.put(nuevaTarea.getId(), nuevaTarea);
        return nuevaTarea;
    }

    // --- MÉTODOS CRUD: FALLAS MAESTRAS ---
    /**
     * Crea una nueva falla maestra y la agrega al sistema.
     */
    public FallaEquipo crearFallaMaestra(String descripcion) {
        FallaEquipo nuevaFalla = new FallaEquipo(siguienteIdFalla++, descripcion);
        fallasMaestras.put(nuevaFalla.getId(), nuevaFalla);
        return nuevaFalla;
    }

    // ... (resto de métodos) ...

    // --- PERSISTENCIA ---
    public void guardar() throws IOException {
        // Guardar con métodos genéricos de JsonUtils
        Type tipoArboles = new TypeToken<List<Equipos>>(){}.getType();
        JsonUtils.guardarListaGenerico(bosque, tipoArboles, RUTA_EQUIPOS);

        List<ProgramaMantenimientoPreventivo> listaProg = new ArrayList<>(programasMantenimiento.values());
        Type tipoProgramas = new TypeToken<List<ProgramaMantenimientoPreventivo>>(){}.getType();
        JsonUtils.guardarListaGenerico(listaProg, tipoProgramas, RUTA_PROGRAMAS);

        Type tipoTareas = new TypeToken<List<TareaMantenimiento>>(){}.getType();
        JsonUtils.guardarListaGenerico(new ArrayList<>(tareasMaestras.values()), tipoTareas, RUTA_TAREAS);

        Type tipoFallas = new TypeToken<List<FallaEquipo>>(){}.getType();
        JsonUtils.guardarListaGenerico(new ArrayList<>(fallasMaestras.values()), tipoFallas, RUTA_FALLAS);

        Type tipoOrdenes = new TypeToken<List<OrdenTrabajo>>(){}.getType();
        JsonUtils.guardarListaConHerencia(listaOrdenes, tipoOrdenes, RUTA_ORDENES); // Usa el Gson con herencia
    }
}