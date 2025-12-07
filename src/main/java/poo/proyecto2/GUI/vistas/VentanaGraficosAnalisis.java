package poo.proyecto2.gui.vistas;

import org.fujion.sparkline.PiePlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import poo.proyecto2.sistema.SistemaPrincipal;
import poo.proyecto2.mantenimiento.OrdenTrabajo;
import poo.proyecto2.equipos.NodoEquipo;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class VentanaGraficosAnalisis extends JFrame {

    private SistemaPrincipal sistema;

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JPanel panelGraficos;
    private JButton btnGenerarGraficos;
    private JButton btnCerrar;

    public VentanaGraficosAnalisis(SistemaPrincipal sistema) {
        this.sistema = sistema;
        inicializarComponentes();
        configurarEventos();
        setTitle("Gráficos de Análisis");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(1200, 800); // Tamaño grande para los gráficos
        setLocationRelativeTo(null); // Centrado en la pantalla
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Gráficos de Análisis de Mantenimiento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Contenedor de Gráficos ---
        panelGraficos = new JPanel(new GridLayout(1, 2, 10, 10)); // Dos gráficos lado a lado
        panelGraficos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen interno
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
        btnGenerarGraficos.addActionListener(e -> generarYMostrarGraficos());

        btnCerrar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    private void generarYMostrarGraficos() {
        // Limpiar el panel de gráficos anterior
        panelGraficos.removeAll();

        // --- Gráfico 1: Órdenes de Trabajo por Estado ---
        ChartPanel panelGrafico1 = crearGraficoBarrasOrdenesPorEstado();
        panelGraficos.add(panelGrafico1);

        // --- Gráfico 2: Costos Totales por Equipo ---
        ChartPanel panelGrafico2 = crearGraficoCircularCostosPorEquipo();
        panelGraficos.add(panelGrafico2);

        // Refrescar el panel
        panelGraficos.revalidate();
        panelGraficos.repaint();
    }

    private ChartPanel crearGraficoBarrasOrdenesPorEstado() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<OrdenTrabajo> ordenes = sistema.obtenerTodasLasOrdenes();

        // Agrupar órdenes por estado
        Map<OrdenTrabajo.EstadoOrden, Long> conteoPorEstado = ordenes.stream()
                .collect(Collectors.groupingBy(OrdenTrabajo::getEstado, Collectors.counting()));

        // Agregar valores al dataset
        for (OrdenTrabajo.EstadoOrden estado : OrdenTrabajo.EstadoOrden.values()) {
            long count = conteoPorEstado.getOrDefault(estado, 0L);
            dataset.addValue(count, "Cantidad", estado.toString());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Órdenes de Trabajo por Estado", // Título del gráfico
                "Estado", // Etiqueta del eje X
                "Cantidad", // Etiqueta del eje Y
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Orientación
                true, // Mostrar leyenda
                true, // Tooltips
                false // URLs
        );

        return new ChartPanel(chart);
    }

    private ChartPanel crearGraficoCircularCostosPorEquipo() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        List<OrdenTrabajo> ordenes = sistema.obtenerTodasLasOrdenes();

        // Agrupar costos por ID de equipo
        Map<Integer, Double> costosPorEquipo = new HashMap<>();
        for (OrdenTrabajo orden : ordenes) {
            if (orden.getEstado() == OrdenTrabajo.EstadoOrden.COMPLETADA) { // Solo órdenes completadas tienen costos reales
                double costoTotal = (double) orden.getCostoManoObra() + orden.getCostoMateriales();
                costosPorEquipo.merge(orden.getIdEquipo(), costoTotal, Double::sum);
            }
        }

        // Agregar valores al dataset (nombre del equipo, costo total)
        for (Map.Entry<Integer, Double> entry : costosPorEquipo.entrySet()) {
            int idEquipo = entry.getKey();
            double costoTotal = entry.getValue();
            // Buscar la descripción del equipo para mostrarla en el gráfico
            NodoEquipo equipo = sistema.buscarEquipoPorId(idEquipo);
            String nombreEquipo = equipo != null ? equipo.getDescripcion() : "Equipo ID " + idEquipo;
            dataset.setValue(nombreEquipo, costoTotal);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Costos Totales por Equipo (Órdenes Completadas)", // Título del gráfico
                dataset, // Dataset
                true, // Mostrar leyenda
                true, // Tooltips
                false // No generar URLs
        );

        return new ChartPanel(chart);
    }
}