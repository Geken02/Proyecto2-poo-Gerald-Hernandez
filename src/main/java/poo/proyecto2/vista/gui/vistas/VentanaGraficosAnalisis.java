package poo.proyecto2.vista.gui.vistas;

import org.jfree.chart.ChartPanel;

import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.controlador.ControladorGraficosAnalisis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaGraficosAnalisis extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; 

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JPanel panelGraficos;
    private JButton btnGenerarGraficos;
    private JButton btnCerrar;

    // Referencia al controlador
    private ControladorGraficosAnalisis controlador;

    public VentanaGraficosAnalisis(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) { // Añadido ventanaPadre
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre;
        // Crear el controlador pasando el modelo y la vista
        this.controlador = new ControladorGraficosAnalisis(sistema, this);

        inicializarComponentes();
        configurarEventos();
        setTitle("Gráficos de Análisis");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(1200, 800); 
        setLocationRelativeTo(ventanaPadre); 
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Gráficos de Análisis de Mantenimiento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Contenedor de Gráficos ---
        panelGraficos = new JPanel(new GridLayout(1, 2, 10, 10)); 
        panelGraficos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        // Inicialmente vacío, se llena con los gráficos generados
        add(panelGraficos, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGenerarGraficos = new JButton("Generar Gráficos");
        btnCerrar = new JButton("Cerrar");
        panelBotones.add(btnGenerarGraficos);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnGenerarGraficos.addActionListener(e -> controlador.generarYMostrarGraficos()); 
        btnCerrar.addActionListener(e -> dispose()); 
    }

    // --- Método para que el controlador actualice la vista ---
    public void mostrarGraficos(ChartPanel panel1, ChartPanel panel2) {
        panelGraficos.removeAll(); 
        panelGraficos.add(panel1);
        panelGraficos.add(panel2);
        panelGraficos.revalidate(); 
        panelGraficos.repaint();
    }
    // ---
}