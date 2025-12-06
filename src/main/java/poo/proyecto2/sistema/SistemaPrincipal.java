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
    private Map<Integer, Map<Integer, LocalDate>> ultimasFechasOrdenGenerada;
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
        Type tipoArboles = new TypeToken<List<Equipos>>(){}.getType(); // Asegúrate de usar EquipoArbol
        bosque = JsonUtils.cargarListaGenerico(RUTA_EQUIPOS, tipoArboles);
        siguienteIdEquipo = calcularSiguienteIdEquipo();

        Type tipoProgramas = new TypeToken<List<ProgramaMantenimientoPreventivo>>(){}.getType();
        List<ProgramaMantenimientoPreventivo> programas = JsonUtils.cargarListaGenerico(RUTA_PROGRAMAS, tipoProgramas);
        System.out.println("DEBUG SP: Cargados " + programas.size() + " programas desde JSON.");
        for (ProgramaMantenimientoPreventivo p : programas) {
            System.out.println("DEBUG SP: Procesando programa con idEquipo (antes de put): " + p.getIdEquipo() + ", toString: " + p);
            programasMantenimiento.put(p.getIdEquipo(), p);
        }
        Type tipoTareas = new TypeToken<List<TareaMantenimiento>>(){}.getType(); // Asegúrate de usar TareaMantenimientoMaestra
        List<TareaMantenimiento> tareas = JsonUtils.cargarListaGenerico(RUTA_TAREAS, tipoTareas);
        for (TareaMantenimiento t : tareas) {
            tareasMaestras.put(t.getId(), t); // Asocia por id
        }
        siguienteIdTarea = calcularSiguienteIdTarea();

        Type tipoFallas = new TypeToken<List<FallaEquipo>>(){}.getType();
        List<FallaEquipo> fallas = JsonUtils.cargarListaGenerico(RUTA_FALLAS, tipoFallas);
        for (FallaEquipo f : fallas) {
            fallasMaestras.put(f.getId(), f); // Asocia por id
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
    generarOrdenesAutomaticamente();

    // --- MENSAJES DE DEPURACIÓN AL FINAL DEL CONSTRUCTOR ---
    System.out.println("DEBUG SP: Carga inicial completada.");
    System.out.println("DEBUG SP: Numero de árboles de equipos: " + bosque.size());
    System.out.println("DEBUG SP: Numero de programas de mantenimiento: " + programasMantenimiento.size());
    System.out.println("DEBUG SP: Numero de tareas maestras: " + tareasMaestras.size());
    System.out.println("DEBUG SP: Numero de fallas maestras: " + fallasMaestras.size());
    System.out.println("DEBUG SP: Numero de órdenes de trabajo: " + listaOrdenes.size());

    // Opcional: Imprimir IDs de los programas cargados
    System.out.println("DEBUG SP: IDs de equipos con programa:");
    for (Integer id : programasMantenimiento.keySet()) {
        System.out.println("  - " + id);
    }

    // Opcional: Imprimir IDs de las órdenes cargadas
    System.out.println("DEBUG SP: IDs de órdenes de trabajo:");
    for (OrdenTrabajo orden : listaOrdenes) {
        System.out.println("  - ID: " + orden.getId() + ", Equipo: " + orden.getIdEquipo() + ", Tipo: " + (orden instanceof OrdenTrabajoPreventiva ? "Preventiva" : "Correctiva"));
    }
    // ---
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

        // --- GUARDAR CAMBIOS DESPUÉS DE MODIFICAR EL BOSQUE ---
        try {
            guardar();
            System.out.println("DEBUG SP: Equipo ID " + nuevo.getId() + " guardado en " + RUTA_EQUIPOS);
        } catch (IOException e) {
            System.err.println("ERROR SP: No se pudo guardar el equipo ID " + nuevo.getId() + ". Error: " + e.getMessage());
            e.printStackTrace();
            // Opcional: Revertir la operación si falla el guardado
            // return null;
            throw new RuntimeException("No se pudo persistir el equipo creado.", e);
        }
        // ---

        return nuevo;
    }

    // Para actualizarEquipo, haz lo mismo al final del método:
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

        // --- GUARDAR CAMBIOS DESPUÉS DE ACTUALIZAR ---
        try {
            guardar();
            System.out.println("DEBUG SP: Equipo ID " + equipo.getId() + " actualizado y guardado en " + RUTA_EQUIPOS);
        } catch (IOException e) {
            System.err.println("ERROR SP: No se pudo guardar la actualización del equipo ID " + equipo.getId() + ". Error: " + e.getMessage());
            e.printStackTrace();
            return false; 
        }
        // ---

        return true;
    }

    /**
     * Elimina un equipo y todos sus subequipos (eliminación recursiva).
     */
    public boolean eliminarEquipo(int id) {
        // Verificar si tiene órdenes asociadas
        List<OrdenTrabajo> ordenes = obtenerOrdenesPorEquipo(id);
        if (!ordenes.isEmpty()) {
            System.out.println("DEBUG SP: No se puede eliminar el equipo con ID " + id + " porque tiene órdenes asociadas.");
            return false;
        }

        for (int i = 0; i < bosque.size(); i++) {
            Equipos arbol = bosque.get(i);
            if (arbol.getRaiz().getId() == id) {
                bosque.remove(i);
                eliminarProgramaYOrdenes(id); // Opcional: eliminar también su programa y órdenes si se decide hacerlo aquí
                // --- GUARDAR CAMBIOS DESPUÉS DE ELIMINAR ---
                try {
                    guardar();
                    System.out.println("DEBUG SP: Equipo raíz ID " + id + " eliminado y guardado en " + RUTA_EQUIPOS);
                    return true;
                } catch (IOException e) {
                    System.err.println("ERROR SP: No se pudo guardar la eliminación del equipo raíz ID " + id + ". Error: " + e.getMessage());
                    e.printStackTrace();
                    bosque.add(i, arbol); // Revertir si falla el guardado (opcional)
                    return false;
                }
                // ---
            }
            if (eliminarEnArbol(arbol.getRaiz(), id)) {
                eliminarProgramaYOrdenes(id); // Opcional
                // --- GUARDAR CAMBIOS DESPUÉS DE ELIMINAR ---
                try {
                    guardar();
                    System.out.println("DEBUG SP: Equipo ID " + id + " eliminado de un subárbol y guardado en " + RUTA_EQUIPOS);
                    return true;
                } catch (IOException e) {
                    System.err.println("ERROR SP: No se pudo guardar la eliminación del equipo ID " + id + " del subárbol. Error: " + e.getMessage());
                    e.printStackTrace();
                    // Revertir la eliminación si falla el guardado (más complejo)
                    return false;
                }
                // ---
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

    // Método actualizado para finalizar órdenes preventivas
    public boolean finalizarOrdenPreventiva(int idOrden, LocalDate fechaFin, float horasTrabajo, int costoManoObra, int costoMateriales, String observaciones, List<OrdenTrabajo.FallaEncontrada> nuevasFallas) {
        OrdenTrabajoPreventiva orden = (OrdenTrabajoPreventiva) buscarOrdenPorId(idOrden);
        if (orden != null && orden.getEstado() == OrdenTrabajo.EstadoOrden.EN_PROGRESO) { // Asumiendo que se inicia antes de finalizar
            orden.finalizar(fechaFin, horasTrabajo, costoManoObra, costoMateriales, observaciones);
            // Asignar las nuevas fallas encontradas a la orden
            orden.setFallasEncontradas(nuevasFallas);
            try {
                guardar(); // <-- Guardar cambios en disco
                System.out.println("DEBUG SP: Orden ID " + idOrden + " finalizada y guardada con nuevas fallas.");
                return true;
            } catch (IOException e) {
                System.err.println("ERROR SP: No se pudo guardar la orden ID " + idOrden + " tras finalizarla. Error: " + e.getMessage());
                e.printStackTrace();
                // Opcional: Revertir el estado y las fallas si falla el guardado
                return false;
            }
        }
        return false;
    }

    // En SistemaPrincipal.java

    // Método para iniciar una orden preventiva
    public boolean iniciarOrdenPreventiva(int idOrden, LocalDate fechaInicio) {
        System.out.println("DEBUG SP: Iniciando orden ID " + idOrden); // DEBUG
        OrdenTrabajoPreventiva orden = (OrdenTrabajoPreventiva) buscarOrdenPorId(idOrden);
        if (orden != null) {
            // Permitir iniciar si está en PENDIENTE
            if (orden.getEstado() == OrdenTrabajo.EstadoOrden.PENDIENTE) {
                orden.iniciar(fechaInicio);
                try {
                    guardar(); // <-- Guardar cambios en disco
                    System.out.println("DEBUG SP: Orden ID " + idOrden + " iniciada y guardada en " + RUTA_ORDENES);
                    return true;
                } catch (IOException e) {
                    System.err.println("ERROR SP: No se pudo guardar la orden ID " + idOrden + " tras iniciarla. Error: " + e.getMessage());
                    e.printStackTrace();
                    // Opcional: Revertir el estado si falla el guardado
                    orden.setEstado(OrdenTrabajo.EstadoOrden.PENDIENTE); // Revertir estado
                    return false;
                }
            } else {
                System.out.println("DEBUG SP: La orden ID " + idOrden + " no está en estado PENDIENTE. Estado actual: " + orden.getEstado()); // DEBUG
                return false; // No se puede iniciar si no está pendiente
            }
        } else {
            System.out.println("DEBUG SP: No se encontró la orden con ID " + idOrden); // DEBUG
            return false; // No existe la orden
        }
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

        // --- GUARDAR CAMBIOS DESPUÉS DE AÑADIR LA TAREA ---
        try {
            guardar();
            System.out.println("DEBUG SP: Tarea maestra ID " + nuevaTarea.getId() + " guardada en " + RUTA_TAREAS);
        } catch (IOException e) {
            System.err.println("ERROR SP: No se pudo guardar la tarea maestra ID " + nuevaTarea.getId() + ". Error: " + e.getMessage());
            e.printStackTrace();
            tareasMaestras.remove(nuevaTarea.getId()); // Revertir si falla el guardado (opcional)
            throw new RuntimeException("No se pudo persistir la tarea maestra creada.", e);
        }
        // ---

        return nuevaTarea;
    }

    // --- MÉTODOS CRUD: FALLAS MAESTRAS ---
    /**
     * Crea una nueva falla maestra y la agrega al sistema.
     */
    public FallaEquipo crearFallaMaestra(String descripcion) {
        FallaEquipo nuevaFalla = new FallaEquipo(siguienteIdFalla++, descripcion);
        fallasMaestras.put(nuevaFalla.getId(), nuevaFalla);

        // --- GUARDAR CAMBIOS DESPUÉS DE AÑADIR LA FALLA ---
        try {
            guardar();
            System.out.println("DEBUG SP: Falla maestra ID " + nuevaFalla.getId() + " guardada en " + RUTA_FALLAS);
        } catch (IOException e) {
            System.err.println("ERROR SP: No se pudo guardar la falla maestra ID " + nuevaFalla.getId() + ". Error: " + e.getMessage());
            e.printStackTrace();
            fallasMaestras.remove(nuevaFalla.getId()); 
            throw new RuntimeException("No se pudo persistir la falla maestra creada.", e);
        }
        // ---

        return nuevaFalla;
    }

    /**
     * Guarda un nuevo programa de mantenimiento preventivo o actualiza uno existente.
     * @param nuevoPrograma El programa a guardar.
     * @return true si se guardó exitosamente, false en caso contrario.
     */
    public boolean guardarPrograma(ProgramaMantenimientoPreventivo nuevoPrograma) {
        if (nuevoPrograma == null || nuevoPrograma.getIdEquipo() <= 0) {
            System.out.println("DEBUG SP: Intento de guardar un programa inválido o sin ID de equipo válido.");
            return false;
        }

        // Actualizar la lista interna: si ya existe un programa para este equipo, lo reemplaza
        programasMantenimiento.put(nuevoPrograma.getIdEquipo(), nuevoPrograma);

        // Crear una lista con todos los programas actuales del mapa
        List<ProgramaMantenimientoPreventivo> listaProgramasActualizada = new ArrayList<>(programasMantenimiento.values());

        try {
            // Usar el método de JsonUtils para guardar la lista completa de programas
            Type tipoListaProgramas = new TypeToken<List<ProgramaMantenimientoPreventivo>>(){}.getType();
            JsonUtils.guardarListaGenerico(listaProgramasActualizada, tipoListaProgramas, RUTA_PROGRAMAS);
            generarOrdenesAutomaticamente();
            System.out.println("DEBUG SP: Programa guardado para equipo ID: " + nuevoPrograma.getIdEquipo() + " en " + RUTA_PROGRAMAS);
            return true;
        } catch (IOException e) {
            System.err.println("ERROR SP: No se pudo guardar el programa en " + RUTA_PROGRAMAS + ". Error: " + e.getMessage());
            e.printStackTrace();
            // Opcional: revertir el cambio en el mapa si falla la escritura
            // programasMantenimiento.remove(nuevoPrograma.getIdEquipo());
            return false;
        }
    }

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

    // ... (otros métodos) ...

    /**
     * Genera automáticamente órdenes de trabajo preventivas para todos los equipos
     * según sus programas y fechas de puesta en servicio o finalización de órdenes anteriores.
     */
    public void generarOrdenesAutomaticamente() {
        System.out.println("DEBUG SP: Iniciando generación automática de órdenes de trabajo preventivas.");

        // Iterar sobre todos los árboles de equipos
        for (Equipos arbol : bosque) {
            // Para cada árbol, procesar recursivamente el equipo raíz
            procesarEquipoParaGenerarOrdenes(arbol.getRaiz());
        }

        System.out.println("DEBUG SP: Generación automática de órdenes finalizada.");
        // Opcional: Guardar las nuevas órdenes generadas
        try {
            guardar(); // <-- Guardar después de generar
            System.out.println("DEBUG SP: Nuevas órdenes generadas guardadas en disco.");
        } catch (IOException e) {
            System.err.println("ERROR SP: No se pudieron guardar las nuevas órdenes generadas. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Proceso recursivo para generar órdenes en un equipo y sus subcomponentes.
     */
    private void procesarEquipoParaGenerarOrdenes(NodoEquipo equipo) {
        if (equipo == null) return;

        // Buscar si el equipo tiene un programa de mantenimiento
        ProgramaMantenimientoPreventivo programa = programasMantenimiento.get(equipo.getId());
        if (programa != null) {
            System.out.println("DEBUG SP: Procesando equipo ID " + equipo.getId() + " con un programa de " + programa.getFases().size() + " fases.");
            // Iterar sobre las fases del programa
            for (int i = 0; i < programa.getFases().size(); i++) {
                FaseMantenimiento fase = programa.getFases().get(i);
                // Generar órdenes para esta fase específica del equipo
                generarOrdenesParaFase(equipo, fase, i);
            }
        }

        // Procesar recursivamente los hijos del equipo
        procesarEquipoParaGenerarOrdenes(equipo.getPrimerHijo());
        procesarEquipoParaGenerarOrdenes(equipo.getSiguienteHermano());
    }

    /**
     * Genera órdenes de trabajo para una fase específica de un equipo.
     * @param equipo El equipo al que pertenece la fase.
     * @param fase La fase del programa.
     * @param indiceFase El índice de la fase en la lista del programa (para poder asociar la orden generada correctamente).
     */
    private void generarOrdenesParaFase(NodoEquipo equipo, FaseMantenimiento fase, int indiceFase) {
        System.out.println("DEBUG SP: Procesando fase " + indiceFase + " para equipo ID " + equipo.getId());

        // Obtener la fecha base para calcular la próxima fecha de mantenimiento
        LocalDate fechaBaseCalculo = obtenerFechaBaseParaFase(equipo, indiceFase);

        if (fechaBaseCalculo == null) {
            // Si no hay fecha base (por ejemplo, equipo no tiene fecha de puesta en servicio o es un caso raro),
            // no se puede calcular la próxima fecha. Opcionalmente, se puede usar la fecha actual o una fecha por defecto.
            System.out.println("DEBUG SP: No se pudo determinar la fecha base para la fase " + indiceFase + " del equipo " + equipo.getId() + ". No se generará orden.");
            return;
        }

        // Calcular la próxima fecha de mantenimiento según la frecuencia de la fase
        LocalDate proximaFechaMantenimiento = calcularProximaFechaMantenimiento(fechaBaseCalculo, fase);

        // Obtener la fecha actual para comparar
        LocalDate fechaActual = LocalDate.now();

        // Verificar si ya se generó una orden para esta fase en esta fecha o una fecha posterior
        // Esto es para evitar generar múltiples órdenes para la misma fecha o si ya se generó una futura.
        boolean yaExisteOrdenParaFecha = listaOrdenes.stream()
                .filter(orden -> orden instanceof OrdenTrabajoPreventiva)
                .anyMatch(orden -> {
                    OrdenTrabajoPreventiva ordPrev = (OrdenTrabajoPreventiva) orden;
                    return ordPrev.getIdEquipo() == equipo.getId() &&
                           ordPrev.getIdFase() == indiceFase &&
                           !orden.getFechaEjecucion().isBefore(proximaFechaMantenimiento); // Fecha de ejecución >= proximaFechaMantenimiento
                });

        // Verificar si se debe generar una nueva orden
        // Condición 1: La próxima fecha es hoy o en el futuro
        // Condición 2: No existe una orden ya generada para esa fecha o una fecha posterior para esta fase específica
        if (!proximaFechaMantenimiento.isBefore(fechaActual) && !yaExisteOrdenParaFecha) {
             System.out.println("DEBUG SP: Se debe generar una nueva orden para la fase " + indiceFase + " del equipo " + equipo.getId() + " para la fecha " + proximaFechaMantenimiento);
             // Crear la nueva orden de trabajo preventiva
             OrdenTrabajoPreventiva nuevaOrden = new OrdenTrabajoPreventiva(siguienteIdOrden++, equipo.getId(), indiceFase, LocalDate.now(), proximaFechaMantenimiento);

             // Asignar las tareas de la fase a la orden
             for (int idTarea : fase.getIdsTareasMaestras()) {
                 TareaMantenimiento tarea = buscarTareaMaestraPorId(idTarea);
                 if (tarea != null) {
                     nuevaOrden.agregarTarea(tarea);
                 } else {
                     System.err.println("ERROR SP: Tarea con ID " + idTarea + " no encontrada al crear orden para fase " + indiceFase + " del equipo " + equipo.getId());
                 }
             }

             // Añadir la nueva orden a la lista global de órdenes
             listaOrdenes.add(nuevaOrden);

             // Actualizar el ID siguiente
             siguienteIdOrden = Math.max(siguienteIdOrden, nuevaOrden.getId() + 1);

             System.out.println("DEBUG SP: Nueva orden preventiva ID " + nuevaOrden.getId() + " generada para equipo " + equipo.getId() + " y fase " + indiceFase + ".");
        } else {
            System.out.println("DEBUG SP: No se debe generar nueva orden para la fase " + indiceFase + " del equipo " + equipo.getId() + ". Fecha base: " + fechaBaseCalculo + ", Próxima fecha: " + proximaFechaMantenimiento + ", Ya existe orden: " + yaExisteOrdenParaFecha);
        }
    }

    /**
     * Obtiene la fecha base para calcular la próxima fecha de mantenimiento de una fase específica.
     * La regla es:
     * - Primera orden: Basada en la fecha de puesta en servicio del equipo.
     * - Órdenes siguientes: Basada en la fecha de finalización de la última orden completada de esta fase para este equipo.
     * @param equipo El equipo.
     * @param indiceFase El índice de la fase en el programa del equipo.
     * @return La fecha base, o null si no se puede determinar.
     */
    private LocalDate obtenerFechaBaseParaFase(NodoEquipo equipo, int indiceFase) {
        // Buscar la última orden completada de esta fase específica para este equipo
        // Opcional: Tambien considerar órdenes canceladas si la lógica lo requiere
        OrdenTrabajoPreventiva ultimaOrdenCompletada = null;
        for (OrdenTrabajo orden : listaOrdenes) {
            if (orden instanceof OrdenTrabajoPreventiva) {
                OrdenTrabajoPreventiva ordPrev = (OrdenTrabajoPreventiva) orden;
                if (ordPrev.getIdEquipo() == equipo.getId() && ordPrev.getIdFase() == indiceFase && ordPrev.getEstado() == OrdenTrabajo.EstadoOrden.COMPLETADA) {
                    if (ultimaOrdenCompletada == null || orden.getFechaFinReal().isAfter(ultimaOrdenCompletada.getFechaFinReal())) {
                        ultimaOrdenCompletada = ordPrev;
                    }
                }
            }
        }

        if (ultimaOrdenCompletada != null && ultimaOrdenCompletada.getFechaFinReal() != null) {
            System.out.println("DEBUG SP: Fecha base para fase " + indiceFase + " del equipo " + equipo.getId() + " obtenida de la última orden completada: " + ultimaOrdenCompletada.getFechaFinReal());
            return ultimaOrdenCompletada.getFechaFinReal(); // Basada en la fecha de finalización de la última orden
        } else {
            // Si no hay órdenes completadas, usar la fecha de puesta en servicio del equipo
            LocalDate fechaPuestaEnServicio = equipo.getFechaPuestaEnServicio();
            if (fechaPuestaEnServicio != null) {
                 System.out.println("DEBUG SP: Fecha base para fase " + indiceFase + " del equipo " + equipo.getId() + " obtenida de la fecha de puesta en servicio: " + fechaPuestaEnServicio);
                 return fechaPuestaEnServicio; // Basada en la fecha de puesta en servicio
            } else {
                System.out.println("DEBUG SP: El equipo ID " + equipo.getId() + " no tiene fecha de puesta en servicio definida.");
                return null; // No se puede calcular sin fecha base
            }
        }
    }

    /**
     * Calcula la próxima fecha de mantenimiento según la fecha base y la frecuencia de la fase.
     * @param fechaBase La fecha desde la cual se calcula (puesta en servicio o fin de última orden).
     * @param fase La fase que define la frecuencia.
     * @return La próxima fecha de mantenimiento.
     */
    private LocalDate calcularProximaFechaMantenimiento(LocalDate fechaBase, FaseMantenimiento fase) {
        long unidades = fase.getMedidorFrecuencia(); // Ej: 3
        TipoFrecuencia tipo = fase.getTipoFrecuencia(); // Ej: MES

        switch (tipo) {
            case DIA:
                return fechaBase.plusDays(unidades);
            case SEMANA:
                return fechaBase.plusWeeks(unidades);
            case MES:
                return fechaBase.plusMonths(unidades);
            case ANO: // Cambiado de "ANO" a "ANIO" si es el nombre del enum
                return fechaBase.plusYears(unidades);
            default:
                // Si el tipo de frecuencia no es reconocido, devolver la fecha base (no debería ocurrir si el enum es correcto)
                System.err.println("ERROR SP: Tipo de frecuencia desconocido: " + tipo);
                return fechaBase;
        }
    }

    // Método para obtener el siguiente ID disponible para una orden
    public int obtenerSiguienteIdOrden() {
        return siguienteIdOrden;
    }

    // Método para registrar una nueva orden correctiva y guardarla
    public void registrarOrdenCorrectiva(OrdenTrabajoCorrectiva orden) {
        // Añadir la nueva orden a la lista interna
        listaOrdenes.add(orden);
        // Actualizar el siguiente ID si es necesario
        siguienteIdOrden = Math.max(siguienteIdOrden, orden.getId() + 1);

        // Guardar todos los datos en disco
        try {
            guardar(); // <-- Llama al método guardar del sistema
            System.out.println("DEBUG SP: Orden correctiva ID " + orden.getId() + " registrada y guardada en " + RUTA_ORDENES);
        } catch (IOException e) {
            System.err.println("ERROR SP: No se pudo guardar la orden correctiva ID " + orden.getId() + ". Error: " + e.getMessage());
            e.printStackTrace();
            // Opcional: Revertir la adición a la lista si falla el guardado
            listaOrdenes.remove(orden);
            throw new RuntimeException("No se pudo persistir la orden correctiva creada.", e);
        }
    }

    
    public boolean finalizarOrdenCorrectiva(int idOrden, LocalDate fechaFin, float horasTrabajo, int costoManoObra, int costoMateriales, String observaciones, List<OrdenTrabajo.FallaEncontrada> nuevasFallas) {
        OrdenTrabajoCorrectiva orden = (OrdenTrabajoCorrectiva) buscarOrdenPorId(idOrden);
        if (orden != null && orden.getEstado() == OrdenTrabajo.EstadoOrden.EN_PROGRESO) {
            orden.finalizar(fechaFin, horasTrabajo, costoManoObra, costoMateriales, observaciones);
            if (nuevasFallas != null) {
                orden.setFallasEncontradas(nuevasFallas); // Asegúrate de tener este setter en OrdenTrabajoCorrectiva o en la clase base
            }
            try {
                guardar(); // <-- Guardar cambios
                System.out.println("DEBUG SP: Orden correctiva ID " + idOrden + " finalizada y guardada.");
                return true;
            } catch (IOException e) {
                System.err.println("ERROR SP: No se pudo guardar la orden correctiva ID " + idOrden + " tras finalizarla. Error: " + e.getMessage());
                e.printStackTrace();
                // Revertir cambios si falla el guardado (estado, fechas, costos)
                orden.setEstado(OrdenTrabajo.EstadoOrden.EN_PROGRESO);
                orden.setFechaFinReal(null);
                orden.setHorasTrabajo(0.0f);
                orden.setCostoManoObra(0);
                orden.setCostoMateriales(0);
                orden.setObservacionesEjecucion(null);
                return false;
            }
        }
        return false;
    }

    // Cancelar Orden Correctiva
    public boolean cancelarOrdenCorrectiva(int idOrden, LocalDate fechaCancelacion, String motivo) {
        OrdenTrabajoCorrectiva orden = (OrdenTrabajoCorrectiva) buscarOrdenPorId(idOrden);
        if (orden != null && (orden.getEstado() == OrdenTrabajo.EstadoOrden.PENDIENTE || orden.getEstado() == OrdenTrabajo.EstadoOrden.EN_PROGRESO)) {
            orden.cancelar(fechaCancelacion, motivo);
            try {
                guardar(); // <-- Guardar cambios
                System.out.println("DEBUG SP: Orden correctiva ID " + idOrden + " cancelada y guardada.");
                return true;
            } catch (IOException e) {
                System.err.println("ERROR SP: No se pudo guardar la orden correctiva ID " + idOrden + " tras cancelarla. Error: " + e.getMessage());
                e.printStackTrace();
                // Revertir el estado si falla el guardado
                if (orden.getEstado() == OrdenTrabajo.EstadoOrden.PENDIENTE) {
                    orden.setFechaCancelacion(null);
                    orden.setMotivoCancelacion(null);
                } else if (orden.getEstado() == OrdenTrabajo.EstadoOrden.EN_PROGRESO) {
                    // Si estaba en progreso, al revertir, se quita la cancelación pero no se vuelve al estado PENDIENTE, sino que se queda EN_PROGRESO
                    orden.setEstado(OrdenTrabajo.EstadoOrden.EN_PROGRESO);
                    orden.setFechaCancelacion(null);
                    orden.setMotivoCancelacion(null);
                }
                return false;
            }
        }
        return false;
    }


}