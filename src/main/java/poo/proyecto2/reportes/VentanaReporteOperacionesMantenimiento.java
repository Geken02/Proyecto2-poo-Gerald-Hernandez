package poo.proyecto2.reportes;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;

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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class VentanaReporteOperacionesMantenimiento extends JFrame {

    private SistemaPrincipal sistema;

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblOpcionImpresion;
    private JRadioButton rbUnEquipoSimple;
    private JRadioButton rbUnEquipoConComponentes;
    private JRadioButton rbTodosEquiposConComponentes;
    private ButtonGroup grupoOpciones;
    private JPanel panelOpciones;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo; // Visible solo si se elige una opción de "un equipo"
    private JButton btnGenerarPDF;
    private JButton btnCerrar;

    public VentanaReporteOperacionesMantenimiento(SistemaPrincipal sistema) {
        this.sistema = sistema;
        inicializarComponentes();
        configurarEventos();
        setTitle("Reporte de Operaciones de Mantenimiento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack(); // Ajusta al contenido
        setLocationRelativeTo(null); // Centrado en la pantalla
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Generar Reporte de Operaciones de Mantenimiento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel Central: Opciones de Impresión ---
        lblOpcionImpresion = new JLabel("Seleccione la opción de impresión:", SwingConstants.LEFT);
        lblOpcionImpresion.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        grupoOpciones = new ButtonGroup();
        rbUnEquipoSimple = new JRadioButton("Un equipo (sin sus componentes)");
        rbUnEquipoConComponentes = new JRadioButton("Un equipo (con sus componentes)");
        rbTodosEquiposConComponentes = new JRadioButton("Todos los equipos (con sus componentes)");

        grupoOpciones.add(rbUnEquipoSimple);
        grupoOpciones.add(rbUnEquipoConComponentes);
        grupoOpciones.add(rbTodosEquiposConComponentes);

        // Seleccionar por defecto la opción de "Todos los equipos"
        rbTodosEquiposConComponentes.setSelected(true);

        panelOpciones = new JPanel(new GridLayout(0, 1));
        panelOpciones.add(rbUnEquipoSimple);
        panelOpciones.add(rbUnEquipoConComponentes);
        panelOpciones.add(rbTodosEquiposConComponentes);

        JPanel panelOpcionesContenedor = new JPanel(new BorderLayout());
        panelOpcionesContenedor.add(lblOpcionImpresion, BorderLayout.NORTH);
        panelOpcionesContenedor.add(panelOpciones, BorderLayout.CENTER);

        // Panel para ID de equipo (visible solo si se elige una opción de "un equipo")
        JPanel panelIdEquipo = new JPanel(new FlowLayout());
        lblIdEquipo = new JLabel("ID del Equipo:");
        txtIdEquipo = new JTextField(10);
        txtIdEquipo.setVisible(false); // Inicialmente invisible
        panelIdEquipo.add(lblIdEquipo);
        panelIdEquipo.add(txtIdEquipo);
        panelIdEquipo.setVisible(false); // Inicialmente invisible

        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelOpcionesContenedor, BorderLayout.CENTER);
        panelCentral.add(panelIdEquipo, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);

        // --- Panel Inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGenerarPDF = new JButton("Generar PDF");
        btnCerrar = new JButton("Cerrar");
        panelBotones.add(btnGenerarPDF);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        // Evento para mostrar/ocultar el campo de ID según la selección
        ActionListener listenerRadio = e -> {
            boolean mostrarCampo = rbUnEquipoSimple.isSelected() || rbUnEquipoConComponentes.isSelected();
            txtIdEquipo.setVisible(mostrarCampo);
            txtIdEquipo.getParent().setVisible(mostrarCampo); // Mostrar el panel que lo contiene
            // Reajusta el tamaño de la ventana
            pack();
        };
        rbUnEquipoSimple.addActionListener(listenerRadio);
        rbUnEquipoConComponentes.addActionListener(listenerRadio);
        rbTodosEquiposConComponentes.addActionListener(listenerRadio);

        btnGenerarPDF.addActionListener(e -> generarPDF());

        btnCerrar.addActionListener(e -> dispose()); // Cierra la ventana
    }

    private void generarPDF() {
        String destino = "reportes/reporte_operaciones_mantenimiento.pdf"; // Nombre del archivo de salida
        new java.io.File("reportes").mkdirs(); // Crear carpeta si no existe

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(destino));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Reporte de Operaciones de Mantenimiento").setFontSize(20).setBold());

            // Obtener la opción seleccionada
            boolean esUnEquipo = rbUnEquipoSimple.isSelected() || rbUnEquipoConComponentes.isSelected();
            boolean incluirComponentes = rbUnEquipoConComponentes.isSelected() || rbTodosEquiposConComponentes.isSelected();

            if (esUnEquipo) {
                String idStr = txtIdEquipo.getText().trim();
                if (idStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese el ID del equipo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    int idEquipo = Integer.parseInt(idStr);
                    NodoEquipo equipo = sistema.buscarEquipoPorId(idEquipo);
                    if (equipo == null) {
                        JOptionPane.showMessageDialog(this, "No se encontró un equipo con ID: " + idEquipo, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Generar reporte para un solo equipo
                    if (rbUnEquipoSimple.isSelected()) {
                        agregarEquipoSimpleConMantenimientoAPDF(document, equipo, false); // No incluir componentes
                    } else { // rbUnEquipoConComponentes.isSelected()
                        agregarEquipoSimpleConMantenimientoAPDF(document, equipo, true); // Incluir componentes
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo válido (número entero).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else { // rbTodosEquiposConComponentes.isSelected()
                 // Generar reporte para todos los equipos
                 List<NodoEquipo> todosLosEquipos = sistema.obtenerTodosLosEquipos();
                 // Agrupar por ID de equipo principal para obtener las raíces
                 List<NodoEquipo> equiposRaiz = todosLosEquipos.stream()
                         .filter(eq -> eq.getEquipoPrincipal() == 0) // Solo raíces
                         .collect(Collectors.toList());

                 for (NodoEquipo equipoRaiz : equiposRaiz) {
                     agregarEquipoSimpleConMantenimientoAPDF(document, equipoRaiz, true); // Incluir componentes
                 }
            }

            document.close();
            JOptionPane.showMessageDialog(this, "Reporte generado exitosamente en: " + destino, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo crear el archivo PDF en la ubicación: " + destino + "\nError: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Método para agregar un equipo y su mantenimiento (programa y correctivas) al PDF
    private void agregarEquipoSimpleConMantenimientoAPDF(Document document, NodoEquipo equipo, boolean incluirComponentes) {
        // 1. Agregar datos del equipo
        document.add(new Paragraph("Equipo ID: " + equipo.getId() + " - " + equipo.getDescripcion()).setBold());
        Table tableEquipo = crearTablaEquipo(equipo);
        document.add(tableEquipo);

       // 2. Agregar Mantenimiento Preventivo (Programa)
        ProgramaMantenimientoPreventivo programa = sistema.obtenerProgramaDeEquipo(equipo.getId());
        if (programa != null && !programa.getFases().isEmpty()) {
            document.add(new Paragraph("\nMANTENIMIENTO PREVENTIVO").setBold());
            for (int i = 0; i < programa.getFases().size(); i++) { // <-- Este 'i' cambia en cada iteración
                FaseMantenimiento fase = programa.getFases().get(i);
                document.add(new Paragraph("Fase " + (i+1) + ": " + fase).setItalic());
                // Agregar detalles de la fase
                Table tableFase = crearTablaFase(fase);
                document.add(tableFase);

                // --- CORRECCIÓN: Crear una copia de 'i' DENTRO del bucle, justo antes del stream ---
                final int indiceFaseActual = i; // <-- Esta línea debe estar DENTRO del bucle 'for'

                // Agregar órdenes asociadas a esta fase
                List<OrdenTrabajo> ordenesFase = sistema.obtenerOrdenesPorEquipo(equipo.getId()).stream()
                        .filter(orden -> orden instanceof OrdenTrabajoPreventiva && ((OrdenTrabajoPreventiva) orden).getIdFase() == indiceFaseActual) // <-- Usar la copia
                        .collect(Collectors.toList());
                if (!ordenesFase.isEmpty()) {
                    document.add(new Paragraph("  Órdenes Generadas para esta Fase:").setItalic());
                    for (OrdenTrabajo orden : ordenesFase) {
                        agregarOrdenAPDF(document, orden, "    "); // Indentar
                    }
                } else {
                    document.add(new Paragraph("    No hay órdenes generadas para esta fase.").setItalic());
                }
            }
        } else {
            document.add(new Paragraph("MANTENIMIENTO PREVENTIVO: No hay un programa asociado a este equipo.").setItalic());
        }

        // 3. Agregar Mantenimiento Correctivo (Órdenes correctivas)
        List<OrdenTrabajo> ordenesCorrectivas = sistema.obtenerOrdenesPorEquipo(equipo.getId()).stream()
                .filter(orden -> orden instanceof OrdenTrabajoCorrectiva)
                .collect(Collectors.toList());
        if (!ordenesCorrectivas.isEmpty()) {
            document.add(new Paragraph("\nMANTENIMIENTO CORRECTIVO").setBold());
            for (OrdenTrabajo orden : ordenesCorrectivas) {
                agregarOrdenAPDF(document, orden, ""); // No indentar, nivel raíz del equipo
            }
        } else {
            document.add(new Paragraph("MANTENIMIENTO CORRECTIVO: No hay órdenes correctivas registradas para este equipo.").setItalic());
        }

        // 4. Agregar Componentes si es necesario (llamada recursiva)
        if (incluirComponentes) {
            NodoEquipo hijo = equipo.getPrimerHijo();
            while (hijo != null) {
                // Llamada recursiva para cada hijo directo
                agregarEquipoSimpleConMantenimientoAPDF(document, hijo, true); // Incluir sub-componentes de este hijo también
                hijo = hijo.getSiguienteHermano();
            }
        }

        document.add(new Paragraph("\n")); // Espacio entre equipos principales
    }

    // Método auxiliar para crear tabla de datos del equipo
    private Table crearTablaEquipo(NodoEquipo equipo) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3}));
        table.setWidth(UnitValue.createPercentValue(90)); // Ligeramente más estrecha que el 100%

        table.addHeaderCell("Campo").setBold();
        table.addHeaderCell("Valor").setBold();

        table.addCell("ID:");
        table.addCell(String.valueOf(equipo.getId()));
        table.addCell("Descripción:");
        table.addCell(equipo.getDescripcion());
        table.addCell("Tipo:");
        table.addCell(equipo.getTipo());
        table.addCell("Ubicación:");
        table.addCell(equipo.getUbicacion());
        table.addCell("Fabricante:");
        table.addCell(equipo.getFabricante());
        table.addCell("Serie:");
        table.addCell(equipo.getSerie());
        table.addCell("Fecha Adquisición:");
        table.addCell(equipo.getFechaAdquisicion() != null ? equipo.getFechaAdquisicion().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
        table.addCell("Fecha Puesta en Servicio:");
        table.addCell(equipo.getFechaPuestaEnServicio() != null ? equipo.getFechaPuestaEnServicio().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
        table.addCell("Meses Vida Útil:");
        table.addCell(String.valueOf(equipo.getMesesVidaUtil()));
        table.addCell("Estado:");
        table.addCell(equipo.getEstado().toString());
        table.addCell("Costo Inicial:");
        table.addCell(String.format("%.2f", equipo.getCostoInicial()));
        table.addCell("Equipo Principal (ID):");
        table.addCell(String.valueOf(equipo.getEquipoPrincipal()));

        return table;
    }

    // Método auxiliar para crear tabla de datos de la fase
    private Table crearTablaFase(FaseMantenimiento fase) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3}));
        table.setWidth(UnitValue.createPercentValue(85)); // Más estrecha que la del equipo

        table.addHeaderCell("Campo").setBold();
        table.addHeaderCell("Valor").setBold();

        table.addCell("Tipo Frecuencia:");
        table.addCell(fase.getTipoFrecuencia().toString());
        table.addCell("Medidor Frecuencia:");
        table.addCell(String.valueOf(fase.getMedidorFrecuencia()));
        table.addCell("Cantidad Ciclos:");
        table.addCell(String.valueOf(fase.getCantidadCiclos() == 0 ? "Recurrente" : fase.getCantidadCiclos()));
        table.addCell("Partes/Repuestos:");
        table.addCell(fase.getPartes() != null ? fase.getPartes() : "N/A");
        table.addCell("Herramientas:");
        table.addCell(fase.getHerramientas() != null ? fase.getHerramientas() : "N/A");
        table.addCell("Personal Requerido:");
        table.addCell(fase.getPersonal() != null ? fase.getPersonal() : "N/A");
        table.addCell("Horas Estimadas:");
        table.addCell(String.valueOf(fase.getHorasEstimadas()));
        table.addCell("Tareas Asociadas:");
        table.addCell(String.join(", ", fase.getIdsTareasMaestras().stream().map(String::valueOf).toArray(String[]::new)));

        return table;
    }

    // Método auxiliar para agregar detalles de una orden al PDF
    private void agregarOrdenAPDF(Document document, OrdenTrabajo orden, String indent) {
        String tipoOrden = (orden instanceof OrdenTrabajoPreventiva) ? "ORDEN PREVENTIVA" : "ORDEN CORRECTIVA";
        document.add(new Paragraph(indent + tipoOrden + " # " + orden.getId() + " - Estado: " + orden.getEstado()).setBold());

        Table tableOrden = new Table(UnitValue.createPercentArray(new float[]{1, 3}));
        tableOrden.setWidth(UnitValue.createPercentValue(80)); // Más estrecha que la del equipo o fase

        tableOrden.addHeaderCell("Campo").setBold();
        tableOrden.addHeaderCell("Valor").setBold();

        tableOrden.addCell("ID Orden:");
        tableOrden.addCell(String.valueOf(orden.getId()));
        tableOrden.addCell("ID Equipo:");
        tableOrden.addCell(String.valueOf(orden.getIdEquipo()));
        tableOrden.addCell("Fecha Orden:");
        tableOrden.addCell(orden.getFechaOrden() != null ? orden.getFechaOrden().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
        tableOrden.addCell("Fecha Ejecución:");
        tableOrden.addCell(orden.getFechaEjecucion() != null ? orden.getFechaEjecucion().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
        tableOrden.addCell("Fecha Inicio Real:");
        tableOrden.addCell(orden.getFechaInicioReal() != null ? orden.getFechaInicioReal().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
        tableOrden.addCell("Fecha Fin Real:");
        tableOrden.addCell(orden.getFechaFinReal() != null ? orden.getFechaFinReal().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
        tableOrden.addCell("Horas Trabajo:");
        tableOrden.addCell(orden.getHorasTrabajo() != 0.0f ? String.valueOf(orden.getHorasTrabajo()) : "N/A");
        tableOrden.addCell("Costo Mano de Obra:");
        tableOrden.addCell(orden.getCostoManoObra() != 0 ? String.valueOf(orden.getCostoManoObra()) : "N/A");
        tableOrden.addCell("Costo Materiales:");
        tableOrden.addCell(orden.getCostoMateriales() != 0 ? String.valueOf(orden.getCostoMateriales()) : "N/A");
        tableOrden.addCell("Observaciones:");
        tableOrden.addCell(orden.getObservaciones() != null ? orden.getObservaciones() : "N/A");
        tableOrden.addCell("Observaciones de Ejecución:");
        tableOrden.addCell(orden.getObservacionesEjecucion() != null ? orden.getObservacionesEjecucion() : "N/A");
        tableOrden.addCell("Fecha Cancelación:");
        tableOrden.addCell(orden.getFechaCancelacion() != null ? orden.getFechaCancelacion().format(DateTimeFormatter.ISO_LOCAL_DATE) : "N/A");
        tableOrden.addCell("Motivo Cancelación:");
        tableOrden.addCell(orden.getMotivoCancelacion() != null ? orden.getMotivoCancelacion() : "N/A");

        document.add(tableOrden);

        // Agregar Fallas Reportadas
        if (!orden.getFallasReportadas().isEmpty()) {
            document.add(new Paragraph(indent + "  Fallas Reportadas:").setItalic());
            Table tableFallasRep = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2}));
            tableFallasRep.setWidth(UnitValue.createPercentValue(75)); // Más estrecha
            tableFallasRep.addHeaderCell("Causas").setBold();
            tableFallasRep.addHeaderCell("Acciones Tomadas").setBold();
            for (var falla : orden.getFallasReportadas()) {
                tableFallasRep.addCell(falla.getCausas());
                tableFallasRep.addCell(falla.getAccionesTomadas());
            }
            document.add(tableFallasRep);
        }

        // Agregar Fallas Encontradas
        if (!orden.getFallasEncontradas().isEmpty()) {
            document.add(new Paragraph(indent + "  Fallas Encontradas:").setItalic());
            Table tableFallasEnc = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 2}));
            tableFallasEnc.setWidth(UnitValue.createPercentValue(75)); // Más estrecha
            tableFallasEnc.addHeaderCell("ID Falla").setBold();
            tableFallasEnc.addHeaderCell("Descripción").setBold();
            tableFallasEnc.addHeaderCell("Causas").setBold();
            tableFallasEnc.addHeaderCell("Acciones Tomadas").setBold();
            for (var falla : orden.getFallasEncontradas()) {
                tableFallasEnc.addCell(String.valueOf(falla.getIdFalla()));
                tableFallasEnc.addCell(falla.getDescripcionFalla());
                tableFallasEnc.addCell(falla.getCausas());
                tableFallasEnc.addCell(falla.getAccionesTomadas());
            }
            document.add(tableFallasEnc);
        }

        document.add(new Paragraph(indent + "\n")); // Espacio entre órdenes
    }
}