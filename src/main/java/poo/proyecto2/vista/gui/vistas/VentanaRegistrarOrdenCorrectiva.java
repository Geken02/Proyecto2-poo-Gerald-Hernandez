package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorRegistrarOrdenCorrectiva;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VentanaRegistrarOrdenCorrectiva extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre;

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo;
    private JButton btnBuscarEquipo;
    private JLabel lblInfoEquipo; 
    private JLabel lblFechaOrden;
    private JFormattedTextField txtFechaOrden; 
    private JLabel lblFechaEjecucion;
    private JFormattedTextField txtFechaEjecucion; 
    private JLabel lblObservaciones;
    private JTextArea txtObservaciones;
    private JScrollPane scrollObservaciones;

    // Panel para gestionar fallas reportadas (solo causas y acciones)
    private JPanel panelFallasReportadas;
    private JLabel lblFallasRepTitulo;
    private JScrollPane scrollTablaFallasRep;
    private JTable tablaFallasRep;
    private DefaultTableModel modeloTablaFallasRep;
    private JButton btnAgregarFallaRep;
    private JButton btnEliminarFallaRep;

    // Ventana secundaria para agregar falla reportada (JDialog)
    private JDialog ventanaAgregarFallaRep;
    private JTextField txtCausasRep;
    private JTextField txtAccionesRep;
    private JButton btnAceptarAgregarRep;
    private JButton btnCancelarAgregarRep;

    private JButton btnRegistrar;
    private JButton btnCancelar;

    // Columnas de la tabla de fallas reportadas
    private static final String[] COLUMNAS_FALLAS_REP = {"Causas", "Acciones Tomadas"};

    // Variables temporales para almacenar fallas reportadas antes de guardar
    private List<OrdenTrabajo.FallaReportada> fallasReportadasTemp;

    // Referencia al controlador
    private ControladorRegistrarOrdenCorrectiva controlador;

    public VentanaRegistrarOrdenCorrectiva(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        this.fallasReportadasTemp = new ArrayList<>(); 
        // Crear el controlador pasando el modelo y la vista
        this.controlador = new ControladorRegistrarOrdenCorrectiva(sistema, this);
        inicializarComponentes();
        configurarEventos();
        setTitle("Registrar Nueva Orden de Trabajo Correctiva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(800, 600); // Tamaño ajustado
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Registrar Nueva Orden de Trabajo Correctiva", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Formulario ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        lblIdEquipo = new JLabel("ID del Equipo *:");
        panelFormulario.add(lblIdEquipo, gbc);

        gbc.gridx = 1; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 
        txtIdEquipo = new JTextField(30); 
        txtIdEquipo.setMinimumSize(new Dimension(150, 25)); 
        panelFormulario.add(txtIdEquipo, gbc);
        gbc.weightx = 0.0; 

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        btnBuscarEquipo = new JButton("Buscar");
        panelFormulario.add(btnBuscarEquipo, gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblInfoEquipo = new JLabel(" (Equipo no encontrado)");
        lblInfoEquipo.setFont(lblInfoEquipo.getFont().deriveFont(Font.ITALIC));
        panelFormulario.add(lblInfoEquipo, gbc);

        // Fila 1: Fecha Orden
        gbc.gridx = 0; gbc.gridy = 1;
        lblFechaOrden = new JLabel("Fecha de la Orden *:");
        panelFormulario.add(lblFechaOrden, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFechaOrden = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaOrden.setValue(LocalDate.now()); // Fecha por defecto: hoy
        panelFormulario.add(txtFechaOrden, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 2: Fecha Ejecución
        gbc.gridx = 0; gbc.gridy = 2;
        lblFechaEjecucion = new JLabel("Fecha de Ejecución *:");
        panelFormulario.add(lblFechaEjecucion, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFechaEjecucion = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaEjecucion.setValue(LocalDate.now().plusDays(1)); // Fecha por defecto: mañana
        panelFormulario.add(txtFechaEjecucion, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 3: Observaciones
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;
        lblObservaciones = new JLabel("Observaciones Iniciales:");
        panelFormulario.add(lblObservaciones, gbc);

        gbc.gridy = 4;
        txtObservaciones = new JTextArea(3, 30);
        scrollObservaciones = new JScrollPane(txtObservaciones);
        panelFormulario.add(scrollObservaciones, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel Superior Central: Fallas Reportadas ---
        panelFallasReportadas = new JPanel(new BorderLayout());
        panelFallasReportadas.setBorder(BorderFactory.createTitledBorder("Fallas Reportadas Inicialmente"));

        modeloTablaFallasRep = new DefaultTableModel(COLUMNAS_FALLAS_REP, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No se edita directamente la tabla
            }
        };
        tablaFallasRep = new JTable(modeloTablaFallasRep);
        scrollTablaFallasRep = new JScrollPane(tablaFallasRep);
        panelFallasReportadas.add(scrollTablaFallasRep, BorderLayout.CENTER);

        JPanel panelBotonesRep = new JPanel(new FlowLayout());
        btnAgregarFallaRep = new JButton("Agregar");
        btnEliminarFallaRep = new JButton("Eliminar");
        panelBotonesRep.add(btnAgregarFallaRep);
        panelBotonesRep.add(btnEliminarFallaRep);
        panelFallasReportadas.add(panelBotonesRep, BorderLayout.SOUTH);

        // Añadir el panel de fallas reportadas al sur del formulario principal
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        panelFormulario.add(panelFallasReportadas, gbc);
        gbc.weighty = 0.0; // Resetear weighty

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar Orden");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnBuscarEquipo.addActionListener(e -> controlador.buscarEquipo()); 
        btnAgregarFallaRep.addActionListener(e -> controlador.agregarFallaReportada());
        btnEliminarFallaRep.addActionListener(e -> controlador.eliminarFallaReportada());
        btnRegistrar.addActionListener(e -> controlador.registrarOrden());
        btnCancelar.addActionListener(e -> controlador.cancelar()); 
    }

    // --- Métodos para que el controlador interactúe con la vista ---
    public JTextField getTxtIdEquipo() { return txtIdEquipo; }
    public JFormattedTextField getTxtFechaOrden() { return txtFechaOrden; }
    public JFormattedTextField getTxtFechaEjecucion() { return txtFechaEjecucion; }
    public JTextArea getTxtObservaciones() { return txtObservaciones; }

    public void mostrarInfoEquipo(String texto) { lblInfoEquipo.setText(texto); }
    public void mostrarMensaje(String mensaje, String titulo, int tipo) { JOptionPane.showMessageDialog(this, mensaje, titulo, tipo); }
    public void limpiarInfoEquipo() { lblInfoEquipo.setText(" (Equipo no encontrado)"); }

    public JTable getTablaFallasRep() { return tablaFallasRep; }
    public DefaultTableModel getModeloTablaFallasRep() { return modeloTablaFallasRep; }

    public List<OrdenTrabajo.FallaReportada> getFallasReportadasTemp() { return new ArrayList<>(fallasReportadasTemp); } // Devolver copia

    public void limpiarFormulario() {
        txtIdEquipo.setText("");
        limpiarInfoEquipo();
        txtFechaOrden.setValue(LocalDate.now());
        txtFechaEjecucion.setValue(LocalDate.now().plusDays(1));
        txtObservaciones.setText("");
        modeloTablaFallasRep.setRowCount(0);
        fallasReportadasTemp.clear();
    }

    public void cerrarVentana() { this.dispose(); }

    // --- Métodos específicos para la gestión de fallas reportadas ---
    public void abrirVentanaAgregarFallaRep() {
        if (ventanaAgregarFallaRep != null && ventanaAgregarFallaRep.isVisible()) {
            ventanaAgregarFallaRep.toFront();
            return;
        }

        ventanaAgregarFallaRep = new JDialog(this, "Agregar Falla Reportada", true);
        ventanaAgregarFallaRep.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaAgregarFallaRep.setLayout(new BorderLayout());

        JPanel panelAgregarFalla = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panelAgregarFalla.add(new JLabel("Causas *:"), gbc);
        txtCausasRep = new JTextField(20);
        gbc.gridx = 1;
        panelAgregarFalla.add(txtCausasRep, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelAgregarFalla.add(new JLabel("Acciones Tomadas *:"), gbc);
        txtAccionesRep = new JTextField(20);
        gbc.gridx = 1;
        panelAgregarFalla.add(txtAccionesRep, gbc);

        ventanaAgregarFallaRep.add(panelAgregarFalla, BorderLayout.CENTER);

        JPanel panelBotonesAgregar = new JPanel(new FlowLayout());
        btnAceptarAgregarRep = new JButton("Aceptar");
        btnCancelarAgregarRep = new JButton("Cancelar");
        panelBotonesAgregar.add(btnAceptarAgregarRep);
        panelBotonesAgregar.add(btnCancelarAgregarRep);
        ventanaAgregarFallaRep.add(panelBotonesAgregar, BorderLayout.SOUTH);

        btnAceptarAgregarRep.addActionListener(aceptarEvt -> {
            String causas = txtCausasRep.getText().trim();
            String acciones = txtAccionesRep.getText().trim();

            if (causas.isEmpty() || acciones.isEmpty()) {
                JOptionPane.showMessageDialog(ventanaAgregarFallaRep, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear la falla reportada y añadirla a la lista temporal
            OrdenTrabajo.FallaReportada fallaRep = new OrdenTrabajo.FallaReportada(causas, acciones);
            fallasReportadasTemp.add(fallaRep);

            // Añadir la fila a la tabla de la ventana principal
            modeloTablaFallasRep.addRow(new Object[]{causas, acciones});

            ventanaAgregarFallaRep.dispose();
        });

        btnCancelarAgregarRep.addActionListener(cancelarEvt -> ventanaAgregarFallaRep.dispose());

        ventanaAgregarFallaRep.pack();
        ventanaAgregarFallaRep.setLocationRelativeTo(this);
        ventanaAgregarFallaRep.setVisible(true);
    }

    public void eliminarFallaReportadaSeleccionada() {
        int filaSeleccionada = tablaFallasRep.getSelectedRow();
        if (filaSeleccionada >= 0) {
            fallasReportadasTemp.remove(filaSeleccionada);
            modeloTablaFallasRep.removeRow(filaSeleccionada);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una falla reportada de la lista para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
}