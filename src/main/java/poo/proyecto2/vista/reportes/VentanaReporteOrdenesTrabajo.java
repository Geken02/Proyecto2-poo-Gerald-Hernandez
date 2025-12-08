package poo.proyecto2.vista.reportes;

import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorReporteOrdenesTrabajo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaReporteOrdenesTrabajo extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; 

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblFiltros;
    private JLabel lblEstado;
    private JComboBox<String> cmbEstado;
    private JLabel lblTipo;
    private JComboBox<String> cmbTipo;
    private JLabel lblFechaDesde;
    private JFormattedTextField txtFechaDesde;
    private JLabel lblFechaHasta;
    private JFormattedTextField txtFechaHasta;
    private JButton btnGenerarPDF;
    private JButton btnCerrar;

    // Referencia al controlador
    private ControladorReporteOrdenesTrabajo controlador;

    public VentanaReporteOrdenesTrabajo(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        // Crear el controlador pasando el modelo y la vista
        this.controlador = new ControladorReporteOrdenesTrabajo(sistema, this);

        inicializarComponentes();
        configurarEventos();
        setTitle("Reporte de Órdenes de Trabajo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Generar Reporte de Órdenes de Trabajo", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Filtros ---
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        lblFiltros = new JLabel("Filtros de Impresión:", SwingConstants.LEFT);
        lblFiltros.setFont(new Font("Arial", Font.BOLD, 14));
        panelFiltros.add(lblFiltros, gbc);

        gbc.gridy = 1;
        lblEstado = new JLabel("Estado de las órdenes:");
        panelFiltros.add(lblEstado, gbc);

        gbc.gridx = 1;
        cmbEstado = new JComboBox<>(new String[]{"Todas", "Pendientes", "Terminadas", "Canceladas"});
        panelFiltros.add(cmbEstado, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        lblTipo = new JLabel("Órdenes incluidas:");
        panelFiltros.add(lblTipo, gbc);

        gbc.gridx = 1;
        cmbTipo = new JComboBox<>(new String[]{"Mantenimiento preventivo y correctivo", "Mantenimiento preventivo", "Mantenimiento correctivo"});
        panelFiltros.add(cmbTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        lblFechaDesde = new JLabel("Fecha Desde (AAAA-MM-DD):");
        panelFiltros.add(lblFechaDesde, gbc);

        gbc.gridx = 1;
        txtFechaDesde = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaDesde.setValue(LocalDate.now().minusMonths(1));
        panelFiltros.add(txtFechaDesde, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        lblFechaHasta = new JLabel("Fecha Hasta (AAAA-MM-DD):");
        panelFiltros.add(lblFechaHasta, gbc);

        gbc.gridx = 1;
        txtFechaHasta = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaHasta.setValue(LocalDate.now());
        panelFiltros.add(txtFechaHasta, gbc);

        add(panelFiltros, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGenerarPDF = new JButton("Generar PDF");
        btnCerrar = new JButton("Cerrar");
        panelBotones.add(btnGenerarPDF);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnGenerarPDF.addActionListener(e -> controlador.generarPDF()); 

        btnCerrar.addActionListener(e -> controlador.cancelarVentana()); 
    }

    // --- Métodos para que el controlador interactúe con la vista ---
    public JComboBox<String> getCmbEstado() { return cmbEstado; }
    public JComboBox<String> getCmbTipo() { return cmbTipo; }
    public JFormattedTextField getTxtFechaDesde() { return txtFechaDesde; }
    public JFormattedTextField getTxtFechaHasta() { return txtFechaHasta; }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) { JOptionPane.showMessageDialog(this, mensaje, titulo, tipo); }
    public void cerrarVentana() { this.dispose(); }
    // ---
}