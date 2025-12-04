package poo.proyecto2.GUI;

import poo.proyecto2.equipos.NodoEquipo;
import poo.proyecto2.mantenimiento.*;
import poo.proyecto2.sistema.SistemaPrincipal;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class VentanaConsultarEquipo extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Opcional

    private JLabel lblTitulo;
    private JLabel lblBuscarPor; // Para ID o descripción
    private JTextField txtBuscarEquipo;
    private JButton btnBuscar;
    private JSplitPane splitPanePrincipal; // Para dividir árbol y detalle
    private JScrollPane scrollArbol;
    private JTree arbolNavegacion;
    private JScrollPane scrollDetalle;
    private JPanel panelDetalle; // Aquí va la info detallada
    private JLabel lblDetalleVacio; // Mensaje cuando no hay selección

    // --- Nuevo campo: Referencia al nodo raíz buscado ---
    private NodoEquipo nodoRaizBuscado = null;

    public VentanaConsultarEquipo(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        inicializarComponentes();
        configurarEventos();
        setTitle("Consulta de Mantenimiento de Equipo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(1200, 800); // Tamaño más grande para la vista dividida
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Búsqueda ---
        JPanel panelBusqueda = new JPanel(new FlowLayout());
        lblBuscarPor = new JLabel("Buscar Equipo por ID:");
        txtBuscarEquipo = new JTextField(10); // Tamaño reducido para ID
        btnBuscar = new JButton("Buscar");
        panelBusqueda.add(lblBuscarPor);
        panelBusqueda.add(txtBuscarEquipo);
        panelBusqueda.add(btnBuscar);
        add(panelBusqueda, BorderLayout.NORTH);

        // --- Panel Central: SplitPane (Árbol y Detalle) ---
        // Inicializar panel detalle vacío
        panelDetalle = new JPanel(new BorderLayout());
        lblDetalleVacio = new JLabel("Seleccione un nodo del árbol para ver detalles aquí.", SwingConstants.CENTER);
        lblDetalleVacio.setFont(new Font("Arial", Font.ITALIC, 14));
        panelDetalle.add(lblDetalleVacio, BorderLayout.CENTER);
        scrollDetalle = new JScrollPane(panelDetalle);

        // Inicializar árbol vacío (se carga al buscar)
        arbolNavegacion = new JTree(); // Inicialmente vacío
        scrollArbol = new JScrollPane(arbolNavegacion);

        // Crear el SplitPane
        splitPanePrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollArbol, scrollDetalle);
        splitPanePrincipal.setDividerLocation(400); // Ancho inicial del árbol
        splitPanePrincipal.setResizeWeight(0.3); // Proporción de resize

        add(splitPanePrincipal, BorderLayout.CENTER);
    }

    private void configurarEventos() {
        btnBuscar.addActionListener(e -> {
            String criterio = txtBuscarEquipo.getText().trim();
            if (!criterio.isEmpty()) {
                try {
                    int idEquipo = Integer.parseInt(criterio);
                    System.out.println("DEBUG: Buscando equipo con ID: " + idEquipo); // DEBUG
                    NodoEquipo equipoRaiz = sistema.buscarEquipoPorId(idEquipo);
                    if (equipoRaiz != null) {
                        System.out.println("DEBUG: Equipo encontrado: " + equipoRaiz.getId() + " - " + equipoRaiz.getDescripcion()); // DEBUG
                        // Guardar la referencia al nodo raíz buscado
                        nodoRaizBuscado = equipoRaiz;
                        // Carga el árbol con el equipo encontrado y sus nodos dependientes inmediatos
                        cargarArbolNavegacion(equipoRaiz);
                        // Mostrar inmediatamente la información del nodo raíz en el panel derecho
                        mostrarDetalleNodo(equipoRaiz);
                        // Limpia la selección visual del árbol (opcional, para que no aparezca seleccionado)
                        arbolNavegacion.clearSelection();
                    } else {
                        System.out.println("DEBUG: Equipo con ID " + idEquipo + " NO encontrado en el sistema."); // DEBUG
                        JOptionPane.showMessageDialog(this, "Equipo con ID " + idEquipo + " no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("DEBUG: Error de formato en el ID: " + ex.getMessage()); // DEBUG
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo válido (número entero).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo para buscar.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Evento de selección del árbol
        arbolNavegacion.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolNavegacion.getLastSelectedPathComponent();
            if (nodoSeleccionado != null) {
                Object objeto = nodoSeleccionado.getUserObject(); // El objeto real asociado al nodo
                System.out.println("DEBUG: Nodo seleccionado en el árbol: " + objeto); // DEBUG

                // Verificar si el nodo seleccionado es el nodo raíz buscado
                if (objeto instanceof NodoEquipo && nodoRaizBuscado != null && ((NodoEquipo) objeto).getId() == nodoRaizBuscado.getId()) {
                    System.out.println("DEBUG: Nodo seleccionado es el nodo raíz buscado. No actualizamos detalle, expandimos si es necesario."); // DEBUG
                    // Si es el nodo raíz, no actualizamos el panel derecho (la info ya está mostrada)
                    // Solo expandimos el nodo si es necesario
                    if (nodoSeleccionado.getChildCount() == 0) {
                        System.out.println("DEBUG: Nodo raíz no tiene hijos en el árbol. Recargando nodos inmediatos..."); // DEBUG
                        // Recargar los nodos inmediatos para expandir
                        DefaultTreeModel model = (DefaultTreeModel) arbolNavegacion.getModel();
                        DefaultMutableTreeNode rootTreeNode = (DefaultMutableTreeNode) model.getRoot();
                        construirNodosInmediatos(rootTreeNode, (NodoEquipo) objeto);
                        model.reload(rootTreeNode); // Recarga el modelo para reflejar los cambios
                        // Expandir el nodo raíz
                        arbolNavegacion.expandRow(0);
                        System.out.println("DEBUG: Nodo raíz recargado y expandido."); // DEBUG
                    }
                    // Importante: No llamamos a mostrarDetalleNodo aquí para que no se sobrescriba la info del nodo raíz
                    return; // Salir del evento para no actualizar el detalle
                }

                // Si no es el nodo raíz, mostramos su detalle normalmente
                mostrarDetalleNodo(objeto);

                // Si el nodo es un NodoEquipo y no tiene hijos en el árbol, expandirlo para mostrar sus hijos directos
                if (objeto instanceof NodoEquipo) {
                    NodoEquipo equipo = (NodoEquipo) objeto;
                    // Verificar si el nodo tiene hijos en el modelo del árbol
                    DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbolNavegacion.getLastSelectedPathComponent();
                    if (nodo.getChildCount() == 0) {
                        System.out.println("DEBUG: Nodo equipo no tiene hijos en el árbol. Añadiendo hijos directos..."); // DEBUG
                        // Añadir los hijos directos a este nodo
                        agregarHijosDirectos(nodo, equipo);
                        // Expandir el nodo
                        TreePath path = arbolNavegacion.getSelectionPath();
                        if (path != null) {
                            arbolNavegacion.expandPath(path);
                        }
                        System.out.println("DEBUG: Hijos directos añadidos y nodo expandido."); // DEBUG
                    }
                }
            }
        });
    }

    private void cargarArbolNavegacion(NodoEquipo equipoRaiz) {
        System.out.println("DEBUG: Cargando árbol para equipo ID: " + equipoRaiz.getId()); // DEBUG
        // Crear el nodo raíz del árbol Swing
        DefaultMutableTreeNode nodoRaizArbol = new DefaultMutableTreeNode(equipoRaiz);
        // Construir los nodos dependientes inmediatos (componentes, fases, órdenes)
        construirNodosInmediatos(nodoRaizArbol, equipoRaiz);

        // Crear el modelo del árbol y asignarlo
        DefaultTreeModel modelo = new DefaultTreeModel(nodoRaizArbol);
        arbolNavegacion.setModel(modelo);

        // Expandir el nodo raíz para mostrar sus nodos inmediatos
        arbolNavegacion.expandRow(0); // Expande la primera fila (el nodo raíz)
        System.out.println("DEBUG: Árbol cargado y nodo raíz expandido para equipo ID: " + equipoRaiz.getId()); // DEBUG
    }

    private void construirNodosInmediatos(DefaultMutableTreeNode nodoPadreArbol, NodoEquipo equipoActual) {
        System.out.println("DEBUG: Construyendo nodos inmediatos para equipo ID: " + equipoActual.getId()); // DEBUG
        // Limpiar nodos actuales (excepto el nodo del equipo en sí, que es el padre)
        nodoPadreArbol.removeAllChildren();

        // 1. Añadir los nodos de Componentes (hijos directos) directamente bajo el nodo padre
        System.out.println("DEBUG: Buscando hijos directos para equipo ID: " + equipoActual.getId()); // DEBUG
        NodoEquipo hijo = equipoActual.getPrimerHijo();
        if (hijo != null) {
            System.out.println("DEBUG: Encontrado primer hijo: " + hijo.getId() + " - " + hijo.getDescripcion()); // DEBUG
            while (hijo != null) {
                DefaultMutableTreeNode nodoHijo = new DefaultMutableTreeNode(hijo);
                nodoPadreArbol.add(nodoHijo);
                System.out.println("DEBUG: Añadido hijo al árbol: " + hijo.getId() + " - " + hijo.getDescripcion()); // DEBUG
                // No añadimos subcomponentes aquí, solo los hijos directos
                hijo = hijo.getSiguienteHermano();
                if (hijo != null) {
                    System.out.println("DEBUG: Buscando siguiente hermano..."); // DEBUG
                }
            }
        } else {
            System.out.println("DEBUG: El equipo ID: " + equipoActual.getId() + " no tiene hijos directos."); // DEBUG
        }

        // 2. Añadir nodo de Mantenimiento Preventivo (como una carpeta)
        System.out.println("DEBUG: Buscando programa de mantenimiento preventivo para equipo ID: " + equipoActual.getId()); // DEBUG
        DefaultMutableTreeNode nodoPreventivo = new DefaultMutableTreeNode("MANTENIMIENTO PREVENTIVO");
        ProgramaMantenimientoPreventivo programa = sistema.obtenerProgramaDeEquipo(equipoActual.getId());
        if (programa != null && !programa.getFases().isEmpty()) {
            System.out.println("DEBUG: Programa encontrado con " + programa.getFases().size() + " fases."); // DEBUG
            for (int i = 0; i < programa.getFases().size(); i++) {
                FaseMantenimiento fase = programa.getFases().get(i);
                System.out.println("DEBUG: Procesando fase " + i + ": " + fase); // DEBUG
                DefaultMutableTreeNode nodoFase = new DefaultMutableTreeNode(fase);
                // Buscar órdenes preventivas asociadas a esta fase para este equipo
                List<OrdenTrabajo> ordenesEquipo = sistema.obtenerOrdenesPorEquipo(equipoActual.getId());
                System.out.println("DEBUG: Numero total de órdenes para el equipo: " + ordenesEquipo.size()); // DEBUG
                for (OrdenTrabajo orden : ordenesEquipo) {
                    if (orden instanceof OrdenTrabajoPreventiva) {
                        System.out.println("DEBUG: Revisando Orden Preventiva ID: " + orden.getId() + " con idFase: " + ((OrdenTrabajoPreventiva) orden).getIdFase()); // DEBUG
                        // Comparamos por índice de fase o por ID de fase si lo guardamos en OrdenTrabajoPreventiva
                        // Por ahora, asumiremos que el idFase en la orden coincide con el índice 'i' de la lista del programa
                        if (((OrdenTrabajoPreventiva) orden).getIdFase() == i) {
                             System.out.println("DEBUG: Coincidencia encontrada! Añadiendo orden preventiva ID: " + orden.getId() + " a la fase " + i); // DEBUG
                             nodoFase.add(new DefaultMutableTreeNode(orden));
                        } else {
                            System.out.println("DEBUG: No coincide idFase (" + ((OrdenTrabajoPreventiva) orden).getIdFase() + ") con índice de fase (" + i + ")"); // DEBUG
                        }
                    }
                }
                nodoPreventivo.add(nodoFase);
            }
        } else {
            System.out.println("DEBUG: No se encontró programa o fases para equipo ID: " + equipoActual.getId()); // DEBUG
        }
        nodoPadreArbol.add(nodoPreventivo);

        // 3. Añadir nodo de Mantenimiento Correctivo (como una carpeta)
        System.out.println("DEBUG: Buscando órdenes de mantenimiento correctivo para equipo ID: " + equipoActual.getId()); // DEBUG
        DefaultMutableTreeNode nodoCorrectivo = new DefaultMutableTreeNode("MANTENIMIENTO CORRECTIVO");
        List<OrdenTrabajo> ordenesCorrectivas = sistema.obtenerOrdenesPorEquipo(equipoActual.getId()).stream()
                .filter(orden -> orden instanceof OrdenTrabajoCorrectiva)
                .toList();
        if (!ordenesCorrectivas.isEmpty()) {
            System.out.println("DEBUG: Encontradas " + ordenesCorrectivas.size() + " órdenes correctivas para equipo ID: " + equipoActual.getId()); // DEBUG
            for (OrdenTrabajo orden : ordenesCorrectivas) {
                 System.out.println("DEBUG: Añadiendo orden correctiva ID: " + orden.getId() + " al árbol."); // DEBUG
                 nodoCorrectivo.add(new DefaultMutableTreeNode(orden));
            }
        } else {
            System.out.println("DEBUG: No se encontraron órdenes correctivas para equipo ID: " + equipoActual.getId()); // DEBUG
        }
        nodoPadreArbol.add(nodoCorrectivo);

        System.out.println("DEBUG: Nodos inmediatos construidos para equipo ID: " + equipoActual.getId()); // DEBUG
    }

    private void agregarHijosDirectos(DefaultMutableTreeNode nodoPadre, NodoEquipo equipoActual) {
        // Limpiar los hijos actuales del nodo padre
        nodoPadre.removeAllChildren();

        // Añadir los hijos directos del equipo actual
        NodoEquipo hijo = equipoActual.getPrimerHijo();
        if (hijo != null) {
            while (hijo != null) {
                DefaultMutableTreeNode nodoHijo = new DefaultMutableTreeNode(hijo);
                nodoPadre.add(nodoHijo);
                hijo = hijo.getSiguienteHermano();
            }
        }

        // Actualizar el modelo del árbol
        ((DefaultTreeModel) arbolNavegacion.getModel()).nodeStructureChanged(nodoPadre);
    }

    private void mostrarDetalleNodo(Object nodo) {
        // Limpiar panel anterior
        panelDetalle.removeAll();
        panelDetalle.revalidate();
        panelDetalle.repaint();

        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen

        if (nodo instanceof NodoEquipo) {
            NodoEquipo equipo = (NodoEquipo) nodo;
            JLabel lblTitulo = new JLabel("Detalles del Equipo", SwingConstants.LEFT);
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
            panelInfo.add(lblTitulo);
            panelInfo.add(Box.createVerticalStrut(10)); // Espacio

            panelInfo.add(new JLabel("ID: " + equipo.getId()));
            panelInfo.add(new JLabel("Descripción: " + equipo.getDescripcion()));
            panelInfo.add(new JLabel("Tipo: " + equipo.getTipo()));
            panelInfo.add(new JLabel("Ubicación: " + equipo.getUbicacion()));
            panelInfo.add(new JLabel("Fabricante: " + equipo.getFabricante()));
            panelInfo.add(new JLabel("Serie: " + equipo.getSerie()));
            panelInfo.add(new JLabel("Fecha Adquisición: " + (equipo.getFechaAdquisicion() != null ? equipo.getFechaAdquisicion().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A")));
            panelInfo.add(new JLabel("Fecha Puesta en Servicio: " + (equipo.getFechaPuestaEnServicio() != null ? equipo.getFechaPuestaEnServicio().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A")));
            panelInfo.add(new JLabel("Meses Vida Útil: " + equipo.getMesesVidaUtil()));
            panelInfo.add(new JLabel("Estado: " + equipo.getEstado()));
            panelInfo.add(new JLabel("Costo Inicial: " + String.format("%.2f", equipo.getCostoInicial())));
            panelInfo.add(new JLabel("Equipo Principal (ID): " + equipo.getEquipoPrincipal()));

            // --- Especificaciones Técnicas ---
            panelInfo.add(new JLabel("Especificaciones Técnicas:"));
            String espec = equipo.getEspecificacionesTecnicas() != null ? equipo.getEspecificacionesTecnicas() : "N/A";
            JLabel lblEspec = new JLabel("<html>" + espec.replace("\n", "<br>") + "</html>");
            lblEspec.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5)); // Margen interno
            panelInfo.add(lblEspec);

            // --- Información de Garantía ---
            panelInfo.add(new JLabel("Información de Garantía:"));
            String garantia = equipo.getInformacionGarantia() != null ? equipo.getInformacionGarantia() : "N/A";
            JLabel lblGarantia = new JLabel("<html>" + garantia.replace("\n", "<br>") + "</html>");
            lblGarantia.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
            panelInfo.add(lblGarantia);

        } else if (nodo instanceof FaseMantenimiento) {
            FaseMantenimiento fase = (FaseMantenimiento) nodo;
            JLabel lblTitulo = new JLabel("Detalles de la Fase", SwingConstants.LEFT);
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
            panelInfo.add(lblTitulo);
            panelInfo.add(Box.createVerticalStrut(10)); // Espacio

            panelInfo.add(new JLabel("Tipo Frecuencia: " + fase.getTipoFrecuencia()));
            panelInfo.add(new JLabel("Medidor Frecuencia: " + fase.getMedidorFrecuencia()));
            panelInfo.add(new JLabel("Cantidad Ciclos: " + (fase.getCantidadCiclos() == 0 ? "Recurrente" : fase.getCantidadCiclos())));
            panelInfo.add(new JLabel("Partes/Repuestos: " + (fase.getPartes() != null ? fase.getPartes() : "N/A")));
            panelInfo.add(new JLabel("Herramientas: " + (fase.getHerramientas() != null ? fase.getHerramientas() : "N/A")));
            panelInfo.add(new JLabel("Personal Requerido: " + (fase.getPersonal() != null ? fase.getPersonal() : "N/A")));
            panelInfo.add(new JLabel("Horas Estimadas: " + fase.getHorasEstimadas()));
            panelInfo.add(new JLabel("Tareas:"));
            JTextArea txtTareas = new JTextArea(String.join(", ", fase.getIdsTareasMaestras().stream().map(String::valueOf).toArray(String[]::new)), 3, 30);
            txtTareas.setEditable(false);
            txtTareas.setLineWrap(true);
            txtTareas.setWrapStyleWord(true);
            panelInfo.add(new JScrollPane(txtTareas));

        } else if (nodo instanceof OrdenTrabajoPreventiva) {
            OrdenTrabajoPreventiva orden = (OrdenTrabajoPreventiva) nodo;
            JLabel lblTitulo = new JLabel("Detalles de la Orden Preventiva", SwingConstants.LEFT);
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
            panelInfo.add(lblTitulo);
            panelInfo.add(Box.createVerticalStrut(10)); // Espacio

            mostrarDetalleOrdenComun(panelInfo, orden);
            panelInfo.add(new JLabel("ID Fase de Programa: " + orden.getIdFase()));
            panelInfo.add(new JLabel("Tareas Asociadas:"));
            JTextArea txtTareas = new JTextArea();
            StringBuilder tareasTexto = new StringBuilder();
            for (var tarea : orden.getTareas()) {
                tareasTexto.append("- ").append(tarea.getDescripcion()).append("\n");
            }
            txtTareas.setText(tareasTexto.toString());
            txtTareas.setEditable(false);
            txtTareas.setLineWrap(true);
            txtTareas.setWrapStyleWord(true);
            panelInfo.add(new JScrollPane(txtTareas));

        } else if (nodo instanceof OrdenTrabajoCorrectiva) {
            OrdenTrabajoCorrectiva orden = (OrdenTrabajoCorrectiva) nodo;
            JLabel lblTitulo = new JLabel("Detalles de la Orden Correctiva", SwingConstants.LEFT);
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
            panelInfo.add(lblTitulo);
            panelInfo.add(Box.createVerticalStrut(10)); // Espacio

            mostrarDetalleOrdenComun(panelInfo, orden);

        } else if (nodo instanceof String) { // Nodo de grupo como "MANTENIMIENTO PREVENTIVO", "MANTENIMIENTO CORRECTIVO"
             JLabel lblTitulo = new JLabel("Detalles de " + (String)nodo, SwingConstants.LEFT);
             lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
             panelInfo.add(lblTitulo);
             panelInfo.add(Box.createVerticalStrut(10)); // Espacio
             JLabel lblResumen = new JLabel("Este nodo agrupa los elementos relacionados mostrados debajo en el árbol.");
             panelInfo.add(lblResumen);
        }

        panelDetalle.add(panelInfo, BorderLayout.CENTER);
        panelDetalle.revalidate();
        panelDetalle.repaint();
    }

    private void mostrarDetalleOrdenComun(JPanel panelInfo, OrdenTrabajo orden) {
        panelInfo.add(new JLabel("ID Orden: " + orden.getId()));
        panelInfo.add(new JLabel("ID Equipo: " + orden.getIdEquipo()));
        panelInfo.add(new JLabel("Fecha Orden: " + (orden.getFechaOrden() != null ? orden.getFechaOrden().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A")));
        panelInfo.add(new JLabel("Fecha Ejecución: " + (orden.getFechaEjecucion() != null ? orden.getFechaEjecucion().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A")));
        panelInfo.add(new JLabel("Estado: " + orden.getEstado()));
        panelInfo.add(new JLabel("Observaciones: " + (orden.getObservaciones() != null ? orden.getObservaciones() : "N/A")));
        panelInfo.add(new JLabel("Fecha Inicio Real: " + (orden.getFechaInicioReal() != null ? orden.getFechaInicioReal().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A")));
        panelInfo.add(new JLabel("Fecha Fin Real: " + (orden.getFechaFinReal() != null ? orden.getFechaFinReal().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A")));
        panelInfo.add(new JLabel("Horas Trabajo: " + (orden.getHorasTrabajo() != 0.0f ? orden.getHorasTrabajo() : "N/A")));
        panelInfo.add(new JLabel("Costo Mano de Obra: " + (orden.getCostoManoObra() != 0 ? orden.getCostoManoObra() : "N/A")));
        panelInfo.add(new JLabel("Costo Materiales: " + (orden.getCostoMateriales() != 0 ? orden.getCostoMateriales() : "N/A")));
        panelInfo.add(new JLabel("Observaciones de Ejecución: " + (orden.getObservacionesEjecucion() != null ? orden.getObservacionesEjecucion() : "N/A")));
        panelInfo.add(new JLabel("Fecha Cancelación: " + (orden.getFechaCancelacion() != null ? orden.getFechaCancelacion().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A")));
        panelInfo.add(new JLabel("Motivo Cancelación: " + (orden.getMotivoCancelacion() != null ? orden.getMotivoCancelacion() : "N/A")));

        // Mostrar Fallas Reportadas
        if (!orden.getFallasReportadas().isEmpty()) {
            panelInfo.add(new JLabel("Fallas Reportadas:"));
            JTextArea txtFallasRep = new JTextArea();
            StringBuilder fallasRepTexto = new StringBuilder();
            for (var falla : orden.getFallasReportadas()) {
                fallasRepTexto.append("- Causas: ").append(falla.getCausas()).append(", Acciones: ").append(falla.getAccionesTomadas()).append("\n");
            }
            txtFallasRep.setText(fallasRepTexto.toString());
            txtFallasRep.setEditable(false);
            txtFallasRep.setLineWrap(true);
            txtFallasRep.setWrapStyleWord(true);
            panelInfo.add(new JScrollPane(txtFallasRep));
        }

        // Mostrar Fallas Encontradas
        if (!orden.getFallasEncontradas().isEmpty()) {
            panelInfo.add(new JLabel("Fallas Encontradas:"));
            JTextArea txtFallasEnc = new JTextArea();
            StringBuilder fallasEncTexto = new StringBuilder();
            for (var falla : orden.getFallasEncontradas()) {
                fallasEncTexto.append("- ID: ").append(falla.getIdFalla()).append(", Desc: ").append(falla.getDescripcionFalla()).append(", Causas: ").append(falla.getCausas()).append(", Acciones: ").append(falla.getAccionesTomadas()).append("\n");
            }
            txtFallasEnc.setText(fallasEncTexto.toString());
            txtFallasEnc.setEditable(false);
            txtFallasEnc.setLineWrap(true);
            txtFallasEnc.setWrapStyleWord(true);
            panelInfo.add(new JScrollPane(txtFallasEnc));
        }
    }

    private void limpiarPanelDetalle() {
        panelDetalle.removeAll();
        panelDetalle.add(lblDetalleVacio, BorderLayout.CENTER);
        panelDetalle.revalidate();
        panelDetalle.repaint();
    }
}