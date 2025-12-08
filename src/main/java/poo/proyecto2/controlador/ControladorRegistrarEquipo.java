package poo.proyecto2.controlador;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.EstadoEquipo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaRegistrarEquipo; // <-- Asegúrate que este import sea correcto

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ControladorRegistrarEquipo {

    private SistemaPrincipal modelo;
    private VentanaRegistrarEquipo vista; // Referencia a la vista

    public ControladorRegistrarEquipo(SistemaPrincipal modelo, VentanaRegistrarEquipo vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    public void registrarEquipoDesdeVista() {
        // 1. Validar campos requeridos (marcados con *) - Omitimos fechas aquí, se validan en el parseo
        // Importante: Validar el getText() del JTextField txtEquipoPrincipal
        if (vista.getTxtEquipoPrincipal().getText().trim().isEmpty() || // <-- getText() para JTextField
            vista.getTxtDescripcion().getText().trim().isEmpty() ||
            vista.getTxtTipo().getText().trim().isEmpty() ||
            vista.getTxtUbicacion().getText().trim().isEmpty() ||
            vista.getTxtFabricante().getText().trim().isEmpty() ||
            vista.getTxtSerie().getText().trim().isEmpty() ||
            // Para JTextField numéricos, validamos el texto también por si acaso
            vista.getTxtMesesVidaUtil().getText().trim().isEmpty() || // <-- getText() para validar si está vacío
            vista.getTxtCostoInicial().getText().trim().isEmpty()) { // <-- getText() para validar si está vacío

            vista.mostrarMensaje("Por favor, complete todos los campos obligatorios (marcados con *).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Obtener y convertir datos numéricos y fechas
        int idPadre;
        LocalDate fechaAdq, fechaPuestaEnServ;
        int mesesVidaUtil;
        double costoInicial;

        try {
            // --- Conversión del ID del equipo principal (desde JTextField) ---
            String equipoPrincipalStr = vista.getTxtEquipoPrincipal().getText().trim(); // <-- getText() para JTextField
            idPadre = Integer.parseInt(equipoPrincipalStr); // <-- Parseo de String a int
            if (idPadre < 0) {
                 vista.mostrarMensaje("El ID del Equipo Principal no puede ser negativo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            // --- Conversión de fechas ---
            String fechaAdqStr = vista.getTxtFechaAdquisicion().getText().trim(); // <-- getText() para JTextField de fecha
            String fechaPuestaEnServStr = vista.getTxtFechaPuestaEnServicio().getText().trim(); // <-- getText() para JTextField de fecha
            fechaAdq = LocalDate.parse(fechaAdqStr, DateTimeFormatter.ISO_LOCAL_DATE); // Parsear con formato ISO
            fechaPuestaEnServ = LocalDate.parse(fechaPuestaEnServStr, DateTimeFormatter.ISO_LOCAL_DATE); // Parsear con formato ISO
            if (fechaPuestaEnServ.isBefore(fechaAdq)) {
                vista.mostrarMensaje("La fecha de puesta en servicio debe ser igual o posterior a la fecha de adquisición.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // --- Conversión de meses de vida útil (desde JTextField) ---
            String mesesVidaUtilStr = vista.getTxtMesesVidaUtil().getText().trim(); // <-- getText() para JTextField numérico
            mesesVidaUtil = Integer.parseInt(mesesVidaUtilStr); // <-- Parseo de String a int
            if (mesesVidaUtil <= 0) {
                vista.mostrarMensaje("Los meses de vida útil deben ser un número positivo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // --- Conversión de costo inicial (desde JTextField) ---
            String costoInicialStr = vista.getTxtCostoInicial().getText().trim(); // <-- getText() para JTextField numérico
            costoInicial = Double.parseDouble(costoInicialStr); // <-- Parseo de String a double
            if (costoInicial <= 0) {
                vista.mostrarMensaje("El costo inicial debe ser un número positivo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("Por favor, ingrese valores numéricos válidos para ID del Equipo Principal, Meses Vida Útil y Costo Inicial.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            System.err.println("DEBUG CR: Error de conversión numérica: " + ex.getMessage()); // DEBUG
            return;
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Por favor, ingrese fechas válidas en formato AAAA-MM-DD.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            System.err.println("DEBUG CR: Error de conversión de fecha: " + ex.getMessage()); // DEBUG
            return;
        }

        // 3. Obtener otros datos del formulario (String, Enum, TextArea)
        String descripcion = vista.getTxtDescripcion().getText().trim();
        String tipo = vista.getTxtTipo().getText().trim();
        String ubicacion = vista.getTxtUbicacion().getText().trim();
        String fabricante = vista.getTxtFabricante().getText().trim();
        String serie = vista.getTxtSerie().getText().trim();
        EstadoEquipo estado = (EstadoEquipo) vista.getCmbEstado().getSelectedItem(); // <--.getSelectedItem() para JComboBox
        String especTecnicas = vista.getTxtEspecTecnicas().getText().trim(); // <-- getText() para JTextArea
        String infoGarantia = vista.getTxtInfoGarantia().getText().trim(); // <-- getText() para JTextArea

        // 4. Llamar al modelo para crear el equipo
        try {
            NodoEquipo nuevoEquipo = modelo.crearEquipo(idPadre, descripcion, tipo, ubicacion,
                    fabricante, serie, fechaAdq, fechaPuestaEnServ, mesesVidaUtil,
                    estado, costoInicial);

            // 5. Asignar campos opcionales
            nuevoEquipo.setEspecificacionesTecnicas(especTecnicas.isEmpty() ? null : especTecnicas);
            nuevoEquipo.setInformacionGarantia(infoGarantia.isEmpty() ? null : infoGarantia);

            // 6. Mostrar mensaje de éxito, guardar y limpiar la vista
            vista.mostrarMensaje("Equipo '" + descripcion + "' registrado exitosamente con ID: " + nuevoEquipo.getId(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            vista.cerrarVentana(); // Opcional: Cerrar la ventana después de registrar

        } catch (IllegalArgumentException ex) {
            // Manejar errores específicos del sistema (por ejemplo, equipo padre no encontrado)
             vista.mostrarMensaje("Error al registrar el equipo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             System.err.println("DEBUG CR: Error de argumento al crear equipo: " + ex.getMessage()); // DEBUG
        } catch (Exception ex) {
            // Manejar otros errores inesperados
             vista.mostrarMensaje("Ocurrió un error inesperado al registrar el equipo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             System.err.println("DEBUG CR: Error inesperado al crear equipo: " + ex.getMessage()); // DEBUG
             ex.printStackTrace();
        }
    }
}