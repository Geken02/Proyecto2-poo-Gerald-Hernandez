package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal; // Ventana padre

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class VentanaRegistrarProgramaMantenimiento extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Referencia a la ventana principal

    // Componentes
    private JLabel lblTitulo;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo;
    private JButton btnBuscarEquipo;
    private JLabel lblNombreEquipo; // Para mostrar el nombre del equipo encontrado
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

    public VentanaRegistrarProgramaMantenimiento(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        this.fasesTemporales = new ArrayList<>(); // Inicializar lista temporal
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
        lblNombreEquipo = new JLabel(" (Equipo no encontrado)"); // Inicialmente vacío
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
                return false; // No se edita directamente la tabla
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
        btnBuscarEquipo.addActionListener(e -> {
            String idStr = txtIdEquipo.getText().trim();
            if (!idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    NodoEquipo equipo = sistema.buscarEquipoPorId(id);
                    if (equipo != null) {
                        lblNombreEquipo.setText(" (" + equipo.getDescripcion() + ")"); // Mostrar nombre del equipo
                    } else {
                        lblNombreEquipo.setText(" (Equipo no encontrado)");
                        JOptionPane.showMessageDialog(this, "No se encontró un equipo con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnAgregarFase.addActionListener(e -> {
            String idEquipoStr = txtIdEquipo.getText().trim();
            if (idEquipoStr.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Por favor, busque y seleccione un equipo primero.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            try {
                int idEquipo = Integer.parseInt(idEquipoStr);
                NodoEquipo equipo = sistema.buscarEquipoPorId(idEquipo);
                if (equipo == null) {
                    JOptionPane.showMessageDialog(this, "El equipo seleccionado ya no es válido. Por favor, búsquelo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                abrirVentanaAgregarFase(); // Llama al método que crea el JDialog
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID de equipo inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnGuardarPrograma.addActionListener(e -> {
            String idEquipoStr = txtIdEquipo.getText().trim();
            if (idEquipoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, busque y seleccione un equipo primero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int idEquipo = Integer.parseInt(idEquipoStr);
                NodoEquipo equipo = sistema.buscarEquipoPorId(idEquipo);
                if (equipo == null) {
                    JOptionPane.showMessageDialog(this, "El equipo seleccionado ya no es válido. Por favor, búsquelo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (fasesTemporales.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El programa debe tener al menos una fase.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Crear el programa y asociarlo al equipo
                ProgramaMantenimientoPreventivo nuevoPrograma = new ProgramaMantenimientoPreventivo(idEquipo); // Asegúrate que el constructor reciba el idEquipo
                for (FaseMantenimiento fase : fasesTemporales) {
                    nuevoPrograma.agregarFase(fase);
                }

                // *** LLAMAR AL SISTEMA PARA GUARDARLO ***
                boolean guardado = sistema.guardarPrograma(nuevoPrograma);
                if (guardado) {
                    JOptionPane.showMessageDialog(this, "Programa de mantenimiento guardado exitosamente para el equipo ID: " + idEquipo + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    // Opcional: Limpiar la lista temporal y la tabla después de guardar
                    fasesTemporales.clear();
                    modeloTablaFases.setRowCount(0);
                    lblNombreEquipo.setText(" (Equipo no encontrado)"); // Resetear nombre
                    txtIdEquipo.setText(""); // Resetear ID
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo guardar el programa en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID de equipo inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    private void abrirVentanaAgregarFase() {
        if (ventanaFase != null && ventanaFase.isVisible()) {
            // Si la ventana ya está abierta, la traemos al frente
            ventanaFase.toFront();
            return;
        }

        // --- CREAR EL JDIALOG ---
        ventanaFase = new JDialog(this, "Agregar/Editar Fase", true); // 'true' para modalidad.
        ventanaFase.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaFase.setLayout(new BorderLayout());

        // --- Panel Central: Formulario y Tareas ---
        JSplitPane splitFase = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitFase.setResizeWeight(0.5); // División 50-50

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

        // --- Panel Derecho: Selección de Tareas (CORREGIDO) ---
        JPanel panelTareas = new JPanel(new BorderLayout());
        panelTareas.setBorder(BorderFactory.createTitledBorder("Tareas de la Fase"));

        // Inicializar modelos y listas de tareas
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

        // Botones para mover tareas
        btnAgregarTarea = new JButton(">>");
        btnQuitarTarea = new JButton("<<");
        JPanel panelBotonesTareas = new JPanel(new GridLayout(2, 1));
        panelBotonesTareas.add(btnAgregarTarea);
        panelBotonesTareas.add(btnQuitarTarea);

        // Layout CORRECTO para alinear botones entre listas usando GridBagLayout
        JPanel panelListasConBotones = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTareas = new GridBagConstraints();
        gbcTareas.insets = new Insets(5, 5, 5, 5);

        // Etiquetas para las listas
        gbcTareas.gridx = 0; gbcTareas.gridy = 0; gbcTareas.gridwidth = 1; gbcTareas.anchor = GridBagConstraints.WEST;
        panelListasConBotones.add(new JLabel("Disponibles:"), gbcTareas);
        gbcTareas.gridx = 2; gbcTareas.gridy = 0; gbcTareas.gridwidth = 1; gbcTareas.anchor = GridBagConstraints.WEST;
        panelListasConBotones.add(new JLabel("Seleccionadas:"), gbcTareas);

        // Listas y botones
        gbcTareas.gridx = 0; gbcTareas.gridy = 1; gbcTareas.fill = GridBagConstraints.BOTH; gbcTareas.weightx = 1.0; gbcTareas.weighty = 1.0;
        panelListasConBotones.add(scrollDisp, gbcTareas); // Lista disponibles

        gbcTareas.gridx = 1; gbcTareas.gridy = 1; gbcTareas.fill = GridBagConstraints.NONE; gbcTareas.weightx = 0.0; gbcTareas.weighty = 0.0;
        panelListasConBotones.add(panelBotonesTareas, gbcTareas); // Panel con botones en el centro

        gbcTareas.gridx = 2; gbcTareas.gridy = 1; gbcTareas.fill = GridBagConstraints.BOTH; gbcTareas.weightx = 1.0; gbcTareas.weighty = 1.0;
        panelListasConBotones.add(scrollSel, gbcTareas); // Lista seleccionadas

        // Agregar el panel combinado al panel principal de tareas
        panelTareas.add(panelListasConBotones, BorderLayout.CENTER);

        // Añadir form y tareas al split
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
            for (int i = indices.length - 1; i >= 0; i--) { // Iterar backwards para no alterar índices al remover
                TareaMantenimiento tarea = modeloTareasDisp.getElementAt(indices[i]);
                if (!modeloTareasSel.contains(tarea)) { // Prevenir duplicados
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
                float horas = ((Number) spnHorasFase.getValue()).floatValue(); // CORREGIDO: Casting seguro

                // Crear la nueva fase
                FaseMantenimiento nuevaFase = new FaseMantenimiento(tipoFreq, frecuencia, ciclos, partes, herramientas, personal, horas);

                // --- AÑADIR LAS TAREAS SELECCIONADAS A LA FASE (con depuración) ---
                System.out.println("DEBUG VFase: Iniciando proceso de añadir tareas a la fase. Tareas seleccionadas en modelo: " + modeloTareasSel.getSize()); // DEBUG
                for (int i = 0; i < modeloTareasSel.getSize(); i++) {
                    TareaMantenimiento tarea = modeloTareasSel.getElementAt(i);
                    System.out.println("DEBUG VFase: Procesando tarea en índice " + i + ": ID=" + tarea.getId() + ", Desc=" + tarea.getDescripcion()); // DEBUG
                    nuevaFase.agregarTareaMaestra(tarea.getId()); // Añadir ID a la fase
                    System.out.println("DEBUG VFase: Tarea ID " + tarea.getId() + " añadida a la fase."); // DEBUG
                }
                System.out.println("DEBUG VFase: Tareas añadidas. Lista de IDs en la fase: " + nuevaFase.getIdsTareasMaestras()); // DEBUG
                // --- FIN AÑADIR TAREAS ---

                fasesTemporales.add(nuevaFase);
                System.out.println("DEBUG VFase: Fase añadida a la lista temporal. Tamaño actual de fasesTemporales: " + fasesTemporales.size()); // DEBUG

                // Actualizar tabla de fases en la ventana principal
                Object[] filaTabla = {
                    frecuencia + " " + tipoFreq,
                    tipoFreq,
                    ciclos == 0 ? "Recurrente" : ciclos,
                    horas,
                    modeloTareasSel.getSize() + " tareas" // Mostrar cantidad
                };
                modeloTablaFases.addRow(filaTabla);

                ventanaFase.dispose(); // Cierra la ventana de fase

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ventanaFase, "Por favor, ingrese valores numéricos válidos para Frecuencia, Ciclos y Horas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        ventanaFase.setSize(900, 600);
        ventanaFase.setLocationRelativeTo(this); // Centrado en la ventana padre
        ventanaFase.setVisible(true); // Mostrar el dialogo
    }
}