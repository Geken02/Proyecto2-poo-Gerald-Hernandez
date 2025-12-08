package poo.proyecto2.controlador;

import poo.proyecto2.modelo.mantenimiento.TareaMantenimiento;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaAdministrarTareas; // Referencia a la vista

import javax.swing.JOptionPane;
import java.util.List;

public class ControladorAdministrarTareas {

    private SistemaPrincipal modelo;
    private VentanaAdministrarTareas vista; // Referencia a la vista

    public ControladorAdministrarTareas(SistemaPrincipal modelo, VentanaAdministrarTareas vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    public void agregarTarea() {
        String descripcion = JOptionPane.showInputDialog(vista, "Ingrese la descripción de la nueva tarea:");
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            // Llamar al sistema para crear la tarea
            TareaMantenimiento nuevaTarea = modelo.crearTareaMaestra(descripcion.trim());
            if (nuevaTarea != null) { // Asumiendo que crearTareaMaestra devuelve la tarea creada o null si falla
                JOptionPane.showMessageDialog(vista, "Tarea '" + descripcion.trim() + "' agregada exitosamente con ID: " + nuevaTarea.getId() + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.actualizarTabla(); // Actualiza la vista para mostrar la nueva tarea
            } else {
                // Manejar posibles errores de creación (aunque crearTareaMaestra debería lanzar una excepción si falla)
                JOptionPane.showMessageDialog(vista, "No se pudo agregar la tarea. Ocurrió un error inesperado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Si el usuario cancela (descripcion == null), no se hace nada.
    }

    public void eliminarTarea() {
        int filaSeleccionada = vista.getTablaTareas().getSelectedRow(); // Obtener fila desde la vista
        if (filaSeleccionada >= 0) {
            int idTarea = (int) vista.getModeloTabla().getValueAt(filaSeleccionada, 0); // Obtener ID desde la vista
            int respuesta = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Está seguro de que desea eliminar la tarea con ID " + idTarea + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION
            );
            if (respuesta == JOptionPane.YES_OPTION) {
                // *** VERIFICACIÓN DE USO (como en eliminar equipo) ***
                // Buscar si alguna fase de algún programa la usa
                boolean enUso = modelo.obtenerTodosLosProgramas().stream() // Asumiendo este método en SistemaPrincipal
                        .anyMatch(prog -> prog.getFases().stream()
                                .anyMatch(fase -> fase.getIdsTareasMaestras().contains(idTarea)));

                if (enUso) {
                    JOptionPane.showMessageDialog(vista, "No se puede eliminar la tarea con ID " + idTarea + ". Está asociada a una o más fases de programas de mantenimiento.", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                    return; // Salir del evento, no eliminar
                }
                // Buscar si alguna orden de trabajo la usa (en órdenes preventivas)
                boolean enUsoEnOrden = modelo.obtenerTodasLasOrdenes().stream() // Asumiendo este método en SistemaPrincipal
                        .filter(orden -> orden instanceof poo.proyecto2.modelo.mantenimiento.OrdenTrabajoPreventiva) // Importar OrdenTrabajoPreventiva
                        .anyMatch(orden -> ((poo.proyecto2.modelo.mantenimiento.OrdenTrabajoPreventiva) orden).getTareas().stream()
                                .anyMatch(tarea -> tarea.getId() == idTarea));

                if (enUsoEnOrden) {
                    JOptionPane.showMessageDialog(vista, "No se puede eliminar la tarea con ID " + idTarea + ". Está asociada a una o más órdenes de trabajo preventivas.", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                    return; // Salir del evento, no eliminar
                }
                // *** FIN VERIFICACIÓN ***

                // Si no está en uso, mostrar el mensaje de que no está permitido (como en la vista original)
                JOptionPane.showMessageDialog(vista, "La eliminación de tareas maestras no está permitida para mantener la integridad de los datos históricos (fases, órdenes).", "Advertencia", JOptionPane.WARNING_MESSAGE);
                // Si decides implementar la eliminación (con todas las verificaciones de integridad), aquí llamarías a
                // modelo.eliminarTareaMaestra(idTarea) y luego vista.actualizarTabla().
            }
            // Si la respuesta es NO, no se hace nada.
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione una tarea de la lista para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Método para cargar las tareas al inicio (si la vista lo necesita)
    public void cargarTareasIniciales() {
        vista.actualizarTabla();
    }
}