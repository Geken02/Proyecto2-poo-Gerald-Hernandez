package poo.proyecto2.controlador;

import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaCancelarOrdenCorrectiva; // Referencia a la vista

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ControladorCancelarOrdenCorrectiva {

    private SistemaPrincipal modelo;
    private VentanaCancelarOrdenCorrectiva vista; // Referencia a la vista
    private OrdenTrabajoCorrectiva ordenActual; // Referencia a la orden que se está gestionando

    public ControladorCancelarOrdenCorrectiva(SistemaPrincipal modelo, VentanaCancelarOrdenCorrectiva vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.ordenActual = null;
    }

    public void buscarOrden() {
        String idStr = vista.getTxtIdOrden().getText().trim();
        if (!idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                // Llamar al sistema para buscar la orden
                poo.proyecto2.modelo.mantenimiento.OrdenTrabajo orden = modelo.buscarOrdenPorId(id);
                if (orden != null) {
                    if (orden instanceof OrdenTrabajoCorrectiva) { // <-- Verificar tipo correctivo
                        OrdenTrabajoCorrectiva ordenCorr = (OrdenTrabajoCorrectiva) orden;
                        // Permitir cancelar si está en PENDIENTE o EN_PROGRESO
                        if (ordenCorr.getEstado() == poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.EstadoOrden.PENDIENTE || ordenCorr.getEstado() == poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.EstadoOrden.EN_PROGRESO) {
                            vista.mostrarInfoOrden(" (Orden ID: " + orden.getId() + " - Estado: " + orden.getEstado() + " - Equipo ID: " + orden.getIdEquipo() + ")");
                            // Guardar referencia temporal
                            this.ordenActual = ordenCorr;
                            // Habilitar campos de cancelación
                            vista.habilitarCamposCancelar(true);
                        } else {
                            vista.mostrarMensaje("La orden ID " + id + " no está en estado PENDIENTE ni EN_PROGRESO. No se puede cancelar.", "Error", JOptionPane.ERROR_MESSAGE);
                            vista.mostrarInfoOrden(" (Orden no válida para cancelar)");
                            vista.limpiarCamposDespuesDeBuscar();
                        }
                    } else {
                        vista.mostrarMensaje("El ID " + id + " pertenece a una orden de tipo incorrecto (no Correctiva).", "Error", JOptionPane.ERROR_MESSAGE);
                        vista.mostrarInfoOrden(" (Orden no es Correctiva)");
                        vista.limpiarCamposDespuesDeBuscar();
                    }
                } else {
                    vista.mostrarMensaje("No se encontró una orden con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                    vista.mostrarInfoOrden(" (Orden no encontrada)");
                    vista.limpiarCamposDespuesDeBuscar();
                }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("Por favor, ingrese un ID de orden válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            vista.mostrarMensaje("Por favor, ingrese un ID de orden.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void cancelarOrden() {
        if (ordenActual == null) {
            vista.mostrarMensaje("No hay una orden seleccionada para cancelar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar campos requeridos
        if (vista.getTxtFechaCancelacion().getText().trim().isEmpty() || vista.getTxtMotivoCancelacion().getText().trim().isEmpty()) {
            vista.mostrarMensaje("Por favor, complete todos los campos obligatorios (marcados con *).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaCancelacion;
        String motivo = vista.getTxtMotivoCancelacion().getText().trim();
        try {
            fechaCancelacion = LocalDate.parse(vista.getTxtFechaCancelacion().getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Por favor, ingrese una fecha de cancelación válida en formato AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamar al sistema para cancelar la orden
        boolean cancelada = modelo.cancelarOrdenCorrectiva(ordenActual.getId(), fechaCancelacion, motivo); // <-- Usar método correctivo del modelo

        if (cancelada) {
            vista.mostrarMensaje("Orden ID " + ordenActual.getId() + " cancelada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Limpiar formulario y deshabilitar campos en la vista
            vista.limpiarCamposDespuesDeBuscar();
            // Limpiar referencia temporal
            this.ordenActual = null;
            // Opcional: Cerrar ventana
            // vista.cerrarVentana();
        } else {
            vista.mostrarMensaje("No se pudo cancelar la orden. Puede que ya esté completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cancelarVentana() {
        vista.cerrarVentana();
    }
}