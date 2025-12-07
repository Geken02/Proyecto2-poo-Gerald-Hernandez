package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VentanaCancelarOrdenPreventiva extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Opcional

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdOrden;
    private JTextField txtIdOrden;
    private JButton btnBuscarOrden;
    private JLabel lblInfoOrden; // Muestra info básica de la orden encontrada
    private JLabel lblFechaCancelacion;
    private JFormattedTextField txtFechaCancelacion; // Campo para ingresar la fecha de cancelación
    private JLabel lblMotivoCancelacion;
    private JTextArea txtMotivoCancelacion; // Campo para ingresar el motivo
    private JScrollPane scrollMotivo;
    private JButton btnCancelarOrden;
    private JButton btnCerrar;

    // Variable temporal para la orden actual
    private OrdenTrabajoPreventiva ordenActual;

    public VentanaCancelarOrdenPreventiva(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        inicializarComponentes();
        configurarEventos();
        setTitle("Cancelar Orden de Trabajo Preventiva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(600, 250); // Tamaño ajustado para incluir el TextArea
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Cancelar Orden de Trabajo Preventiva", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Formulario ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: ID Orden
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        lblIdOrden = new JLabel("ID de la Orden *:");
        panelFormulario.add(lblIdOrden, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtIdOrden = new JTextField(15); // <-- Aumentado de 10 a 15
        txtIdOrden.setMinimumSize(new Dimension(100, 25)); // Tamaño mínimo
        txtIdOrden.setPreferredSize(new Dimension(150, 25)); // Tamaño preferido
        panelFormulario.add(txtIdOrden, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        btnBuscarOrden = new JButton("Buscar");
        panelFormulario.add(btnBuscarOrden, gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblInfoOrden = new JLabel(" (Orden no encontrada o no válida para cancelar)");
        lblInfoOrden.setFont(lblInfoOrden.getFont().deriveFont(Font.ITALIC));
        panelFormulario.add(lblInfoOrden, gbc);

        // Fila 1: Fecha Cancelación
        gbc.gridx = 0; gbc.gridy = 1;
        lblFechaCancelacion = new JLabel("Fecha de Cancelación *:");
        panelFormulario.add(lblFechaCancelacion, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFechaCancelacion = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaCancelacion.setValue(LocalDate.now()); // Fecha por defecto
        txtFechaCancelacion.setEnabled(false); // Deshabilitado hasta que se busque una orden válida
        panelFormulario.add(txtFechaCancelacion, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 2: Motivo Cancelación
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;
        lblMotivoCancelacion = new JLabel("Motivo de Cancelación *:");
        panelFormulario.add(lblMotivoCancelacion, gbc);

        gbc.gridy = 3;
        txtMotivoCancelacion = new JTextArea(3, 30);
        txtMotivoCancelacion.setEnabled(false); // Deshabilitado hasta que se busque una orden válida
        scrollMotivo = new JScrollPane(txtMotivoCancelacion);
        panelFormulario.add(scrollMotivo, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCancelarOrden = new JButton("Cancelar Orden");
        btnCerrar = new JButton("Cerrar");
        btnCancelarOrden.setEnabled(false); // Deshabilitado hasta que se busque una orden válida
        panelBotones.add(btnCancelarOrden);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnBuscarOrden.addActionListener(e -> {
            String idStr = txtIdOrden.getText().trim();
            if (!idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    OrdenTrabajo orden = sistema.buscarOrdenPorId(id);
                    if (orden != null) {
                        if (orden instanceof OrdenTrabajoPreventiva) {
                            OrdenTrabajoPreventiva ordenPrev = (OrdenTrabajoPreventiva) orden;
                            // Permitir cancelar si está en PENDIENTE o EN_PROGRESO
                            if (ordenPrev.getEstado() == OrdenTrabajo.EstadoOrden.PENDIENTE || ordenPrev.getEstado() == OrdenTrabajo.EstadoOrden.EN_PROGRESO) {
                                lblInfoOrden.setText(" (Orden ID: " + orden.getId() + " - Estado: " + orden.getEstado() + " - Equipo ID: " + orden.getIdEquipo() + ")");
                                // Guardar referencia temporal
                                ordenActual = ordenPrev;
                                // Habilitar campos de cancelación
                                txtFechaCancelacion.setEnabled(true);
                                txtMotivoCancelacion.setEnabled(true);
                                btnCancelarOrden.setEnabled(true);
                            } else {
                                JOptionPane.showMessageDialog(this, "La orden ID " + id + " no está en estado PENDIENTE ni EN_PROGRESO. No se puede cancelar.", "Error", JOptionPane.ERROR_MESSAGE);
                                lblInfoOrden.setText(" (Orden no válida para cancelar)");
                                txtFechaCancelacion.setEnabled(false);
                                txtMotivoCancelacion.setEnabled(false);
                                btnCancelarOrden.setEnabled(false);
                                ordenActual = null; // Limpiar referencia
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "El ID " + id + " pertenece a una orden de tipo incorrecto (no Preventiva).", "Error", JOptionPane.ERROR_MESSAGE);
                            lblInfoOrden.setText(" (Orden no es Preventiva)");
                            txtFechaCancelacion.setEnabled(false);
                            txtMotivoCancelacion.setEnabled(false);
                            btnCancelarOrden.setEnabled(false);
                            ordenActual = null; // Limpiar referencia
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró una orden con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                        lblInfoOrden.setText(" (Orden no encontrada)");
                        txtFechaCancelacion.setEnabled(false);
                        txtMotivoCancelacion.setEnabled(false);
                        btnCancelarOrden.setEnabled(false);
                        ordenActual = null; // Limpiar referencia
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de orden válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de orden.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancelarOrden.addActionListener(e -> {
            if (ordenActual == null) {
                JOptionPane.showMessageDialog(this, "No hay una orden seleccionada para cancelar.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar campos requeridos
            if (txtFechaCancelacion.getText().trim().isEmpty() || txtMotivoCancelacion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios (marcados con *).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate fechaCancelacion;
            String motivo = txtMotivoCancelacion.getText().trim();
            try {
                fechaCancelacion = LocalDate.parse(txtFechaCancelacion.getText().trim());
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese una fecha de cancelación válida en formato AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Llamar al sistema para cancelar la orden
            boolean cancelada = sistema.cancelarOrdenPreventiva(ordenActual.getId(), fechaCancelacion, motivo);

            if (cancelada) {
                JOptionPane.showMessageDialog(this, "Orden ID " + ordenActual.getId() + " cancelada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Limpiar formulario y deshabilitar campos
                txtIdOrden.setText("");
                lblInfoOrden.setText(" (Orden no encontrada o no válida para cancelar)");
                txtFechaCancelacion.setValue(LocalDate.now());
                txtMotivoCancelacion.setText("");
                txtFechaCancelacion.setEnabled(false);
                txtMotivoCancelacion.setEnabled(false);
                btnCancelarOrden.setEnabled(false);
                ordenActual = null; // Limpiar referencia
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cancelar la orden. Puede que ya esté completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCerrar.addActionListener(e -> dispose()); // Cierra la ventana
    }
}