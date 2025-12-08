package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorCancelarOrdenPreventiva;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VentanaCancelarOrdenPreventiva extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre;

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdOrden;
    private JTextField txtIdOrden;
    private JButton btnBuscarOrden;
    private JLabel lblInfoOrden; 
    private JLabel lblFechaCancelacion;
    private JFormattedTextField txtFechaCancelacion; 
    private JLabel lblMotivoCancelacion;
    private JTextArea txtMotivoCancelacion; 
    private JScrollPane scrollMotivo;
    private JButton btnCancelarOrden;
    private JButton btnCerrar;

    // Referencia al controlador
    private ControladorCancelarOrdenPreventiva controlador;

    public VentanaCancelarOrdenPreventiva(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        // Crear el controlador pasando el sistema y la vista
        this.controlador = new ControladorCancelarOrdenPreventiva(sistema, this);

        inicializarComponentes();
        configurarEventos();
        setTitle("Cancelar Orden de Trabajo Preventiva");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(600, 250); 
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Cancelar Orden de Trabajo Preventiva", SwingConstants.CENTER);
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
        lblInfoOrden = new JLabel(" (Orden no encontrada o no válida para cancelar)");
        lblInfoOrden.setFont(lblInfoOrden.getFont().deriveFont(Font.ITALIC));
        panelFormulario.add(lblInfoOrden, gbc);

        // Fila 1: Fecha Cancelación
        gbc.gridx = 0; gbc.gridy = 1;
        lblFechaCancelacion = new JLabel("Fecha de Cancelación *:");
        panelFormulario.add(lblFechaCancelacion, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtFechaCancelacion = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaCancelacion.setValue(LocalDate.now()); 
        txtFechaCancelacion.setEnabled(false); 
        panelFormulario.add(txtFechaCancelacion, gbc);
        gbc.gridwidth = 1; 

        // Fila 2: Motivo Cancelación
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.BOTH;
        lblMotivoCancelacion = new JLabel("Motivo de Cancelación *:");
        panelFormulario.add(lblMotivoCancelacion, gbc);

        gbc.gridy = 3;
        txtMotivoCancelacion = new JTextArea(3, 30);
        txtMotivoCancelacion.setEnabled(false); 
        scrollMotivo = new JScrollPane(txtMotivoCancelacion);
        panelFormulario.add(scrollMotivo, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCancelarOrden = new JButton("Cancelar Orden");
        btnCerrar = new JButton("Cerrar");
        btnCancelarOrden.setEnabled(false); 
        panelBotones.add(btnCancelarOrden);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        // --- Eventos de Botones ---
        btnBuscarOrden.addActionListener(e -> controlador.buscarOrden()); 
        btnCancelarOrden.addActionListener(e -> controlador.cancelarOrden()); 
        btnCerrar.addActionListener(e -> controlador.cancelarVentana()); 
    }

    // --- Métodos para que el controlador interactúe con la vista ---
    public JTextField getTxtIdOrden() { return txtIdOrden; }
    public JFormattedTextField getTxtFechaCancelacion() { return txtFechaCancelacion; }
    public JTextArea getTxtMotivoCancelacion() { return txtMotivoCancelacion; }
    public void mostrarInfoOrden(String texto) { lblInfoOrden.setText(texto); }
    public void mostrarMensaje(String mensaje, String titulo, int tipo) { JOptionPane.showMessageDialog(this, mensaje, titulo, tipo); }
    public void habilitarCamposCancelar(boolean habilitar) {
        txtFechaCancelacion.setEnabled(habilitar);
        txtMotivoCancelacion.setEnabled(habilitar);
        btnCancelarOrden.setEnabled(habilitar);
    }
    public void limpiarFormulario() {
        txtIdOrden.setText("");
        lblInfoOrden.setText(" (Orden no encontrada o no válida para cancelar)");
        txtFechaCancelacion.setValue(LocalDate.now());
        txtMotivoCancelacion.setText("");
    }
    public void cerrarVentana() { this.dispose(); }
}