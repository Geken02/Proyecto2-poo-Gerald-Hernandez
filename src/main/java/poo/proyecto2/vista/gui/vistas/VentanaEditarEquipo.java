package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.EstadoEquipo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VentanaEditarEquipo extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Opcional: para refrescar datos si es necesario
    private NodoEquipo equipoAErEditar; // Referencia al equipo que se va a editar

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo;
    private JLabel lblInfoEquipo; // Muestra ID y Descripción del equipo a editar
    private JLabel lblDescripcion;
    private JTextField txtDescripcion;
    private JLabel lblTipo;
    private JTextField txtTipo;
    private JLabel lblUbicacion;
    private JTextField txtUbicacion;
    private JLabel lblFabricante;
    private JTextField txtFabricante;
    private JLabel lblSerie;
    private JTextField txtSerie;
    private JLabel lblFechaAdquisicion;
    private JFormattedTextField txtFechaAdquisicion;
    private JLabel lblFechaPuestaEnServicio;
    private JFormattedTextField txtFechaPuestaEnServicio;
    private JLabel lblMesesVidaUtil;
    private JFormattedTextField txtMesesVidaUtil;
    private JLabel lblEstado;
    private JComboBox<EstadoEquipo> cmbEstado;
    private JLabel lblCostoInicial;
    private JFormattedTextField txtCostoInicial;
    private JLabel lblEquipoPrincipal;
    private JTextField txtEquipoPrincipal; // Campo para mostrar/posiblemente editar el ID del equipo padre
    private JLabel lblEspecificacionesTecnicas;
    private JTextArea txtEspecificacionesTecnicas;
    private JScrollPane scrollEspecTecnicas;
    private JLabel lblInformacionGarantia;
    private JTextArea txtInformacionGarantia;
    private JScrollPane scrollInfoGarantia;

    private JButton btnGuardar;
    private JButton btnCancelar;

    public VentanaEditarEquipo(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre, NodoEquipo equipoAErEditar) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        this.equipoAErEditar = equipoAErEditar;
        inicializarComponentes();
        cargarDatosEnFormulario(); // Carga los datos del equipo a editar
        configurarEventos();
        setTitle("Editar Equipo ID: " + equipoAErEditar.getId());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(700, 700); // Tamaño más grande para acomodar todos los campos
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Editar Equipo", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Formulario ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: ID y Info Equipo
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        lblIdEquipo = new JLabel("ID del Equipo:"); // No editable
        txtIdEquipo = new JTextField(String.valueOf(equipoAErEditar.getId()));
        txtIdEquipo.setEditable(false); // Solo lectura
        txtIdEquipo.setBackground(new Color(240, 240, 240)); // Color gris claro para campos no editables
        lblInfoEquipo = new JLabel(" (Equipo seleccionado para editar)"); // Etiqueta descriptiva
        lblInfoEquipo.setFont(lblInfoEquipo.getFont().deriveFont(Font.ITALIC));

        JPanel panelId = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelId.add(lblIdEquipo);
        panelId.add(txtIdEquipo);
        panelId.add(lblInfoEquipo);

        panelFormulario.add(panelId, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 1: Descripción
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        lblDescripcion = new JLabel("Descripción *:");
        panelFormulario.add(lblDescripcion, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtDescripcion = new JTextField(30);
        panelFormulario.add(txtDescripcion, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 2: Tipo
        gbc.gridx = 0; gbc.gridy = 2;
        lblTipo = new JLabel("Tipo *:");
        panelFormulario.add(lblTipo, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtTipo = new JTextField(30);
        panelFormulario.add(txtTipo, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 3: Ubicación
        gbc.gridx = 0; gbc.gridy = 3;
        lblUbicacion = new JLabel("Ubicación *:");
        panelFormulario.add(lblUbicacion, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtUbicacion = new JTextField(30);
        panelFormulario.add(txtUbicacion, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 4: Fabricante
        gbc.gridx = 0; gbc.gridy = 4;
        lblFabricante = new JLabel("Fabricante *:");
        panelFormulario.add(lblFabricante, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFabricante = new JTextField(30);
        panelFormulario.add(txtFabricante, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 5: Serie
        gbc.gridx = 0; gbc.gridy = 5;
        lblSerie = new JLabel("Serie *:");
        panelFormulario.add(lblSerie, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtSerie = new JTextField(30);
        panelFormulario.add(txtSerie, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 6: Fecha Adquisición
        gbc.gridx = 0; gbc.gridy = 6;
        lblFechaAdquisicion = new JLabel("Fecha Adquisición (AAAA-MM-DD) *:");
        panelFormulario.add(lblFechaAdquisicion, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFechaAdquisicion = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        panelFormulario.add(txtFechaAdquisicion, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 7: Fecha Puesta en Servicio
        gbc.gridx = 0; gbc.gridy = 7;
        lblFechaPuestaEnServicio = new JLabel("Fecha Puesta en Servicio (AAAA-MM-DD) *:");
        panelFormulario.add(lblFechaPuestaEnServicio, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFechaPuestaEnServicio = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        panelFormulario.add(txtFechaPuestaEnServicio, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 8: Meses Vida Útil
        gbc.gridx = 0; gbc.gridy = 8;
        lblMesesVidaUtil = new JLabel("Meses Vida Útil *:");
        panelFormulario.add(lblMesesVidaUtil, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        NumberFormatter formatterMeses = new NumberFormatter();
        formatterMeses.setValueClass(Integer.class);
        formatterMeses.setMinimum(1);
        txtMesesVidaUtil = new JFormattedTextField(formatterMeses);
        panelFormulario.add(txtMesesVidaUtil, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 9: Estado
        gbc.gridx = 0; gbc.gridy = 9;
        lblEstado = new JLabel("Estado *:");
        panelFormulario.add(lblEstado, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        cmbEstado = new JComboBox<>(EstadoEquipo.values());
        panelFormulario.add(cmbEstado, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 10: Costo Inicial
        gbc.gridx = 0; gbc.gridy = 10;
        lblCostoInicial = new JLabel("Costo Inicial *:");
        panelFormulario.add(lblCostoInicial, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        NumberFormatter formatterCosto = new NumberFormatter(new java.text.DecimalFormat("#,##0.00"));
        formatterCosto.setValueClass(Double.class);
        formatterCosto.setMinimum(0.01);
        txtCostoInicial = new JFormattedTextField(formatterCosto);
        panelFormulario.add(txtCostoInicial, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 11: Equipo Principal (ID) - Lectura (no editable en esta ventana)
        gbc.gridx = 0; gbc.gridy = 11;
        lblEquipoPrincipal = new JLabel("Equipo Principal (ID):");
        panelFormulario.add(lblEquipoPrincipal, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtEquipoPrincipal = new JTextField(String.valueOf(equipoAErEditar.getEquipoPrincipal()));
        txtEquipoPrincipal.setEditable(false); // Campo de solo lectura
        txtEquipoPrincipal.setBackground(new Color(240, 240, 240));
        panelFormulario.add(txtEquipoPrincipal, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 12: Especificaciones Técnicas (área de texto)
        gbc.gridx = 0; gbc.gridy = 12; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;
        lblEspecificacionesTecnicas = new JLabel("Especificaciones Técnicas:");
        panelFormulario.add(lblEspecificacionesTecnicas, gbc);

        gbc.gridy = 13;
        txtEspecificacionesTecnicas = new JTextArea(5, 30);
        scrollEspecTecnicas = new JScrollPane(txtEspecificacionesTecnicas);
        panelFormulario.add(scrollEspecTecnicas, gbc);

        // Fila 14: Información de Garantía (área de texto)
        gbc.gridy = 14;
        lblInformacionGarantia = new JLabel("Información de Garantía:");
        panelFormulario.add(lblInformacionGarantia, gbc);

        gbc.gridy = 15;
        txtInformacionGarantia = new JTextArea(5, 30);
        scrollInfoGarantia = new JScrollPane(txtInformacionGarantia);
        panelFormulario.add(scrollInfoGarantia, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar Cambios");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatosEnFormulario() {
        // Carga los datos del objeto equipoAErEditar en los campos del formulario
        txtDescripcion.setText(equipoAErEditar.getDescripcion());
        txtTipo.setText(equipoAErEditar.getTipo());
        txtUbicacion.setText(equipoAErEditar.getUbicacion());
        txtFabricante.setText(equipoAErEditar.getFabricante());
        txtSerie.setText(equipoAErEditar.getSerie());

        if (equipoAErEditar.getFechaAdquisicion() != null) {
            txtFechaAdquisicion.setValue(equipoAErEditar.getFechaAdquisicion());
        } else {
            txtFechaAdquisicion.setValue(null);
        }

        if (equipoAErEditar.getFechaPuestaEnServicio() != null) {
            txtFechaPuestaEnServicio.setValue(equipoAErEditar.getFechaPuestaEnServicio());
        } else {
            txtFechaPuestaEnServicio.setValue(null);
        }

        txtMesesVidaUtil.setValue(equipoAErEditar.getMesesVidaUtil());
        cmbEstado.setSelectedItem(equipoAErEditar.getEstado());
        txtCostoInicial.setValue(equipoAErEditar.getCostoInicial());
        // txtEquipoPrincipal.setValue(equipoAErEditar.getEquipoPrincipal()); // Ya se inicializó en el constructor del campo
        txtEspecificacionesTecnicas.setText(equipoAErEditar.getEspecificacionesTecnicas() != null ? equipoAErEditar.getEspecificacionesTecnicas() : "");
        txtInformacionGarantia.setText(equipoAErEditar.getInformacionGarantia() != null ? equipoAErEditar.getInformacionGarantia() : "");
    }

    private void configurarEventos() {
        btnGuardar.addActionListener(e -> guardarCambios());

        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana sin guardar
    }

    private void guardarCambios() {
        // 1. Validar campos requeridos (marcados con *)
        if (txtDescripcion.getText().trim().isEmpty() ||
            txtTipo.getText().trim().isEmpty() ||
            txtUbicacion.getText().trim().isEmpty() ||
            txtFabricante.getText().trim().isEmpty() ||
            txtSerie.getText().trim().isEmpty() ||
            txtFechaAdquisicion.getText().trim().isEmpty() ||
            txtFechaPuestaEnServicio.getText().trim().isEmpty() ||
            txtMesesVidaUtil.getText().trim().isEmpty() ||
            txtCostoInicial.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios (marcados con *).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Obtener datos del formulario
        String descripcion = txtDescripcion.getText().trim();
        String tipo = txtTipo.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();
        String fabricante = txtFabricante.getText().trim();
        String serie = txtSerie.getText().trim();

        LocalDate fechaAdq, fechaPuestaEnServ;
        try {
            fechaAdq = LocalDate.parse(txtFechaAdquisicion.getText().trim());
            fechaPuestaEnServ = LocalDate.parse(txtFechaPuestaEnServicio.getText().trim());
            if (fechaPuestaEnServ.isBefore(fechaAdq)) {
                JOptionPane.showMessageDialog(this, "La fecha de puesta en servicio debe ser igual o posterior a la fecha de adquisición.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese fechas válidas en formato AAAA-MM-DD.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int mesesVidaUtil;
        try {
            mesesVidaUtil = ((Number) txtMesesVidaUtil.getValue()).intValue();
            if (mesesVidaUtil <= 0) {
                JOptionPane.showMessageDialog(this, "Los meses de vida útil deben ser un número positivo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido para meses de vida útil.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EstadoEquipo estado = (EstadoEquipo) cmbEstado.getSelectedItem();

        double costoInicial;
        try {
            costoInicial = ((Number) txtCostoInicial.getValue()).doubleValue();
            if (costoInicial <= 0) {
                JOptionPane.showMessageDialog(this, "El costo inicial debe ser un número positivo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido para el costo inicial.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String especTecnicas = txtEspecificacionesTecnicas.getText().trim();
        String infoGarantia = txtInformacionGarantia.getText().trim();

        // 3. Actualizar los datos del objeto equipoAErEditar en memoria
        equipoAErEditar.setDescripcion(descripcion);
        equipoAErEditar.setTipo(tipo);
        equipoAErEditar.setUbicacion(ubicacion);
        equipoAErEditar.setFabricante(fabricante);
        equipoAErEditar.setSerie(serie);
        equipoAErEditar.setFechaAdquisicion(fechaAdq);
        equipoAErEditar.setFechaPuestaEnServicio(fechaPuestaEnServ);
        equipoAErEditar.setMesesVidaUtil(mesesVidaUtil);
        equipoAErEditar.setEstado(estado);
        equipoAErEditar.setCostoInicial(costoInicial);
        equipoAErEditar.setEspecificacionesTecnicas(especTecnicas.isEmpty() ? null : especTecnicas);
        equipoAErEditar.setInformacionGarantia(infoGarantia.isEmpty() ? null : infoGarantia);

        // 4. Llamar al sistema para guardar los cambios en disco
        // Usamos el metodo actualizarEquipo del SistemaPrincipal, pasando el ID del equipo actual
        boolean actualizado = sistema.actualizarEquipo(equipoAErEditar.getId(), descripcion, tipo, ubicacion,
                fabricante, serie, fechaAdq, fechaPuestaEnServ, mesesVidaUtil,
                estado, costoInicial, especTecnicas.isEmpty() ? null : especTecnicas, infoGarantia.isEmpty() ? null : infoGarantia);

        if (actualizado) {
            JOptionPane.showMessageDialog(this, "Equipo ID " + equipoAErEditar.getId() + " actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            if (ventanaPadre != null) {
                ventanaPadre.actualizarVistaArbol();
            }
            dispose();
        } else {
            // Este caso es raro si el equipoAErEditar fue encontrado previamente
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el equipo. Puede que ya no exista.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}