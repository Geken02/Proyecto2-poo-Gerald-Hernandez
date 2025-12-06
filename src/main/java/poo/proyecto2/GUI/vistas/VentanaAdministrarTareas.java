package poo.proyecto2.gui.vistas;

import poo.proyecto2.mantenimiento.TareaMantenimiento;
import poo.proyecto2.sistema.SistemaPrincipal;
import poo.proyecto2.gui.VentanaMenuPrincipal; // Ventana padre

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaAdministrarTareas extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Referencia a la ventana principal

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

    public VentanaAdministrarTareas(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        inicializarComponentes();
        configurarEventos();
        setTitle("Administrar Tareas Maestras");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(600, 400);
        setLocationRelativeTo(ventanaPadre);
        actualizarTabla(); // Carga inicial de tareas
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
                return false; // No se puede editar directamente la tabla
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
        btnAgregar.addActionListener(e -> {
            String descripcion = JOptionPane.showInputDialog(this, "Ingrese la descripción de la nueva tarea:");
            if (descripcion != null && !descripcion.trim().isEmpty()) {
                sistema.crearTareaMaestra(descripcion.trim());
                JOptionPane.showMessageDialog(this, "Tarea agregada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla(); // Refresca la vista
            }
        });

        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaTareas.getSelectedRow();
            if (filaSeleccionada >= 0) {
                int idTarea = (int) modeloTabla.getValueAt(filaSeleccionada, 0); // Obtener ID de la columna 0
                int respuesta = JOptionPane.showConfirmDialog(
                        this,
                        "¿Está seguro de que desea eliminar la tarea con ID " + idTarea + "?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION
                );
                if (respuesta == JOptionPane.YES_OPTION) {
                    // TODO: Implementar sistema.eliminarTareaMaestra(idTarea) en SistemaPrincipal si es necesario
                    // Por ahora, asumiremos que no se pueden eliminar tareas ya que podrían estar asociadas a fases/órdenes.
                    JOptionPane.showMessageDialog(this, "La eliminación de tareas maestras no está permitida para mantener la integridad de los datos relacionados (fases, órdenes).", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    // Si se implementa eliminación, llamaría a sistema.eliminarTareaMaestra(idTarea) y luego actualizarTabla().
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una tarea de la lista para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCerrar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0); // Limpia la tabla
        List<TareaMantenimiento> tareas = sistema.obtenerTodasLasTareasMaestras();
        for (TareaMantenimiento tarea : tareas) {
            Object[] fila = {tarea.getId(), tarea.getDescripcion()};
            modeloTabla.addRow(fila);
        }
    }
}