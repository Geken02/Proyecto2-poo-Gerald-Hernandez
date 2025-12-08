package poo.proyecto2.controlador;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import poo.proyecto2.modelo.mantenimiento.OrdenTrabajo;
import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaGraficosAnalisis; // Referencia a la vista

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ControladorGraficosAnalisis {

    private SistemaPrincipal modelo;
    private VentanaGraficosAnalisis vista; // Referencia a la vista

    public ControladorGraficosAnalisis(SistemaPrincipal modelo, VentanaGraficosAnalisis vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    public void generarYMostrarGraficos() {
        // --- Gráfico 1: Órdenes de Trabajo por Estado ---
        ChartPanel panelGrafico1 = crearGraficoBarrasOrdenesPorEstado();
        // --- Gráfico 2: Costos Totales por Equipo ---
        ChartPanel panelGrafico2 = crearGraficoCircularCostosPorEquipo();

        // Llamar a la vista para que los muestre
        vista.mostrarGraficos(panelGrafico1, panelGrafico2);
    }

    private ChartPanel crearGraficoBarrasOrdenesPorEstado() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<OrdenTrabajo> ordenes = modelo.obtenerTodasLasOrdenes(); // Usar el modelo pasado

        // Agrupar órdenes por estado
        Map<OrdenTrabajo.EstadoOrden, Long> conteoPorEstado = ordenes.stream()
                .collect(Collectors.groupingBy(OrdenTrabajo::getEstado, Collectors.counting()));

        // Agregar valores al dataset
        for (OrdenTrabajo.EstadoOrden estado : OrdenTrabajo.EstadoOrden.values()) {
            long count = conteoPorEstado.getOrDefault(estado, 0L);
            dataset.addValue(count, "Cantidad", estado.toString());
        }

        JFreeChart chart = org.jfree.chart.ChartFactory.createBarChart(
                "Órdenes de Trabajo por Estado", // Título del gráfico
                "Estado", // Etiqueta del eje X
                "Cantidad", // Etiqueta del eje Y
                dataset, // Dataset
                org.jfree.chart.plot.PlotOrientation.VERTICAL, // Orientación
                true, // Mostrar leyenda
                true, // Tooltips
                false // URLs
        );

        // --- AÑADIR COLORES AL GRÁFICO DE BARRAS ---
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        // Colores para cada estado (debe coincidir con el número de series si hay más de una)
        // En este caso, solo hay una serie ("Cantidad"), así que coloreamos las categorías (estados)
        renderer.setSeriesPaint(0, Color.BLUE); // Aunque solo hay una serie, se puede intentar colorear por categoría si se configura un PaintMap
        // Otra forma más directa es asignar colores basados en el índice de la categoría o el valor
        // Iteramos sobre los estados para asignar un color distinto a cada barra
        for (int i = 0; i < OrdenTrabajo.EstadoOrden.values().length; i++) {
            switch (OrdenTrabajo.EstadoOrden.values()[i]) {
                case PENDIENTE:
                    renderer.setSeriesPaint(i, Color.YELLOW);
                    break;
                case EN_PROGRESO:
                    renderer.setSeriesPaint(i, Color.ORANGE);
                    break;
                case COMPLETADA:
                    renderer.setSeriesPaint(i, Color.GREEN);
                    break;
                case CANCELADA:
                    renderer.setSeriesPaint(i, Color.RED);
                    break;
                default:
                    renderer.setSeriesPaint(i, Color.GRAY); // Color por defecto
            }
        }
        // --- FIN AÑADIR COLORES ---

        return new ChartPanel(chart);
    }

    private ChartPanel crearGraficoCircularCostosPorEquipo() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        List<OrdenTrabajo> ordenes = modelo.obtenerTodasLasOrdenes(); // Usar el modelo pasado

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
            NodoEquipo equipo = modelo.buscarEquipoPorId(idEquipo); // Usar el modelo pasado
            String nombreEquipo = equipo != null ? equipo.getDescripcion() : "Equipo ID " + idEquipo;
            dataset.setValue(nombreEquipo, costoTotal);
        }

        JFreeChart chart = org.jfree.chart.ChartFactory.createPieChart(
                "Costos Totales por Equipo (Órdenes Completadas)", // Título del gráfico
                dataset, // Dataset
                true, // Mostrar leyenda
                true, // Tooltips
                false // No generar URLs
        );

        return new ChartPanel(chart);
    }
}