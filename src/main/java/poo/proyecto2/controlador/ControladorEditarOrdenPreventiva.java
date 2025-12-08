package poo.proyecto2.controlador;

import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.modelo.equipos.FallaEquipo; // Asegúrate del paquete correcto
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaEditarOrdenPreventiva; // Referencia a la vista

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ControladorEditarOrdenPreventiva {

    private SistemaPrincipal modelo;
    private VentanaEditarOrdenPreventiva vista; // Referencia a la vista
    private OrdenTrabajoPreventiva ordenAErEditar; // Referencia a la orden que se está gestionando

    public ControladorEditarOrdenPreventiva(SistemaPrincipal modelo, VentanaEditarOrdenPreventiva vista, OrdenTrabajoPreventiva ordenAErEditar) {
        this.modelo = modelo;
        this.vista = vista;
        this.ordenAErEditar = ordenAErEditar;
    }

    public void iniciarOrden() {
        // Validar campos requeridos
        if (vista.getTxtFechaInicioReal().getText().trim().isEmpty()) {
            vista.mostrarMensaje("Por favor, ingrese la fecha de inicio real.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaInicio;
        try {
            fechaInicio = LocalDate.parse(vista.getTxtFechaInicioReal().getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Por favor, ingrese una fecha de inicio válida en formato AAAA-MM-DD.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamar al sistema para iniciar la orden
        boolean iniciada = modelo.iniciarOrdenPreventiva(ordenAErEditar.getId(), fechaInicio);

        if (iniciada) {
            vista.mostrarMensaje("Orden ID " + ordenAErEditar.getId() + " iniciada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Actualizar el estado de la orden localmente
            ordenAErEditar.iniciar(fechaInicio); // Asumiendo que OrdenTrabajoPreventiva tiene este método
            // Actualizar la interfaz
            vista.getTxtEstado().setText(ordenAErEditar.getEstado().toString());
            vista.getTxtFechaInicioReal().setValue(fechaInicio);
            vista.actualizarInterfazSegunEstado(); // Deshabilita iniciar, habilita finalizar/cancelar
        } else {
            vista.mostrarMensaje("No se pudo iniciar la orden. Puede que ya esté iniciada, completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void finalizarOrden() {
        // Validar campos requeridos
        if (vista.getTxtFechaFinReal().getText().trim().isEmpty() ||
            vista.getSpnHorasTrabajo().getValue().equals(0.0f) ||
            vista.getTxtCostoManoObra().getText().trim().isEmpty() ||
            vista.getTxtCostoMateriales().getText().trim().isEmpty()) {
            vista.mostrarMensaje("Por favor, complete todos los campos obligatorios (marcados con *).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaFin;
        float horas;
        int costoMO, costoMat;
        String obsEjecucion;

        try {
            fechaFin = LocalDate.parse(vista.getTxtFechaFinReal().getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            horas = ((Number) vista.getSpnHorasTrabajo().getValue()).floatValue();
            costoMO = ((Number) vista.getTxtCostoManoObra().getValue()).intValue();
            costoMat = ((Number) vista.getTxtCostoMateriales().getValue()).intValue();
            obsEjecucion = vista.getTxtObservacionesEjecucion().getText().trim();
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Por favor, ingrese una fecha de finalización válida en formato AAAA-MM-DD.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("Por favor, ingrese valores numéricos válidos para Horas, Costo Mano de Obra y Costo Materiales.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- PROCESAR LAS FALLAS DE LA TABLA (DESDE LA VISTA) ---
        // Recorrer la tabla de fallas y crear una nueva lista de fallas encontradas
        // Este paso es opcional si la edición de fallas se maneja en otro lugar o si se usan las existentes
        DefaultTableModel modeloTablaFallas = vista.getModeloTablaFallas();
        List<poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.FallaEncontrada> nuevasFallas = new ArrayList<>();
        for (int i = 0; i < modeloTablaFallas.getRowCount(); i++) {
            // Suponiendo que la estructura de la tabla sea [Tipo, ID Falla, Desc, Causas, Acciones]
            String tipoFalla = (String) modeloTablaFallas.getValueAt(i, 0);
            if ("Encontrada".equals(tipoFalla)) { // O la etiqueta que uses para distinguir
                int idFalla = (Integer) modeloTablaFallas.getValueAt(i, 1);
                String descripcionFalla = (String) modeloTablaFallas.getValueAt(i, 2);
                String causas = (String) modeloTablaFallas.getValueAt(i, 3);
                String acciones = (String) modeloTablaFallas.getValueAt(i, 4);
                nuevasFallas.add(new poo.proyecto2.modelo.mantenimiento.OrdenTrabajo.FallaEncontrada(idFalla, descripcionFalla, causas, acciones));
            }
        }
        // --- FIN PROCESAR FALLAS ---

        // Llamar al sistema para finalizar la orden con las nuevas fallas
        boolean finalizada = modelo.finalizarOrdenPreventiva(ordenAErEditar.getId(), fechaFin, horas, costoMO, costoMat, obsEjecucion, nuevasFallas);

        if (finalizada) {
            vista.mostrarMensaje("Orden ID " + ordenAErEditar.getId() + " finalizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Actualizar el estado y datos de la orden localmente
            ordenAErEditar.finalizar(fechaFin, horas, costoMO, costoMat, obsEjecucion); // Asumiendo que OrdenTrabajoPreventiva tiene este método
            // Actualizar la interfaz
            vista.getTxtEstado().setText(ordenAErEditar.getEstado().toString());
            vista.getTxtFechaFinReal().setValue(fechaFin);
            vista.actualizarInterfazSegunEstado(); // Deshabilita iniciar/finalizar/cancelar
        } else {
            vista.mostrarMensaje("No se pudo finalizar la orden. Puede que ya esté completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cancelarOrden() {
        // Validar campos requeridos
        if (vista.getTxtFechaCancelacion().getText().trim().isEmpty() || vista.getTxtMotivoCancelacion().getText().trim().isEmpty()) {
            vista.mostrarMensaje("Por favor, complete todos los campos obligatorios (marcados con *).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate fechaCancelacion;
        String motivo = vista.getTxtMotivoCancelacion().getText().trim();

        try {
            fechaCancelacion = LocalDate.parse(vista.getTxtFechaCancelacion().getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Por favor, ingrese una fecha de cancelación válida en formato AAAA-MM-DD.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamar al sistema para cancelar la orden
        boolean cancelada = modelo.cancelarOrdenPreventiva(ordenAErEditar.getId(), fechaCancelacion, motivo);

        if (cancelada) {
            vista.mostrarMensaje("Orden ID " + ordenAErEditar.getId() + " cancelada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Actualizar el estado y datos de la orden localmente
            ordenAErEditar.cancelar(fechaCancelacion, motivo); // Asumiendo que OrdenTrabajoPreventiva tiene este método
            // Actualizar la interfaz
            vista.getTxtEstado().setText(ordenAErEditar.getEstado().toString());
            vista.getTxtFechaCancelacion().setValue(fechaCancelacion);
            vista.getTxtMotivoCancelacion().setText(motivo);
            vista.actualizarInterfazSegunEstado(); // Deshabilita iniciar/finalizar/cancelar
        } else {
            vista.mostrarMensaje("No se pudo cancelar la orden. Puede que ya esté completada o cancelada, o hubo un error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cancelarVentana() {
        vista.cerrarVentana();
    }
}