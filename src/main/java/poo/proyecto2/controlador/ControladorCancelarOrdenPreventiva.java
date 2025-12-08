package poo.proyecto2.controlador;

import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaCancelarOrdenPreventiva; 

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ControladorCancelarOrdenPreventiva {

    private SistemaPrincipal modelo;
    private VentanaCancelarOrdenPreventiva vista; 
    private OrdenTrabajoPreventiva ordenActual; 

    public ControladorCancelarOrdenPreventiva(SistemaPrincipal modelo, VentanaCancelarOrdenPreventiva vista) {
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
                    if (orden instanceof OrdenTrabajoPreventiva) {
                        OrdenTrabajoPreventiva ordenPrev = (OrdenTrabajoPreventiva) orden;
                        // Permitir cancelar si está en PENDIENTE o EN_PROGRESO
                        if (ordenPrev.getEstado() == poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.EstadoOrden.PENDIENTE || ordenPrev.getEstado() == poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.EstadoOrden.EN_PROGRESO) {
                            vista.mostrarInfoOrden(" (Orden ID: " + orden.getId() + " - Estado: " + orden.getEstado() + " - Equipo ID: " + orden.getIdEquipo() + ")");
                            // Guardar referencia temporal
                            this.ordenActual = ordenPrev;
                            // Habilitar campos de cancelación
                            vista.habilitarCamposCancelar(true);
                        } else {
                            vista.mostrarMensaje("La orden ID " + id + " no está en estado PENDIENTE ni EN_PROGRESO. No se puede cancelar.", "Error", JOptionPane.ERROR_MESSAGE);
                            vista.mostrarInfoOrden(" (Orden no válida para cancelar)");
                            vista.habilitarCamposCancelar(false);
                            this.ordenActual = null; // Limpiar referencia
                        }
                    } else {
                        vista.mostrarMensaje("El ID " + id + " pertenece a una orden de tipo incorrecto (no Preventiva).", "Error", JOptionPane.ERROR_MESSAGE);
                        vista.mostrarInfoOrden(" (Orden no es Preventiva)");
                        vista.habilitarCamposCancelar(false);
                        this.ordenActual = null; // Limpiar referencia
                    }
                } else {
                    vista.mostrarMensaje("No se encontró una orden con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                    vista.mostrarInfoOrden(" (Orden no encontrada)");
                    vista.habilitarCamposCancelar(false);
                    this.ordenActual = null; // Limpiar referencia
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
        boolean cancelada = modelo.cancelarOrdenPreventiva(ordenActual.getId(), fechaCancelacion, motivo);

        if (cancelada) {
            vista.mostrarMensaje("Orden ID " + ordenActual.getId() + " cancelada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Limpiar formulario y deshabilitar campos en la vista
            vista.limpiarFormulario();
            vista.habilitarCamposCancelar(false);
            this.ordenActual = null; // Limpiar referencia
            // Opcional: Cerrar la ventana
            // vista.cerrarVentana();
        } else {
            vista.mostrarMensaje("No se pudo cancelar la orden. Puede que ya esté completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cancelarVentana() {
        vista.cerrarVentana();
    }
}