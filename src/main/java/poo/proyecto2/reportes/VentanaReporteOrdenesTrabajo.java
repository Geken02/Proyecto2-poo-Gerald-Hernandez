package poo.proyecto2.reportes;

import poo.proyecto2.mantenimiento.*;
import poo.proyecto2.sistema.SistemaPrincipal;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class VentanaReporteOrdenesTrabajo extends JFrame {

    private SistemaPrincipal sistema;

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblFiltros;
    private JLabel lblEstado;
    private JComboBox<String> cmbEstado;
    private JLabel lblTipo;
    private JComboBox<String> cmbTipo;
    private JLabel lblFechaDesde;
    private JFormattedTextField txtFechaDesde;
    private JLabel lblFechaHasta;
    private JFormattedTextField txtFechaHasta;
    private JButton btnGenerarPDF;
    private JButton btnCerrar;

    public VentanaReporteOrdenesTrabajo(SistemaPrincipal sistema) {
        this.sistema = sistema;
        inicializarComponentes();
        configurarEventos();
        setTitle("Reporte de Órdenes de Trabajo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack(); // Ajusta al contenido
        setLocationRelativeTo(null); // Centrado en la pantalla
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Generar Reporte de Órdenes de Trabajo", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Filtros ---
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        lblFiltros = new JLabel("Filtros de Impresión:", SwingConstants.LEFT);
        lblFiltros.setFont(new Font("Arial", Font.BOLD, 14));
        panelFiltros.add(lblFiltros, gbc);

        gbc.gridy = 1;
        lblEstado = new JLabel("Estado de las órdenes:");
        panelFiltros.add(lblEstado, gbc);

        gbc.gridx = 1;
        cmbEstado = new JComboBox<>(new String[]{"Todas", "Pendientes", "Terminadas", "Canceladas"});
        panelFiltros.add(cmbEstado, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        lblTipo = new JLabel("Órdenes incluidas:");
        panelFiltros.add(lblTipo, gbc);

        gbc.gridx = 1;
        cmbTipo = new JComboBox<>(new String[]{"Mantenimiento preventivo y correctivo", "Mantenimiento preventivo", "Mantenimiento correctivo"});
        panelFiltros.add(cmbTipo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        lblFechaDesde = new JLabel("Fecha Desde (AAAA-MM-DD):");
        panelFiltros.add(lblFechaDesde, gbc);

        gbc.gridx = 1;
        txtFechaDesde = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaDesde.setValue(LocalDate.now().minusMonths(1)); // Valor por defecto: hace un mes
        panelFiltros.add(txtFechaDesde, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        lblFechaHasta = new JLabel("Fecha Hasta (AAAA-MM-DD):");
        panelFiltros.add(lblFechaHasta, gbc);

        gbc.gridx = 1;
        txtFechaHasta = new JFormattedTextField(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtFechaHasta.setValue(LocalDate.now()); // Valor por defecto: hoy
        panelFiltros.add(txtFechaHasta, gbc);

        add(panelFiltros, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGenerarPDF = new JButton("Generar PDF");
        btnCerrar = new JButton("Cerrar");
        panelBotones.add(btnGenerarPDF);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnGenerarPDF.addActionListener(e -> generarPDF());

        btnCerrar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    private void generarPDF() {
        String destino = "reportes/reporte_ordenes_trabajo.pdf"; // Nombre del archivo de salida
        new java.io.File("reportes").mkdirs(); // Crear carpeta si no existe

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(destino));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Reporte de Órdenes de Trabajo").setFontSize(20).setBold());

            // --- Obtener filtros ---
            String estadoSeleccionado = (String) cmbEstado.getSelectedItem();
            String tipoSeleccionado = (String) cmbTipo.getSelectedItem();
            LocalDate fechaDesde, fechaHasta;
            try {
                fechaDesde = LocalDate.parse(((JFormattedTextField) txtFechaDesde).getText().trim());
                fechaHasta = LocalDate.parse(((JFormattedTextField) txtFechaHasta).getText().trim());
                if (fechaHasta.isBefore(fechaDesde)) {
                    JOptionPane.showMessageDialog(this, "La fecha 'Hasta' no puede ser anterior a la fecha 'Desde'.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese fechas válidas en formato AAAA-MM-DD.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // ---

            // --- Aplicar filtros a la lista de órdenes ---
            List<OrdenTrabajo> ordenesOriginales = sistema.obtenerTodasLasOrdenes();
            List<OrdenTrabajo> ordenesFiltradas = ordenesOriginales.stream()
                    .filter(orden -> {
                        // Filtro por Estado
                        boolean estadoCoincide = true;
                        switch (estadoSeleccionado) {
                            case "Pendientes":
                                estadoCoincide = orden.getEstado() == OrdenTrabajo.EstadoOrden.PENDIENTE;
                                break;
                            case "Terminadas":
                                estadoCoincide = orden.getEstado() == OrdenTrabajo.EstadoOrden.COMPLETADA;
                                break;
                            case "Canceladas":
                                estadoCoincide = orden.getEstado() == OrdenTrabajo.EstadoOrden.CANCELADA;
                                break;
                            case "Todas":
                                // estadoCoincide = true; // Ya está inicializado como true
                                break;
                        }

                        // Filtro por Tipo
                        boolean tipoCoincide = true;
                        if (orden instanceof OrdenTrabajoPreventiva) {
                            tipoCoincide = tipoSeleccionado.equals("Mantenimiento preventivo") || tipoSeleccionado.equals("Mantenimiento preventivo y correctivo");
                        } else if (orden instanceof OrdenTrabajoCorrectiva) {
                            tipoCoincide = tipoSeleccionado.equals("Mantenimiento correctivo") || tipoSeleccionado.equals("Mantenimiento preventivo y correctivo");
                        }

                        // Filtro por Fecha (usando fecha de orden como referencia)
                        boolean fechaCoincide = !orden.getFechaOrden().isBefore(fechaDesde) && !orden.getFechaOrden().isAfter(fechaHasta);

                        return estadoCoincide && tipoCoincide && fechaCoincide;
                    })
                    .sorted((o1, o2) -> o1.getFechaOrden().compareTo(o2.getFechaOrden())) // Ordenar por fecha de orden
                    .collect(Collectors.toList());
            // ---

            // --- Mostrar resultados en el PDF ---
            if (ordenesFiltradas.isEmpty()) {
                document.add(new Paragraph("No se encontraron órdenes de trabajo que coincidan con los filtros aplicados."));
            } else {
                document.add(new Paragraph("Total de órdenes encontradas: " + ordenesFiltradas.size()).setItalic());

                // Crear tabla para mostrar las órdenes
                Table table = new Table(UnitValue.createPercentArray(new float[]{0.5f, 1, 1, 1, 1, 1})); // Anchos relativos
                table.setWidth(UnitValue.createPercentValue(95)); // Ligeramente menos del 100%

                table.addHeaderCell("ID").setBold();
                table.addHeaderCell("ID Equipo").setBold();
                table.addHeaderCell("Fecha Orden").setBold();
                table.addHeaderCell("Fecha Ejecución").setBold();
                table.addHeaderCell("Estado").setBold();
                table.addHeaderCell("Tipo").setBold();

                for (OrdenTrabajo orden : ordenesFiltradas) {
                    String tipoOrden = (orden instanceof OrdenTrabajoPreventiva) ? "Preventiva" : "Correctiva";
                    table.addCell(String.valueOf(orden.getId()));
                    table.addCell(String.valueOf(orden.getIdEquipo()));
                    table.addCell(orden.getFechaOrden() != null ? orden.getFechaOrden().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
                    table.addCell(orden.getFechaEjecucion() != null ? orden.getFechaEjecucion().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
                    table.addCell(orden.getEstado().toString());
                    table.addCell(tipoOrden);
                }

                document.add(table);
            }
            // ---

            document.close();
            JOptionPane.showMessageDialog(this, "Reporte generado exitosamente en: " + destino, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo crear el archivo PDF en la ubicación: " + destino + "\nError: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}