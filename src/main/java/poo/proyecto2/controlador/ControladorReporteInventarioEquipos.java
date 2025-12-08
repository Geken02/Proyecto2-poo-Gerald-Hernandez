package poo.proyecto2.controlador;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.reportes.VentanaReporteInventarioEquipos;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ControladorReporteInventarioEquipos {

    private SistemaPrincipal modelo;
    private VentanaReporteInventarioEquipos vista; // Referencia a la vista

    public ControladorReporteInventarioEquipos(SistemaPrincipal modelo, VentanaReporteInventarioEquipos vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    public void generarPDF() {
        String destino = "reportes/reporte_inventario_equipos.pdf"; // Nombre del archivo de salida
        new java.io.File("reportes").mkdirs(); // Crear carpeta si no existe

        try {
            PdfWriter writer = new PdfWriter(new FileOutputStream(destino));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Reporte de Inventario de Equipos").setFontSize(20).setBold());

            // Obtener la opción seleccionada desde la vista
            boolean esUnEquipo = vista.esUnEquipoSimpleSeleccionado() || vista.esUnEquipoConComponentesSeleccionado();
            boolean incluirComponentes = vista.esUnEquipoConComponentesSeleccionado() || vista.sonTodosEquiposConComponentesSeleccionados();

            if (esUnEquipo) {
                String idStr = vista.getTxtIdEquipo().getText().trim();
                if (idStr.isEmpty()) {
                    vista.mostrarMensaje("Por favor, ingrese el ID del equipo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    int idEquipo = Integer.parseInt(idStr);
                    NodoEquipo equipo = modelo.buscarEquipoPorId(idEquipo);
                    if (equipo == null) {
                        vista.mostrarMensaje("No se encontró un equipo con ID: " + idEquipo, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Generar reporte para un solo equipo
                    if (vista.esUnEquipoSimpleSeleccionado()) {
                        agregarEquipoSimpleAPDF(document, equipo);
                    } else { // vista.esUnEquipoConComponentesSeleccionado()
                        agregarEquipoConComponentesAPDF(document, equipo);
                    }
                } catch (NumberFormatException ex) {
                    vista.mostrarMensaje("Por favor, ingrese un ID de equipo válido (número entero).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else { // vista.sonTodosEquiposConComponentesSeleccionados()
                 // Generar reporte para todos los equipos
                 List<NodoEquipo> todosLosEquipos = modelo.obtenerTodosLosEquipos();
                 for (NodoEquipo equipo : todosLosEquipos) {
                     // Solo procesar raíces (porque los componentes se incluyen recursivamente)
                     if (equipo.getEquipoPrincipal() == 0) {
                         agregarEquipoConComponentesAPDF(document, equipo);
                     }
                 }
            }

            document.close();
            vista.mostrarMensaje("Reporte generado exitosamente en: " + destino, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (FileNotFoundException ex) {
            vista.mostrarMensaje("No se pudo crear el archivo PDF en la ubicación: " + destino + "\nError: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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