package poo.proyecto2.controlador;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.EstadoEquipo;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.VentanaMenuPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaEditarEquipo; // Referencia a la vista

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ControladorEditarEquipo {

    private SistemaPrincipal modelo;
    private VentanaEditarEquipo vista; // Referencia a la vista
    private NodoEquipo equipoAErEditar; // Referencia al equipo que se está editando

    public ControladorEditarEquipo(SistemaPrincipal modelo, VentanaEditarEquipo vista, NodoEquipo equipoAErEditar) {
        this.modelo = modelo;
        this.vista = vista;
        this.equipoAErEditar = equipoAErEditar;
    }

    public void guardarCambios(VentanaMenuPrincipal ventanaPadre) {
        // 1. Validar campos requeridos (marcados con *)
        // --- CORRECCIÓN: Usar getText() en lugar de getValue() para JTextField ---
        if (vista.getTxtDescripcion().getText().trim().isEmpty() ||
            vista.getTxtTipo().getText().trim().isEmpty() ||
            vista.getTxtUbicacion().getText().trim().isEmpty() ||
            vista.getTxtFabricante().getText().trim().isEmpty() ||
            vista.getTxtSerie().getText().trim().isEmpty() ||
            vista.getTxtFechaAdquisicion().getText().trim().isEmpty() || // <-- getText()
            vista.getTxtFechaPuestaEnServicio().getText().trim().isEmpty() || // <-- getText()
            vista.getTxtMesesVidaUtil().getText().trim().isEmpty() || // <-- getText()
            vista.getTxtCostoInicial().getText().trim().isEmpty()) { // <-- getText()

            vista.mostrarMensaje("Por favor, complete todos los campos obligatorios (marcados con *).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // --- FIN CORRECCIÓN ---

        // 2. Obtener datos del formulario
        String descripcion = vista.getTxtDescripcion().getText().trim();
        String tipo = vista.getTxtTipo().getText().trim();
        String ubicacion = vista.getTxtUbicacion().getText().trim();
        String fabricante = vista.getTxtFabricante().getText().trim();
        String serie = vista.getTxtSerie().getText().trim();

        LocalDate fechaAdq, fechaPuestaEnServ;
        try {
            // --- CORRECCIÓN: Parsear el texto de JTextField ---
            fechaAdq = LocalDate.parse(vista.getTxtFechaAdquisicion().getText().trim()); // <-- getText()
            fechaPuestaEnServ = LocalDate.parse(vista.getTxtFechaPuestaEnServicio().getText().trim()); // <-- getText()
            // --- FIN CORRECCIÓN ---
            if (fechaPuestaEnServ.isBefore(fechaAdq)) {
                vista.mostrarMensaje("La fecha de puesta en servicio debe ser igual o posterior a la fecha de adquisición.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (java.time.format.DateTimeParseException ex) {
            vista.mostrarMensaje("Por favor, ingrese fechas válidas en formato AAAA-MM-DD.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int mesesVidaUtil;
        try {
            // --- CORRECCIÓN: Parsear el texto de JTextField ---
            mesesVidaUtil = Integer.parseInt(vista.getTxtMesesVidaUtil().getText().trim()); // <-- getText()
            // --- FIN CORRECCIÓN ---
            if (mesesVidaUtil <= 0) {
                vista.mostrarMensaje("Los meses de vida útil deben ser un número positivo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("Por favor, ingrese un número entero válido para meses de vida útil.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EstadoEquipo estado = (EstadoEquipo) vista.getCmbEstado().getSelectedItem();

        double costoInicial;
        try {
            // --- CORRECCIÓN: Parsear el texto de JTextField ---
            costoInicial = Double.parseDouble(vista.getTxtCostoInicial().getText().trim()); // <-- getText()
            // --- FIN CORRECCIÓN ---
            if (costoInicial <= 0) {
                vista.mostrarMensaje("El costo inicial debe ser un número positivo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("Por favor, ingrese un número decimal válido para el costo inicial.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String especTecnicas = vista.getTxtEspecificacionesTecnicas().getText().trim();
        String infoGarantia = vista.getTxtInformacionGarantia().getText().trim();

        // 3. Actualizar los datos del objeto equipoAErEditar en memoria
        equipoAErEditar.setDescripcion(descripcion);
        equipoAErEditar.setTipo(tipo);
        equipoAErEditar.setUbicacion(ubicacion);
        equipoAErEditar.setFabricante(fabricante);
        equipoAErEditar.setSerie(serie);
        equipoAErEditar.setFechaAdquisicion(fechaAdq);
        equipoAErEditar.setFechaPuestaEnServicio(fechaPuestaEnServ);
        equipoAErEditar.setMesesVidaUtil(mesesVidaUtil);
        equipoAErEditar.setEstado(estado);
        equipoAErEditar.setCostoInicial(costoInicial);
        equipoAErEditar.setEspecificacionesTecnicas(especTecnicas.isEmpty() ? null : especTecnicas);
        equipoAErEditar.setInformacionGarantia(infoGarantia.isEmpty() ? null : infoGarantia);

        // 4. Llamar al sistema para guardar los cambios en disco
        // Usamos el metodo actualizarEquipo del SistemaPrincipal, pasando el ID del equipo actual
        boolean actualizado = modelo.actualizarEquipo(equipoAErEditar.getId(), descripcion, tipo, ubicacion,
                fabricante, serie, fechaAdq, fechaPuestaEnServ, mesesVidaUtil,
                estado, costoInicial, especTecnicas.isEmpty() ? null : especTecnicas, infoGarantia.isEmpty() ? null : infoGarantia);

        if (actualizado) {
            vista.mostrarMensaje("Equipo ID " + equipoAErEditar.getId() + " actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Opcional: Actualizar la vista del árbol en la ventana padre si es necesario
            if (ventanaPadre != null) {
                ventanaPadre.actualizarVistaArbol(); // Llama al método de actualización si existe
            }
            vista.cerrarVentana(); // Cierra la ventana de edición
        } else {
            // Este caso es raro si el equipoAErEditar fue encontrado previamente
            vista.mostrarMensaje("No se pudo actualizar el equipo. Puede que ya no exista.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cancelar() {
        vista.cerrarVentana();
    }
}