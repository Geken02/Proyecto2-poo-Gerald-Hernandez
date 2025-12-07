package poo.proyecto2.gui.vistas;

import poo.proyecto2.equipos.NodoEquipo;
import poo.proyecto2.mantenimiento.*;
import poo.proyecto2.sistema.SistemaPrincipal;
import poo.proyecto2.gui.VentanaMenuPrincipal; // Ventana padre

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class VentanaEditarProgramaMantenimiento extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Referencia a la ventana principal

    // Componentes (iguales o similares a la ventana de registro)
    private JLabel lblTitulo;
    private JLabel lblIdEquipo; // Solo para mostrar, no editable
    private JTextField txtIdEquipo;
    private JLabel lblNombreEquipo; // Para mostrar el nombre del equipo encontrado
    private JLabel lblFasesTitulo;
    private JScrollPane scrollTablaFases;
    private JTable tablaFases;
    private DefaultTableModel modeloTablaFases;
    private JButton btnAgregarFase;
    private JButton btnEditarFase; // Nuevo botón
    private JButton btnEliminarFase; // Nuevo botón
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

    // Lista temporal para almacenar las fases *editadas* del programa actual
    private List<FaseMantenimiento> fasesTemporales;

    // Referencia al programa original que se está editando
    private ProgramaMantenimientoPreventivo programaOriginal;

    // Referencia al equipo al que pertenece el programa
    private NodoEquipo equipoDelPrograma;

    // Indice temporal de la fase seleccionada para edición
    private int indiceFaseAErEditar = -1; // -1 significa que no hay una fase seleccionada para editar

    public VentanaEditarProgramaMantenimiento(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre, int idEquipo) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;

        // Buscar el equipo y el programa asociado al ID
        this.equipoDelPrograma = sistema.buscarEquipoPorId(idEquipo);
        if (equipoDelPrograma == null) {
            JOptionPane.showMessageDialog(this, "No se encontró un equipo con ID: " + idEquipo + ". No se puede editar su programa.", "Error", JOptionPane.ERROR_MESSAGE);
            dispose(); // Cierra la ventana si no se encuentra el equipo
            return;
        }

        this.programaOriginal = sistema.obtenerProgramaDeEquipo(idEquipo);
        if (programaOriginal == null) {
            JOptionPane.showMessageDialog(this, "El equipo con ID: " + idEquipo + " no tiene un programa de mantenimiento preventivo asociado para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            // Opcional: Permitir crear uno nuevo aquí
            // dispose();
            // return;
        }

        // Inicializar la lista temporal con las fases originales (si existen)
        this.fasesTemporales = new ArrayList<>();
        if (programaOriginal != null) {
            this.fasesTemporales.addAll(programaOriginal.getFases());
        }

        inicializarComponentes();
        cargarDatosIniciales(); // Carga los datos del programa original en la interfaz
        configurarEventos();
        setTitle("Editar Programa de Mantenimiento - Equipo ID: " + idEquipo);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(900, 600);
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Información del Equipo (no editable) ---
        JPanel panelInfoEquipo = new JPanel(new FlowLayout());
        lblIdEquipo = new JLabel("ID del Equipo:"); // Etiqueta
        txtIdEquipo = new JTextField(String.valueOf(equipoDelPrograma.getId()), 10); // Campo con ID
        txtIdEquipo.setEditable(false); // No editable
        txtIdEquipo.setBackground(new Color(240, 240, 240)); // Color gris claro
        lblNombreEquipo = new JLabel(" (" + equipoDelPrograma.getDescripcion() + ")"); // Nombre del equipo
        lblNombreEquipo.setFont(lblNombreEquipo.getFont().deriveFont(Font.ITALIC));

        panelInfoEquipo.add(lblIdEquipo);
        panelInfoEquipo.add(txtIdEquipo);
        panelInfoEquipo.add(lblNombreEquipo);

        add(panelInfoEquipo, BorderLayout.NORTH);

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
        btnEditarFase = new JButton("Editar Fase"); // <-- Nuevo botón
        btnEliminarFase = new JButton("Eliminar Fase"); // <-- Nuevo botón
        btnGuardarPrograma = new JButton("Guardar Cambios");
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnAgregarFase);
        panelBotones.add(btnEditarFase); // <-- Añadir botón
        panelBotones.add(btnEliminarFase); // <-- Añadir botón
        panelBotones.add(btnGuardarPrograma);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatosIniciales() {
        // Carga las fases del programa original (o de la lista temporal si ya se modificó algo) en la tabla
        modeloTablaFases.setRowCount(0); // Limpiar tabla
        for (FaseMantenimiento fase : fasesTemporales) {
            Object[] fila = {
                fase.getMedidorFrecuencia() + " " + fase.getTipoFrecuencia(),
                fase.getTipoFrecuencia(),
                fase.getCantidadCiclos() == 0 ? "Recurrente" : fase.getCantidadCiclos(),
                fase.getHorasEstimadas(),
                fase.getIdsTareasMaestras().size() + " tareas" // Mostrar cantidad
            };
            modeloTablaFases.addRow(fila);
        }
    }

    private void configurarEventos() {
        btnAgregarFase.addActionListener(e -> {
            indiceFaseAErEditar = -1; // Indicar que no es edición
            abrirVentanaAgregarFase(); // Llama al método que crea el JDialog
        });

        btnEditarFase.addActionListener(e -> {
            int filaSeleccionada = tablaFases.getSelectedRow();
            if (filaSeleccionada >= 0) {
                indiceFaseAErEditar = filaSeleccionada; // Guardar índice de la fase a editar
                FaseMantenimiento fase = fasesTemporales.get(filaSeleccionada); // Obtener la fase de la lista temporal
                abrirVentanaAgregarFase(fase); // Pasa la fase para pre-cargarla
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una fase de la tabla para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnEliminarFase.addActionListener(e -> {
            int filaSeleccionada = tablaFases.getSelectedRow();
            if (filaSeleccionada >= 0) {
                int respuesta = JOptionPane.showConfirmDialog(
                        this,
                        "¿Está seguro de que desea eliminar la fase seleccionada?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (respuesta == JOptionPane.YES_OPTION) {
                    fasesTemporales.remove(filaSeleccionada); // Remover de la lista temporal
                    modeloTablaFases.removeRow(filaSeleccionada); // Remover de la tabla visual
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una fase de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnGuardarPrograma.addActionListener(e -> {
            if (fasesTemporales.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El programa debe tener al menos una fase.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Crear un nuevo programa con las fases editadas
            ProgramaMantenimientoPreventivo nuevoPrograma = new ProgramaMantenimientoPreventivo(equipoDelPrograma.getId());
            for (FaseMantenimiento fase : fasesTemporales) {
                nuevoPrograma.agregarFase(fase);
            }

            // *** LLAMAR AL SISTEMA PARA ACTUALIZAR EL PROGRAMA ***
            // El sistema debe tener un método que reemplace el programa existente para el equipo
            // Por ejemplo: sistema.actualizarPrograma(nuevoPrograma);
            // O si no existe, crearlo: sistema.guardarPrograma(nuevoPrograma); // Este método debe manejar la sobreescritura si ya existe
            // Suponiendo que guardarPrograma sobrescribe si ya existe:
            boolean guardado = sistema.guardarPrograma(nuevoPrograma); // <-- Usar el método existente

            if (guardado) {
                JOptionPane.showMessageDialog(this, "Programa de mantenimiento actualizado exitosamente para el equipo ID: " + equipoDelPrograma.getId() + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Opcional: Cerrar la ventana después de guardar
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el programa actualizado en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana sin guardar
    }

    private void abrirVentanaAgregarFase() {
        abrirVentanaAgregarFase(null); // Llama a la versión con parámetro null (nueva fase)
    }

    // Versión sobrecargada para editar una fase existente
    private void abrirVentanaAgregarFase(FaseMantenimiento faseAErEditar) {
        if (ventanaFase != null && ventanaFase.isVisible()) {
            // Si la ventana ya está abierta, la traemos al frente
            ventanaFase.toFront();
            return;
        }

        // --- CREAR EL JDIALOG ---
        ventanaFase = new JDialog(this, faseAErEditar != null ? "Editar Fase" : "Agregar Nueva Fase", true); // 'true' para modalidad.
        ventanaFase.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaFase.setLayout(new BorderLayout());

        // --- Panel Central: Formulario y Tareas ---
        JSplitPane splitFase = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitFase.setResizeWeight(0.5); // División 50-50

        // --- Panel Izquierdo: Formulario de Fase (CORREGIDO) ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: Frecuencia, Tipo, Ciclos (en una sola fila)
        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(new JLabel("Frecuencia:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFrecuenciaFase = new JTextField(3); // Reducido a 3 caracteres
        txtFrecuenciaFase.setText("1");
        panelForm.add(txtFrecuenciaFase, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        cmbTipoFrecuenciaFase = new JComboBox<>(TipoFrecuencia.values());
        panelForm.add(cmbTipoFrecuenciaFase, gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelForm.add(new JLabel("Ciclos (0=Recurrente):"), gbc);

        gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE;
        spnCiclosFase = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        panelForm.add(spnCiclosFase, gbc);

        // Fila 1: Partes/Repuestos
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 5; gbc.fill = GridBagConstraints.BOTH;
        panelForm.add(new JLabel("Partes/Repuestos:"), gbc);

        gbc.gridy = 2;
        txtPartesFase = new JTextArea(2, 20);
        JScrollPane scrollPartes = new JScrollPane(txtPartesFase);
        panelForm.add(scrollPartes, gbc);

        // Fila 3: Herramientas
        gbc.gridy = 3;
        panelForm.add(new JLabel("Herramientas:"), gbc);

        gbc.gridy = 4;
        txtHerramientasFase = new JTextArea(2, 20);
        JScrollPane scrollHerramientas = new JScrollPane(txtHerramientasFase);
        panelForm.add(scrollHerramientas, gbc);

        // Fila 5: Personal Requerido
        gbc.gridy = 5;
        panelForm.add(new JLabel("Personal Requerido:"), gbc);

        gbc.gridy = 6;
        txtPersonalFase = new JTextArea(2, 20);
        JScrollPane scrollPersonal = new JScrollPane(txtPersonalFase);
        panelForm.add(scrollPersonal, gbc);

        // Fila 7: Horas Estimadas
        gbc.gridy = 7;
        panelForm.add(new JLabel("Horas Estimadas:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.NONE;
        spnHorasFase = new JSpinner(new SpinnerNumberModel(1.0f, 0.1f, 1000.0f, 0.5f));
        panelForm.add(spnHorasFase, gbc);

        // --- Panel Derecho: Selección de Tareas ---
        JPanel panelTareas = new JPanel(new BorderLayout());
        panelTareas.setBorder(BorderFactory.createTitledBorder("Tareas de la Fase"));

        // Inicializar modelos y listas de tareas
        modeloTareasDisp = new DefaultListModel<>();
        modeloTareasSel = new DefaultListModel<>();
        List<TareaMantenimiento> todasLasTareas = sistema.obtenerTodasLasTareasMaestras();
        for (TareaMantenimiento tarea : todasLasTareas) {
            modeloTareasDisp.addElement(tarea);
        }
        listaTareasDisponibles = new JList<>(modeloTareasDisp);
        JScrollPane scrollDisp = new JScrollPane(listaTareasDisponibles);

        listaTareasSeleccionadas = new JList<>(modeloTareasSel);
        JScrollPane scrollSel = new JScrollPane(listaTareasSeleccionadas);

        // Botones para mover tareas
        btnAgregarTarea = new JButton(">>");
        btnQuitarTarea = new JButton("<<");
        JPanel panelBotonesTareas = new JPanel(new GridLayout(2, 1));
        panelBotonesTareas.add(btnAgregarTarea);
        panelBotonesTareas.add(btnQuitarTarea);

        // Layout para alinear botones entre listas
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

        // --- Cargar datos si es edición ---
        if (faseAErEditar != null) {
            txtFrecuenciaFase.setText(String.valueOf(faseAErEditar.getMedidorFrecuencia()));
            cmbTipoFrecuenciaFase.setSelectedItem(faseAErEditar.getTipoFrecuencia());
            spnCiclosFase.setValue(faseAErEditar.getCantidadCiclos());
            txtPartesFase.setText(faseAErEditar.getPartes() != null ? faseAErEditar.getPartes() : "");
            txtHerramientasFase.setText(faseAErEditar.getHerramientas() != null ? faseAErEditar.getHerramientas() : "");
            txtPersonalFase.setText(faseAErEditar.getPersonal() != null ? faseAErEditar.getPersonal() : "");
            spnHorasFase.setValue(faseAErEditar.getHorasEstimadas());

            // Cargar tareas seleccionadas en la lista derecha
            for (int idTarea : faseAErEditar.getIdsTareasMaestras()) {
                TareaMantenimiento tarea = sistema.buscarTareaMaestraPorId(idTarea);
                if (tarea != null) {
                    modeloTareasSel.addElement(tarea);
                }
            }
            // Quitar de la lista izquierda las tareas ya seleccionadas
            for (int i = modeloTareasDisp.getSize() - 1; i >= 0; i--) {
                TareaMantenimiento tareaDisp = modeloTareasDisp.getElementAt(i);
                if (faseAErEditar.getIdsTareasMaestras().contains(tareaDisp.getId())) {
                    modeloTareasDisp.remove(i);
                }
            }
        }

        // --- Eventos Ventana Fase ---
        btnAgregarTarea.addActionListener(e -> {
            int[] indices = listaTareasDisponibles.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) { // Iterar backwards para no alterar índices al remover
                TareaMantenimiento tarea = modeloTareasDisp.getElementAt(indices[i]);
                if (!modeloTareasSel.contains(tarea)) { // Prevenir duplicados
                    modeloTareasSel.addElement(tarea);
                    modeloTareasDisp.remove(indices[i]); // Remover de la lista de disponibles
                }
            }
            listaTareasDisponibles.clearSelection();
        });

        btnQuitarTarea.addActionListener(e -> {
            int[] indices = listaTareasSeleccionadas.getSelectedIndices();
            for (int i = indices.length - 1; i >= 0; i--) {
                TareaMantenimiento tarea = modeloTareasSel.getElementAt(indices[i]);
                modeloTareasSel.remove(indices[i]); // Remover de la lista de seleccionadas
                modeloTareasDisp.addElement(tarea); // Añadir de vuelta a la lista de disponibles
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
                float horas = ((Number) spnHorasFase.getValue()).floatValue(); // Casting seguro

                // Crear la nueva fase o actualizar la existente
                FaseMantenimiento nuevaFase = new FaseMantenimiento(tipoFreq, frecuencia, ciclos, partes, herramientas, personal, horas);

                // --- AÑADIR LAS TAREAS SELECCIONADAS A LA FASE ---
                for (int i = 0; i < modeloTareasSel.getSize(); i++) {
                    TareaMantenimiento tarea = modeloTareasSel.getElementAt(i);
                    nuevaFase.agregarTareaMaestra(tarea.getId()); // Añadir ID a la fase
                }
                // --- FIN AÑADIR TAREAS ---

                if (indiceFaseAErEditar != -1) {
                    // ESTAMOS EDITANDO: Reemplazar la fase en la lista temporal
                    fasesTemporales.set(indiceFaseAErEditar, nuevaFase);
                    // Actualizar la fila en la tabla principal
                    modeloTablaFases.setValueAt(frecuencia + " " + tipoFreq, indiceFaseAErEditar, 0);
                    modeloTablaFases.setValueAt(tipoFreq, indiceFaseAErEditar, 1);
                    modeloTablaFases.setValueAt(ciclos == 0 ? "Recurrente" : ciclos, indiceFaseAErEditar, 2);
                    modeloTablaFases.setValueAt(horas, indiceFaseAErEditar, 3);
                    modeloTablaFases.setValueAt(modeloTareasSel.getSize() + " tareas", indiceFaseAErEditar, 4);
                    System.out.println("DEBUG: Fase en índice " + indiceFaseAErEditar + " actualizada en la lista temporal y en la tabla.");
                } else {
                    // ESTAMOS AGREGANDO: Añadir la nueva fase a la lista temporal
                    fasesTemporales.add(nuevaFase);
                    // Añadir la fila a la tabla principal
                    Object[] filaTabla = {
                        frecuencia + " " + tipoFreq,
                        tipoFreq,
                        ciclos == 0 ? "Recurrente" : ciclos,
                        horas,
                        modeloTareasSel.getSize() + " tareas" // Mostrar cantidad
                    };
                    modeloTablaFases.addRow(filaTabla);
                    System.out.println("DEBUG: Nueva fase añadida a la lista temporal y a la tabla.");
                }

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