package poo.proyecto2.vista.gui;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.EstadoEquipo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorRegistrarEquipo;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VentanaRegistrarEquipo extends JFrame {

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblEquipoPrincipal; 
    private JTextField txtEquipoPrincipal;
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
    private JLabel lblEspecTecnicas;
    private JTextArea txtEspecTecnicas;
    private JScrollPane scrollEspecTecnicas;
    private JLabel lblInfoGarantia;
    private JTextArea txtInfoGarantia;
    private JScrollPane scrollInfoGarantia;
    private JButton btnRegistrar;
    private JButton btnCancelar;

    // Referencia al controlador
    private ControladorRegistrarEquipo controlador;

    public VentanaRegistrarEquipo(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        // Crear el controlador pasándole el sistema y la vista
        this.controlador = new ControladorRegistrarEquipo(sistema, this);

        inicializarComponentes();
        configurarEventos();
        setTitle("Registrar Nuevo Equipo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack(); // Ajusta al contenido
        setLocationRelativeTo(ventanaPadre); // Centrado en la ventana padre
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Registrar Nuevo Equipo", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Formulario ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        lblEquipoPrincipal = new JLabel("Equipo Principal (0 si es raíz) *:");
        panelFormulario.add(lblEquipoPrincipal, gbc);

        gbc.gridx = 1;
        txtEquipoPrincipal = new JTextField(10); 
        txtEquipoPrincipal.setText("0"); 
        panelFormulario.add(txtEquipoPrincipal, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        lblDescripcion = new JLabel("Descripción *:");
        panelFormulario.add(lblDescripcion, gbc);

        gbc.gridx = 1;
        txtDescripcion = new JTextField(30);
        panelFormulario.add(txtDescripcion, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        lblTipo = new JLabel("Tipo *:");
        panelFormulario.add(lblTipo, gbc);

        gbc.gridx = 1;
        txtTipo = new JTextField(30);
        panelFormulario.add(txtTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        lblUbicacion = new JLabel("Ubicación *:");
        panelFormulario.add(lblUbicacion, gbc);

        gbc.gridx = 1;
        txtUbicacion = new JTextField(30);
        panelFormulario.add(txtUbicacion, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        lblFabricante = new JLabel("Fabricante *:");
        panelFormulario.add(lblFabricante, gbc);

        gbc.gridx = 1;
        txtFabricante = new JTextField(30);
        panelFormulario.add(txtFabricante, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        lblSerie = new JLabel("Serie *:");
        panelFormulario.add(lblSerie, gbc);

        gbc.gridx = 1;
        txtSerie = new JTextField(30);
        panelFormulario.add(txtSerie, gbc);

        

        // --- Fila 6: Fecha Adquisición (cambiado a JTextField) ---
        gbc.gridx = 0; gbc.gridy = 6;
        lblFechaAdquisicion = new JLabel("Fecha Adquisición (AAAA-MM-DD) *:");
        panelFormulario.add(lblFechaAdquisicion, gbc);

        gbc.gridx = 1;
        txtFechaAdquisicion = new JTextField(10); // <-- Cambiado de JFormattedTextField a JTextField
        txtFechaAdquisicion.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // Valor por defecto
        panelFormulario.add(txtFechaAdquisicion, gbc);

        // --- Fila 7: Fecha Puesta en Servicio (cambiado a JTextField) ---
        gbc.gridx = 0; gbc.gridy = 7;
        lblFechaPuestaEnServicio = new JLabel("Fecha Puesta en Servicio (AAAA-MM-DD) *:");
        panelFormulario.add(lblFechaPuestaEnServicio, gbc);

        gbc.gridx = 1;
        txtFechaPuestaEnServicio = new JTextField(10); // <-- Cambiado de JFormattedTextField a JTextField
        txtFechaPuestaEnServicio.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // Valor por defecto
        panelFormulario.add(txtFechaPuestaEnServicio, gbc);

        // --- Fila 8: Meses Vida Útil (cambiado a JTextField) ---
        gbc.gridx = 0; gbc.gridy = 8;
        lblMesesVidaUtil = new JLabel("Meses Vida Útil *:");
        panelFormulario.add(lblMesesVidaUtil, gbc);

        gbc.gridx = 1;
        txtMesesVidaUtil = new JTextField(10); // <-- Cambiado de JFormattedTextField a JTextField
        txtMesesVidaUtil.setText("60"); // Valor por defecto
        panelFormulario.add(txtMesesVidaUtil, gbc);

        gbc.gridx = 0; gbc.gridy = 9;
        lblEstado = new JLabel("Estado *:");
        panelFormulario.add(lblEstado, gbc);

        gbc.gridx = 1;
        // --- CORRECCIÓN: Crear una nueva instancia de JComboBox ---
        cmbEstado = new JComboBox<>(EstadoEquipo.values()); // <-- Esta línea debe estar aquí
        cmbEstado.setSelectedItem(EstadoEquipo.FUNCIONANDO); // Valor por defecto
        panelFormulario.add(cmbEstado, gbc);

        // --- Fila 10: Costo Inicial (cambiado a JTextField) ---
        gbc.gridx = 0; gbc.gridy = 10;
        lblCostoInicial = new JLabel("Costo Inicial *:");
        panelFormulario.add(lblCostoInicial, gbc);

        gbc.gridx = 1;
        txtCostoInicial = new JTextField(10); 
        txtCostoInicial.setText("0.00"); 
        panelFormulario.add(txtCostoInicial, gbc);

        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        lblEspecTecnicas = new JLabel("Especificaciones Técnicas:");
        panelFormulario.add(lblEspecTecnicas, gbc);

        gbc.gridy = 12;
        txtEspecTecnicas = new JTextArea(3, 30);
        scrollEspecTecnicas = new JScrollPane(txtEspecTecnicas);
        panelFormulario.add(scrollEspecTecnicas, gbc);

        gbc.gridy = 13;
        lblInfoGarantia = new JLabel("Información de Garantía:");
        panelFormulario.add(lblInfoGarantia, gbc);

        gbc.gridy = 14;
        txtInfoGarantia = new JTextArea(3, 30);
        scrollInfoGarantia = new JScrollPane(txtInfoGarantia);
        panelFormulario.add(scrollInfoGarantia, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        // --- Eventos ---
        btnRegistrar.addActionListener(e -> controlador.registrarEquipoDesdeVista()); 
        btnCancelar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    // --- Métodos para que el controlador acceda a los componentes de la vista ---
    // Para campos de texto simples (JTextField, JTextArea)
    public JTextField getTxtEquipoPrincipal() { return txtEquipoPrincipal; }
    public JTextField getTxtDescripcion() { return txtDescripcion; }
    public JTextField getTxtTipo() { return txtTipo; }
    public JTextField getTxtUbicacion() { return txtUbicacion; }
    public JTextField getTxtFabricante() { return txtFabricante; }
    public JTextField getTxtSerie() { return txtSerie; }
    public JTextField getTxtFechaAdquisicion() { return txtFechaAdquisicion; }
    public JTextField getTxtFechaPuestaEnServicio() { return txtFechaPuestaEnServicio; }
    public JTextField getTxtMesesVidaUtil() { return txtMesesVidaUtil; }
    public JTextField getTxtCostoInicial() { return txtCostoInicial; }
    // Para combos (JComboBox)
    public JComboBox<EstadoEquipo> getCmbEstado() { return cmbEstado; }
    // Para áreas de texto (JTextArea)
    public JTextArea getTxtEspecTecnicas() { return txtEspecTecnicas; }
    public JTextArea getTxtInfoGarantia() { return txtInfoGarantia; }

    // --- Métodos para que el controlador actualice la vista ---
    public void mostrarMensaje(String mensaje, String titulo, int tipo) { JOptionPane.showMessageDialog(this, mensaje, titulo, tipo); }
    public void limpiarFormulario() {
        // Limpiar campos de texto
        getTxtEquipoPrincipal().setText("0"); // Campo de texto para ID del equipo padre, valor por defecto 0
        getTxtDescripcion().setText("");
        getTxtTipo().setText("");
        getTxtUbicacion().setText("");
        getTxtFabricante().setText("");
        getTxtSerie().setText("");

        getTxtFechaAdquisicion().setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); 
        getTxtFechaPuestaEnServicio().setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); 

        getTxtMesesVidaUtil().setText("60"); 
        getTxtCostoInicial().setText("0.00"); 

        // Limpiar combos
        getCmbEstado().setSelectedItem(EstadoEquipo.FUNCIONANDO);

        // Limpiar áreas de texto
        getTxtEspecTecnicas().setText("");
        getTxtInfoGarantia().setText("");
    }
    public void cerrarVentana() { this.dispose(); }
}