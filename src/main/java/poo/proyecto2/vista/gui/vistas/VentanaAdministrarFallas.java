package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.equipos.FallaEquipo;
import poo.proyecto2.modelo.mantenimiento.OrdenTrabajo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal; 
import poo.proyecto2.controlador.ControladorAdministrarFallas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaAdministrarFallas extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre;

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JScrollPane scrollTabla;
    private JTable tablaFallas;
    private DefaultTableModel modeloTabla;
    private JPanel panelBotones;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    // Columnas de la tabla
    private static final String[] COLUMNAS = {"ID", "Descripción"};

    // Referencia al controlador
    private ControladorAdministrarFallas controlador;

    public VentanaAdministrarFallas(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        // Crear el controlador pasando el modelo y la vista
        this.controlador = new ControladorAdministrarFallas(sistema, this);

        inicializarComponentes();
        configurarEventos();
        setTitle("Administrar Fallas Maestras");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(700, 500);
        setLocationRelativeTo(ventanaPadre);
        // Llamar al controlador para cargar los datos iniciales
        controlador.cargarFallasIniciales();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Catálogo de Fallas Maestras", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Tabla ---
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No se edita directamente la tabla
            }
        };
        tablaFallas = new JTable(modeloTabla);
        scrollTabla = new JScrollPane(tablaFallas);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Lista de Fallas"));
        add(scrollTabla, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        panelBotones = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar Falla");
        btnEliminar = new JButton("Eliminar Falla");
        btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        // --- Eventos de Botones ---
        btnAgregar.addActionListener(e -> controlador.agregarFalla()); // <-- Llama al controlador
        btnEliminar.addActionListener(e -> controlador.eliminarFalla()); // <-- Llama al controlador
        btnCerrar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    // --- Métodos para que el controlador interactúe con la vista ---
    public void actualizarTabla() {
        modeloTabla.setRowCount(0); // Limpia la tabla
        List<FallaEquipo> fallas = sistema.obtenerTodasLasFallasMaestras();
        for (FallaEquipo falla : fallas) {
            Object[] fila = {falla.getId(), falla.getDescripcion()};
            modeloTabla.addRow(fila);
        }
    }

    // Métodos para que el controlador acceda a componentes específicos
    public JTable getTablaFallas() { return tablaFallas; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    // ---
}