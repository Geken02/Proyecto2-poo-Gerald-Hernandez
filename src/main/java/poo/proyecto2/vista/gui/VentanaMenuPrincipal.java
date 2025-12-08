package poo.proyecto2.vista.gui;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.*;
import poo.proyecto2.vista.reportes.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ventana principal del sistema de mantenimiento de equipos.
 * Proporciona acceso a todas las funcionalidades del sistema a través de un menú
 * y una vista jerárquica de los equipos y su información de mantenimiento.
 */
public class VentanaMenuPrincipal extends JFrame {

    private SistemaPrincipal sistema;

    // Componentes de la interfaz
    private JMenuBar menuBar;
    private JMenu menuMantenimiento;
    private JMenu menuReportes;
    private JMenu menuGraficos;

    // Submenús de Mantenimiento
    private JMenu submenuEquipos;
    private JMenuItem menuItemRegistroEquipo;
    private JMenuItem menuItemEliminacionEquipo;

    private JMenu submenuProgramaPrev;
    private JMenuItem menuItemRegistroPrograma;
    private JMenuItem menuItemVisualizarTareas;

    private JMenu submenuOrdenesPrev;
    private JMenuItem menuItemDatosInicio;
    private JMenuItem menuItemDatosFin;  
    private JMenuItem menuItemCancelacionOrdenes; 

    private JMenu submenuFallas;
    private JMenuItem menuItemRegistroFallas;

    private JMenu submenuOrdenesCorr;
    private JMenuItem menuItemRegistroOrdenCorr;
    private JMenuItem menuItemDatosInicioCorr;
    private JMenuItem menuItemDatosFinCorr;  
    private JMenuItem menuItemCancelacionOrdenCorr;

    // Submenús de Reportes
    private JMenuItem menuItemRepInventario;
    private JMenuItem menuItemRepOperaciones;
    private JMenuItem menuItemRepOrdenes;

    // Submenús de Gráficos
    private JMenuItem menuItemGrafico1;
    // private JMenuItem menuItemGrafico2; // Eliminado

    // Componentes de búsqueda y árbol
    private JLabel lblBuscarPor;
    private JTextField txtBuscarEquipo;
    private JButton btnBuscar;
    private JSplitPane splitPanePrincipal;
    private JScrollPane scrollArbol;
    private JTree arbolNavegacion;
    private JScrollPane scrollDetalle;
    private JPanel panelDetalle;
    private JLabel lblDetalleVacio;

    // Botón de modificación
    private JButton btnModificarInformacion;

    // Panel de estado
    private JPanel panelEstado;
    private JLabel lblEstado;

    // Referencia al nodo raíz buscado
    private NodoEquipo nodoRaizBuscado = null;

