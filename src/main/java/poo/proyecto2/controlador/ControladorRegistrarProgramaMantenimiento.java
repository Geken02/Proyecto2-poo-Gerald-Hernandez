package poo.proyecto2.controlador;

import poo.proyecto2.modelo.equipos.NodoEquipo;
import poo.proyecto2.modelo.mantenimiento.*;
import poo.proyecto2.controlador.sistema.SistemaPrincipal;
import poo.proyecto2.vista.gui.vistas.VentanaRegistrarProgramaMantenimiento; // Referencia a la vista

import javax.swing.*;
import java.util.List;

public class ControladorRegistrarProgramaMantenimiento {

    private SistemaPrincipal modelo;
    private VentanaRegistrarProgramaMantenimiento vista; // Referencia a la vista

    public ControladorRegistrarProgramaMantenimiento(SistemaPrincipal modelo, VentanaRegistrarProgramaMantenimiento vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    public void buscarEquipo() {
        String idStr = vista.getTxtIdEquipo().getText().trim();
        if (!idStr.isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                NodoEquipo equipo = modelo.buscarEquipoPorId(id);
                if (equipo != null) {
                    vista.mostrarNombreEquipo(equipo.getDescripcion());
                } else {
                    vista.mostrarMensaje("No se encontró un equipo con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                    vista.resetearNombreEquipo();
                }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("Por favor, ingrese un ID de equipo válido (número entero).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            vista.mostrarMensaje("Por favor, ingrese un ID de equipo.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void agregarFase() {
        String idEquipoStr = vista.getTxtIdEquipo().getText().trim();
        if (idEquipoStr.isEmpty()) {
             vista.mostrarMensaje("Por favor, busque y seleccione un equipo primero.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        try {
            int idEquipo = Integer.parseInt(idEquipoStr);
            NodoEquipo equipo = modelo.buscarEquipoPorId(idEquipo);
            if (equipo == null) {
                vista.mostrarMensaje("El equipo seleccionado ya no es válido. Por favor, búsquelo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
                vista.resetearNombreEquipo();
                vista.limpiarCampoIdEquipo();
                return;
            }
            // Abrir la ventana de agregar/editar fase
            vista.abrirVentanaAgregarFase();
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("ID de equipo inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void guardarPrograma() {
        String idEquipoStr = vista.getTxtIdEquipo().getText().trim();
        if (idEquipoStr.isEmpty()) {
            vista.mostrarMensaje("Por favor, busque y seleccione un equipo primero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int idEquipo = Integer.parseInt(idEquipoStr);
            NodoEquipo equipo = modelo.buscarEquipoPorId(idEquipo);
            if (equipo == null) {
                vista.mostrarMensaje("El equipo seleccionado ya no es válido. Por favor, búsquelo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obtener la lista temporal de fases desde la vista
            List<FaseMantenimiento> fases = vista.getFasesTemporales();
            if (fases.isEmpty()) {
                vista.mostrarMensaje("El programa debe tener al menos una fase.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Crear el programa y asociarlo al equipo
            ProgramaMantenimientoPreventivo nuevoPrograma = new ProgramaMantenimientoPreventivo(idEquipo);
            for (FaseMantenimiento fase : fases) {
                nuevoPrograma.agregarFase(fase);
            }

            // Llamar al modelo para guardar el programa
            boolean guardado = modelo.guardarPrograma(nuevoPrograma); // Asumiendo que tienes este método en SistemaPrincipal

            if (guardado) {
                vista.mostrarMensaje("Programa de mantenimiento guardado exitosamente para el equipo ID: " + idEquipo + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarFormulario(); // Limpia campos y tabla de fases
            } else {
                vista.mostrarMensaje("No se pudo guardar el programa en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("ID de equipo inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cancelar() {
        vista.cerrarVentana();
    }
}