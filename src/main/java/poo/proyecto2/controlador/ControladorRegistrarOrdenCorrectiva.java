package poo.proyecto2.controlador;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaRegistrarOrdenCorrectiva; // Referencia a la vista

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ControladorRegistrarOrdenCorrectiva {

    private SistemaPrincipal modelo;
    private VentanaRegistrarOrdenCorrectiva vista; // Referencia a la vista
    private List<OrdenTrabajo.FallaReportada> fallasReportadasTemp;

    public ControladorRegistrarOrdenCorrectiva(SistemaPrincipal modelo, VentanaRegistrarOrdenCorrectiva vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.fallasReportadasTemp = vista.getFallasReportadasTemp(); // <-- OJO: Esto no es ideal, mejor pasarlo al constructor del controlador o manejarlo internamente
        // La mejor forma es inicializarla aquí y manejarla internamente
        this.fallasReportadasTemp = new java.util.ArrayList<>();
    }

    public void buscarEquipo() {
        String idStr = vista.getTxtIdEquipo().getText().trim();
        if (!idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                NodoEquipo equipo = modelo.buscarEquipoPorId(id);
                if (equipo != null) {
                    vista.mostrarInfoEquipo(" (" + equipo.getDescripcion() + " - ID: " + equipo.getId() + ")");
                } else {
                    vista.mostrarMensaje("No se encontró un equipo con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                    vista.limpiarInfoEquipo();
                }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("Por favor, ingrese un ID de equipo válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            vista.mostrarMensaje("Por favor, ingrese un ID de equipo.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void agregarFallaReportada() {
        vista.abrirVentanaAgregarFallaRep(); // Llama a la vista para abrir el diálogo
    }

    public void eliminarFallaReportada() {
        vista.eliminarFallaReportadaSeleccionada(); // Llama a la vista para manejar la eliminación
    }

    public void registrarOrden() {
        String idEquipoStr = vista.getTxtIdEquipo().getText().trim();
        if (idEquipoStr.isEmpty()) {
             vista.mostrarMensaje("Por favor, busque y seleccione un equipo.", "Error", JOptionPane.WARNING_MESSAGE);
             return;
        }

        try {
            int idEquipo = Integer.parseInt(idEquipoStr);
            NodoEquipo equipo = modelo.buscarEquipoPorId(idEquipo);
            if (equipo == null) {
                vista.mostrarMensaje("El equipo seleccionado ya no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar fechas
            LocalDate fechaOrden, fechaEjecucion;
            try {
                fechaOrden = LocalDate.parse(vista.getTxtFechaOrden().getText().trim());
                fechaEjecucion = LocalDate.parse(vista.getTxtFechaEjecucion().getText().trim());
                if (fechaEjecucion.isBefore(fechaOrden)) {
                    vista.mostrarMensaje("La fecha de ejecución debe ser igual o posterior a la fecha de la orden.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (DateTimeParseException ex) {
                vista.mostrarMensaje("Por favor, ingrese fechas válidas en formato AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String observaciones = vista.getTxtObservaciones().getText().trim();

            // Crear la nueva orden correctiva
            int nuevoIdOrden = modelo.obtenerSiguienteIdOrden();
            OrdenTrabajoCorrectiva nuevaOrden = new OrdenTrabajoCorrectiva(nuevoIdOrden, idEquipo, fechaOrden, fechaEjecucion);
            nuevaOrden.setObservaciones(observaciones);

            // Asignar las fallas reportadas temporales a la orden
            // Usamos la lista interna del controlador, no la de la vista (para evitar problemas de sincronización si la vista la reinicializa)
            for (OrdenTrabajo.FallaReportada fr : fallasReportadasTemp) {
                nuevaOrden.registrarFallaReportada(fr.getCausas(), fr.getAccionesTomadas());
            }

            // Llamar al sistema para registrar y guardar la nueva orden
            boolean registrada = modelo.registrarOrdenCorrectiva(nuevaOrden); // Asumiendo que este método ahora exista y guarde

            if (registrada) {
                vista.mostrarMensaje("Orden correctiva ID " + nuevaOrden.getId() + " registrada exitosamente para el equipo ID " + idEquipo + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Limpiar formulario y listas temporales en la vista
                vista.limpiarFormulario();
                // Limpiar lista temporal interna del controlador
                fallasReportadasTemp.clear();
                // Opcional: Cerrar ventana
                // vista.cerrarVentana();
            } else {
                vista.mostrarMensaje("No se pudo registrar la orden en el sistema. Puede que ocurrió un error inesperado.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("ID de equipo inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cancelar() {
        vista.cerrarVentana();
    }
}