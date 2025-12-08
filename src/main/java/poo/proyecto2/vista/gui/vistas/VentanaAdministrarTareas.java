package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.mantenimiento.TareaMantenimiento;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorAdministrarTareas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaAdministrarTareas extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre;

    // Componentes
    private JLabel lblTitulo;
    private JScrollPane scrollTabla;
    private JTable tablaTareas;
    private DefaultTableModel modeloTabla;
    private JPanel panelBotones;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnCerrar;

    // Columnas de la tabla
    private static final String[] COLUMNAS = {"ID", "Descripción"};

    // Referencia al controlador
    private ControladorAdministrarTareas controlador;

    public VentanaAdministrarTareas(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        // Crear el controlador pasando el modelo y la vista
        this.controlador = new ControladorAdministrarTareas(sistema, this);
        inicializarComponentes();
        configurarEventos();
        setTitle("Administrar Tareas Maestras");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(600, 400);
        setLocationRelativeTo(ventanaPadre);
        // Llamar al controlador para cargar los datos iniciales
        controlador.cargarTareasIniciales();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Título ---
        lblTitulo = new JLabel("Catálogo de Tareas Maestras", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Tabla ---
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tablaTareas = new JTable(modeloTabla);
        scrollTabla = new JScrollPane(tablaTareas);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Lista de Tareas"));
        add(scrollTabla, BorderLayout.CENTER);

        // --- Botones ---
        panelBotones = new JPanel(new FlowLayout());
        btnAgregar = new JButton("Agregar Tarea");
        btnEliminar = new JButton("Eliminar Tarea");
        btnCerrar = new JButton("Cerrar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnAgregar.addActionListener(e -> controlador.agregarTarea());
        btnEliminar.addActionListener(e -> controlador.eliminarTarea());
        btnCerrar.addActionListener(e -> dispose()); 
    }

    // --- Métodos para que el controlador interactúe con la vista ---
    public void actualizarTabla() {
        modeloTabla.setRowCount(0); 
        List<TareaMantenimiento> tareas = sistema.obtenerTodasLasTareasMaestras();
        for (TareaMantenimiento tarea : tareas) {
            Object[] fila = {tarea.getId(), tarea.getDescripcion()};
            modeloTabla.addRow(fila);
        }
    }

    // Métodos para que el controlador acceda a componentes específicos
    public JTable getTablaTareas() { return tablaTareas; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    // ---
}