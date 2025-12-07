package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.equipos.NodoEquipo;
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

public class VentanaRegistrarOrdenCorrectiva extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Opcional

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo;
    private JButton btnBuscarEquipo;
    private JLabel lblInfoEquipo; // Para mostrar info del equipo encontrado
    private JLabel lblFechaOrden;
    private JFormattedTextField txtFechaOrden;
    private JLabel lblFechaEjecucion;
    private JFormattedTextField txtFechaEjecucion;
    private JLabel lblObservaciones;
    private JTextArea txtObservaciones;
    private JScrollPane scrollObservaciones;

    // Panel para gestionar fallas reportadas (solo causas y acciones)
    private JPanel panelFallasReportadas;
    private JLabel lblFallasRepTitulo;
    private JScrollPane scrollTablaFallasRep;
    private JTable tablaFallasRep;
    private DefaultTableModel modeloTablaFallasRep;
    private JButton btnAgregarFallaRep;
    private JButton btnEliminarFallaRep;

    // Panel para gestionar fallas encontradas (con ID de falla maestra)
    private JPanel panelFallasEncontradas;
    private JLabel lblFallasEncTitulo;
    private JScrollPane scrollTablaFallasEnc;
    private JTable tablaFallasEnc;
    private DefaultTableModel modeloTablaFallasEnc;
    private JButton btnAgregarFallaEnc;
    private JButton btnEliminarFallaEnc;

    // Ventana secundaria para agregar falla encontrada
    private JDialog ventanaAgregarFallaEnc;
    private JComboBox<FallaEquipo> cmbFallasDisponibles;
    private JTextField txtCausasEnc;
    private JTextField txtAccionesEnc;

    private JButton btnRegistrar;
    private JButton btnCancelar;

    // Columnas de las tablas de fallas
    private static final String[] COLUMNAS_FALLAS_REP = {"Causas", "Acciones Tomadas"};
    private static final String[] COLUMNAS_FALLAS_ENC = {"ID Falla", "Descripción", "Causas", "Acciones Tomadas"};

    // Variables temporales para almacenar fallas antes de guardar
    private List<OrdenTrabajo.FallaReportada> fallasReportadasTemp;
    private List<OrdenTrabajo.FallaEncontrada> fallasEncontradasTemp;

    public VentanaRegistrarOrdenCorrectiva(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        this.fallasReportadasTemp = new ArrayList<>();
        this.fallasEncontradasTemp = new ArrayList<>();
        inicializarComponentes();
        configurarEventos();
        setTitle("Registrar Nueva Orden de Trabajo Correctiva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(900, 700);
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Registrar Nueva Orden de Trabajo Correctiva", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Formulario ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: ID Equipo
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        lblIdEquipo = new JLabel("ID del Equipo *:");
        panelFormulario.add(lblIdEquipo, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtIdEquipo = new JTextField(10);
        panelFormulario.add(txtIdEquipo, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        btnBuscarEquipo = new JButton("Buscar");
        panelFormulario.add(btnBuscarEquipo, gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblInfoEquipo = new JLabel(" (Equipo no encontrado)");
        lblInfoEquipo.setFont(lblInfoEquipo.getFont().deriveFont(Font.ITALIC));
        panelFormulario.add(lblInfoEquipo, gbc);

        // Fila 1: Fecha Orden
        gbc.gridx = 0; gbc.gridy = 1;
        lblFechaOrden = new JLabel("Fecha de la Orden *:");
        panelFormulario.add(lblFechaOrden, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFechaOrden = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaOrden.setValue(LocalDate.now()); // Fecha por defecto
        panelFormulario.add(txtFechaOrden, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 2: Fecha Ejecución
        gbc.gridx = 0; gbc.gridy = 2;
        lblFechaEjecucion = new JLabel("Fecha de Ejecución *:");
        panelFormulario.add(lblFechaEjecucion, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFechaEjecucion = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaEjecucion.setValue(LocalDate.now().plusDays(1)); // Fecha por defecto: mañana
        panelFormulario.add(txtFechaEjecucion, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 3: Observaciones
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;
        lblObservaciones = new JLabel("Observaciones:");
        panelFormulario.add(lblObservaciones, gbc);

        gbc.gridy = 4;
        txtObservaciones = new JTextArea(3, 30);
        scrollObservaciones = new JScrollPane(txtObservaciones);
        panelFormulario.add(scrollObservaciones, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel Superior Central: Fallas Reportadas y Encontradas ---
        JSplitPane splitFallas = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // --- Panel de Fallas Reportadas ---
        panelFallasReportadas = new JPanel(new BorderLayout());
        panelFallasReportadas.setBorder(BorderFactory.createTitledBorder("Fallas Reportadas Inicialmente"));

        modeloTablaFallasRep = new DefaultTableModel(COLUMNAS_FALLAS_REP, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No se edita directamente la tabla
            }
        };
        tablaFallasRep = new JTable(modeloTablaFallasRep);
        scrollTablaFallasRep = new JScrollPane(tablaFallasRep);
        panelFallasReportadas.add(scrollTablaFallasRep, BorderLayout.CENTER);

        JPanel panelBotonesRep = new JPanel(new FlowLayout());
        btnAgregarFallaRep = new JButton("Agregar");
        btnEliminarFallaRep = new JButton("Eliminar");
        panelBotonesRep.add(btnAgregarFallaRep);
        panelBotonesRep.add(btnEliminarFallaRep);
        panelFallasReportadas.add(panelBotonesRep, BorderLayout.SOUTH);

        // --- Panel de Fallas Encontradas ---
        panelFallasEncontradas = new JPanel(new BorderLayout());
        panelFallasEncontradas.setBorder(BorderFactory.createTitledBorder("Fallas Encontradas Durante el Trabajo"));

        modeloTablaFallasEnc = new DefaultTableModel(COLUMNAS_FALLAS_ENC, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No se edita directamente la tabla
            }
        };
        tablaFallasEnc = new JTable(modeloTablaFallasEnc);
        scrollTablaFallasEnc = new JScrollPane(tablaFallasEnc);
        panelFallasEncontradas.add(scrollTablaFallasEnc, BorderLayout.CENTER);

        JPanel panelBotonesEnc = new JPanel(new FlowLayout());
        btnAgregarFallaEnc = new JButton("Agregar");
        btnEliminarFallaEnc = new JButton("Eliminar");
        panelBotonesEnc.add(btnAgregarFallaEnc);
        panelBotonesEnc.add(btnEliminarFallaEnc);
        panelFallasEncontradas.add(panelBotonesEnc, BorderLayout.SOUTH);

        splitFallas.setTopComponent(panelFallasReportadas);
        splitFallas.setBottomComponent(panelFallasEncontradas);
        splitFallas.setResizeWeight(0.5); // División 50-50

        // Añadir el split de fallas al centro del formulario principal
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        panelFormulario.add(splitFallas, gbc);
        gbc.weighty = 0.0; // Resetear weighty

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar Orden");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnBuscarEquipo.addActionListener(e -> {
            String idStr = txtIdEquipo.getText().trim();
            if (!idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    NodoEquipo equipo = sistema.buscarEquipoPorId(id);
                    if (equipo != null) {
                        lblInfoEquipo.setText(" (" + equipo.getDescripcion() + " - ID: " + equipo.getId() + ")");
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró un equipo con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                        lblInfoEquipo.setText(" (Equipo no encontrado)");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnAgregarFallaRep.addActionListener(e -> {
            String causas = JOptionPane.showInputDialog(this, "Ingrese las causas de la falla reportada:");
            if (causas != null && !causas.trim().isEmpty()) {
                String acciones = JOptionPane.showInputDialog(this, "Ingrese las acciones tomadas:");
                if (acciones != null && !acciones.trim().isEmpty()) {
                    OrdenTrabajo.FallaReportada fallaRep = new OrdenTrabajo.FallaReportada(causas.trim(), acciones.trim());
                    fallasReportadasTemp.add(fallaRep);
                    modeloTablaFallasRep.addRow(new Object[]{causas.trim(), acciones.trim()});
                }
            }
        });

        btnEliminarFallaRep.addActionListener(e -> {
            int filaSeleccionada = tablaFallasRep.getSelectedRow();
            if (filaSeleccionada >= 0) {
                fallasReportadasTemp.remove(filaSeleccionada);
                modeloTablaFallasRep.removeRow(filaSeleccionada);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una falla reportada de la lista para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnAgregarFallaEnc.addActionListener(e -> abrirVentanaAgregarFallaEnc());

        btnEliminarFallaEnc.addActionListener(e -> {
            int filaSeleccionada = tablaFallasEnc.getSelectedRow();
            if (filaSeleccionada >= 0) {
                fallasEncontradasTemp.remove(filaSeleccionada);
                modeloTablaFallasEnc.removeRow(filaSeleccionada);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una falla encontrada de la lista para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnRegistrar.addActionListener(e -> registrarOrden());

        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    private void abrirVentanaAgregarFallaEnc() {
        if (ventanaAgregarFallaEnc != null && ventanaAgregarFallaEnc.isVisible()) {
            ventanaAgregarFallaEnc.toFront();
            return;
        }

        ventanaAgregarFallaEnc = new JDialog(this, "Agregar Falla Encontrada", true);
        ventanaAgregarFallaEnc.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaAgregarFallaEnc.setLayout(new BorderLayout());

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
        txtCausasEnc = new JTextField(20);
        gbc.gridx = 1;
        panelAgregarFalla.add(txtCausasEnc, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelAgregarFalla.add(new JLabel("Acciones Tomadas:"), gbc);
        txtAccionesEnc = new JTextField(20);
        gbc.gridx = 1;
        panelAgregarFalla.add(txtAccionesEnc, gbc);

        ventanaAgregarFallaEnc.add(panelAgregarFalla, BorderLayout.CENTER);

        JPanel panelBotonesAgregar = new JPanel(new FlowLayout());
        JButton btnAceptarAgregar = new JButton("Aceptar");
        JButton btnCancelarAgregar = new JButton("Cancelar");
        panelBotonesAgregar.add(btnAceptarAgregar);
        panelBotonesAgregar.add(btnCancelarAgregar);
        ventanaAgregarFallaEnc.add(panelBotonesAgregar, BorderLayout.SOUTH);

        btnAceptarAgregar.addActionListener(aceptarEvt -> {
            FallaEquipo fallaSeleccionada = (FallaEquipo) cmbFallasDisponibles.getSelectedItem();
            String causas = txtCausasEnc.getText().trim();
            String acciones = txtAccionesEnc.getText().trim();

            if (fallaSeleccionada == null || causas.isEmpty() || acciones.isEmpty()) {
                JOptionPane.showMessageDialog(ventanaAgregarFallaEnc, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear la falla encontrada y añadirla a la lista temporal
            OrdenTrabajo.FallaEncontrada fallaEnc = new OrdenTrabajo.FallaEncontrada(fallaSeleccionada.getId(), fallaSeleccionada.getDescripcion(), causas, acciones);
            fallasEncontradasTemp.add(fallaEnc);

            // Añadir la fila a la tabla de la ventana principal
            modeloTablaFallasEnc.addRow(new Object[]{fallaSeleccionada.getId(), fallaSeleccionada.getDescripcion(), causas, acciones});

            ventanaAgregarFallaEnc.dispose();
        });

        btnCancelarAgregar.addActionListener(cancelarEvt -> ventanaAgregarFallaEnc.dispose());

        ventanaAgregarFallaEnc.pack();
        ventanaAgregarFallaEnc.setLocationRelativeTo(this);
        ventanaAgregarFallaEnc.setVisible(true);
    }

    private void registrarOrden() {
        String idEquipoStr = txtIdEquipo.getText().trim();
        if (idEquipoStr.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Por favor, busque y seleccione un equipo.", "Error", JOptionPane.WARNING_MESSAGE);
             return;
        }

        try {
            int idEquipo = Integer.parseInt(idEquipoStr);
            NodoEquipo equipo = sistema.buscarEquipoPorId(idEquipo);
            if (equipo == null) {
                JOptionPane.showMessageDialog(this, "El equipo seleccionado ya no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar fechas
            LocalDate fechaOrden, fechaEjecucion;
            try {
                fechaOrden = LocalDate.parse(txtFechaOrden.getText().trim());
                fechaEjecucion = LocalDate.parse(txtFechaEjecucion.getText().trim());
                if (fechaEjecucion.isBefore(fechaOrden)) {
                    JOptionPane.showMessageDialog(this, "La fecha de ejecución debe ser igual o posterior a la fecha de la orden.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese fechas válidas en formato AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String observaciones = txtObservaciones.getText().trim();

            // Crear la nueva orden correctiva
            int nuevoIdOrden = sistema.obtenerSiguienteIdOrden(); // Asumiendo que tienes este método en SistemaPrincipal
            OrdenTrabajoCorrectiva nuevaOrden = new OrdenTrabajoCorrectiva(nuevoIdOrden, idEquipo, fechaOrden, fechaEjecucion);
            nuevaOrden.setObservaciones(observaciones);

            // Asignar las fallas reportadas y encontradas temporales a la orden
            for (OrdenTrabajo.FallaReportada fr : fallasReportadasTemp) {
                nuevaOrden.registrarFallaReportada(fr.getCausas(), fr.getAccionesTomadas());
            }
            for (OrdenTrabajo.FallaEncontrada fe : fallasEncontradasTemp) {
                nuevaOrden.registrarFallaEncontrada(fe.getIdFalla(), fe.getDescripcionFalla(), fe.getCausas(), fe.getAccionesTomadas());
            }

            // Llamar al sistema para guardar la nueva orden
            sistema.registrarOrdenCorrectiva(nuevaOrden); // Asumiendo que tienes este método

            JOptionPane.showMessageDialog(this, "Orden correctiva ID " + nuevaOrden.getId() + " registrada exitosamente para el equipo ID " + idEquipo + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Opcional: Limpiar formulario y listas temporales
            txtIdEquipo.setText("");
            lblInfoEquipo.setText(" (Equipo no encontrado)");
            txtFechaOrden.setValue(LocalDate.now());
            txtFechaEjecucion.setValue(LocalDate.now().plusDays(1));
            txtObservaciones.setText("");
            modeloTablaFallasRep.setRowCount(0);
            modeloTablaFallasEnc.setRowCount(0);
            fallasReportadasTemp.clear();
            fallasEncontradasTemp.clear();

            // Cerrar ventana
            // dispose(); // Opcional: dejarla abierta para registrar otra

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID de equipo inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}