package poo.proyecto2.controlador;

import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.modelo.equipos.FallaEquipo; // Asegúrate del paquete correcto
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaFinalizarOrdenPreventiva; // Referencia a la vista

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ControladorFinalizarOrdenPreventiva {

    private SistemaPrincipal modelo;
    private VentanaFinalizarOrdenPreventiva vista; // Referencia a la vista
    private OrdenTrabajoPreventiva ordenActual; // Referencia a la orden que se está gestionando

    public ControladorFinalizarOrdenPreventiva(SistemaPrincipal modelo, VentanaFinalizarOrdenPreventiva vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.ordenActual = null;
    }

    public void buscarOrden() {
        String idStr = vista.getTxtIdOrden().getText().trim();
        if (!idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                poo.proyecto2.modelo.mantenimiento.OrdenTrabajo orden = modelo.buscarOrdenPorId(id);
                if (orden != null) {
                    if (orden instanceof OrdenTrabajoPreventiva) {
                        OrdenTrabajoPreventiva ordenPrev = (OrdenTrabajoPreventiva) orden;
                        // Permitir finalizar si está en PENDIENTE o EN_PROGRESO
                        if (ordenPrev.getEstado() == poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.EstadoOrden.PENDIENTE ||
                            ordenPrev.getEstado() == poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.EstadoOrden.EN_PROGRESO) {

                            vista.mostrarInfoOrden(" (Orden ID: " + orden.getId() + " - Estado: " + orden.getEstado() + " - Equipo ID: " + orden.getIdEquipo() + ")");
                            // Guardar referencia temporal
                            this.ordenActual = ordenPrev;
                            // Limpiar tabla de fallas y cargar las existentes (si las hay)
                            vista.limpiarTablaFallas();
                            for (poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.FallaEncontrada falla : ordenPrev.getFallasEncontradas()) {
                                Object[] fila = {falla.getIdFalla(), falla.getDescripcionFalla(), falla.getCausas(), falla.getAccionesTomadas()};
                                vista.getModeloTablaFallas().addRow(fila);
                            }
                            // Habilitar campos de finalización
                            vista.habilitarCamposFinalizar(true);
                        } else {
                            vista.mostrarMensaje("La orden ID " + id + " no está en estado PENDIENTE ni EN_PROGRESO. No se puede finalizar.", "Error", JOptionPane.ERROR_MESSAGE);
                            vista.mostrarInfoOrden(" (Orden no válida para finalizar)");
                            vista.limpiarCamposDespuesDeBuscar();
                        }
                    } else {
                        vista.mostrarMensaje("El ID " + id + " pertenece a una orden de tipo incorrecto (no Preventiva).", "Error", JOptionPane.ERROR_MESSAGE);
                        vista.mostrarInfoOrden(" (Orden no es Preventiva)");
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

    public void agregarFalla() {
        if (ordenActual == null) {
            vista.mostrarMensaje("Por favor, busque una orden primero.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        vista.abrirVentanaAgregarFalla(); // Llama al método de la vista para abrir el diálogo
    }

    public void eliminarFalla() {
        vista.eliminarFallaSeleccionadaDeTabla(); // Llama al método de la vista para manejar la eliminación
    }

    public void finalizarOrden() {
        if (ordenActual == null) {
            vista.mostrarMensaje("No hay una orden seleccionada para finalizar.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar campos requeridos
        if (vista.getTxtFechaFin().getText().trim().isEmpty() ||
            vista.getSpnHorasTrabajo().getValue().equals(0.0f) ||
            vista.getTxtCostoManoObra().getText().trim().isEmpty() ||
            vista.getTxtCostoMateriales().getText().trim().isEmpty()) {
            vista.mostrarMensaje("Por favor, complete todos los campos obligatorios (marcados con *).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaFin;
        float horas;
        int costoMO, costoMat;
        String observaciones;

        try {
            fechaFin = LocalDate.parse(vista.getTxtFechaFin().getText().trim());
            horas = ((Number) vista.getSpnHorasTrabajo().getValue()).floatValue();
            costoMO = ((Number) vista.getTxtCostoManoObra().getValue()).intValue();
            costoMat = ((Number) vista.getTxtCostoMateriales().getValue()).intValue();
            observaciones = vista.getTxtObservaciones().getText().trim();
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Por favor, ingrese una fecha de finalización válida en formato AAAA-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("Por favor, ingrese valores numéricos válidos para Horas, Costo Mano de Obra y Costo Materiales.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- PROCESAR LAS FALLAS DE LA TABLA (DESDE LA VISTA) ---
        DefaultTableModel modeloTablaFallas = vista.getModeloTablaFallas();
        List<poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.FallaEncontrada> nuevasFallas = new ArrayList<>();
        for (int i = 0; i < modeloTablaFallas.getRowCount(); i++) {
            int idFalla = (Integer) modeloTablaFallas.getValueAt(i, 0);
            String descripcionFalla = (String) modeloTablaFallas.getValueAt(i, 1);
            String causas = (String) modeloTablaFallas.getValueAt(i, 2);
            String acciones = (String) modeloTablaFallas.getValueAt(i, 3);
            nuevasFallas.add(new poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.FallaEncontrada(idFalla, descripcionFalla, causas, acciones));
        }
        // --- FIN PROCESAR FALLAS ---

        // Llamar al sistema para finalizar la orden con las nuevas fallas
        boolean finalizada = modelo.finalizarOrdenPreventiva(ordenActual.getId(), fechaFin, horas, costoMO, costoMat, observaciones, nuevasFallas);

        if (finalizada) {
            vista.mostrarMensaje("Orden ID " + ordenActual.getId() + " finalizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Limpiar formulario y deshabilitar campos en la vista
            vista.limpiarCamposDespuesDeBuscar();
            // Limpiar la referencia temporal
            this.ordenActual = null;
            // Opcional: Cerrar la ventana
            // vista.cerrarVentana();
        } else {
            vista.mostrarMensaje("No se pudo finalizar la orden. Puede que ya esté completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cancelar() {
        vista.cerrarVentana();
    }
}