package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorRegistrarProgramaMantenimiento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class VentanaRegistrarProgramaMantenimiento extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; 

    // Componentes
    private JLabel lblTitulo;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo;
    private JButton btnBuscarEquipo;
    private JLabel lblNombreEquipo; 
    private JLabel lblFasesTitulo;
    private JScrollPane scrollTablaFases;
    private JTable tablaFases;
    private DefaultTableModel modeloTablaFases;
    private JButton btnAgregarFase;
    private JButton btnGuardarPrograma;
    private JButton btnCancelar;

    // Ventana secundaria para agregar/editar fase (ahora es un JDialog)
    private JDialog ventanaFase; // Cambiado de JFrame a JDialog
    private JTextField txtFrecuenciaFase;
    private JComboBox<TipoFrecuencia> cmbTipoFrecuenciaFase;
    private JSpinner spnCiclosFase;
    private JTextArea txtPartesFase;
    private JTextArea txtHerramientasFase;
    private JTextArea txtPersonalFase;
    private JSpinner spnHorasFase;
    private JList<TareaMantenimiento> listaTareasDisponibles;
    private DefaultListModel<TareaMantenimiento> modeloTareasDisp;
    private JList<TareaMantenimiento> listaTareasSeleccionadas;
    private DefaultListModel<TareaMantenimiento> modeloTareasSel;
    private JButton btnAgregarTarea;
    private JButton btnQuitarTarea;
    private JButton btnAceptarFase;
    private JButton btnCancelarFase;

    // Columnas de la tabla de fases
    private static final String[] COLUMNAS_FASES = {"Frecuencia", "Tipo", "Ciclos", "Horas Est.", "Tareas"};

    // Lista temporal para almacenar las fases que se van a guardar
    private List<FaseMantenimiento> fasesTemporales;

    // Referencia al controlador
    private ControladorRegistrarProgramaMantenimiento controlador;

    public VentanaRegistrarProgramaMantenimiento(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        this.fasesTemporales = new ArrayList<>(); // Inicializar lista temporal
        // Crear el controlador pasando el sistema y la vista
        this.controlador = new ControladorRegistrarProgramaMantenimiento(sistema, this);

        inicializarComponentes();
        configurarEventos();
        setTitle("Registrar Nuevo Programa de Mantenimiento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(900, 600);
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Búsqueda de Equipo ---
        JPanel panelBusqueda = new JPanel(new FlowLayout());
        lblIdEquipo = new JLabel("ID del Equipo:");
        txtIdEquipo = new JTextField(10);
        btnBuscarEquipo = new JButton("Buscar");
        lblNombreEquipo = new JLabel(" (Equipo no encontrado)"); 
        lblNombreEquipo.setFont(lblNombreEquipo.getFont().deriveFont(Font.ITALIC));

        panelBusqueda.add(lblIdEquipo);
        panelBusqueda.add(txtIdEquipo);
        panelBusqueda.add(btnBuscarEquipo);
        panelBusqueda.add(lblNombreEquipo);

        add(panelBusqueda, BorderLayout.NORTH);

        // --- Panel Central: Tabla de Fases ---
        lblFasesTitulo = new JLabel("Fases del Programa:", SwingConstants.LEFT);
        lblFasesTitulo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        modeloTablaFases = new DefaultTableModel(COLUMNAS_FASES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaFases = new JTable(modeloTablaFases);
        scrollTablaFases = new JScrollPane(tablaFases);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder(""));
        panelTabla.add(lblFasesTitulo, BorderLayout.NORTH);
        panelTabla.add(scrollTablaFases, BorderLayout.CENTER);

        add(panelTabla, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnAgregarFase = new JButton("Agregar Fase");
        btnGuardarPrograma = new JButton("Guardar Programa");
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnAgregarFase);
        panelBotones.add(btnGuardarPrograma);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        // --- Eventos de Botones ---
        btnBuscarEquipo.addActionListener(e -> controlador.buscarEquipo());
        btnAgregarFase.addActionListener(e -> controlador.agregarFase());
        btnGuardarPrograma.addActionListener(e -> controlador.guardarPrograma());
        btnCancelar.addActionListener(e -> controlador.cancelar()); 
    }

    // --- Métodos para que el controlador acceda y manipule la vista ---
    public JTextField getTxtIdEquipo() { return txtIdEquipo; }
    public void mostrarNombreEquipo(String nombre) { lblNombreEquipo.setText(" (" + nombre + ")"); }
    public void resetearNombreEquipo() { lblNombreEquipo.setText(" (Equipo no encontrado)"); }
    public void limpiarCampoIdEquipo() { txtIdEquipo.setText(""); }
    public void mostrarMensaje(String mensaje, String titulo, int tipo) { JOptionPane.showMessageDialog(this, mensaje, titulo, tipo); }
    public List<FaseMantenimiento> getFasesTemporales() { return new ArrayList<>(fasesTemporales); } // Devolver copia
    public void limpiarFormulario() {
        txtIdEquipo.setText("");
        resetearNombreEquipo();
        modeloTablaFases.setRowCount(0);
        fasesTemporales.clear();
    }
    public void cerrarVentana() { this.dispose(); }

    // --- Método para abrir la ventana de agregar/editar fase ---
    public void abrirVentanaAgregarFase() {
        if (ventanaFase != null && ventanaFase.isVisible()) {
            ventanaFase.toFront();
            return;
        }

        ventanaFase = new JDialog(this, "Agregar/Editar Fase", true);
        ventanaFase.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaFase.setLayout(new BorderLayout());

        // --- Panel Central: Formulario y Tareas ---
        JSplitPane splitFase = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitFase.setResizeWeight(0.5);

        // --- Panel Izquierdo: Formulario de Fase ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Frecuencia:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFrecuenciaFase = new JTextField(5);
        txtFrecuenciaFase.setText("1");
        panelForm.add(txtFrecuenciaFase, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        cmbTipoFrecuenciaFase = new JComboBox<>(TipoFrecuencia.values());
        panelForm.add(cmbTipoFrecuenciaFase, gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelForm.add(new JLabel("Ciclos (0=Recurrente):"), gbc);
        gbc.gridx = 4;
        spnCiclosFase = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        panelForm.add(spnCiclosFase, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 5; gbc.fill = GridBagConstraints.BOTH;
        panelForm.add(new JLabel("Partes/Repuestos:"), gbc);
        gbc.gridy = 2;
        txtPartesFase = new JTextArea(2, 20);
        JScrollPane scrollPartes = new JScrollPane(txtPartesFase);
        panelForm.add(scrollPartes, gbc);

        gbc.gridy = 3;
        panelForm.add(new JLabel("Herramientas:"), gbc);
        gbc.gridy = 4;
        txtHerramientasFase = new JTextArea(2, 20);
        JScrollPane scrollHerramientas = new JScrollPane(txtHerramientasFase);
        panelForm.add(scrollHerramientas, gbc);

        gbc.gridy = 5;
        panelForm.add(new JLabel("Personal Requerido:"), gbc);
        gbc.gridy = 6;
        txtPersonalFase = new JTextArea(2, 20);
        JScrollPane scrollPersonal = new JScrollPane(txtPersonalFase);
        panelForm.add(scrollPersonal, gbc);

        gbc.gridy = 7;
        panelForm.add(new JLabel("Horas Estimadas:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE;
        spnHorasFase = new JSpinner(new SpinnerNumberModel(1.0f, 0.1f, 1000.0f, 0.5f));
        panelForm.add(spnHorasFase, gbc);

        // --- Panel Derecho: Selección de Tareas ---
        JPanel panelTareas = new JPanel(new BorderLayout());
        panelTareas.setBorder(BorderFactory.createTitledBorder("Tareas de la Fase"));

        modeloTareasDisp = new DefaultListModel<>();
        List<TareaMantenimiento> todasLasTareas = sistema.obtenerTodasLasTareasMaestras();
        System.out.println("DEBUG VFase: Número de tareas obtenidas desde sistema: " + todasLasTareas.size()); // DEBUG
        for (TareaMantenimiento tarea : todasLasTareas) {
            System.out.println("DEBUG VFase: Tarea ID " + tarea.getId() + ": " + tarea.getDescripcion()); // DEBUG
            modeloTareasDisp.addElement(tarea);
        }
        listaTareasDisponibles = new JList<>(modeloTareasDisp);
        JScrollPane scrollDisp = new JScrollPane(listaTareasDisponibles);

        modeloTareasSel = new DefaultListModel<>();
        listaTareasSeleccionadas = new JList<>(modeloTareasSel);
        JScrollPane scrollSel = new JScrollPane(listaTareasSeleccionadas);

        btnAgregarTarea = new JButton(">>");
        btnQuitarTarea = new JButton("<<");
        JPanel panelBotonesTareas = new JPanel(new GridLayout(2, 1));
        panelBotonesTareas.add(btnAgregarTarea);
        panelBotonesTareas.add(btnQuitarTarea);

        JPanel panelListasConBotones = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTareas = new GridBagConstraints();
        gbcTareas.insets = new Insets(5, 5, 5, 5);

        gbcTareas.gridx = 0; gbcTareas.gridy = 0; gbcTareas.anchor = GridBagConstraints.WEST;
        panelListasConBotones.add(new JLabel("Disponibles:"), gbcTareas);
        gbcTareas.gridy = 1; gbcTareas.fill = GridBagConstraints.BOTH; gbcTareas.weightx = 1.0; gbcTareas.weighty = 1.0;
        panelListasConBotones.add(scrollDisp, gbcTareas);

        gbcTareas.gridx = 1; gbcTareas.gridy = 1; gbcTareas.fill = GridBagConstraints.NONE; gbcTareas.weightx = 0.0; gbcTareas.weighty = 0.0;
        panelListasConBotones.add(panelBotonesTareas, gbcTareas);

        gbcTareas.gridx = 2; gbcTareas.gridy = 0; gbcTareas.anchor = GridBagConstraints.WEST;
        panelListasConBotones.add(new JLabel("Seleccionadas:"), gbcTareas);
        gbcTareas.gridy = 1; gbcTareas.fill = GridBagConstraints.BOTH; gbcTareas.weightx = 1.0; gbcTareas.weighty = 1.0;
        panelListasConBotones.add(scrollSel, gbcTareas);

        panelTareas.add(panelListasConBotones, BorderLayout.CENTER);

        splitFase.setLeftComponent(panelForm);
        splitFase.setRightComponent(panelTareas);

        ventanaFase.add(splitFase, BorderLayout.CENTER);

        // --- Panel Botones Fase ---
        JPanel panelBotonesFase = new JPanel(new FlowLayout());
        btnAceptarFase = new JButton("Aceptar");
        btnCancelarFase = new JButton("Cancelar");
        panelBotonesFase.add(btnAceptarFase);
        panelBotonesFase.add(btnCancelarFase);
        ventanaFase.add(panelBotonesFase, BorderLayout.SOUTH);

        // --- Eventos Ventana Fase ---
        btnAgregarTarea.addActionListener(e -> {
            int[] indices = listaTareasDisponibles.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                TareaMantenimiento tarea = modeloTareasDisp.getElementAt(indices[i]);
                if (!modeloTareasSel.contains(tarea)) {
                    modeloTareasSel.addElement(tarea);
                }
            }
            listaTareasDisponibles.clearSelection();
        });

        btnQuitarTarea.addActionListener(e -> {
            int[] indices = listaTareasSeleccionadas.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                modeloTareasSel.remove(indices[i]);
            }
            listaTareasSeleccionadas.clearSelection();
        });

        btnCancelarFase.addActionListener(e -> ventanaFase.dispose());

        btnAceptarFase.addActionListener(e -> {
            try {
                int frecuencia = Integer.parseInt(txtFrecuenciaFase.getText().trim());
                if (frecuencia <= 0) {
                    JOptionPane.showMessageDialog(ventanaFase, "La frecuencia debe ser un número mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                TipoFrecuencia tipoFreq = (TipoFrecuencia) cmbTipoFrecuenciaFase.getSelectedItem();
                int ciclos = (Integer) spnCiclosFase.getValue();
                String partes = txtPartesFase.getText().trim();
                String herramientas = txtHerramientasFase.getText().trim();
                String personal = txtPersonalFase.getText().trim();
                float horas = ((Number) spnHorasFase.getValue()).floatValue();

                FaseMantenimiento nuevaFase = new FaseMantenimiento(tipoFreq, frecuencia, ciclos, partes, herramientas, personal, horas);

                // --- AÑADIR LAS TAREAS SELECCIONADAS A LA FASE ---
                for (int i = 0; i < modeloTareasSel.getSize(); i++) {
                    TareaMantenimiento tarea = modeloTareasSel.getElementAt(i);
                    nuevaFase.agregarTareaMaestra(tarea.getId());
                }

                fasesTemporales.add(nuevaFase);

                // Actualizar tabla de fases en la ventana principal
                Object[] filaTabla = {
                    frecuencia + " " + tipoFreq,
                    tipoFreq,
                    ciclos == 0 ? "Recurrente" : ciclos,
                    horas,
                    modeloTareasSel.getSize() + " tareas"
                };
                modeloTablaFases.addRow(filaTabla);

                ventanaFase.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ventanaFase, "Por favor, ingrese valores numéricos válidos para Frecuencia, Ciclos y Horas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        ventanaFase.setSize(900, 600);
        ventanaFase.setLocationRelativeTo(this);
        ventanaFase.setVisible(true);
    }
}