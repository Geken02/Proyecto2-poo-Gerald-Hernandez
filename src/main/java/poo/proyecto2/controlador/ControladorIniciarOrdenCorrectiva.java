package poo.proyecto2.controlador;

import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaIniciarOrdenCorrectiva; // Referencia a la vista

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ControladorIniciarOrdenCorrectiva {

    private SistemaPrincipal modelo;
    private VentanaIniciarOrdenCorrectiva vista; // Referencia a la vista
    private OrdenTrabajoCorrectiva ordenActual; // Referencia a la orden que se está gestionando

    public ControladorIniciarOrdenCorrectiva(SistemaPrincipal modelo, VentanaIniciarOrdenCorrectiva vista) {
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
                        // Permitir iniciar si está en PENDIENTE
                        if (ordenCorr.getEstado() == poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.EstadoOrden.PENDIENTE) {
                            vista.mostrarInfoOrden(" (Orden ID: " + orden.getId() + " - Estado: " + orden.getEstado() + " - Equipo ID: " + orden.getIdEquipo() + ")");
                            // Guardar referencia temporal
                            this.ordenActual = ordenCorr;
                            // Habilitar campo de fecha y botón de iniciar
                            vista.habilitarCamposIniciar(true);
                        } else {
                            vista.mostrarMensaje("La orden ID " + id + " no está en estado PENDIENTE. No se puede iniciar.", "Error", JOptionPane.ERROR_MESSAGE);
                            vista.mostrarInfoOrden(" (Orden no válida para iniciar)");
                            vista.habilitarCamposIniciar(false);
                            this.ordenActual = null; // Limpiar referencia
                        }
                    } else {
                        vista.mostrarMensaje("El ID " + id + " pertenece a una orden de tipo incorrecto (no Correctiva).", "Error", JOptionPane.ERROR_MESSAGE);
                        vista.mostrarInfoOrden(" (Orden no es Correctiva)");
                        vista.habilitarCamposIniciar(false);
                        this.ordenActual = null; // Limpiar referencia
                    }
                } else {
                    vista.mostrarMensaje("No se encontró una orden con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                    vista.mostrarInfoOrden(" (Orden no encontrada)");
                    vista.habilitarCamposIniciar(false);
                    this.ordenActual = null; // Limpiar referencia
                }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("Por favor, ingrese un ID de orden válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            vista.mostrarMensaje("Por favor, ingrese un ID de orden.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void iniciarOrden() {
        if (ordenActual == null) {
            vista.mostrarMensaje("No hay una orden seleccionada para iniciar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar campos requeridos
        if (vista.getTxtFechaInicio().getText().trim().isEmpty()) {
            vista.mostrarMensaje("Por favor, complete todos los campos obligatorios (marcados con *).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaInicio;
        try {
            fechaInicio = LocalDate.parse(vista.getTxtFechaInicio().getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Por favor, ingrese una fecha de inicio válida en formato AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamar al sistema para iniciar la orden
        boolean iniciada = modelo.iniciarOrdenCorrectiva(ordenActual.getId(), fechaInicio); // <-- Usar método correctivo del modelo

        if (iniciada) {
            vista.mostrarMensaje("Orden ID " + ordenActual.getId() + " iniciada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Limpiar formulario y deshabilitar campos en la vista
            vista.limpiarFormulario();
            vista.habilitarCamposIniciar(false);
            this.ordenActual = null; // Limpiar referencia
            // Opcional: Cerrar ventana
            // vista.cerrarVentana();
        } else {
            vista.mostrarMensaje("No se pudo iniciar la orden. Puede que ya esté iniciada, completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cancelar() {
        vista.cerrarVentana();
    }
}