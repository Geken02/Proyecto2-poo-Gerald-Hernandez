package poo.proyecto2.gui.vistas;

import poo.proyecto2.mantenimiento.*;
import poo.proyecto2.sistema.SistemaPrincipal;
import poo.proyecto2.gui.VentanaMenuPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VentanaEditarOrdenPreventiva extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Referencia a la ventana principal (opcional)
    private OrdenTrabajoPreventiva ordenAErEditar; // La orden específica que se va a editar

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdOrden;
    private JTextField txtIdOrden;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo;
    private JLabel lblEstado;
    private JTextField txtEstado;
    private JLabel lblFechaOrden;
    private JLabel lblFechaEjecucion;
    private JLabel lblFechasInfo; // Para mostrar fechas de orden y ejecución (no editables)
    private JTextField txtFechaOrden;
    private JTextField txtFechaEjecucion;

    // Panel para Tareas
    private JPanel panelTareas;
    private JLabel lblTareasTitulo;
    private JScrollPane scrollTablaTareas;
    private JTable tablaTareas;
    private DefaultTableModel modeloTablaTareas;

    // Panel para Fallas
    private JPanel panelFallas;
    private JLabel lblFallasTitulo;
    private JScrollPane scrollTablaFallas;
    private JTable tablaFallas;
    private DefaultTableModel modeloTablaFallas;

    // Panel Inferior: Acciones
    private JPanel panelAcciones;
    private JPanel panelIniciar;
    private JLabel lblIniciarTitulo;
    private JLabel lblFechaInicioReal;
    private JFormattedTextField txtFechaInicioReal;
    private JButton btnIniciar;

    private JPanel panelFinalizar;
    private JLabel lblFinalizarTitulo;
    private JLabel lblFechaFinReal;
    private JFormattedTextField txtFechaFinReal;
    private JLabel lblHorasTrabajo;
    private JSpinner spnHorasTrabajo;
    private JLabel lblCostoManoObra;
    private JFormattedTextField txtCostoManoObra;
    private JLabel lblCostoMateriales;
    private JFormattedTextField txtCostoMateriales;
    private JLabel lblObservacionesEjecucion;
    private JTextArea txtObservacionesEjecucion;
    private JScrollPane scrollObsEjecucion;
    private JButton btnFinalizar;

    private JPanel panelCancelar;
    private JLabel lblCancelarTitulo;
    private JLabel lblFechaCancelacion;
    private JFormattedTextField txtFechaCancelacion;
    private JLabel lblMotivoCancelacion;
    private JTextArea txtMotivoCancelacion;
    private JScrollPane scrollMotivoCancelacion;
    private JButton btnCancelar;

    private JButton btnCerrar;

    // Columnas de las tablas
    private static final String[] COLUMNAS_TAREAS = {"ID Tarea", "Descripción"};
    private static final String[] COLUMNAS_FALLAS = {"ID Falla", "Descripción", "Causas", "Acciones Tomadas"};

    public VentanaEditarOrdenPreventiva(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre, OrdenTrabajoPreventiva ordenAErEditar) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        this.ordenAErEditar = ordenAErEditar;
        inicializarComponentes();
        cargarDatosIniciales();
        configurarEventos();
        setTitle("Editar Orden Preventiva ID: " + ordenAErEditar.getId());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(1000, 800); // Tamaño más grande para acomodar todo
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Información General ---
        JPanel panelInfoGeneral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        lblIdOrden = new JLabel("ID Orden:");
        panelInfoGeneral.add(lblIdOrden, gbc);

        gbc.gridx = 1;
        txtIdOrden = new JTextField(String.valueOf(ordenAErEditar.getId()));
        txtIdOrden.setEditable(false);
        txtIdOrden.setBackground(new Color(240, 240, 240));
        panelInfoGeneral.add(txtIdOrden, gbc);

        gbc.gridx = 2;
        lblIdEquipo = new JLabel("ID Equipo:");
        panelInfoGeneral.add(lblIdEquipo, gbc);

        gbc.gridx = 3;
        txtIdEquipo = new JTextField(String.valueOf(ordenAErEditar.getIdEquipo()));
        txtIdEquipo.setEditable(false);
        txtIdEquipo.setBackground(new Color(240, 240, 240));
        panelInfoGeneral.add(txtIdEquipo, gbc);

        gbc.gridx = 4;
        lblEstado = new JLabel("Estado:");
        panelInfoGeneral.add(lblEstado, gbc);

        gbc.gridx = 5;
        txtEstado = new JTextField(ordenAErEditar.getEstado().toString());
        txtEstado.setEditable(false);
        txtEstado.setBackground(new Color(240, 240, 240));
        panelInfoGeneral.add(txtEstado, gbc);

        gbc.gridx = 6;
        lblFechaOrden = new JLabel("Fecha Orden:");
        panelInfoGeneral.add(lblFechaOrden, gbc);

        gbc.gridx = 7;
        txtFechaOrden = new JTextField(ordenAErEditar.getFechaOrden() != null ? ordenAErEditar.getFechaOrden().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
        txtFechaOrden.setEditable(false);
        txtFechaOrden.setBackground(new Color(240, 240, 240));
        panelInfoGeneral.add(txtFechaOrden, gbc);

        gbc.gridx = 8;
        lblFechaEjecucion = new JLabel("Fecha Ejecución:");
        panelInfoGeneral.add(lblFechaEjecucion, gbc);

        gbc.gridx = 9;
        txtFechaEjecucion = new JTextField(ordenAErEditar.getFechaEjecucion() != null ? ordenAErEditar.getFechaEjecucion().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
        txtFechaEjecucion.setEditable(false);
        txtFechaEjecucion.setBackground(new Color(240, 240, 240));
        panelInfoGeneral.add(txtFechaEjecucion, gbc);

        add(panelInfoGeneral, BorderLayout.NORTH);

        // --- Panel Central: Tareas y Fallas ---
        JSplitPane splitCentro = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitCentro.setResizeWeight(0.5); // División 50-50

        // Panel de Tareas
        panelTareas = new JPanel(new BorderLayout());
        panelTareas.setBorder(BorderFactory.createTitledBorder("Tareas Asociadas a la Orden"));
        modeloTablaTareas = new DefaultTableModel(COLUMNAS_TAREAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaTareas = new JTable(modeloTablaTareas);
        scrollTablaTareas = new JScrollPane(tablaTareas);
        panelTareas.add(scrollTablaTareas, BorderLayout.CENTER);

        // Panel de Fallas
        panelFallas = new JPanel(new BorderLayout());
        panelFallas.setBorder(BorderFactory.createTitledBorder("Fallas Reportadas/Encontradas"));
        modeloTablaFallas = new DefaultTableModel(COLUMNAS_FALLAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaFallas = new JTable(modeloTablaFallas);
        scrollTablaFallas = new JScrollPane(tablaFallas);
        panelFallas.add(scrollTablaFallas, BorderLayout.CENTER);

        splitCentro.setLeftComponent(panelTareas);
        splitCentro.setRightComponent(panelFallas);

        add(splitCentro, BorderLayout.CENTER);

        // --- Panel Inferior: Acciones (Iniciar, Finalizar, Cancelar) ---
        panelAcciones = new JPanel(new GridLayout(1, 3, 10, 10)); // 3 columnas, espacio entre ellas

        // Panel Iniciar
        panelIniciar = new JPanel(new GridBagLayout());
        panelIniciar.setBorder(BorderFactory.createTitledBorder("Registrar Inicio de la Orden"));
        GridBagConstraints gbcIniciar = new GridBagConstraints();
        gbcIniciar.insets = new Insets(5, 5, 5, 5);
        gbcIniciar.anchor = GridBagConstraints.WEST;

        gbcIniciar.gridx = 0; gbcIniciar.gridy = 0;
        lblIniciarTitulo = new JLabel("Fecha de Inicio Real *:");
        panelIniciar.add(lblIniciarTitulo, gbcIniciar);

        gbcIniciar.gridx = 1;
        txtFechaInicioReal = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaInicioReal.setValue(LocalDate.now());
        txtFechaInicioReal.setEnabled(false);
        panelIniciar.add(txtFechaInicioReal, gbcIniciar);

        gbcIniciar.gridx = 0; gbcIniciar.gridy = 1; gbcIniciar.gridwidth = 2; gbcIniciar.fill = GridBagConstraints.HORIZONTAL;
        btnIniciar = new JButton("Iniciar Orden");
        btnIniciar.setEnabled(false);
        panelIniciar.add(btnIniciar, gbcIniciar);

        // Panel Finalizar
        panelFinalizar = new JPanel(new GridBagLayout());
        panelFinalizar.setBorder(BorderFactory.createTitledBorder("Registrar Finalización de la Orden"));
        GridBagConstraints gbcFinalizar = new GridBagConstraints();
        gbcFinalizar.insets = new Insets(5, 5, 5, 5);
        gbcFinalizar.anchor = GridBagConstraints.WEST;

        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 0;
        lblFinalizarTitulo = new JLabel("Fecha de Fin Real *:");
        panelFinalizar.add(lblFinalizarTitulo, gbcFinalizar);

        gbcFinalizar.gridx = 1;
        txtFechaFinReal = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaFinReal.setValue(LocalDate.now());
        txtFechaFinReal.setEnabled(false);
        panelFinalizar.add(txtFechaFinReal, gbcFinalizar);

        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 1;
        lblHorasTrabajo = new JLabel("Horas de Trabajo *:");
        panelFinalizar.add(lblHorasTrabajo, gbcFinalizar);

        gbcFinalizar.gridx = 1;
        spnHorasTrabajo = new JSpinner(new SpinnerNumberModel(0.0f, 0.0f, 1000.0f, 0.5f));
        spnHorasTrabajo.setEnabled(false);
        panelFinalizar.add(spnHorasTrabajo, gbcFinalizar);

        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 2;
        lblCostoManoObra = new JLabel("Costo Mano de Obra *:");
        panelFinalizar.add(lblCostoManoObra, gbcFinalizar);

        gbcFinalizar.gridx = 1;
        txtCostoManoObra = new JFormattedTextField(new java.text.DecimalFormat("#,##0.00"));
        txtCostoManoObra.setValue(0.0);
        txtCostoManoObra.setEnabled(false);
        panelFinalizar.add(txtCostoManoObra, gbcFinalizar);

        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 3;
        lblCostoMateriales = new JLabel("Costo Materiales *:");
        panelFinalizar.add(lblCostoMateriales, gbcFinalizar);

        gbcFinalizar.gridx = 1;
        txtCostoMateriales = new JFormattedTextField(new java.text.DecimalFormat("#,##0.00"));
        txtCostoMateriales.setValue(0.0);
        txtCostoMateriales.setEnabled(false);
        panelFinalizar.add(txtCostoMateriales, gbcFinalizar);

        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 4; gbcFinalizar.gridwidth = 2; gbcFinalizar.fill = GridBagConstraints.BOTH;
        lblObservacionesEjecucion = new JLabel("Observaciones de Ejecución:");
        panelFinalizar.add(lblObservacionesEjecucion, gbcFinalizar);

        gbcFinalizar.gridy = 5;
        txtObservacionesEjecucion = new JTextArea(3, 30);
        txtObservacionesEjecucion.setEnabled(false);
        scrollObsEjecucion = new JScrollPane(txtObservacionesEjecucion);
        panelFinalizar.add(scrollObsEjecucion, gbcFinalizar);

        gbcFinalizar.gridx = 0; gbcFinalizar.gridy = 6; gbcFinalizar.gridwidth = 2; gbcFinalizar.fill = GridBagConstraints.NONE;
        btnFinalizar = new JButton("Finalizar Orden");
        btnFinalizar.setEnabled(false);
        panelFinalizar.add(btnFinalizar, gbcFinalizar);

        // Panel Cancelar
        panelCancelar = new JPanel(new GridBagLayout());
        panelCancelar.setBorder(BorderFactory.createTitledBorder("Cancelar la Orden"));
        GridBagConstraints gbcCancelar = new GridBagConstraints();
        gbcCancelar.insets = new Insets(5, 5, 5, 5);
        gbcCancelar.anchor = GridBagConstraints.WEST;

        gbcCancelar.gridx = 0; gbcCancelar.gridy = 0;
        lblCancelarTitulo = new JLabel("Fecha de Cancelación *:");
        panelCancelar.add(lblCancelarTitulo, gbcCancelar);

        gbcCancelar.gridx = 1;
        txtFechaCancelacion = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaCancelacion.setValue(LocalDate.now());
        txtFechaCancelacion.setEnabled(false);
        panelCancelar.add(txtFechaCancelacion, gbcCancelar);

        gbcCancelar.gridx = 0; gbcCancelar.gridy = 1; gbcCancelar.gridwidth = 2; gbcCancelar.fill = GridBagConstraints.BOTH;
        lblMotivoCancelacion = new JLabel("Motivo de Cancelación *:");
        panelCancelar.add(lblMotivoCancelacion, gbcCancelar);

        gbcCancelar.gridy = 2;
        txtMotivoCancelacion = new JTextArea(3, 30);
        txtMotivoCancelacion.setEnabled(false);
        scrollMotivoCancelacion = new JScrollPane(txtMotivoCancelacion);
        panelCancelar.add(scrollMotivoCancelacion, gbcCancelar);

        gbcCancelar.gridx = 0; gbcCancelar.gridy = 3; gbcCancelar.gridwidth = 2; gbcCancelar.fill = GridBagConstraints.NONE;
        btnCancelar = new JButton("Cancelar Orden");
        btnCancelar.setEnabled(false);
        panelCancelar.add(btnCancelar, gbcCancelar);

        // Añadir los tres paneles al panel de acciones
        panelAcciones.add(panelIniciar);
        panelAcciones.add(panelFinalizar);
        panelAcciones.add(panelCancelar);

        add(panelAcciones, BorderLayout.SOUTH);

        // --- Botón Cerrar ---
        JPanel panelBotonCerrar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCerrar = new JButton("Cerrar");
        panelBotonCerrar.add(btnCerrar);
        add(panelBotonCerrar, BorderLayout.SOUTH);
    }

    private void cargarDatosIniciales() {
        // Cargar Tareas
        modeloTablaTareas.setRowCount(0);
        for (var tarea : ordenAErEditar.getTareas()) {
            Object[] fila = {tarea.getId(), tarea.getDescripcion()};
            modeloTablaTareas.addRow(fila);
        }

        // Cargar Fallas
        modeloTablaFallas.setRowCount(0);
        // Fallas Reportadas
        for (var falla : ordenAErEditar.getFallasReportadas()) {
            Object[] fila = {"(Reportada)", "", falla.getCausas(), falla.getAccionesTomadas()};
            modeloTablaFallas.addRow(fila);
        }
        // Fallas Encontradas
        for (var falla : ordenAErEditar.getFallasEncontradas()) {
            Object[] fila = {falla.getIdFalla(), falla.getDescripcionFalla(), falla.getCausas(), falla.getAccionesTomadas()};
            modeloTablaFallas.addRow(fila);
        }

        // Cargar datos de inicio/fin/cancelación si existen
        if (ordenAErEditar.getFechaInicioReal() != null) {
            txtFechaInicioReal.setValue(ordenAErEditar.getFechaInicioReal());
        }
        if (ordenAErEditar.getFechaFinReal() != null) {
            txtFechaFinReal.setValue(ordenAErEditar.getFechaFinReal());
            spnHorasTrabajo.setValue(ordenAErEditar.getHorasTrabajo());
            txtCostoManoObra.setValue(ordenAErEditar.getCostoManoObra());
            txtCostoMateriales.setValue(ordenAErEditar.getCostoMateriales());
            txtObservacionesEjecucion.setText(ordenAErEditar.getObservacionesEjecucion() != null ? ordenAErEditar.getObservacionesEjecucion() : "");
        }
        if (ordenAErEditar.getFechaCancelacion() != null) {
            txtFechaCancelacion.setValue(ordenAErEditar.getFechaCancelacion());
            txtMotivoCancelacion.setText(ordenAErEditar.getMotivoCancelacion() != null ? ordenAErEditar.getMotivoCancelacion() : "");
        }

        // Habilitar campos según el estado actual de la orden
        actualizarInterfazSegunEstado();
    }

    private void actualizarInterfazSegunEstado() {
        OrdenTrabajo.EstadoOrden estado = ordenAErEditar.getEstado();
        btnIniciar.setEnabled(estado == OrdenTrabajo.EstadoOrden.PENDIENTE);
        btnFinalizar.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        btnCancelar.setEnabled(estado == OrdenTrabajo.EstadoOrden.PENDIENTE || estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);

        txtFechaInicioReal.setEnabled(estado == OrdenTrabajo.EstadoOrden.PENDIENTE);
        txtFechaFinReal.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        spnHorasTrabajo.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        txtCostoManoObra.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        txtCostoMateriales.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        txtObservacionesEjecucion.setEnabled(estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        txtFechaCancelacion.setEnabled(estado == OrdenTrabajo.EstadoOrden.PENDIENTE || estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
        txtMotivoCancelacion.setEnabled(estado == OrdenTrabajo.EstadoOrden.PENDIENTE || estado == OrdenTrabajo.EstadoOrden.EN_PROGRESO);
    }

    private void configurarEventos() {
        btnIniciar.addActionListener(e -> iniciarOrden());

        btnFinalizar.addActionListener(e -> finalizarOrden());

        btnCancelar.addActionListener(e -> cancelarOrden());

        btnCerrar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    private void iniciarOrden() {
        // Validar campos requeridos
        if (txtFechaInicioReal.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese la fecha de inicio real.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaInicio;
        try {
            fechaInicio = LocalDate.parse(txtFechaInicioReal.getText().trim());
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una fecha de inicio válida en formato AAAA-MM-DD.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamar al sistema para iniciar la orden
        boolean iniciada = sistema.iniciarOrdenPreventiva(ordenAErEditar.getId(), fechaInicio);

        if (iniciada) {
            JOptionPane.showMessageDialog(this, "Orden ID " + ordenAErEditar.getId() + " iniciada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Actualizar el estado de la orden localmente
            ordenAErEditar.iniciar(fechaInicio);
            // Actualizar la interfaz
            txtEstado.setText(ordenAErEditar.getEstado().toString());
            txtFechaInicioReal.setValue(fechaInicio);
            actualizarInterfazSegunEstado(); // Deshabilita iniciar, habilita finalizar/cancelar
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo iniciar la orden. Puede que ya esté iniciada, completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizarOrden() {
        // Validar campos requeridos
        if (txtFechaFinReal.getText().trim().isEmpty() ||
            spnHorasTrabajo.getValue().equals(0.0f) ||
            txtCostoManoObra.getText().trim().isEmpty() ||
            txtCostoMateriales.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios (marcados con *).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaFin;
        float horas;
        int costoMO, costoMat;
        String obsEjecucion;

        try {
            fechaFin = LocalDate.parse(txtFechaFinReal.getText().trim());
            horas = ((Number) spnHorasTrabajo.getValue()).floatValue();
            costoMO = ((Number) txtCostoManoObra.getValue()).intValue();
            costoMat = ((Number) txtCostoMateriales.getValue()).intValue();
            obsEjecucion = txtObservacionesEjecucion.getText().trim();
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una fecha de finalización válida en formato AAAA-MM-DD.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese valores numéricos válidos para Horas, Costo Mano de Obra y Costo Materiales.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamar al sistema para finalizar la orden
        boolean finalizada = sistema.finalizarOrdenPreventiva(ordenAErEditar.getId(), fechaFin, horas, costoMO, costoMat, obsEjecucion, null);

        if (finalizada) {
            JOptionPane.showMessageDialog(this, "Orden ID " + ordenAErEditar.getId() + " finalizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Actualizar el estado y datos de la orden localmente
            ordenAErEditar.finalizar(fechaFin, horas, costoMO, costoMat, obsEjecucion);
            // Actualizar la interfaz
            txtEstado.setText(ordenAErEditar.getEstado().toString());
            txtFechaFinReal.setValue(fechaFin);
            actualizarInterfazSegunEstado(); // Deshabilita iniciar/finalizar/cancelar
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo finalizar la orden. Puede que ya esté completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarOrden() {
        // Validar campos requeridos
        if (txtFechaCancelacion.getText().trim().isEmpty() || txtMotivoCancelacion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios (marcados con *).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaCancelacion;
        String motivo = txtMotivoCancelacion.getText().trim();

        try {
            fechaCancelacion = LocalDate.parse(txtFechaCancelacion.getText().trim());
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una fecha de cancelación válida en formato AAAA-MM-DD.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamar al sistema para cancelar la orden
        boolean cancelada = sistema.cancelarOrdenPreventiva(ordenAErEditar.getId(), fechaCancelacion, motivo);

        if (cancelada) {
            JOptionPane.showMessageDialog(this, "Orden ID " + ordenAErEditar.getId() + " cancelada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Actualizar el estado y datos de la orden localmente
            ordenAErEditar.cancelar(fechaCancelacion, motivo);
            // Actualizar la interfaz
            txtEstado.setText(ordenAErEditar.getEstado().toString());
            txtFechaCancelacion.setValue(fechaCancelacion);
            txtMotivoCancelacion.setText(motivo);
            actualizarInterfazSegunEstado(); // Deshabilita iniciar/finalizar/cancelar
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo cancelar la orden. Puede que ya esté completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}