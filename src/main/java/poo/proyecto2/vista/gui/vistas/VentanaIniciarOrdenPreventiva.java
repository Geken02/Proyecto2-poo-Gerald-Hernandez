package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.mantenimiento.OrdenTrabajoPreventiva;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorIniciarOrdenPreventiva;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VentanaIniciarOrdenPreventiva extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; 

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdOrden;
    private JTextField txtIdOrden;
    private JButton btnBuscarOrden;
    private JLabel lblInfoOrden; 
    private JLabel lblFechaInicio;
    private JFormattedTextField txtFechaInicio; 
    private JButton btnIniciarOrden;
    private JButton btnCancelar;

    // Referencia al controlador
    private ControladorIniciarOrdenPreventiva controlador;

    public VentanaIniciarOrdenPreventiva(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        // Crear el controlador pasando el sistema y la vista
        this.controlador = new ControladorIniciarOrdenPreventiva(sistema, this);

        inicializarComponentes();
        configurarEventos();
        setTitle("Iniciar Orden de Trabajo Preventiva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(600, 200); 
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
        txtIdOrden = new JTextField(15);
        txtIdOrden.setMinimumSize(new Dimension(100, 25));
        txtIdOrden.setPreferredSize(new Dimension(150, 25));
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
        txtFechaInicio.setValue(LocalDate.now());
        txtFechaInicio.setEnabled(false); // Deshabilitado inicialmente
        panelFormulario.add(txtFechaInicio, gbc);
        gbc.gridwidth = 1; // Resetear gridwidth

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnIniciarOrden = new JButton("Iniciar Orden");
        btnCancelar = new JButton("Cancelar");
        btnIniciarOrden.setEnabled(false); 
        panelBotones.add(btnIniciarOrden);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnBuscarOrden.addActionListener(e -> controlador.buscarOrden()); 
        btnIniciarOrden.addActionListener(e -> controlador.iniciarOrden()); 
        btnCancelar.addActionListener(e -> controlador.cancelar()); 
    }

    // --- Métodos para que el controlador interactúe con la vista ---
    public JTextField getTxtIdOrden() { return txtIdOrden; }
    public JFormattedTextField getTxtFechaInicio() { return txtFechaInicio; }
    public void mostrarInfoOrden(String texto) { lblInfoOrden.setText(texto); }
    public void mostrarMensaje(String mensaje, String titulo, int tipo) { JOptionPane.showMessageDialog(this, mensaje, titulo, tipo); }
    public void habilitarCamposIniciar(boolean habilitar) {
        txtFechaInicio.setEnabled(habilitar);
        btnIniciarOrden.setEnabled(habilitar);
    }
    public void limpiarFormulario() {
        txtIdOrden.setText("");
        lblInfoOrden.setText(" (Orden no encontrada o no válida para iniciar)");
        txtFechaInicio.setValue(LocalDate.now());
    }
    public void cerrarVentana() { this.dispose(); }
    // ---
}