    /**
     * Constructor de la ventana principal.
     * @param sistema Instancia del SistemaPrincipal que gestiona los datos.
     */
    public VentanaMenuPrincipal(SistemaPrincipal sistema) {
        this.sistema = sistema;
        inicializarComponentes();
        configurarEventos();
        setTitle("Sistema de Mantenimiento de Equipos - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(1200, 800); // Tamaño más grande
        setLocationRelativeTo(null);
    }

    /**
     * Inicializa todos los componentes de la interfaz gráfica.
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Inicializar Barra de Menú Superior ---
        menuBar = new JMenuBar();

        // --- Menús Principales ---
        menuMantenimiento = new JMenu("Mantenimiento");
        menuReportes = new JMenu("Reportes");
        menuGraficos = new JMenu("Gráfico de Análisis");

        // Ajustar fuente del menú
        Font fontMenu = new Font("Arial", Font.BOLD, 16);
        menuMantenimiento.setFont(fontMenu);
        menuReportes.setFont(fontMenu);
        menuGraficos.setFont(fontMenu);

        // --- Submenús de Mantenimiento ---
        submenuEquipos = new JMenu("Equipos");
        menuItemRegistroEquipo = new JMenuItem("Registro de equipos");
        menuItemEliminacionEquipo = new JMenuItem("Eliminación de equipos");
        submenuEquipos.add(menuItemRegistroEquipo);
        submenuEquipos.add(menuItemEliminacionEquipo);

        submenuProgramaPrev = new JMenu("Programa de mantenimiento preventivo");
        menuItemRegistroPrograma = new JMenuItem("Registro de un programa");
        menuItemVisualizarTareas = new JMenuItem("Administrar lista de tareas de un programa");
        submenuProgramaPrev.add(menuItemRegistroPrograma);
        submenuProgramaPrev.add(menuItemVisualizarTareas);

        submenuOrdenesPrev = new JMenu("Órdenes de trabajo para el mantenimiento preventivo");

        menuItemDatosInicio = new JMenuItem("Inicia la orden"); 
        menuItemDatosFin = new JMenuItem("Finalizacion de la orden"); 
        menuItemCancelacionOrdenes = new JMenuItem("Cancelación de la orden");

        submenuOrdenesPrev.add(menuItemDatosInicio);
        submenuOrdenesPrev.add(menuItemDatosFin);
        submenuOrdenesPrev.addSeparator(); // Separador antes de cancelación
        submenuOrdenesPrev.add(menuItemCancelacionOrdenes);

        submenuFallas = new JMenu("Fallas");
        menuItemRegistroFallas = new JMenuItem("Registro de fallas de una orden de mantenimiento");
        submenuFallas.add(menuItemRegistroFallas);

        submenuOrdenesCorr = new JMenu("Órdenes de trabajo para el mantenimiento correctivo");
        menuItemRegistroOrdenCorr = new JMenuItem("Registro");
        menuItemDatosInicioCorr = new JMenuItem("Iniciar una orden");
        menuItemDatosFinCorr = new JMenuItem("Finalizar una orden");
        menuItemCancelacionOrdenCorr = new JMenuItem("Cancelación de órdenes");
        submenuOrdenesCorr.add(menuItemRegistroOrdenCorr);
        submenuOrdenesCorr.add(menuItemDatosInicioCorr);
        submenuOrdenesCorr.add(menuItemDatosFinCorr);
        submenuOrdenesCorr.addSeparator(); 
        submenuOrdenesCorr.add(menuItemCancelacionOrdenCorr);

        // Añadir submenús al menú principal de Mantenimiento
        menuMantenimiento.add(submenuEquipos);
        menuMantenimiento.add(submenuProgramaPrev);
        menuMantenimiento.add(submenuOrdenesPrev);
        menuMantenimiento.add(submenuFallas);
        menuMantenimiento.add(submenuOrdenesCorr);

        // --- Submenús de Reportes ---
        menuItemRepInventario = new JMenuItem("Reporte del inventario de equipos");
        menuItemRepOperaciones = new JMenuItem("Reporte de operaciones de mantenimiento");
        menuItemRepOrdenes = new JMenuItem("Reporte de órdenes de trabajo");
        menuReportes.add(menuItemRepInventario);
        menuReportes.add(menuItemRepOperaciones);
        menuReportes.add(menuItemRepOrdenes);

        // --- Submenús de Gráficos ---
        menuItemGrafico1 = new JMenuItem("Gráfico de Análisis");
        
        menuGraficos.add(menuItemGrafico1);
  

        // Añadir menús principales a la barra
        menuBar.add(menuMantenimiento);
        menuBar.add(menuReportes);
        menuBar.add(menuGraficos);

        // --- Panel Superior: Menú y Búsqueda ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(menuBar, BorderLayout.CENTER);

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblBuscarPor = new JLabel("Buscar Equipo por ID:");
        txtBuscarEquipo = new JTextField(10);
        btnBuscar = new JButton("Buscar");
        panelBusqueda.add(lblBuscarPor);
        panelBusqueda.add(txtBuscarEquipo);
        panelBusqueda.add(btnBuscar);
        panelSuperior.add(panelBusqueda, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

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

        // --- Panel Inferior: Botón de Modificación y Estado ---
        JPanel panelInferior = new JPanel(new BorderLayout());

        // Botón de modificación (abajo a la derecha)
        btnModificarInformacion = new JButton("Modificar Información");
        JPanel panelBotonMod = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        panelBotonMod.add(btnModificarInformacion);
        panelInferior.add(panelBotonMod, BorderLayout.CENTER);

        // Panel de estado (abajo a la izquierda)
        panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblEstado = new JLabel("Sistema listo.");
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 12));
        panelEstado.setBorder(BorderFactory.createLoweredBevelBorder()); 
        panelEstado.add(lblEstado);
        panelInferior.add(panelEstado, BorderLayout.WEST);

        add(panelInferior, BorderLayout.SOUTH);
    }

    /**
     * Configura todos los eventos de los componentes de la interfaz.
     */
    private void configurarEventos() {
        // --- Eventos de Menú ---
        menuItemRegistroEquipo.addActionListener(e -> {
            lblEstado.setText("Acción: Registro de equipos");
            // Abrir VentanaRegistrarEquipo
            VentanaRegistrarEquipo ventanaReg = new VentanaRegistrarEquipo(sistema, this);
            ventanaReg.setVisible(true);
        });

        menuItemEliminacionEquipo.addActionListener(e -> {
            lblEstado.setText("Acción: Eliminación de equipos");

            VentanaEliminarEquipo ventanaElim = new VentanaEliminarEquipo(sistema, this, () -> actualizarVistaArbol()); // Pasa el callback
            ventanaElim.setVisible(true);
        });


        btnBuscar.addActionListener(e -> {
            String criterio = txtBuscarEquipo.getText().trim();
            if (!criterio.isEmpty()) {
                try {
                    int idEquipo = Integer.parseInt(criterio);
            
                    NodoEquipo equipoRaiz = sistema.buscarEquipoPorId(idEquipo);
                    if (equipoRaiz != null) {
                        nodoRaizBuscado = equipoRaiz;
                        // Carga el árbol con el equipo encontrado y sus nodos dependientes inmediatos
                        cargarArbolNavegacion(equipoRaiz);
                        // Mostrar inmediatamente la información del nodo raíz en el panel derecho
                        mostrarDetalleNodo(equipoRaiz);
                        // Limpia la selección visual del árbol (opcional, para que no aparezca seleccionado)
                        arbolNavegacion.clearSelection();
                    } else {
                        
                        JOptionPane.showMessageDialog(this, "Equipo con ID " + idEquipo + " no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo válido (número entero).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo para buscar.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // --- Evento de selección del árbol ---
        arbolNavegacion.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolNavegacion.getLastSelectedPathComponent();
            if (nodoSeleccionado != null) {
                Object objeto = nodoSeleccionado.getUserObject(); // El objeto real asociado al nodo
               
                if (objeto instanceof NodoEquipo && nodoRaizBuscado != null && ((NodoEquipo) objeto).getId() == nodoRaizBuscado.getId()) {
                    
                    if (nodoSeleccionado.getChildCount() == 0) {
                       
                        DefaultTreeModel model = (DefaultTreeModel) arbolNavegacion.getModel();
                        DefaultMutableTreeNode rootTreeNode = (DefaultMutableTreeNode) model.getRoot();
                        construirNodosInmediatos(rootTreeNode, (NodoEquipo) objeto);
                        model.reload(rootTreeNode); // Recarga el modelo para reflejar los cambios
                        // Expandir el nodo raíz
                        arbolNavegacion.expandRow(0);
                       
                    }
                    
                    return;
                }

                // Si no es el nodo raíz, mostramos su detalle normalmente
                mostrarDetalleNodo(objeto);

                // Si el nodo es un NodoEquipo y no tiene hijos en el árbol, expandirlo para mostrar sus hijos directos y mantenimiento
                if (objeto instanceof NodoEquipo) {
                    NodoEquipo equipo = (NodoEquipo) objeto;
                    // Verificar si el nodo tiene hijos en el modelo del árbol
                    DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbolNavegacion.getLastSelectedPathComponent();
                    if (nodo.getChildCount() == 0) {
                       
                        agregarHijosYMantenimiento(nodo, equipo);
                        // Expandir el nodo
                        TreePath path = arbolNavegacion.getSelectionPath();
                        if (path != null) {
                            arbolNavegacion.expandPath(path);
                        }
                        
                    }
                }
            }
        });

        // --- Evento del Botón Modificar Información ---
        btnModificarInformacion.addActionListener(e -> {
            DefaultMutableTreeNode nodoSeleccionado = (DefaultMutableTreeNode) arbolNavegacion.getLastSelectedPathComponent();
            if (nodoSeleccionado != null) {
                Object objeto = nodoSeleccionado.getUserObject();
                if (objeto instanceof NodoEquipo) {
                    NodoEquipo equipo = (NodoEquipo) objeto;
                    VentanaEditarEquipo ventanaEdit = new VentanaEditarEquipo(sistema, this, equipo);
                    ventanaEdit.setVisible(true);
                    cargarArbolNavegacion(nodoRaizBuscado); // <-- Recargar el árbol para reflejar cambios
                } else if (objeto instanceof String) {
                    String textoNodo = (String) objeto;
                    // Verificar si el texto del nodo es "MANTENIMIENTO PREVENTIVO"
                    if ("MANTENIMIENTO PREVENTIVO".equals(textoNodo)) {
                        // Obtener el nodo padre del árbol, que debería ser un NodoEquipo
                        TreeNode parent = nodoSeleccionado.getParent();
                        if (parent instanceof DefaultMutableTreeNode) {
                            Object objetoPadre = ((DefaultMutableTreeNode) parent).getUserObject();
                            if (objetoPadre instanceof NodoEquipo) {
                                int idEquipo = ((NodoEquipo) objetoPadre).getId();
                                // Abrir VentanaEditarProgramaMantenimiento con el ID del equipo
                                VentanaEditarProgramaMantenimiento ventanaEditProg = new VentanaEditarProgramaMantenimiento(sistema, this, idEquipo);
                                ventanaEditProg.setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(this, "No se pudo determinar el equipo padre del nodo de mantenimiento.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "No se pudo determinar el nodo padre del nodo de mantenimiento.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if ("MANTENIMIENTO CORRECTIVO".equals(textoNodo)) {
                        TreeNode parent = nodoSeleccionado.getParent();
                        if (parent instanceof DefaultMutableTreeNode) {
                            Object objetoPadre = ((DefaultMutableTreeNode) parent).getUserObject();
                            if (objetoPadre instanceof NodoEquipo) {
                                int idEquipo = ((NodoEquipo) objetoPadre).getId();
                                
                                JOptionPane.showMessageDialog(this, "Consultar Órdenes Correctivas para el equipo ID: " + idEquipo, "Seleccione una orden para editar", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } else {
                        // Otros nodos tipo String que no son mantenimiento
                        JOptionPane.showMessageDialog(this, "No se puede modificar la información de este tipo de nodo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    }
                } else if (objeto instanceof FaseMantenimiento) {
            
                    JOptionPane.showMessageDialog(this, "Editar Fase", "Se edita a traves del programa de mantenimiento", JOptionPane.INFORMATION_MESSAGE);
                } else if (objeto instanceof OrdenTrabajo) {
                    OrdenTrabajo orden = (OrdenTrabajo) objeto;
                    if (orden instanceof OrdenTrabajoPreventiva) { 
                        // Abrir VentanaEditarOrdenPreventiva
                        VentanaEditarOrdenPreventiva ventanaEditOrdPrev = new VentanaEditarOrdenPreventiva(sistema, this, (OrdenTrabajoPreventiva) orden);
                        ventanaEditOrdPrev.setVisible(true);
                    } else if (orden instanceof OrdenTrabajoCorrectiva) { 
                        // Abrir VentanaEditarOrdenCorrectiva
                        VentanaEditarOrdenCorrectiva ventanaEditOrdCorr = new VentanaEditarOrdenCorrectiva(sistema, this, (OrdenTrabajoCorrectiva) orden);
                        ventanaEditOrdCorr.setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No se puede modificar la información de este tipo de nodo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un nodo del árbol para modificar su información.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // --- Eventos de Catálogos ---
        menuItemVisualizarTareas.addActionListener(e -> { 
            lblEstado.setText("Acción: Administrar Tareas Maestras");
            VentanaAdministrarTareas ventanaTareas = new VentanaAdministrarTareas(sistema, this);
            ventanaTareas.setVisible(true);
        });

        menuItemRegistroPrograma.addActionListener(e -> { 
            lblEstado.setText("Acción: Registrar Programa de Mantenimiento");
            VentanaRegistrarProgramaMantenimiento ventanaProg = new VentanaRegistrarProgramaMantenimiento(sistema, this);
            ventanaProg.setVisible(true);
        });

        menuItemDatosInicio.addActionListener(e -> {
            lblEstado.setText("Acción: Iniciar Órdenes Preventivas"); 
            VentanaIniciarOrdenPreventiva ventanaInicio = new VentanaIniciarOrdenPreventiva(sistema, this);
            ventanaInicio.setVisible(true);
        });

        menuItemDatosFin.addActionListener(e -> {
            lblEstado.setText("Acción: Finalizar Órdenes Preventivas");
            // Abrir VentanaFinalizarOrdenPreventiva
            VentanaFinalizarOrdenPreventiva ventanaFinalizar = new VentanaFinalizarOrdenPreventiva(sistema, this);
            ventanaFinalizar.setVisible(true);
        });

        // --- Mantenimiento -> Órdenes de trabajo para el mantenimiento preventivo -> Cancelación de órdenes ---
        menuItemCancelacionOrdenes.addActionListener(e -> {
            lblEstado.setText("Acción: Cancelar Orden Preventiva");
            // Abrir VentanaCancelarOrdenPreventiva
            VentanaCancelarOrdenPreventiva ventanaCancelar = new VentanaCancelarOrdenPreventiva(sistema, this);
            ventanaCancelar.setVisible(true);
        });

        menuItemRegistroFallas.addActionListener(e -> {
            lblEstado.setText("Acción: Administrar Fallas Maestras");
            // Abrir VentanaAdministrarFallasMaestras
            VentanaAdministrarFallas ventanaFallas = new VentanaAdministrarFallas(sistema, this);
            ventanaFallas.setVisible(true);
        });

        menuItemRegistroOrdenCorr.addActionListener(e -> {
            lblEstado.setText("Acción: Registrar Nueva Orden Correctiva"); // Opcional
            VentanaRegistrarOrdenCorrectiva ventanaReg = new VentanaRegistrarOrdenCorrectiva(sistema, this);
            ventanaReg.setVisible(true);
        });

        // --- Mantenimiento -> Órdenes Correctivas -> Iniciar Órdenes ---
        menuItemDatosInicioCorr.addActionListener(e -> {
            lblEstado.setText("Acción: Iniciar Órdenes Correctivas");
            // Abrir VentanaIniciarOrdenCorrectiva
            VentanaIniciarOrdenCorrectiva ventanaInicio = new VentanaIniciarOrdenCorrectiva(sistema, this);
            ventanaInicio.setVisible(true);
        });

        // --- Mantenimiento -> Órdenes Correctivas -> Finalizar Órdenes ---
        menuItemDatosFinCorr.addActionListener(e -> {
            lblEstado.setText("Acción: Finalizar Órdenes Correctivas");
            // Abrir VentanaFinalizarOrdenCorrectiva
            VentanaFinalizarOrdenCorrectiva ventanaFin = new VentanaFinalizarOrdenCorrectiva(sistema, this);
            ventanaFin.setVisible(true);
        });

        // --- Mantenimiento -> Órdenes Correctivas -> Cancelación de órdenes ---
        menuItemCancelacionOrdenCorr.addActionListener(e -> {
            lblEstado.setText("Acción: Cancelar Órdenes Correctivas");
            // Abrir VentanaCancelarOrdenCorrectiva
            VentanaCancelarOrdenCorrectiva ventanaCancel = new VentanaCancelarOrdenCorrectiva(sistema, this);
            ventanaCancel.setVisible(true);
        });

        menuItemGrafico1.addActionListener(e -> {
            lblEstado.setText("Acción: Gráficos de Análisis");
            VentanaGraficosAnalisis ventanaGraf = new VentanaGraficosAnalisis(sistema, this); // Pasa la instancia de sistema
            ventanaGraf.setVisible(true);
        });

        menuItemRepInventario.addActionListener(e -> {
            lblEstado.setText("Acción: Reporte de Inventario de Equipos");
            // Abrir VentanaReporteInventarioEquipos
            VentanaReporteInventarioEquipos ventanaRepInv = new VentanaReporteInventarioEquipos(sistema, this); // Pasa la instancia de sistema
            ventanaRepInv.setVisible(true);
        });

        menuItemRepOperaciones.addActionListener(e -> {
            lblEstado.setText("Acción: Reporte de Operaciones de Mantenimiento");
            // Abrir VentanaReporteOperacionesMantenimiento
            VentanaReporteOperacionesMantenimiento ventanaRepOp = new VentanaReporteOperacionesMantenimiento(sistema,this); // Pasa la instancia de sistema
            ventanaRepOp.setVisible(true);
        });

        menuItemRepOrdenes.addActionListener(e -> {
            lblEstado.setText("Acción: Reporte de Órdenes de Trabajo");
            // Abrir VentanaReporteOrdenesTrabajo
            VentanaReporteOrdenesTrabajo ventanaRepOrd = new VentanaReporteOrdenesTrabajo(sistema,this); // Pasa la instancia de sistema
            ventanaRepOrd.setVisible(true);
        });
    }

    /**
     * Carga el árbol de navegación con la información del equipo raíz.
     * @param equipoRaiz El equipo raíz para construir el árbol.
     */
    private void cargarArbolNavegacion(NodoEquipo equipoRaiz) {
       
        DefaultMutableTreeNode nodoRaizArbol = new DefaultMutableTreeNode(equipoRaiz);
        // Construir los nodos dependientes inmediatos (componentes, mantenimiento)
        construirNodosInmediatos(nodoRaizArbol, equipoRaiz);

        // Crear el modelo del árbol y asignarlo
        DefaultTreeModel modelo = new DefaultTreeModel(nodoRaizArbol);
        arbolNavegacion.setModel(modelo);

        // Expandir el nodo raíz para mostrar sus nodos inmediatos
        arbolNavegacion.expandRow(0); // Expande la primera fila (el nodo raíz)
    }

    /**
     * Construye los nodos inmediatos (componentes, mantenimiento) para un nodo padre en el árbol.
     * @param nodoPadreArbol El nodo padre en el modelo del árbol Swing.
     * @param equipoActual El objeto NodoEquipo correspondiente al nodo padre.
     */
    private void construirNodosInmediatos(DefaultMutableTreeNode nodoPadreArbol, NodoEquipo equipoActual) {
        
        nodoPadreArbol.removeAllChildren();

        NodoEquipo hijo = equipoActual.getPrimerHijo();
        if (hijo != null) {
            
            while (hijo != null) {
                DefaultMutableTreeNode nodoHijo = new DefaultMutableTreeNode(hijo);
                nodoPadreArbol.add(nodoHijo);
                
                hijo = hijo.getSiguienteHermano();
                if (hijo != null) {
                }
            }
        } else {
        }

        ProgramaMantenimientoPreventivo programa = sistema.obtenerProgramaDeEquipo(equipoActual.getId());
        if (programa != null && !programa.getFases().isEmpty()) { // <-- Condición crucial
            
            DefaultMutableTreeNode nodoPreventivo = new DefaultMutableTreeNode("MANTENIMIENTO PREVENTIVO");
            for (int i = 0; i < programa.getFases().size(); i++) {
                FaseMantenimiento fase = programa.getFases().get(i);
                DefaultMutableTreeNode nodoFase = new DefaultMutableTreeNode(fase);
                // Buscar órdenes preventivas asociadas a esta fase para este equipo
                List<OrdenTrabajo> ordenesEquipo = sistema.obtenerOrdenesPorEquipo(equipoActual.getId()).stream()
                        .filter(orden -> orden instanceof OrdenTrabajoPreventiva)
                        .collect(Collectors.toList()); // Usa Collectors importado
                for (OrdenTrabajo orden : ordenesEquipo) {
                    if (orden instanceof OrdenTrabajoPreventiva) {
                        if (((OrdenTrabajoPreventiva) orden).getIdFase() == i) {
                             
                             nodoFase.add(new DefaultMutableTreeNode(orden));
                        } else {
                            
                        }
                    }
                }
                nodoPreventivo.add(nodoFase);
            }
            nodoPadreArbol.add(nodoPreventivo);
        } else {
           
        }
        List<OrdenTrabajo> ordenesCorrectivas = sistema.obtenerOrdenesPorEquipo(equipoActual.getId()).stream()
                .filter(orden -> orden instanceof OrdenTrabajoCorrectiva)
                .collect(Collectors.toList()); // Usa Collectors importado
        if (!ordenesCorrectivas.isEmpty()) { 
           
            DefaultMutableTreeNode nodoCorrectivo = new DefaultMutableTreeNode("MANTENIMIENTO CORRECTIVO");
            for (OrdenTrabajo orden : ordenesCorrectivas) {
               
                 nodoCorrectivo.add(new DefaultMutableTreeNode(orden));
            }
            nodoPadreArbol.add(nodoCorrectivo); 
        } else {
           
        }

    }

    // Nuevo método para añadir hijos y mantenimiento a un nodo existente (cuando se expande)
    /**
     * Añade los hijos directos y nodos de mantenimiento a un nodo del árbol si no los tiene ya.
     * @param nodoPadre El nodo padre en el modelo del árbol Swing.
     * @param equipoActual El objeto NodoEquipo correspondiente al nodo padre.
     */
    private void agregarHijosYMantenimiento(DefaultMutableTreeNode nodoPadre, NodoEquipo equipoActual) {
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

        // Añadir nodos de mantenimiento (igual que en construirNodosInmediatos)
        // 2. Mantenimiento Preventivo
        ProgramaMantenimientoPreventivo programa = sistema.obtenerProgramaDeEquipo(equipoActual.getId());
        if (programa != null && !programa.getFases().isEmpty()) {
            DefaultMutableTreeNode nodoPreventivo = new DefaultMutableTreeNode("MANTENIMIENTO PREVENTIVO");
            for (int i = 0; i < programa.getFases().size(); i++) {
                FaseMantenimiento fase = programa.getFases().get(i);
                DefaultMutableTreeNode nodoFase = new DefaultMutableTreeNode(fase);
                List<OrdenTrabajo> ordenesEquipo = sistema.obtenerOrdenesPorEquipo(equipoActual.getId()).stream()
                        .filter(orden -> orden instanceof OrdenTrabajoPreventiva)
                        .collect(Collectors.toList());
                for (OrdenTrabajo orden : ordenesEquipo) {
                    if (orden instanceof OrdenTrabajoPreventiva) {
                        if (((OrdenTrabajoPreventiva) orden).getIdFase() == i) {
                             nodoFase.add(new DefaultMutableTreeNode(orden));
                        }
                    }
                }
                nodoPreventivo.add(nodoFase);
            }
            nodoPadre.add(nodoPreventivo);
        }

        // 3. Mantenimiento Correctivo
        List<OrdenTrabajo> ordenesCorrectivas = sistema.obtenerOrdenesPorEquipo(equipoActual.getId()).stream()
                .filter(orden -> orden instanceof OrdenTrabajoCorrectiva)
                .collect(Collectors.toList());
        if (!ordenesCorrectivas.isEmpty()) {
            DefaultMutableTreeNode nodoCorrectivo = new DefaultMutableTreeNode("MANTENIMIENTO CORRECTIVO");
            for (OrdenTrabajo orden : ordenesCorrectivas) {
                 nodoCorrectivo.add(new DefaultMutableTreeNode(orden));
            }
            nodoPadre.add(nodoCorrectivo);
        }

        // Actualizar el modelo del árbol
        ((DefaultTreeModel) arbolNavegacion.getModel()).nodeStructureChanged(nodoPadre);
    }

    /**
     * Muestra los detalles de un nodo seleccionado en el panel derecho.
     * @param nodo El objeto asociado al nodo del árbol seleccionado.
     */
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
            JLabel lblEspec = new JLabel("<html>" + espec.replace(" ", "<br>") + "</html>");
            lblEspec.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5)); // Margen interno
            panelInfo.add(lblEspec);

            // --- Información de Garantía ---
            panelInfo.add(new JLabel("Información de Garantía:"));
            String garantia = equipo.getInformacionGarantia() != null ? equipo.getInformacionGarantia() : "N/A";
            JLabel lblGarantia = new JLabel("<html>" + garantia.replace(" ", "<br>") + "</html>");
            lblGarantia.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
            panelInfo.add(lblGarantia);

            // --- INFORMACIÓN DE MANTENIMIENTO (Resumen al seleccionar el equipo) ---
            panelInfo.add(Box.createVerticalStrut(15)); // Espacio antes de la sección de mantenimiento
            JLabel lblTituloMantenimiento = new JLabel("INFORMACIÓN DE MANTENIMIENTO", SwingConstants.LEFT);
            lblTituloMantenimiento.setFont(new Font("Arial", Font.BOLD, 14));
            lblTituloMantenimiento.setForeground(Color.BLUE.darker()); // Color para destacar
            panelInfo.add(lblTituloMantenimiento);

            // Obtener y mostrar IDs de Fases del Programa Preventivo
            ProgramaMantenimientoPreventivo programa = sistema.obtenerProgramaDeEquipo(equipo.getId());
            if (programa != null && !programa.getFases().isEmpty()) {
                panelInfo.add(new JLabel("Fases de Mantenimiento Preventivo:"));
                StringBuilder fasesTexto = new StringBuilder();
                for (int i = 0; i < programa.getFases().size(); i++) {
                    fasesTexto.append("- Fase #").append(i).append(": ").append(programa.getFases().get(i).getTipoFrecuencia()).append(" cada ").append(programa.getFases().get(i).getMedidorFrecuencia()).append(" ").append(programa.getFases().get(i).getTipoFrecuencia()).append("<br>"); // Usa <br> para saltos de línea en HTML
                }
                JLabel lblFases = new JLabel("<html>" + fasesTexto.toString() + "</html>");
                lblFases.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Margen interno
                panelInfo.add(lblFases);
            } else {
                panelInfo.add(new JLabel("No hay fases de mantenimiento preventivo registradas para este equipo."));
            }

            // Obtener y mostrar IDs de Órdenes de Trabajo
            List<OrdenTrabajo> ordenesEquipo = sistema.obtenerOrdenesPorEquipo(equipo.getId());
            if (!ordenesEquipo.isEmpty()) {
                panelInfo.add(new JLabel("Órdenes de Trabajo Asociadas:"));
                StringBuilder ordenesTexto = new StringBuilder();
                for (OrdenTrabajo orden : ordenesEquipo) {
                    String tipoOrden = (orden instanceof OrdenTrabajoPreventiva) ? "Preventiva" : "Correctiva";
                    ordenesTexto.append("- Orden #").append(orden.getId()).append(" (").append(tipoOrden).append(") - Estado: ").append(orden.getEstado()).append("<br>");
                }
                JLabel lblOrdenes = new JLabel("<html>" + ordenesTexto.toString() + "</html>");
                lblOrdenes.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                panelInfo.add(lblOrdenes);
            } else {
                panelInfo.add(new JLabel("No hay órdenes de trabajo registradas para este equipo."));
            }

        } else if (nodo instanceof FaseMantenimiento) {
            FaseMantenimiento fase = (FaseMantenimiento) nodo;
            JLabel lblTitulo = new JLabel("Detalles de la Fase", SwingConstants.LEFT);
            lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
            panelInfo.add(lblTitulo);
            panelInfo.add(Box.createVerticalStrut(10)); 

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
            panelInfo.add(Box.createVerticalStrut(10)); 

            mostrarDetalleOrdenComun(panelInfo, orden);
            panelInfo.add(new JLabel("ID Fase de Programa: " + orden.getIdFase()));
            panelInfo.add(new JLabel("Tareas Asociadas:"));
            JTextArea txtTareas = new JTextArea();
            StringBuilder tareasTexto = new StringBuilder();
            for (var tarea : orden.getTareas()) {
                tareasTexto.append("- ").append(tarea.getDescripcion()).append(" ");
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
            panelInfo.add(Box.createVerticalStrut(10)); 

            mostrarDetalleOrdenComun(panelInfo, orden);

        } else if (nodo instanceof String) { // Nodo de grupo como "MANTENIMIENTO PREVENTIVO", "MANTENIMIENTO CORRECTIVO"
             JLabel lblTitulo = new JLabel("Detalles de " + (String)nodo, SwingConstants.LEFT);
             lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
             panelInfo.add(lblTitulo);
             panelInfo.add(Box.createVerticalStrut(10)); 
             JLabel lblResumen = new JLabel("Este nodo agrupa los elementos relacionados mostrados debajo en el árbol.");
             panelInfo.add(lblResumen);
        }

        panelDetalle.add(panelInfo, BorderLayout.CENTER);
        panelDetalle.revalidate();
        panelDetalle.repaint();
    }

    /**
     * Muestra los campos comunes de una OrdenTrabajo.
     * @param panelInfo El panel donde se agregarán los componentes.
     * @param orden La orden de trabajo.
     */
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
                fallasRepTexto.append("- Causas: ").append(falla.getCausas()).append(", Acciones: ").append(falla.getAccionesTomadas()).append(" ");
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
                fallasEncTexto.append("- ID: ").append(falla.getIdFalla()).append(", Desc: ").append(falla.getDescripcionFalla()).append(", Causas: ").append(falla.getCausas()).append(", Acciones: ").append(falla.getAccionesTomadas()).append(" ");
            }
            txtFallasEnc.setText(fallasEncTexto.toString());
            txtFallasEnc.setEditable(false);
            txtFallasEnc.setLineWrap(true);
            txtFallasEnc.setWrapStyleWord(true);
            panelInfo.add(new JScrollPane(txtFallasEnc));
        }
    }

    /**
     * Actualiza la vista del árbol (llamado desde otras ventanas, como VentanaEliminarEquipo).
     * Recarga el árbol con el nodo raíz actualmente visualizado.
     */
    public void actualizarVistaArbol() {
        if (nodoRaizBuscado != null) {
            // Vuelve a cargar el árbol con el nodo raíz actual
            cargarArbolNavegacion(nodoRaizBuscado);
            // Muestra la información del nodo raíz otra vez
            mostrarDetalleNodo(nodoRaizBuscado);
        }
        // Si no hay un nodo raíz buscado, no hay nada que actualizar en la vista de árbol actual.
    }

    public static void main(String[] args) {
        SistemaPrincipal sistema = new SistemaPrincipal(); // Inicializa el sistema

        SwingUtilities.invokeLater(() -> {
            try {
                // Intenta usar el look and feel del sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si falla, usa el look and feel por defecto de Java
                e.printStackTrace();
            }
            new VentanaMenuPrincipal(sistema).setVisible(true); // Crea y muestra la ventana
        });
    }
}