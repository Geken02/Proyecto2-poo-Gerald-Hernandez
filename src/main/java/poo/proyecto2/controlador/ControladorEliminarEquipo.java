package poo.proyecto2.controlador;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.mantenimiento.OrdenTrabajo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaEliminarEquipo; // Referencia a la vista

import javax.swing.*;
import java.util.List;

public class ControladorEliminarEquipo {

    private SistemaPrincipal modelo;
    private VentanaEliminarEquipo vista; // Referencia a la vista
    private Runnable despuesDeEliminar; // Callback para actualizar la ventana padre

    public ControladorEliminarEquipo(SistemaPrincipal modelo, VentanaEliminarEquipo vista, Runnable despuesDeEliminar) {
        this.modelo = modelo;
        this.vista = vista;
        this.despuesDeEliminar = despuesDeEliminar;
    }

    public void buscarEquipo() {
        String idStr = vista.getTxtIdEquipo().getText().trim();
        if (!idStr.isEmpty()) {
            try {
                int idEquipo = Integer.parseInt(idStr);
                NodoEquipo equipo = modelo.buscarEquipoPorId(idEquipo);
                if (equipo != null) {
                    // Mostrar detalles del equipo encontrado
                    vista.mostrarDetalleEquipo(equipo); // <-- Llama al método de la vista
                    vista.habilitarBotonEliminar(true); // <-- Llama al método de la vista
                } else {
                    vista.mostrarMensaje("Equipo con ID " + idEquipo + " no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                    vista.limpiarDetalle(); // <-- Llama al método de la vista
                    vista.habilitarBotonEliminar(false); // <-- Llama al método de la vista
                }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("Por favor, ingrese un ID de equipo válido (número entero).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        } else {
             vista.mostrarMensaje("Por favor, ingrese un ID de equipo para buscar.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void eliminarEquipo() {
        String idStr = vista.getTxtIdEquipo().getText().trim();
        if (!idStr.isEmpty()) {
            try {
                int idEquipo = Integer.parseInt(idStr);

                // --- VERIFICACIÓN DE ÓRDENES ASOCIADAS ---
                List<OrdenTrabajo> ordenesAsociadas = modelo.obtenerOrdenesPorEquipo(idEquipo);
                if (!ordenesAsociadas.isEmpty()) {
                    StringBuilder mensajeErrores = new StringBuilder("No se puede eliminar el equipo con ID ").append(idEquipo).append(".\nTiene las siguientes órdenes de trabajo asociadas:\n");
                    for (OrdenTrabajo orden : ordenesAsociadas) {
                        String tipoOrden = (orden instanceof poo.proyecto2.modelo.mantenimiento.OrdenTrabajoPreventiva) ? "Preventiva" : "Correctiva";
                        mensajeErrores.append("- Orden #").append(orden.getId()).append(" (").append(tipoOrden).append(") - Estado: ").append(orden.getEstado()).append("\n");
                    }
                    vista.mostrarMensaje(mensajeErrores.toString(), "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                    return; // Salir del método, no eliminar
                }
                // --- FIN VERIFICACIÓN ---

                int respuesta = JOptionPane.showConfirmDialog(
                        vista,
                        "¿Está seguro de que desea eliminar el equipo con ID " + idEquipo + "?\nEsta acción no se puede deshacer.",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (respuesta == JOptionPane.YES_OPTION) {
                    boolean eliminado = modelo.eliminarEquipo(idEquipo);
                    if (eliminado) {
                        vista.mostrarMensaje("Equipo con ID " + idEquipo + " eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        vista.limpiarFormulario(); // <-- Llama al método de la vista
                        vista.habilitarBotonEliminar(false); // <-- Llama al método de la vista

                        // --- LLAMAR AL CALLBACK ---
                        if (despuesDeEliminar != null) {
                            despuesDeEliminar.run(); // Ejecuta el método pasado como Runnable
                        }
                        // --- FIN CALLBACK ---

                        // Opcional: Cerrar la ventana después de eliminar
                        // vista.cerrarVentana(); // Si quieres cerrarla
                    } else {
                        vista.mostrarMensaje("No se pudo eliminar el equipo. Puede que no exista o que ocurrió un error inesperado.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                // Si la respuesta es NO, no se hace nada.
            } catch (NumberFormatException ex) {
                 vista.mostrarMensaje("ID inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}