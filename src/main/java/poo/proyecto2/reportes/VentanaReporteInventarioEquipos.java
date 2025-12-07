package poo.proyecto2.reportes;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaReporteInventarioEquipos extends JFrame {

    private SistemaPrincipal sistema;

    // Componentes de la interfaz
    private JLabel lblTitulo;
    private JLabel lblOpcionImpresion;
    private ButtonGroup grupoOpciones; // Para agrupar los radio buttons
    private JRadioButton rbUnEquipoSimple;
    private JRadioButton rbUnEquipoConComponentes;
    private JRadioButton rbTodosEquiposConComponentes;
    private JPanel panelOpciones;
    private JLabel lblIdEquipo;
    private JTextField txtIdEquipo; // Visible solo si se elige una opción de "un equipo"
    private JButton btnGenerarPDF;
    private JButton btnCerrar;

    public VentanaReporteInventarioEquipos(SistemaPrincipal sistema) {
        this.sistema = sistema;
        inicializarComponentes();
        configurarEventos();
        setTitle("Reporte de Inventario de Equipos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack(); // Ajusta al contenido
        setLocationRelativeTo(null); // Centrado en la pantalla
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // --- Panel Superior: Título ---
        lblTitulo = new JLabel("Generar Reporte de Inventario de Equipos", SwingConstants.CENTER);
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

        // Agrupar los radio buttons
        grupoOpciones.add(rbUnEquipoSimple);
        grupoOpciones.add(rbUnEquipoConComponentes);
        grupoOpciones.add(rbTodosEquiposConComponentes);

        // Seleccionar por defecto la opción de "Todos los equipos"
        rbTodosEquiposConComponentes.setSelected(true);

        panelOpciones = new JPanel(new GridLayout(0, 1)); // 0 filas, 1 columna
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
        String destino = "reportes/reporte_inventario_equipos.pdf"; // Nombre del archivo de salida
        new java.io.File("reportes").mkdirs(); // Crear carpeta si no existe

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(destino));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Reporte de Inventario de Equipos").setFontSize(20).setBold());

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
                        agregarEquipoSimpleAPDF(document, equipo);
                    } else { // rbUnEquipoConComponentes.isSelected()
                        agregarEquipoConComponentesAPDF(document, equipo);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de equipo válido (número entero).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else { // rbTodosEquiposConComponentes.isSelected()
                 // Generar reporte para todos los equipos
                 List<NodoEquipo> todosLosEquipos = sistema.obtenerTodosLosEquipos();
                 for (NodoEquipo equipo : todosLosEquipos) {
                     // Solo procesar raíces (porque los componentes se incluyen recursivamente)
                     if (equipo.getEquipoPrincipal() == 0) {
                         agregarEquipoConComponentesAPDF(document, equipo);
                     }
                 }
            }

            document.close();
            JOptionPane.showMessageDialog(this, "Reporte generado exitosamente en: " + destino, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo crear el archivo PDF en la ubicación: " + destino + "\nError: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void agregarEquipoSimpleAPDF(Document document, NodoEquipo equipo) {
        // Crear una tabla para el equipo
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3}));
        table.setWidth(UnitValue.createPercentValue(100));

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

        document.add(new Paragraph("Equipo ID: " + equipo.getId() + " - " + equipo.getDescripcion()).setBold());
        document.add(table);
        document.add(new Paragraph("\n")); // Espacio entre equipos
    }

    private void agregarEquipoConComponentesAPDF(Document document, NodoEquipo equipo) {
        // Agregar el equipo principal
        agregarEquipoSimpleAPDF(document, equipo);

        // Agregar subtítulo para componentes
        document.add(new Paragraph("Componentes:").setItalic());

        // Recorrer hijos recursivamente
        NodoEquipo hijo = equipo.getPrimerHijo();
        if (hijo != null) {
            Table tableComp = new Table(UnitValue.createPercentArray(new float[]{1, 3}));
            tableComp.setWidth(UnitValue.createPercentValue(90)); // Ligeramente más estrecha que la principal
            tableComp.addHeaderCell("ID").setBold();
            tableComp.addHeaderCell("Descripción").setBold();

            while (hijo != null) {
                tableComp.addCell(String.valueOf(hijo.getId()));
                tableComp.addCell(hijo.getDescripcion());
                // Llamada recursiva para agregar subcomponentes
                agregarSubcomponentesAPDF(document, tableComp, hijo, 1); // Nivel de indentación
                hijo = hijo.getSiguienteHermano();
            }
            document.add(tableComp);
        }
        document.add(new Paragraph("\n")); // Espacio entre equipos principales
    }

    // Método auxiliar recursivo para agregar subcomponentes a la tabla de componentes
    private void agregarSubcomponentesAPDF(Document document, Table table, NodoEquipo nodo, int nivel) {
        NodoEquipo hijo = nodo.getPrimerHijo();
        String indent = "  ".repeat(nivel); // Indentación visual
        while (hijo != null) {
            // Añadir con indentación
            table.addCell(indent + hijo.getId());
            table.addCell(indent + hijo.getDescripcion());
            // Llamada recursiva para los hijos de este hijo
            agregarSubcomponentesAPDF(document, table, hijo, nivel + 1);
            hijo = hijo.getSiguienteHermano();
        }
    }
}