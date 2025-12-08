package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.modelo.equipos.FallaEquipo; 
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorEditarOrdenCorrectiva;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class VentanaEditarOrdenCorrectiva extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre;
    private OrdenTrabajoCorrectiva ordenAErEditar; 

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdOrden;
    private JTextField txtIdOrden;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo;
    private JLabel lblEstado;
    private JTextField txtEstado;
    private JLabel lblFechaOrden;
    private JLabel lblFechaEjecucion;
    private JLabel lblFechasInfo; 

    // Panel para gestionar fallas encontradas
    private JPanel panelFallas;
    private JLabel lblFallasTitulo;
    private JScrollPane scrollTablaFallas;
    private JTable tablaFallas;
    private DefaultTableModel modeloTablaFallas;

    // Panel Inferior: Acciones
    private JPanel panelAcciones;
    private JPanel panelIniciar;
    private JLabel lblIniciarTitulo;
    private JLabel lblFechaInicioReal;
    private JTextField txtFechaInicioReal; 
    private JButton btnIniciar;

    private JPanel panelFinalizar;
    private JLabel lblFinalizarTitulo;
    private JLabel lblFechaFinReal;
    private JTextField txtFechaFinReal; 
    private JLabel lblHorasTrabajo;
    private JTextField txtHorasTrabajo; 
    private JLabel lblCostoManoObra;
    private JTextField txtCostoManoObra; 
    private JLabel lblCostoMateriales;
    private JTextField txtCostoMateriales; 
    private JLabel lblObservacionesEjecucion;
    private JTextArea txtObservacionesEjecucion;
    private JScrollPane scrollObsEjecucion;
    private JButton btnFinalizar;

    private JPanel panelCancelar;
    private JLabel lblCancelarTitulo;
    private JLabel lblFechaCancelacion;
    private JTextField txtFechaCancelacion; 
    private JLabel lblMotivoCancelacion;
    private JTextArea txtMotivoCancelacion;
    private JScrollPane scrollMotivoCancelacion;
    private JButton btnCancelar;

    private JButton btnCerrar;

    // Columnas de la tabla de fallas
    private static final String[] COLUMNAS_FALLAS = {"Tipo", "ID Falla", "Descripción", "Causas", "Acciones Tomadas"};

    // Referencia al controlador
    private ControladorEditarOrdenCorrectiva controlador;

    public VentanaEditarOrdenCorrectiva(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre, OrdenTrabajoCorrectiva ordenAErEditar) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        this.ordenAErEditar = ordenAErEditar;
        // Crear el controlador pasando el sistema, la vista y la orden a editar
        this.controlador = new ControladorEditarOrdenCorrectiva(sistema, this, ordenAErEditar);
        inicializarComponentes();
        cargarDatosIniciales();
        configurarEventos();
        setTitle("Editar Orden Correctiva ID: " + ordenAErEditar.getId());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(1000, 700); 
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Información General ---
        JPanel panelInfoGeneral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        lblIdOrden = new JLabel("ID Orden:");
        panelInfoGeneral.add(lblIdOrden, gbc);

        gbc.gridx = 1;
        txtIdOrden = new JTextField(String.valueOf(ordenAErEditar.getId()));
        txtIdOrden.setEditable(false);
        txtIdOrden.setBackground(new Color(240, 240, 240));
        panelInfoGeneral.add(txtIdOrden, gbc);

        gbc.gridx = 2;
        lblIdEquipo = new JLabel("ID Equipo:");
        panelInfoGeneral.add(lblIdEquipo, gbc);

        gbc.gridx = 3;
        txtIdEquipo = new JTextField(String.valueOf(ordenAErEditar.getIdEquipo()));
        txtIdEquipo.setEditable(false);
        txtIdEquipo.setBackground(new Color(240, 240, 240));
        panelInfoGeneral.add(txtIdEquipo, gbc);

        gbc.gridx = 4;
        lblEstado = new JLabel("Estado:");
        panelInfoGeneral.add(lblEstado, gbc);

        gbc.gridx = 5;
        txtEstado = new JTextField(ordenAErEditar.getEstado().toString());
        txtEstado.setEditable(false);
        txtEstado.setBackground(new Color(240, 240, 240));
        panelInfoGeneral.add(txtEstado, gbc);

        gbc.gridx = 6; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblFechasInfo = new JLabel("Fecha Orden: " + (ordenAErEditar.getFechaOrden() != null ? ordenAErEditar.getFechaOrden().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A") +
                                   " | Fecha Ejecución: " + (ordenAErEditar.getFechaEjecucion() != null ? ordenAErEditar.getFechaEjecucion().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A"));
        lblFechasInfo.setFont(lblFechasInfo.getFont().deriveFont(Font.ITALIC));
        panelInfoGeneral.add(lblFechasInfo, gbc);

        add(panelInfoGeneral, BorderLayout.NORTH);

        // --- Panel Central: Fallas ---
        panelFallas = new JPanel(new BorderLayout());
        panelFallas.setBorder(BorderFactory.createTitledBorder("Fallas Reportadas/Encontradas"));
        modeloTablaFallas = new DefaultTableModel(COLUMNAS_FALLAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaFallas = new JTable(modeloTablaFallas);
        scrollTablaFallas = new JScrollPane(tablaFallas);
        panelFallas.add(scrollTablaFallas, BorderLayout.CENTER);

        add(panelFallas, BorderLayout.CENTER);

        // --- Panel Inferior: Acciones (Iniciar, Finalizar, Cancelar) ---
        panelAcciones = new JPanel(new GridLayout(1, 3, 10, 10)); 

        // Panel Iniciar
        panelIniciar = new JPanel(new GridBagLayout());
        panelIniciar.setBorder(BorderFactory.createTitledBorder("Registrar Inicio de la Orden"));
        GridBagConstraints gbcIniciar = new GridBagConstraints();
        gbcIniciar.insets = new Insets(5, 5, 5, 5);
        gbcIniciar.anchor = GridBagConstraints.WEST;

        gbcIniciar.gridx = 0; gbcIniciar.gridy = 0;
        lblFechaInicioReal = new JLabel("Fecha de Inicio Real *:");
        panelIniciar.add(lblFechaInicioReal, gbcIniciar);

        gbcIniciar.gridx = 1;
     
        txtFechaInicioReal = new JTextField(10); 
        txtFechaInicioReal.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)); 
        txtFechaInicioReal.setEnabled(false); 
        panelIniciar.add(txtFechaInicioReal, gbcIniciar);


        gbcIniciar.gridx = 0; gbcIniciar.gridy = 1; gbcIniciar.gridwidth = 2; gbcIniciar.fill = GridBagConstraints.HORIZONTAL;
        btnIniciar = new JButton("Iniciar Orden");
        btnIniciar.setEnabled(false);
        panelIniciar.add(btnIniciar, gbcIniciar);

        // Panel Finalizar
        panelFinalizar = new JPanel(new GridBagLayout());
        panelFinalizar.setBorder(BorderFactory.createTitledBorder("Registrar Finalización de la Orden"));
        GridBagConstraints gbcFinalizar = new GridBagConstraints();
        gbcFinalizar.insets = new Insets(5, 5, 5, 5);
        gbcFinalizar.anchor = GridBagConstraints.WEST;

        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 0;
        lblFechaFinReal = new JLabel("Fecha de Fin Real *:");
        panelFinalizar.add(lblFechaFinReal, gbcFinalizar);

        gbcFinalizar.gridx = 1;

        txtFechaFinReal = new JTextField(10);
        txtFechaFinReal.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)); 
        txtFechaFinReal.setEnabled(false); 
        panelFinalizar.add(txtFechaFinReal, gbcFinalizar);


        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 1;
        lblHorasTrabajo = new JLabel("Horas de Trabajo *:");
        panelFinalizar.add(lblHorasTrabajo, gbcFinalizar);

        gbcFinalizar.gridx = 1;

        txtHorasTrabajo = new JTextField(10);
        txtHorasTrabajo.setText("0.0"); 
        txtHorasTrabajo.setEnabled(false); 
        panelFinalizar.add(txtHorasTrabajo, gbcFinalizar);
 

        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 2;
        lblCostoManoObra = new JLabel("Costo Mano de Obra *:");
        panelFinalizar.add(lblCostoManoObra, gbcFinalizar);

        gbcFinalizar.gridx = 1;

        txtCostoManoObra = new JTextField(15); 
        txtCostoManoObra.setText("0.0");
        txtCostoManoObra.setEnabled(false); 
        panelFinalizar.add(txtCostoManoObra, gbcFinalizar);


        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 3;
        lblCostoMateriales = new JLabel("Costo Materiales *:");
        panelFinalizar.add(lblCostoMateriales, gbcFinalizar);

        gbcFinalizar.gridx = 1;

        txtCostoMateriales = new JTextField(15); 
        txtCostoMateriales.setText("0.0"); 
        txtCostoMateriales.setEnabled(false); 
        panelFinalizar.add(txtCostoMateriales, gbcFinalizar);


        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 4; gbcFinalizar.gridwidth = 2; gbcFinalizar.fill = GridBagConstraints.BOTH;
        lblObservacionesEjecucion = new JLabel("Observaciones de Ejecución:");
        panelFinalizar.add(lblObservacionesEjecucion, gbcFinalizar);

        gbcFinalizar.gridy = 5;
        txtObservacionesEjecucion = new JTextArea(3, 30);
        txtObservacionesEjecucion.setEnabled(false); 
        scrollObsEjecucion = new JScrollPane(txtObservacionesEjecucion);
        panelFinalizar.add(scrollObsEjecucion, gbcFinalizar);

        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 6; gbcFinalizar.gridwidth = 2; gbcFinalizar.fill = GridBagConstraints.NONE;
        btnFinalizar = new JButton("Finalizar Orden");
        btnFinalizar.setEnabled(false);
        panelFinalizar.add(btnFinalizar, gbcFinalizar);

        // Panel Cancelar
        panelCancelar = new JPanel(new GridBagLayout());
        panelCancelar.setBorder(BorderFactory.createTitledBorder("Cancelar la Orden"));
        GridBagConstraints gbcCancelar = new GridBagConstraints();
        gbcCancelar.insets = new Insets(5, 5, 5, 5);
        gbcCancelar.anchor = GridBagConstraints.WEST;

        gbcCancelar.gridx = 0; gbcCancelar.gridy = 0;
        lblFechaCancelacion = new JLabel("Fecha de Cancelación *:");
        panelCancelar.add(lblFechaCancelacion, gbcCancelar);

        gbcCancelar.gridx = 1;

        txtFechaCancelacion = new JTextField(10); 
        txtFechaCancelacion.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)); 
        txtFechaCancelacion.setEnabled(false); 
        panelCancelar.add(txtFechaCancelacion, gbcCancelar);


        gbcCancelar.gridx = 0; gbcCancelar.gridy = 1; gbcCancelar.gridwidth = 2; gbcCancelar.fill = GridBagConstraints.BOTH;
        lblMotivoCancelacion = new JLabel("Motivo de Cancelación *:");
        panelCancelar.add(lblMotivoCancelacion, gbcCancelar);

        gbcCancelar.gridy = 2;
        txtMotivoCancelacion = new JTextArea(3, 30);
        txtMotivoCancelacion.setEnabled(false); 
        scrollMotivoCancelacion = new JScrollPane(txtMotivoCancelacion);
        panelCancelar.add(scrollMotivoCancelacion, gbcCancelar);

        gbcCancelar.gridx = 0; gbcCancelar.gridy = 3; gbcCancelar.gridwidth = 2; gbcCancelar.fill = GridBagConstraints.NONE;
        btnCancelar = new JButton("Cancelar Orden");
        btnCancelar.setEnabled(false); 
        panelCancelar.add(btnCancelar, gbcCancelar);

        // Añadir los tres paneles al panel de acciones
        panelAcciones.add(panelIniciar);
        panelAcciones.add(panelFinalizar);
        panelAcciones.add(panelCancelar);

        add(panelAcciones, BorderLayout.SOUTH);

        // --- Botón Cerrar ---
        JPanel panelBotonCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCerrar = new JButton("Cerrar");
        panelBotonCerrar.add(btnCerrar);
        add(panelBotonCerrar, BorderLayout.SOUTH);
    }

    private void cargarDatosIniciales() {
        // Cargar Fallas (reportadas y encontradas)
        modeloTablaFallas.setRowCount(0);
        // Fallas Reportadas
        for (var falla : ordenAErEditar.getFallasReportadas()) {
            Object[] fila = {"Reportada", "(N/A)", "(No aplica)", falla.getCausas(), falla.getAccionesTomadas()};
            modeloTablaFallas.addRow(fila);
        }
        // Fallas Encontradas
        for (var falla : ordenAErEditar.getFallasEncontradas()) {
            Object[] fila = {"Encontrada", falla.getIdFalla(), falla.getDescripcionFalla(), falla.getCausas(), falla.getAccionesTomadas()};
            modeloTablaFallas.addRow(fila);
        }

        // Cargar datos de inicio/fin/cancelación si existen
        if (ordenAErEditar.getFechaInicioReal() != null) {

            txtFechaInicioReal.setText(ordenAErEditar.getFechaInicioReal().format(DateTimeFormatter.ISO_LOCAL_DATE));

        }
        if (ordenAErEditar.getFechaFinReal() != null) {

            txtFechaFinReal.setText(ordenAErEditar.getFechaFinReal().format(DateTimeFormatter.ISO_LOCAL_DATE));
            txtHorasTrabajo.setText(String.valueOf(ordenAErEditar.getHorasTrabajo()));
            txtCostoManoObra.setText(String.valueOf(ordenAErEditar.getCostoManoObra()));
            txtCostoMateriales.setText(String.valueOf(ordenAErEditar.getCostoMateriales()));

            txtObservacionesEjecucion.setText(ordenAErEditar.getObservacionesEjecucion() != null ? ordenAErEditar.getObservacionesEjecucion() : "");
        }
        if (ordenAErEditar.getFechaCancelacion() != null) {

            txtFechaCancelacion.setText(ordenAErEditar.getFechaCancelacion().format(DateTimeFormatter.ISO_LOCAL_DATE));

            txtMotivoCancelacion.setText(ordenAErEditar.getMotivoCancelacion() != null ? ordenAErEditar.getMotivoCancelacion() : "");
        }

        // Actualizar la interfaz según el estado inicial de la orden
        actualizarInterfazSegunEstado();
    }

    public void actualizarInterfazSegunEstado() {
        OrdenTrabajo.EstadoOrden estado = ordenAErEditar.getEstado();
        btnIniciar.setEnabled(estado == OrdenTrabajo.EstadoOrden.PENDIENTE);
        btnFinalizar.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        btnCancelar.setEnabled(estado == OrdenTrabajo.EstadoOrden.PENDIENTE || estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);

        txtFechaInicioReal.setEnabled(estado == OrdenTrabajo.EstadoOrden.PENDIENTE);
        txtFechaFinReal.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        txtHorasTrabajo.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        txtCostoManoObra.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        txtCostoMateriales.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        txtObservacionesEjecucion.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        txtFechaCancelacion.setEnabled(estado == OrdenTrabajo.EstadoOrden.PENDIENTE || estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        txtMotivoCancelacion.setEnabled(estado == OrdenTrabajo.EstadoOrden.PENDIENTE || estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
    }

    private void configurarEventos() {
        btnIniciar.addActionListener(e -> controlador.iniciarOrden()); 
        btnFinalizar.addActionListener(e -> controlador.finalizarOrden());
        btnCancelar.addActionListener(e -> controlador.cancelarOrden());
        btnCerrar.addActionListener(e -> controlador.cancelarVentana());
    }

    // --- Métodos para que el controlador interactúe con la vista ---
    public JTextField getTxtIdOrden() { return txtIdOrden; }
    public JTextField getTxtIdEquipo() { return txtIdEquipo; }
    public JTextField getTxtEstado() { return txtEstado; }

    public JTextField getTxtFechaInicioReal() { return txtFechaInicioReal; }
    public JTextField getTxtFechaFinReal() { return txtFechaFinReal; }
    public JTextField getTxtHorasTrabajo() { return txtHorasTrabajo; }
    public JTextField getTxtCostoManoObra() { return txtCostoManoObra; }
    public JTextField getTxtCostoMateriales() { return txtCostoMateriales; }
    public JTextField getTxtFechaCancelacion() { return txtFechaCancelacion; }

    public JTextArea getTxtObservacionesEjecucion() { return txtObservacionesEjecucion; }
    public JTextArea getTxtMotivoCancelacion() { return txtMotivoCancelacion; }
    public DefaultTableModel getModeloTablaFallas() { return modeloTablaFallas; }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) { JOptionPane.showMessageDialog(this, mensaje, titulo, tipo); }
    public void cerrarVentana() { this.dispose(); }
    // ---
}