package poo.proyecto2;

import poo.proyecto2.gui.VentanaMenuPrincipal;
import poo.proyecto2.equipos.*;
import poo.proyecto2.sistema.*;
import poo.proyecto2.mantenimiento.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    
    public static void main(String[] args) {
    

        SistemaPrincipal sistema = new SistemaPrincipal(); // Inicializa el sistema

        SwingUtilities.invokeLater(() -> {
            try {
                // Intenta usar el look and feel del sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Si falla, usa el look and feel por defecto de Java
                e.printStackTrace();
            }
            new VentanaMenuPrincipal(sistema).setVisible(true); // Crea y muestra la ventana
        });
    }
}
