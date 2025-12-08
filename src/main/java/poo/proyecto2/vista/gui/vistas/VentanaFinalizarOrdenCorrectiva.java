package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.modelo.equipos.FallaEquipo; 
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorFinalizarOrdenCorrectiva;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VentanaFinalizarOrdenCorrectiva extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; 

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdOrden;
    private JTextField txtIdOrden;
    private JButton btnBuscarOrden;
    private JLabel lblInfoOrden; 
    private JLabel lblFechaFin;
    private JFormattedTextField txtFechaFin;
    private JLabel lblHorasTrabajo;
    private JSpinner spnHorasTrabajo;
    private JLabel lblCostoManoObra;
    private JFormattedTextField txtCostoManoObra;
    private JLabel lblCostoMateriales;
    private JFormattedTextField txtCostoMateriales;
    private JLabel lblObservaciones;
    private JTextArea txtObservaciones;
    private JScrollPane scrollObservaciones;

    // Panel para gestionar fallas encontradas
    private JPanel panelFallas;
    private JLabel lblFallasTitulo;
    private JScrollPane scrollTablaFallas;
    private JTable tablaFallas;
    private DefaultTableModel modeloTablaFallas;
    private JButton btnAgregarFalla;
    private JButton btnEliminarFalla;

    // Ventana secundaria para agregar falla (JDialog)
    private JDialog ventanaAgregarFalla;
    private JComboBox<FallaEquipo> cmbFallasDisponibles;
    private JTextField txtCausas;
    private JTextField txtAcciones;
    private JButton btnAceptarAgregar;
    private JButton btnCancelarAgregar;

    private JButton btnFinalizar;
    private JButton btnCancelar;

    // Columnas de la tabla de fallas
    private static final String[] COLUMNAS_FALLAS = {"ID Falla", "Descripción", "Causas", "Acciones"};

    // Referencia al controlador
    private ControladorFinalizarOrdenCorrectiva controlador;

    public VentanaFinalizarOrdenCorrectiva(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        // Crear el controlador pasando el modelo y la vista
        this.controlador = new ControladorFinalizarOrdenCorrectiva(sistema, this);

        inicializarComponentes();
        configurarEventos();
        setTitle("Finalizar Orden de Trabajo Correctiva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(900, 700);
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Finalizar Orden de Trabajo Correctiva", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Formulario y Fallas ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: ID Orden
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        lblIdOrden = new JLabel("ID de la Orden *:");
        panelFormulario.add(lblIdOrden, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtIdOrden = new JTextField(15);
        txtIdOrden.setMinimumSize(new Dimension(100, 25));
        txtIdOrden.setPreferredSize(new Dimension(150, 25));
        panelFormulario.add(txtIdOrden, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        btnBuscarOrden = new JButton("Buscar");
        panelFormulario.add(btnBuscarOrden, gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblInfoOrden = new JLabel(" (Orden no encontrada o no válida para finalizar)");
        lblInfoOrden.setFont(lblInfoOrden.getFont().deriveFont(Font.ITALIC));
        panelFormulario.add(lblInfoOrden, gbc);

        // Fila 1: Fecha Fin
        gbc.gridx = 0; gbc.gridy = 1;
        lblFechaFin = new JLabel("Fecha de Finalización *:");
        panelFormulario.add(lblFechaFin, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFechaFin = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaFin.setValue(LocalDate.now()); 
        txtFechaFin.setEnabled(false);
        panelFormulario.add(txtFechaFin, gbc);
        gbc.gridwidth = 1; 

        // Fila 2: Horas Trabajo
        gbc.gridx = 0; gbc.gridy = 2;
        lblHorasTrabajo = new JLabel("Horas de Trabajo *:");
        panelFormulario.add(lblHorasTrabajo, gbc);

        gbc.gridx = 1;
        spnHorasTrabajo = new JSpinner(new SpinnerNumberModel(0.0f, 0.0f, 1000.0f, 0.5f));
        spnHorasTrabajo.setEnabled(false); 
        panelFormulario.add(spnHorasTrabajo, gbc);

        // Fila 3: Costo Mano de Obra
        gbc.gridx = 0; gbc.gridy = 3;
        lblCostoManoObra = new JLabel("Costo Mano de Obra *:");
        panelFormulario.add(lblCostoManoObra, gbc);

        gbc.gridx = 1;
        txtCostoManoObra = new JFormattedTextField(new java.text.DecimalFormat("#,##0.00"));
        txtCostoManoObra.setValue(0.0); 
        txtCostoManoObra.setEnabled(false); 
        panelFormulario.add(txtCostoManoObra, gbc);

        // Fila 4: Costo Materiales
        gbc.gridx = 0; gbc.gridy = 4;
        lblCostoMateriales = new JLabel("Costo Materiales *:");
        panelFormulario.add(lblCostoMateriales, gbc);

        gbc.gridx = 1;
        txtCostoMateriales = new JFormattedTextField(new java.text.DecimalFormat("#,##0.00"));
        txtCostoMateriales.setValue(0.0);
        txtCostoMateriales.setEnabled(false);
        panelFormulario.add(txtCostoMateriales, gbc);

        // Fila 5: Observaciones
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;
        lblObservaciones = new JLabel("Observaciones:");
        panelFormulario.add(lblObservaciones, gbc);

        gbc.gridy = 6;
        txtObservaciones = new JTextArea(3, 30);
        txtObservaciones.setEnabled(false);
        scrollObservaciones = new JScrollPane(txtObservaciones);
        panelFormulario.add(scrollObservaciones, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel de Fallas ---
        panelFallas = new JPanel(new BorderLayout());
        panelFallas.setBorder(BorderFactory.createTitledBorder("Fallas Encontradas Durante el Trabajo"));

        modeloTablaFallas = new DefaultTableModel(COLUMNAS_FALLAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

       
        tablaFallas = new JTable(modeloTablaFallas);
        
        tablaFallas.setPreferredScrollableViewportSize(new Dimension(600, tablaFallas.getRowHeight() * 4));
        tablaFallas.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        scrollTablaFallas = new JScrollPane(tablaFallas);
        scrollTablaFallas.setMinimumSize(new Dimension(600, 150)); 

        panelFallas.add(scrollTablaFallas, BorderLayout.CENTER);

        JPanel panelBotonesFallas = new JPanel(new FlowLayout());
        btnAgregarFalla = new JButton("Agregar Falla");
        btnEliminarFalla = new JButton("Eliminar Falla");
        btnAgregarFalla.setEnabled(false); 
        btnEliminarFalla.setEnabled(false);
        panelBotonesFallas.add(btnAgregarFalla);
        panelBotonesFallas.add(btnEliminarFalla);
        panelFallas.add(panelBotonesFallas, BorderLayout.SOUTH);


        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;

        gbc.weighty = 0.5; 
        panelFormulario.add(panelFallas, gbc);
        gbc.weighty = 0.0; 

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnFinalizar = new JButton("Finalizar Orden");
        btnCancelar = new JButton("Cancelar");
        btnFinalizar.setEnabled(false); 
        panelBotones.add(btnFinalizar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnBuscarOrden.addActionListener(e -> controlador.buscarOrden()); 
        btnAgregarFalla.addActionListener(e -> controlador.agregarFalla()); 
        btnEliminarFalla.addActionListener(e -> controlador.eliminarFalla()); 
        btnFinalizar.addActionListener(e -> controlador.finalizarOrden()); 
        btnCancelar.addActionListener(e -> controlador.cancelar()); 
    }

    // --- Métodos para que el controlador interactúe con la vista ---
    public JTextField getTxtIdOrden() { return txtIdOrden; }
    public JFormattedTextField getTxtFechaFin() { return txtFechaFin; }
    public JSpinner getSpnHorasTrabajo() { return spnHorasTrabajo; }
    public JFormattedTextField getTxtCostoManoObra() { return txtCostoManoObra; }
    public JFormattedTextField getTxtCostoMateriales() { return txtCostoMateriales; }
    public JTextArea getTxtObservaciones() { return txtObservaciones; }

    public void mostrarInfoOrden(String texto) { lblInfoOrden.setText(texto); }
    public void mostrarMensaje(String mensaje, String titulo, int tipo) { JOptionPane.showMessageDialog(this, mensaje, titulo, tipo); }

    public DefaultTableModel getModeloTablaFallas() { return modeloTablaFallas; }
    public void limpiarTablaFallas() { modeloTablaFallas.setRowCount(0); }

    public void habilitarCamposFinalizar(boolean habilitar) {
        txtFechaFin.setEnabled(habilitar);
        spnHorasTrabajo.setEnabled(habilitar);
        txtCostoManoObra.setEnabled(habilitar);
        txtCostoMateriales.setEnabled(habilitar);
        txtObservaciones.setEnabled(habilitar);
        btnAgregarFalla.setEnabled(habilitar);
        btnEliminarFalla.setEnabled(habilitar);
        btnFinalizar.setEnabled(habilitar);
    }

    public void limpiarCamposDespuesDeBuscar() {
        txtFechaFin.setValue(LocalDate.now());
        spnHorasTrabajo.setValue(0.0f);
        txtCostoManoObra.setValue(0.0);
        txtCostoMateriales.setValue(0.0);
        txtObservaciones.setText("");
        modeloTablaFallas.setRowCount(0); 
        txtFechaFin.setEnabled(false);
        spnHorasTrabajo.setEnabled(false);
        txtCostoManoObra.setEnabled(false);
        txtCostoMateriales.setEnabled(false);
        txtObservaciones.setEnabled(false);
        btnAgregarFalla.setEnabled(false);
        btnEliminarFalla.setEnabled(false);
        btnFinalizar.setEnabled(false);
    }

    public void cerrarVentana() { this.dispose(); }

    // --- Métodos específicos para la gestión de fallas ---
    public void abrirVentanaAgregarFalla() {
        if (ventanaAgregarFalla != null && ventanaAgregarFalla.isVisible()) {
            ventanaAgregarFalla.toFront();
            return;
        }

        ventanaAgregarFalla = new JDialog(this, "Agregar Falla Encontrada", true);
        ventanaAgregarFalla.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaAgregarFalla.setLayout(new BorderLayout());

        JPanel panelAgregarFalla = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panelAgregarFalla.add(new JLabel("Falla:"), gbc);
        cmbFallasDisponibles = new JComboBox<>();
        List<FallaEquipo> fallasMaestras = sistema.obtenerTodasLasFallasMaestras();
        for (FallaEquipo falla : fallasMaestras) {
            cmbFallasDisponibles.addItem(falla);
        }
        gbc.gridx = 1;
        panelAgregarFalla.add(cmbFallasDisponibles, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelAgregarFalla.add(new JLabel("Causas:"), gbc);
        txtCausas = new JTextField(20);
        gbc.gridx = 1;
        panelAgregarFalla.add(txtCausas, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelAgregarFalla.add(new JLabel("Acciones Tomadas:"), gbc);
        txtAcciones = new JTextField(20);
        gbc.gridx = 1;
        panelAgregarFalla.add(txtAcciones, gbc);

        ventanaAgregarFalla.add(panelAgregarFalla, BorderLayout.CENTER);

        JPanel panelBotonesAgregar = new JPanel(new FlowLayout());
        btnAceptarAgregar = new JButton("Aceptar");
        btnCancelarAgregar = new JButton("Cancelar");
        panelBotonesAgregar.add(btnAceptarAgregar);
        panelBotonesAgregar.add(btnCancelarAgregar);
        ventanaAgregarFalla.add(panelBotonesAgregar, BorderLayout.SOUTH);

        btnAceptarAgregar.addActionListener(aceptarEvt -> {
            FallaEquipo fallaSeleccionada = (FallaEquipo) cmbFallasDisponibles.getSelectedItem();
            String causas = txtCausas.getText().trim();
            String acciones = txtAcciones.getText().trim();

            if (fallaSeleccionada == null || causas.isEmpty() || acciones.isEmpty()) {
                JOptionPane.showMessageDialog(ventanaAgregarFalla, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Object[] filaFalla = {fallaSeleccionada.getId(), fallaSeleccionada.getDescripcion(), causas, acciones};
            modeloTablaFallas.addRow(filaFalla);

            ventanaAgregarFalla.dispose();
        });

        btnCancelarAgregar.addActionListener(cancelarEvt -> ventanaAgregarFalla.dispose());

        ventanaAgregarFalla.pack();
        ventanaAgregarFalla.setLocationRelativeTo(this);
        ventanaAgregarFalla.setVisible(true);
    }

    public void eliminarFallaSeleccionadaDeTabla() {
        int filaSeleccionada = tablaFallas.getSelectedRow();
        if (filaSeleccionada >= 0) {
            modeloTablaFallas.removeRow(filaSeleccionada);
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una falla de la lista para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    // ---
}