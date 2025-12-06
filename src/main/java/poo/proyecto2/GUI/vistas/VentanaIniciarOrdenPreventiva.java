package poo.proyecto2.gui.vistas;

import poo.proyecto2.mantenimiento.*;
import poo.proyecto2.sistema.SistemaPrincipal;
import poo.proyecto2.gui.VentanaMenuPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaIniciarOrdenPreventiva extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Opcional

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdOrden;
    private JTextField txtIdOrden;
    private JButton btnBuscarOrden;
    private JLabel lblInfoOrden; // Muestra info básica de la orden encontrada
    private JLabel lblFechaInicio;
    private JFormattedTextField txtFechaInicio; // Campo para ingresar la fecha de inicio real
    private JButton btnIniciarOrden;
    private JButton btnCancelar;

    // Variable temporal para la orden actual
    private OrdenTrabajoPreventiva ordenActual;

    public VentanaIniciarOrdenPreventiva(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        inicializarComponentes();
        configurarEventos();
        setTitle("Iniciar Orden de Trabajo Preventiva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(600, 200); // Tamaño ajustado
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Iniciar Orden de Trabajo Preventiva", SwingConstants.CENTER);
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
        lblInfoOrden = new JLabel(" (Orden no encontrada o no válida para iniciar)");
        lblInfoOrden.setFont(lblInfoOrden.getFont().deriveFont(Font.ITALIC));
        panelFormulario.add(lblInfoOrden, gbc);

        // Fila 1: Fecha Inicio Real
        gbc.gridx = 0; gbc.gridy = 1;
        lblFechaInicio = new JLabel("Fecha de Inicio Real *:");
        panelFormulario.add(lblFechaInicio, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFechaInicio = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaInicio.setValue(LocalDate.now()); // Fecha por defecto
        txtFechaInicio.setEnabled(false); // Deshabilitado hasta que se busque una orden válida
        panelFormulario.add(txtFechaInicio, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnIniciarOrden = new JButton("Iniciar Orden");
        btnCancelar = new JButton("Cancelar");
        btnIniciarOrden.setEnabled(false); // Deshabilitado hasta que se busque una orden válida
        panelBotones.add(btnIniciarOrden);
        panelBotones.add(btnCancelar);
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
                            // Permitir iniciar si está en PENDIENTE
                            if (ordenPrev.getEstado() == OrdenTrabajo.EstadoOrden.PENDIENTE) {
                                lblInfoOrden.setText(" (Orden ID: " + orden.getId() + " - Estado: " + orden.getEstado() + " - Equipo ID: " + orden.getIdEquipo() + ")");
                                // Guardar referencia temporal
                                ordenActual = ordenPrev;
                                // Habilitar campo de fecha y botón de iniciar
                                txtFechaInicio.setEnabled(true);
                                btnIniciarOrden.setEnabled(true);
                            } else {
                                JOptionPane.showMessageDialog(this, "La orden ID " + id + " no está en estado PENDIENTE. No se puede iniciar.", "Error", JOptionPane.ERROR_MESSAGE);
                                lblInfoOrden.setText(" (Orden no válida para iniciar)");
                                txtFechaInicio.setEnabled(false);
                                btnIniciarOrden.setEnabled(false);
                                ordenActual = null; // Limpiar referencia
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "El ID " + id + " pertenece a una orden de tipo incorrecto (no Preventiva).", "Error", JOptionPane.ERROR_MESSAGE);
                            lblInfoOrden.setText(" (Orden no es Preventiva)");
                            txtFechaInicio.setEnabled(false);
                            btnIniciarOrden.setEnabled(false);
                            ordenActual = null; // Limpiar referencia
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró una orden con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                        lblInfoOrden.setText(" (Orden no encontrada)");
                        txtFechaInicio.setEnabled(false);
                        btnIniciarOrden.setEnabled(false);
                        ordenActual = null; // Limpiar referencia
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de orden válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de orden.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnIniciarOrden.addActionListener(e -> {
            if (ordenActual == null) {
                JOptionPane.showMessageDialog(this, "No hay una orden seleccionada para iniciar.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar campos requeridos
            if (txtFechaInicio.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios (marcados con *).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate fechaInicio;
            try {
                fechaInicio = LocalDate.parse(txtFechaInicio.getText().trim());
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese una fecha de inicio válida en formato AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Llamar al sistema para iniciar la orden
            boolean iniciada = sistema.iniciarOrdenPreventiva(ordenActual.getId(), fechaInicio);

            if (iniciada) {
                JOptionPane.showMessageDialog(this, "Orden ID " + ordenActual.getId() + " iniciada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Limpiar formulario y deshabilitar campos
                txtIdOrden.setText("");
                lblInfoOrden.setText(" (Orden no encontrada o no válida para iniciar)");
                txtFechaInicio.setValue(LocalDate.now());
                txtFechaInicio.setEnabled(false);
                btnIniciarOrden.setEnabled(false);
                ordenActual = null; // Limpiar referencia
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo iniciar la orden. Puede que ya esté iniciada, completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana
    }
}