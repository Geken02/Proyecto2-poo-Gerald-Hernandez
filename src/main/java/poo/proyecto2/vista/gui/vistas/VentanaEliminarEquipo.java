package poo.proyecto2.vista.gui.vistas;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal; 
import poo.proyecto2.controlador.ControladorEliminarEquipo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VentanaEliminarEquipo extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; 
    private Runnable despuesDeEliminar; 

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo;
    private JButton btnBuscar;
    private JButton btnEliminar;
    private JButton btnCancelar;

    // Panel para mostrar detalles del equipo encontrado (opcional, para confirmación)
    private JPanel panelDetalle;
    private JLabel lblDetalleVacio;
    private JLabel lblId;
    private JLabel lblDescripcion;
    private JLabel lblTipo;
    private JLabel lblUbicacion;

    // --- Componentes para mostrar los detalles ---
    private JLabel valId;
    private JLabel valDescripcion;
    private JLabel valTipo;
    private JLabel valUbicacion;

    // Referencia al controlador
    private ControladorEliminarEquipo controlador;

    public VentanaEliminarEquipo(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre, Runnable despuesDeEliminar) { 
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        this.despuesDeEliminar = despuesDeEliminar;
        inicializarComponentes();
        configurarEventos();
        setTitle("Eliminar Equipo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(ventanaPadre);
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Eliminar Equipo", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Formulario ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        lblIdEquipo = new JLabel("ID del Equipo a Eliminar: *");
        panelFormulario.add(lblIdEquipo, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtIdEquipo = new JTextField(10);
        panelFormulario.add(txtIdEquipo, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        btnBuscar = new JButton("Buscar");
        panelFormulario.add(btnBuscar, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel de Detalle (para mostrar info del equipo encontrado) ---
        panelDetalle = new JPanel(new BorderLayout());
        panelDetalle.setBorder(BorderFactory.createTitledBorder("Detalles del Equipo (para confirmación)"));
        lblDetalleVacio = new JLabel("Ingrese un ID y presione 'Buscar'.", SwingConstants.CENTER);
        lblDetalleVacio.setFont(new Font("Arial", Font.ITALIC, 12));
        panelDetalle.add(lblDetalleVacio, BorderLayout.CENTER);

        // Componentes para mostrar detalles (inicialmente invisibles)
        JPanel panelInfo = new JPanel(new GridLayout(0, 2, 5, 5)); // 2 columnas
        lblId = new JLabel("ID:");
        lblDescripcion = new JLabel("Descripción:");
        lblTipo = new JLabel("Tipo:");
        lblUbicacion = new JLabel("Ubicación:");

        valId = new JLabel(" - "); // Etiquetas para los valores
        valDescripcion = new JLabel(" - ");
        valTipo = new JLabel(" - ");
        valUbicacion = new JLabel(" - ");

        panelInfo.add(lblId); panelInfo.add(valId);
        panelInfo.add(lblDescripcion); panelInfo.add(valDescripcion);
        panelInfo.add(lblTipo); panelInfo.add(valTipo);
        panelInfo.add(lblUbicacion); panelInfo.add(valUbicacion);

        panelDetalle.add(panelInfo, BorderLayout.CENTER);
        panelDetalle.setVisible(false); // Oculto inicialmente

        add(panelDetalle, BorderLayout.SOUTH);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setEnabled(false); // Inicialmente deshabilitado
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        // Crear el controlador pasándole el sistema, la vista y el callback
        this.controlador = new ControladorEliminarEquipo(sistema, this, despuesDeEliminar);
    }

    private void configurarEventos() {
        btnBuscar.addActionListener(e -> controlador.buscarEquipo()); 
        btnEliminar.addActionListener(e -> controlador.eliminarEquipo()); 
        btnCancelar.addActionListener(e -> dispose());
    }

    // --- Métodos para que el controlador acceda a los datos y actualice la vista ---
    public JTextField getTxtIdEquipo() { return txtIdEquipo; } 
    public void mostrarMensaje(String mensaje, String titulo, int tipo) { JOptionPane.showMessageDialog(this, mensaje, titulo, tipo); }
    public void mostrarDetalleEquipo(NodoEquipo equipo) {
        lblDetalleVacio.setVisible(false);
        panelDetalle.setVisible(true);
        valId.setText(String.valueOf(equipo.getId()));
        valDescripcion.setText(equipo.getDescripcion());
        valTipo.setText(equipo.getTipo());
        valUbicacion.setText(equipo.getUbicacion());
    }
    public void limpiarDetalle() {
        lblDetalleVacio.setVisible(true);
        panelDetalle.setVisible(false);
        valId.setText(" - ");
        valDescripcion.setText(" - ");
        valTipo.setText(" - ");
        valUbicacion.setText(" - ");
    }
    public void limpiarFormulario() {
        txtIdEquipo.setText("");
        limpiarDetalle();
    }
    public void habilitarBotonEliminar(boolean habilitar) {
        btnEliminar.setEnabled(habilitar);
    }
    public void cerrarVentana() { this.dispose(); } 
}