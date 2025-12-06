package poo.proyecto2.gui.vistas;

import poo.proyecto2.equipos.NodoEquipo;
import poo.proyecto2.mantenimiento.OrdenTrabajo;
import poo.proyecto2.sistema.SistemaPrincipal;
import poo.proyecto2.gui.VentanaMenuPrincipal; // Referencia a la ventana padre

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaEliminarEquipo extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Referencia a la ventana principal
    private Runnable despuesDeEliminar; // Callback para actualizar la ventana padre

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo;
    private JButton btnBuscar;
    private JButton btnEliminar;
    private JButton btnCancelar;

    // Panel para mostrar detalles del equipo encontrado (opcional, para confirmación)
    private JPanel panelDetalle;
    private JLabel lblDetalleVacio;
    private JLabel lblId;
    private JLabel lblDescripcion;
    private JLabel lblTipo;
    private JLabel lblUbicacion;

    // --- Componentes para mostrar los detalles ---
    private JLabel valId;
    private JLabel valDescripcion;
    private JLabel valTipo;
    private JLabel valUbicacion;

    public VentanaEliminarEquipo(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre, Runnable despuesDeEliminar) { // Añadido Runnable
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        this.despuesDeEliminar = despuesDeEliminar; // Guardar el callback
        inicializarComponentes();
        configurarEventos();
        setTitle("Eliminar Equipo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Eliminar Equipo", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Formulario ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        lblIdEquipo = new JLabel("ID del Equipo a Eliminar: *");
        panelFormulario.add(lblIdEquipo, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtIdEquipo = new JTextField(10);
        panelFormulario.add(txtIdEquipo, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        btnBuscar = new JButton("Buscar");
        panelFormulario.add(btnBuscar, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel de Detalle (para mostrar info del equipo encontrado) ---
        panelDetalle = new JPanel(new BorderLayout());
        panelDetalle.setBorder(BorderFactory.createTitledBorder("Detalles del Equipo (para confirmación)"));
        lblDetalleVacio = new JLabel("Ingrese un ID y presione 'Buscar'.", SwingConstants.CENTER);
        lblDetalleVacio.setFont(new Font("Arial", Font.ITALIC, 12));
        panelDetalle.add(lblDetalleVacio, BorderLayout.CENTER);

        // Componentes para mostrar detalles (inicialmente invisibles)
        JPanel panelInfo = new JPanel(new GridLayout(0, 2, 5, 5)); // 2 columnas
        lblId = new JLabel("ID:");
        lblDescripcion = new JLabel("Descripción:");
        lblTipo = new JLabel("Tipo:");
        lblUbicacion = new JLabel("Ubicación:");

        valId = new JLabel(" - "); // Etiquetas para los valores
        valDescripcion = new JLabel(" - ");
        valTipo = new JLabel(" - ");
        valUbicacion = new JLabel(" - ");

        panelInfo.add(lblId); panelInfo.add(valId);
        panelInfo.add(lblDescripcion); panelInfo.add(valDescripcion);
        panelInfo.add(lblTipo); panelInfo.add(valTipo);
        panelInfo.add(lblUbicacion); panelInfo.add(valUbicacion);

        panelDetalle.add(panelInfo, BorderLayout.CENTER);
        panelDetalle.setVisible(false); // Oculto inicialmente

        add(panelDetalle, BorderLayout.SOUTH);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setEnabled(false); // Inicialmente deshabilitado
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnBuscar.addActionListener(e -> {
            String idStr = txtIdEquipo.getText().trim();
            if (!idStr.isEmpty()) {
                try {
                    int idEquipo = Integer.parseInt(idStr);
                    // Buscar el equipo en el sistema
                    NodoEquipo equipo = sistema.buscarEquipoPorId(idEquipo);
                    if (equipo != null) {
                        // Mostrar detalles del equipo encontrado
                        lblDetalleVacio.setVisible(false);
                        panelDetalle.setVisible(true);
                        // Actualizar valores
                        valId.setText(String.valueOf(equipo.getId()));
                        valDescripcion.setText(equipo.getDescripcion());
                        valTipo.setText(equipo.getTipo());
                        valUbicacion.setText(equipo.getUbicacion());

                        btnEliminar.setEnabled(true); // Habilitar botón de eliminar
                    } else {
                        JOptionPane.showMessageDialog(this, "Equipo con ID " + idEquipo + " no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                        limpiarDetalle();
                        btnEliminar.setEnabled(false);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo válido (número entero).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                 JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo para buscar.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            String idStr = txtIdEquipo.getText().trim();
            if (!idStr.isEmpty()) {
                try {
                    int idEquipo = Integer.parseInt(idStr);

                    // --- VERIFICACIÓN DE ÓRDENES ASOCIADAS ---
                    List<OrdenTrabajo> ordenesAsociadas = sistema.obtenerOrdenesPorEquipo(idEquipo);
                    if (!ordenesAsociadas.isEmpty()) {
                        StringBuilder mensajeErrores = new StringBuilder("No se puede eliminar el equipo con ID ").append(idEquipo).append(".\nTiene las siguientes órdenes de trabajo asociadas:\n");
                        for (OrdenTrabajo orden : ordenesAsociadas) {
                            String tipoOrden = (orden instanceof poo.proyecto2.mantenimiento.OrdenTrabajoPreventiva) ? "Preventiva" : "Correctiva";
                            mensajeErrores.append("- Orden #").append(orden.getId()).append(" (").append(tipoOrden).append(") - Estado: ").append(orden.getEstado()).append("\n");
                        }
                        JOptionPane.showMessageDialog(this, mensajeErrores.toString(), "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                        return; // Salir del evento, no eliminar
                    }
                    // --- FIN VERIFICACIÓN ---

                    int respuesta = JOptionPane.showConfirmDialog(
                            this,
                            "¿Está seguro de que desea eliminar el equipo con ID " + idEquipo + "?\nEsta acción no se puede deshacer.",
                            "Confirmar Eliminación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );
                    if (respuesta == JOptionPane.YES_OPTION) {
                        boolean eliminado = sistema.eliminarEquipo(idEquipo);
                        if (eliminado) {
                            JOptionPane.showMessageDialog(this, "Equipo con ID " + idEquipo + " eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            limpiarFormulario(); // Limpiar campos y detalle
                            btnEliminar.setEnabled(false); // Deshabilitar botón eliminar

                            // --- LLAMAR AL CALLBACK ---
                            if (despuesDeEliminar != null) {
                                despuesDeEliminar.run(); // Ejecuta el método pasado como Runnable
                            }
                            // --- FIN CALLBACK ---

                            // Opcional: Cerrar la ventana después de eliminar
                            // dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "No se pudo eliminar el equipo. Puede que no exista o que ocurrió un error inesperado.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    // Si la respuesta es NO, no se hace nada.
                } catch (NumberFormatException ex) {
                     JOptionPane.showMessageDialog(this, "ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    private void limpiarDetalle() {
        lblDetalleVacio.setVisible(true);
        panelDetalle.setVisible(false);
        valId.setText(" - ");
        valDescripcion.setText(" - ");
        valTipo.setText(" - ");
        valUbicacion.setText(" - ");
    }

    private void limpiarFormulario() {
        txtIdEquipo.setText("");
        limpiarDetalle();
    }
}