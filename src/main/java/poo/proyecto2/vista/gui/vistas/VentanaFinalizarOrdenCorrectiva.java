package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.modelo.equipos.FallaEquipo;

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
    private VentanaMenuPrincipal ventanaPadre; // Opcional

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdOrden;
    private JTextField txtIdOrden;
    private JButton btnBuscarOrden;
    private JLabel lblInfoOrden; // Muestra info básica de la orden encontrada
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

    // Variable temporal para la orden actual
    private OrdenTrabajoCorrectiva ordenActual;

    public VentanaFinalizarOrdenCorrectiva(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
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
        txtIdOrden = new JTextField(15); // <-- Aumentado de 10 a 15
        txtIdOrden.setMinimumSize(new Dimension(100, 25)); // Tamaño mínimo
        txtIdOrden.setPreferredSize(new Dimension(150, 25)); // Tamaño preferido
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
        txtFechaFin.setValue(LocalDate.now()); // Fecha por defecto
        txtFechaFin.setEnabled(false); // Deshabilitado hasta que se busque una orden
        panelFormulario.add(txtFechaFin, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 2: Horas Trabajo
        gbc.gridx = 0; gbc.gridy = 2;
        lblHorasTrabajo = new JLabel("Horas de Trabajo *:");
        panelFormulario.add(lblHorasTrabajo, gbc);

        gbc.gridx = 1;
        spnHorasTrabajo = new JSpinner(new SpinnerNumberModel(0.0f, 0.0f, 1000.0f, 0.5f));
        spnHorasTrabajo.setEnabled(false); // Deshabilitado hasta que se busque una orden
        panelFormulario.add(spnHorasTrabajo, gbc);

        // Fila 3: Costo Mano de Obra
        gbc.gridx = 0; gbc.gridy = 3;
        lblCostoManoObra = new JLabel("Costo Mano de Obra *:");
        panelFormulario.add(lblCostoManoObra, gbc);

        gbc.gridx = 1;
        txtCostoManoObra = new JFormattedTextField(new java.text.DecimalFormat("#,##0.00"));
        txtCostoManoObra.setValue(0.0); // Valor por defecto
        txtCostoManoObra.setEnabled(false); // Deshabilitado hasta que se busque una orden
        panelFormulario.add(txtCostoManoObra, gbc);

        // Fila 4: Costo Materiales
        gbc.gridx = 0; gbc.gridy = 4;
        lblCostoMateriales = new JLabel("Costo Materiales *:");
        panelFormulario.add(lblCostoMateriales, gbc);

        gbc.gridx = 1;
        txtCostoMateriales = new JFormattedTextField(new java.text.DecimalFormat("#,##0.00"));
        txtCostoMateriales.setValue(0.0); // Valor por defecto
        txtCostoMateriales.setEnabled(false); // Deshabilitado hasta que se busque una orden
        panelFormulario.add(txtCostoMateriales, gbc);

        // Fila 5: Observaciones
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;
        lblObservaciones = new JLabel("Observaciones:");
        panelFormulario.add(lblObservaciones, gbc);

        gbc.gridy = 6;
        txtObservaciones = new JTextArea(3, 30);
        txtObservaciones.setEnabled(false); // Deshabilitado hasta que se busque una orden
        scrollObservaciones = new JScrollPane(txtObservaciones);
        panelFormulario.add(scrollObservaciones, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel de Fallas ---
        panelFallas = new JPanel(new BorderLayout());
        panelFallas.setBorder(BorderFactory.createTitledBorder("Fallas Encontradas Durante el Trabajo"));

        modeloTablaFallas = new DefaultTableModel(COLUMNAS_FALLAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No se edita directamente la tabla
            }
        };
        tablaFallas = new JTable(modeloTablaFallas);
        scrollTablaFallas = new JScrollPane(tablaFallas);
        panelFallas.add(scrollTablaFallas, BorderLayout.CENTER);

        JPanel panelBotonesFallas = new JPanel(new FlowLayout());
        btnAgregarFalla = new JButton("Agregar Falla");
        btnEliminarFalla = new JButton("Eliminar Falla");
        btnAgregarFalla.setEnabled(false); // Deshabilitado hasta que se busque una orden
        btnEliminarFalla.setEnabled(false); // Deshabilitado hasta que se busque una orden
        panelBotonesFallas.add(btnAgregarFalla);
        panelBotonesFallas.add(btnEliminarFalla);
        panelFallas.add(panelBotonesFallas, BorderLayout.SOUTH);

        // Agregar panel de fallas al sur del formulario
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;
        panelFormulario.add(panelFallas, gbc);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnFinalizar = new JButton("Finalizar Orden");
        btnCancelar = new JButton("Cancelar");
        btnFinalizar.setEnabled(false); // Deshabilitado hasta que se busque una orden
        panelBotones.add(btnFinalizar);
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
                        if (orden instanceof OrdenTrabajoCorrectiva) { // <-- Verificar tipo correctivo
                            OrdenTrabajoCorrectiva ordenCorr = (OrdenTrabajoCorrectiva) orden;
                            // Permitir finalizar si está en EN_PROGRESO
                            if (ordenCorr.getEstado() == OrdenTrabajo.EstadoOrden.EN_PROGRESO) {
                                lblInfoOrden.setText(" (Orden ID: " + orden.getId() + " - Estado: " + orden.getEstado() + " - Equipo ID: " + orden.getIdEquipo() + ")");
                                // Guardar referencia temporal
                                ordenActual = ordenCorr;
                                // Limpiar tabla de fallas y cargar las existentes (si las hay) - Opcional, si quieres mostrar las que ya estaban registradas
                                modeloTablaFallas.setRowCount(0); // Limpiar tabla
                                for (OrdenTrabajo.FallaEncontrada falla : ordenCorr.getFallasEncontradas()) {
                                    Object[] fila = {falla.getIdFalla(), falla.getDescripcionFalla(), falla.getCausas(), falla.getAccionesTomadas()};
                                    modeloTablaFallas.addRow(fila);
                                }
                                // Habilitar campos de finalización
                                txtFechaFin.setEnabled(true);
                                spnHorasTrabajo.setEnabled(true);
                                txtCostoManoObra.setEnabled(true);
                                txtCostoMateriales.setEnabled(true);
                                txtObservaciones.setEnabled(true);
                                btnAgregarFalla.setEnabled(true);
                                btnEliminarFalla.setEnabled(true);
                                btnFinalizar.setEnabled(true);
                            } else {
                                JOptionPane.showMessageDialog(this, "La orden ID " + id + " no está en estado EN PROGRESO. No se puede finalizar.", "Error", JOptionPane.ERROR_MESSAGE);
                                lblInfoOrden.setText(" (Orden no válida para finalizar)");
                                limpiarCamposDespuesDeBuscar();
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "El ID " + id + " pertenece a una orden de tipo incorrecto (no Correctiva).", "Error", JOptionPane.ERROR_MESSAGE);
                            lblInfoOrden.setText(" (Orden no es Correctiva)");
                            limpiarCamposDespuesDeBuscar();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró una orden con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                        lblInfoOrden.setText(" (Orden no encontrada)");
                        limpiarCamposDespuesDeBuscar();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de orden válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de orden.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnAgregarFalla.addActionListener(e -> abrirVentanaAgregarFalla());

        btnEliminarFalla.addActionListener(e -> {
            int filaSeleccionada = tablaFallas.getSelectedRow();
            if (filaSeleccionada >= 0) {
                modeloTablaFallas.removeRow(filaSeleccionada);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una falla de la lista para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnFinalizar.addActionListener(e -> finalizarOrden());

        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    private void limpiarCamposDespuesDeBuscar() {
        // Limpiar campos de finalización si no se encontró una orden válida
        txtFechaFin.setValue(LocalDate.now());
        spnHorasTrabajo.setValue(0.0f);
        txtCostoManoObra.setValue(0.0);
        txtCostoMateriales.setValue(0.0);
        txtObservaciones.setText("");
        // Limpiar tabla de fallas
        modeloTablaFallas.setRowCount(0);
        // Deshabilitar campos
        txtFechaFin.setEnabled(false);
        spnHorasTrabajo.setEnabled(false);
        txtCostoManoObra.setEnabled(false);
        txtCostoMateriales.setEnabled(false);
        txtObservaciones.setEnabled(false);
        btnAgregarFalla.setEnabled(false);
        btnEliminarFalla.setEnabled(false);
        btnFinalizar.setEnabled(false);
        // Limpiar referencia temporal
        ordenActual = null;
    }

    private void abrirVentanaAgregarFalla() {
        if (ordenActual == null) {
            JOptionPane.showMessageDialog(this, "Por favor, busque una orden primero.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

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

    private void finalizarOrden() {
        if (ordenActual == null) {
            JOptionPane.showMessageDialog(this, "No hay una orden seleccionada para finalizar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar campos requeridos
        if (txtFechaFin.getText().trim().isEmpty() ||
            spnHorasTrabajo.getValue().equals(0.0f) ||
            txtCostoManoObra.getText().trim().isEmpty() ||
            txtCostoMateriales.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios (marcados con *).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaFin;
        try {
            fechaFin = LocalDate.parse(txtFechaFin.getText().trim());
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una fecha de finalización válida en formato AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float horas = ((Number) spnHorasTrabajo.getValue()).floatValue();
        int costoMO = ((Number) txtCostoManoObra.getValue()).intValue();
        int costoMat = ((Number) txtCostoMateriales.getValue()).intValue();
        String observaciones = txtObservaciones.getText().trim();

        // --- PROCESAR LAS FALLAS DE LA TABLA ---
        List<OrdenTrabajo.FallaEncontrada> nuevasFallas = new ArrayList<>();
        for (int i = 0; i < modeloTablaFallas.getRowCount(); i++) {
            int idFalla = (Integer) modeloTablaFallas.getValueAt(i, 0);
            String descripcionFalla = (String) modeloTablaFallas.getValueAt(i, 1);
            String causas = (String) modeloTablaFallas.getValueAt(i, 2);
            String acciones = (String) modeloTablaFallas.getValueAt(i, 3);
            nuevasFallas.add(new OrdenTrabajo.FallaEncontrada(idFalla, descripcionFalla, causas, acciones));
        }
        // --- FIN PROCESAR FALLAS ---

        // Llamar al sistema para finalizar la orden con las nuevas fallas
        boolean finalizada = sistema.finalizarOrdenCorrectiva(ordenActual.getId(), fechaFin, horas, costoMO, costoMat, observaciones, nuevasFallas); // <-- Usar método correctivo

        if (finalizada) {
            JOptionPane.showMessageDialog(this, "Orden ID " + ordenActual.getId() + " finalizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Limpiar formulario y deshabilitar campos
            limpiarCamposDespuesDeBuscar();
            // Actualizar el árbol en la ventana principal si es necesario
            if (ventanaPadre != null) {
                // ventanaPadre.actualizarVistaArbol(); // Llama al método de actualización si existe
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo finalizar la orden. Puede que ya esté completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}