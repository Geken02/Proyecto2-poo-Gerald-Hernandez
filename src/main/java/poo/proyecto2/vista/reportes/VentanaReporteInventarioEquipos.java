package poo.proyecto2.vista.reportes;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorReporteInventarioEquipos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaReporteInventarioEquipos extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre;

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblOpcionImpresion;
    private ButtonGroup grupoOpciones; 
    private JRadioButton rbUnEquipoSimple;
    private JRadioButton rbUnEquipoConComponentes;
    private JRadioButton rbTodosEquiposConComponentes;
    private JPanel panelOpciones;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo; 
    private JButton btnGenerarPDF;
    private JButton btnCerrar;

    // Referencia al controlador
    private ControladorReporteInventarioEquipos controlador;

    public VentanaReporteInventarioEquipos(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        // Crear el controlador pasando el modelo y la vista
        this.controlador = new ControladorReporteInventarioEquipos(sistema, this);

        inicializarComponentes();
        configurarEventos();
        setTitle("Reporte de Inventario de Equipos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack(); 
        setLocationRelativeTo(ventanaPadre); 
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Generar Reporte de Inventario de Equipos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Opciones de Impresión ---
        lblOpcionImpresion = new JLabel("Seleccione la opción de impresión:", SwingConstants.LEFT);
        lblOpcionImpresion.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        grupoOpciones = new ButtonGroup();
        rbUnEquipoSimple = new JRadioButton("Un equipo (sin sus componentes)");
        rbUnEquipoConComponentes = new JRadioButton("Un equipo (con sus componentes)");
        rbTodosEquiposConComponentes = new JRadioButton("Todos los equipos (con sus componentes)");

        // Agrupar los radio buttons
        grupoOpciones.add(rbUnEquipoSimple);
        grupoOpciones.add(rbUnEquipoConComponentes);
        grupoOpciones.add(rbTodosEquiposConComponentes);

        // Seleccionar por defecto la opción de "Todos los equipos"
        rbTodosEquiposConComponentes.setSelected(true);

        panelOpciones = new JPanel(new GridLayout(0, 1)); 
        panelOpciones.add(rbUnEquipoSimple);
        panelOpciones.add(rbUnEquipoConComponentes);
        panelOpciones.add(rbTodosEquiposConComponentes);

        JPanel panelOpcionesContenedor = new JPanel(new BorderLayout());
        panelOpcionesContenedor.add(lblOpcionImpresion, BorderLayout.NORTH);
        panelOpcionesContenedor.add(panelOpciones, BorderLayout.CENTER);

        // Panel para ID de equipo (visible solo si se elige una opción de "un equipo")
        JPanel panelIdEquipo = new JPanel(new FlowLayout());
        lblIdEquipo = new JLabel("ID del Equipo:");
        txtIdEquipo = new JTextField(10);
        txtIdEquipo.setVisible(false); 
        panelIdEquipo.add(lblIdEquipo);
        panelIdEquipo.add(txtIdEquipo);
        panelIdEquipo.setVisible(false); 

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelOpcionesContenedor, BorderLayout.CENTER);
        panelCentral.add(panelIdEquipo, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGenerarPDF = new JButton("Generar PDF");
        btnCerrar = new JButton("Cerrar");
        panelBotones.add(btnGenerarPDF);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        // Evento para mostrar/ocultar el campo de ID según la selección
        ActionListener listenerRadio = e -> {
            boolean mostrarCampo = rbUnEquipoSimple.isSelected() || rbUnEquipoConComponentes.isSelected();
            txtIdEquipo.setVisible(mostrarCampo);
            txtIdEquipo.getParent().setVisible(mostrarCampo); 
            pack();
        };
        rbUnEquipoSimple.addActionListener(listenerRadio);
        rbUnEquipoConComponentes.addActionListener(listenerRadio);
        rbTodosEquiposConComponentes.addActionListener(listenerRadio);

        btnGenerarPDF.addActionListener(e -> controlador.generarPDF()); 

        btnCerrar.addActionListener(e -> dispose()); 
    }

    // --- Métodos para que el controlador interactúe con la vista ---
    public JTextField getTxtIdEquipo() { return txtIdEquipo; }
    public void mostrarMensaje(String mensaje, String titulo, int tipo) { JOptionPane.showMessageDialog(this, mensaje, titulo, tipo); }

    // Métodos para que el controlador consulte el estado de los radio buttons
    public boolean esUnEquipoSimpleSeleccionado() { return rbUnEquipoSimple.isSelected(); }
    public boolean esUnEquipoConComponentesSeleccionado() { return rbUnEquipoConComponentes.isSelected(); }
    public boolean sonTodosEquiposConComponentesSeleccionados() { return rbTodosEquiposConComponentes.isSelected(); }
    // ---
}