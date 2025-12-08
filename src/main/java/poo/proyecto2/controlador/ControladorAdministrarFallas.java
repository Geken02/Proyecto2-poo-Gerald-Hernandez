package poo.proyecto2.controlador;

import poo.proyecto2.modelo.equipos.FallaEquipo; // Asegúrate del paquete correcto
import poo.proyecto2.modelo.mantenimiento.OrdenTrabajo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaAdministrarFallas; // Referencia a la vista

import javax.swing.JOptionPane;
import java.util.List;

public class ControladorAdministrarFallas {

    private SistemaPrincipal modelo;
    private VentanaAdministrarFallas vista; // Referencia a la vista

    public ControladorAdministrarFallas(SistemaPrincipal modelo, VentanaAdministrarFallas vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    public void agregarFalla() {
        String descripcion = JOptionPane.showInputDialog(vista, "Ingrese la descripción de la nueva falla:");
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            // Llamar al sistema para crear la falla
            FallaEquipo nuevaFalla = modelo.crearFallaMaestra(descripcion.trim());
            if (nuevaFalla != null) { // Asumiendo que crearFallaMaestra devuelve la nueva falla o null si falla
                JOptionPane.showMessageDialog(vista, "Falla '" + descripcion.trim() + "' agregada exitosamente con ID: " + nuevaFalla.getId() + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.actualizarTabla(); // Refresca la vista
            } else {
                // Manejar posibles errores de creación (aunque crearFallaMaestra debería lanzar una excepción si falla)
                JOptionPane.showMessageDialog(vista, "No se pudo agregar la falla. Ocurrió un error inesperado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Si el usuario cancela (descripcion == null), no se hace nada.
    }

    public void eliminarFalla() {
        int filaSeleccionada = vista.getTablaFallas().getSelectedRow(); // Obtener fila desde la vista
        if (filaSeleccionada >= 0) {
            int idFalla = (int) vista.getModeloTabla().getValueAt(filaSeleccionada, 0); // Obtener ID desde la vista
            int respuesta = JOptionPane.showConfirmDialog(
                    vista,
                    "¿Está seguro de que desea eliminar la falla con ID " + idFalla + "?\nEsta acción no se puede deshacer.",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (respuesta == JOptionPane.YES_OPTION) {
                // *** VERIFICACIÓN DE USO ***
                // Buscar si alguna orden de trabajo (preventiva o correctiva) contiene esta falla en 'fallasEncontradas'
                boolean enUso = modelo.obtenerTodasLasOrdenes().stream() // Asumiendo este método en SistemaPrincipal
                        .anyMatch(orden -> orden.getFallasEncontradas().stream()
                                .anyMatch(falla -> falla.getIdFalla() == idFalla));

                if (enUso) {
                    JOptionPane.showMessageDialog(vista, "No se puede eliminar la falla con ID " + idFalla + ". Está asociada a una o más órdenes de trabajo.", "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                    return; // Salir del evento, no eliminar
                }
                // *** FIN VERIFICACIÓN ***

                // Si no está en uso, mostrar el mensaje de que no está permitido (como en la vista original)
                JOptionPane.showMessageDialog(vista, "La eliminación de fallas maestras no está permitida para mantener la integridad de los datos históricos (órdenes de trabajo).", "Advertencia", JOptionPane.WARNING_MESSAGE);
                // Si decides implementar la eliminación (con todas las verificaciones de integridad), aquí llamarías a
                // boolean eliminada = modelo.eliminarFallaMaestra(idFalla); // <-- Método que debes implementar en SistemaPrincipal
                // if (eliminada) {
                //     JOptionPane.showMessageDialog(vista, "Falla con ID " + idFalla + " eliminada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                //     vista.actualizarTabla(); // Refresca la vista
                // } else {
                //     JOptionPane.showMessageDialog(vista, "No se pudo eliminar la falla. Puede que ya no exista.", "Error", JOptionPane.ERROR_MESSAGE);
                // }
            }
            // Si la respuesta es NO, no se hace nada.
        } else {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione una falla de la lista para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Método para cargar las fallas al inicio (si la vista lo necesita)
    public void cargarFallasIniciales() {
        vista.actualizarTabla();
    }
}