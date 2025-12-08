package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.EstadoEquipo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorEditarEquipo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VentanaEditarEquipo extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; 
    private NodoEquipo equipoAErEditar; 

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo; 
    private JLabel lblInfoEquipo; 
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
    private JTextField txtFechaAdquisicion;
    private JLabel lblFechaPuestaEnServicio;
    private JTextField txtFechaPuestaEnServicio;
    private JLabel lblMesesVidaUtil;
    private JTextField txtMesesVidaUtil; 
    private JLabel lblEstado;
    private JComboBox<EstadoEquipo> cmbEstado;
    private JLabel lblCostoInicial;
    private JTextField txtCostoInicial; 
    private JLabel lblEquipoPrincipal; 
    private JTextField txtEquipoPrincipal;
    private JLabel lblEspecificacionesTecnicas;
    private JTextArea txtEspecificacionesTecnicas;
    private JScrollPane scrollEspecTecnicas;
    private JLabel lblInformacionGarantia;
    private JTextArea txtInformacionGarantia;
    private JScrollPane scrollInfoGarantia;

    private JButton btnGuardar;
    private JButton btnCancelar;

    // Referencia al controlador
    private ControladorEditarEquipo controlador;

    public VentanaEditarEquipo(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre, NodoEquipo equipoAErEditar) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        this.equipoAErEditar = equipoAErEditar;
        // Crear el controlador pasando el modelo, la vista y el equipo a editar
        this.controlador = new ControladorEditarEquipo(sistema, this, equipoAErEditar);
        inicializarComponentes();
        cargarDatosEnFormulario(); 
        configurarEventos();
        setTitle("Editar Equipo ID: " + equipoAErEditar.getId());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false); 
        pack(); 
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

        // Fila 0: ID y Info Equipo (solo lectura)
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
        txtFechaAdquisicion = new JTextField(10); 
        panelFormulario.add(txtFechaAdquisicion, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 7: Fecha Puesta en Servicio
        gbc.gridx = 0; gbc.gridy = 7;
        lblFechaPuestaEnServicio = new JLabel("Fecha Puesta en Servicio (AAAA-MM-DD) *:");
        panelFormulario.add(lblFechaPuestaEnServicio, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFechaPuestaEnServicio = new JTextField(10); 
        panelFormulario.add(txtFechaPuestaEnServicio, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 8: Meses Vida Útil
        gbc.gridx = 0; gbc.gridy = 8;
        lblMesesVidaUtil = new JLabel("Meses Vida Útil *:");
        panelFormulario.add(lblMesesVidaUtil, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtMesesVidaUtil = new JTextField(10); // <-- Cambiado a JTextField
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
        txtCostoInicial = new JTextField(15);
        panelFormulario.add(txtCostoInicial, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        // Fila 11: Equipo Principal (ID) - Lectura (no editable en esta ventana)
        gbc.gridx = 0; gbc.gridy = 11;
        lblEquipoPrincipal = new JLabel("Equipo Principal (ID):");
        panelFormulario.add(lblEquipoPrincipal, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtEquipoPrincipal = new JTextField(String.valueOf(equipoAErEditar.getEquipoPrincipal()));
        txtEquipoPrincipal.setEditable(false);
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
            txtFechaAdquisicion.setText(equipoAErEditar.getFechaAdquisicion().format(DateTimeFormatter.ISO_LOCAL_DATE)); // <-- setText en JTextField
        } else {
            txtFechaAdquisicion.setText(""); 
        }

        if (equipoAErEditar.getFechaPuestaEnServicio() != null) {
            txtFechaPuestaEnServicio.setText(equipoAErEditar.getFechaPuestaEnServicio().format(DateTimeFormatter.ISO_LOCAL_DATE)); // <-- setText en JTextField
        } else {
            txtFechaPuestaEnServicio.setText("");
        }

        txtMesesVidaUtil.setText(String.valueOf(equipoAErEditar.getMesesVidaUtil()));
        cmbEstado.setSelectedItem(equipoAErEditar.getEstado());
        txtCostoInicial.setText(String.valueOf(equipoAErEditar.getCostoInicial()));
        txtEspecificacionesTecnicas.setText(equipoAErEditar.getEspecificacionesTecnicas() != null ? equipoAErEditar.getEspecificacionesTecnicas() : "");
        txtInformacionGarantia.setText(equipoAErEditar.getInformacionGarantia() != null ? equipoAErEditar.getInformacionGarantia() : "");
    }

    private void configurarEventos() {
        btnGuardar.addActionListener(e -> controlador.guardarCambios(ventanaPadre));
        btnCancelar.addActionListener(e -> controlador.cancelar()); 
    }

    // --- Métodos para que el controlador interactúe con la vista ---
    public JTextField getTxtDescripcion() { return txtDescripcion; }
    public JTextField getTxtTipo() { return txtTipo; }
    public JTextField getTxtUbicacion() { return txtUbicacion; }
    public JTextField getTxtFabricante() { return txtFabricante; }
    public JTextField getTxtSerie() { return txtSerie; }
    public JTextField getTxtFechaAdquisicion() { return txtFechaAdquisicion; }
    public JTextField getTxtFechaPuestaEnServicio() { return txtFechaPuestaEnServicio; }
    public JTextField getTxtMesesVidaUtil() { return txtMesesVidaUtil; }
    public JComboBox<EstadoEquipo> getCmbEstado() { return cmbEstado; }
    public JTextField getTxtCostoInicial() { return txtCostoInicial; }
    public JTextArea getTxtEspecificacionesTecnicas() { return txtEspecificacionesTecnicas; }
    public JTextArea getTxtInformacionGarantia() { return txtInformacionGarantia; }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) { JOptionPane.showMessageDialog(this, mensaje, titulo, tipo); }
    public void cerrarVentana() { this.dispose(); }
    // ---
}