package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.equipos.FallaEquipo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaAdministrarFallas extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Opcional

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

    public VentanaAdministrarFallas(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        inicializarComponentes();
        configurarEventos();
        setTitle("Administrar Fallas Maestras");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(700, 500);
        setLocationRelativeTo(ventanaPadre);
        actualizarTabla(); // Carga inicial de fallas
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
        btnAgregar.addActionListener(e -> {
            String descripcion = JOptionPane.showInputDialog(this, "Ingrese la descripción de la nueva falla:");
            if (descripcion != null && !descripcion.trim().isEmpty()) {
                sistema.crearFallaMaestra(descripcion.trim());
                JOptionPane.showMessageDialog(this, "Falla agregada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla(); // Refresca la vista
            }
        });

        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaFallas.getSelectedRow();
            if (filaSeleccionada >= 0) {
                int idFalla = (int) modeloTabla.getValueAt(filaSeleccionada, 0); // Obtener ID de la columna 0
                int respuesta = JOptionPane.showConfirmDialog(
                        this,
                        "¿Está seguro de que desea eliminar la falla con ID " + idFalla + "?\nEsta acción no se puede deshacer.",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (respuesta == JOptionPane.YES_OPTION) {
                    // TODO: Implementar en SistemaPrincipal un método para eliminar fallas maestras
                    // y verificar que no estén asociadas a órdenes de trabajo antes de eliminarlas.
                    // Por ahora, simulamos que no se puede eliminar si está en uso.
                    // Buscar si alguna orden de trabajo contiene esta falla
                    boolean enUso = sistema.obtenerTodasLasOrdenes().stream()
                            .anyMatch(orden -> orden.getFallasEncontradas().stream()
                                    .anyMatch(falla -> falla.getIdFalla() == idFalla));

                    if (enUso) {
                        JOptionPane.showMessageDialog(this, "No se puede eliminar la falla con ID " + idFalla + ". Está asociada a una o más órdenes de trabajo.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Si no está en uso, intentamos eliminarla (esto requiere un método en SistemaPrincipal)
                        // Por ejemplo: boolean eliminada = sistema.eliminarFallaMaestra(idFalla);
                        // if(eliminada) { ... }
                        // Para esta demo, asumiremos que el sistema no tiene un método de eliminación directa
                        // o que la eliminación está prohibida para mantener integridad.
                        JOptionPane.showMessageDialog(this, "La eliminación de fallas maestras no está permitida para mantener la integridad de los datos históricos (órdenes de trabajo).", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        // Si decides implementar eliminación, aquí iría la llamada al sistema y luego actualizarTabla()
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una falla de la lista para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCerrar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0); // Limpia la tabla
        List<FallaEquipo> fallas = sistema.obtenerTodasLasFallasMaestras();
        for (FallaEquipo falla : fallas) {
            Object[] fila = {falla.getId(), falla.getDescripcion()};
            modeloTabla.addRow(fila);
        }
    }
}