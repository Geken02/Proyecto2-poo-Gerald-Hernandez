package poo.proyecto2.GUI;

import poo.proyecto2.sistema.SistemaPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VentanaMenuPrincipal extends JFrame {

    private SistemaPrincipal sistema;

    // Componentes de la interfaz
    private JMenuBar menuBar;
    private JMenu menuEquipos;
    private JMenu menuMantenimiento;
    private JMenu menuCatalogos;
    private JMenu menuReportes;

    private JMenuItem menuItemVerEquipos;
    private JMenuItem menuItemAgregarEquipo;
    private JMenuItem menuItemVerArbolEquipo;

    private JMenuItem menuItemProgramasMantenimiento;
    private JMenuItem menuItemGenerarOrdenPreventiva;
    private JMenuItem menuItemConsultarModificarOrdenPreventiva;
    private JMenuItem menuItemIniciarFinalizarOrdenPreventiva;
    private JMenuItem menuItemRegistrarOrdenCorrectiva;
    private JMenuItem menuItemConsultarModificarOrdenCorrectiva;
    private JMenuItem menuItemIniciarFinalizarOrdenCorrectiva;

    private JMenuItem menuItemTareasMaestras;
    private JMenuItem menuItemFallasMaestras;

    private JMenuItem menuItemReporteOrdenesEquipo;
    private JMenuItem menuItemReporteFallasEquipo;
    private JMenuItem menuItemReporteCostos;

    // Panel de bienvenida (opcional, en lugar del panel de botones)
    private JPanel panelBienvenida;

    // Panel de estado
    private JPanel panelEstado;
    private JLabel lblEstado;

    public VentanaMenuPrincipal(SistemaPrincipal sistema) {
        this.sistema = sistema;
        inicializarComponentes();
        configurarEventos();
        setTitle("Sistema de Mantenimiento de Equipos - Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        // pack(); // No usamos pack, definimos un tamaño fijo más grande
        setSize(1000, 700); // Tamaño más grande
        setLocationRelativeTo(null); // Centra la ventana
    }

    private void inicializarComponentes() {
        // --- Inicializar Barra de Menú ---
        menuBar = new JMenuBar();

        // --- Menús ---
        menuEquipos = new JMenu("Equipos");
        menuMantenimiento = new JMenu("Mantenimiento");
        menuCatalogos = new JMenu("Catálogos");
        menuReportes = new JMenu("Reportes");

        // Ajustar fuente del menú
        Font fontMenu = new Font("Arial", Font.BOLD, 16); // Fuente más grande y en negrita
        menuEquipos.setFont(fontMenu);
        menuMantenimiento.setFont(fontMenu);
        menuCatalogos.setFont(fontMenu);
        menuReportes.setFont(fontMenu);

        // --- Items del Menú Equipos ---
        menuItemVerEquipos = new JMenuItem("Ver/Editar Equipos");
        menuItemAgregarEquipo = new JMenuItem("Agregar Equipo");
        menuItemVerArbolEquipo = new JMenuItem("Ver Árbol de Equipo");

        menuEquipos.add(menuItemVerEquipos);
        menuEquipos.add(menuItemAgregarEquipo);
        menuEquipos.add(menuItemVerArbolEquipo);

        // --- Items del Menú Mantenimiento ---
        menuItemProgramasMantenimiento = new JMenuItem("Programas de Mantenimiento");

        JMenuItem subMenuGenPrev = new JMenuItem("Generar Órdenes");
        JMenuItem subMenuConsModPrev = new JMenuItem("Consultar/Modificar Órdenes");
        JMenuItem subMenuInicFinPrev = new JMenuItem("Iniciar/Finalizar Órdenes");

        JMenuItem subMenuRegCorr = new JMenuItem("Registrar Nueva");
        JMenuItem subMenuConsModCorr = new JMenuItem("Consultar/Modificar Órdenes");
        JMenuItem subMenuInicFinCorr = new JMenuItem("Iniciar/Finalizar Órdenes");

        menuMantenimiento.add(menuItemProgramasMantenimiento);
        menuMantenimiento.addSeparator(); // Separador antes de preventivas
        menuMantenimiento.add(new JLabel("Preventivas:")); // Etiqueta para agrupar
        menuMantenimiento.add(subMenuGenPrev);
        menuMantenimiento.add(subMenuConsModPrev);
        menuMantenimiento.add(subMenuInicFinPrev);
        menuMantenimiento.addSeparator(); // Separador entre preventivas y correctivas
        menuMantenimiento.add(new JLabel("Correctivas:")); // Etiqueta para agrupar
        menuMantenimiento.add(subMenuRegCorr);
        menuMantenimiento.add(subMenuConsModCorr);
        menuMantenimiento.add(subMenuInicFinCorr);

        // Asignar variables a los items para los eventos
        menuItemGenerarOrdenPreventiva = subMenuGenPrev;
        menuItemConsultarModificarOrdenPreventiva = subMenuConsModPrev;
        menuItemIniciarFinalizarOrdenPreventiva = subMenuInicFinPrev;
        menuItemRegistrarOrdenCorrectiva = subMenuRegCorr;
        menuItemConsultarModificarOrdenCorrectiva = subMenuConsModCorr;
        menuItemIniciarFinalizarOrdenCorrectiva = subMenuInicFinCorr;

        // --- Items del Menú Catálogos ---
        menuItemTareasMaestras = new JMenuItem("Tareas Maestras");
        menuItemFallasMaestras = new JMenuItem("Fallas Maestras");

        menuCatalogos.add(menuItemTareasMaestras);
        menuCatalogos.add(menuItemFallasMaestras);

        // --- Items del Menú Reportes ---
        menuItemReporteOrdenesEquipo = new JMenuItem("Reporte de Órdenes por Equipo");
        menuItemReporteFallasEquipo = new JMenuItem("Reporte de Fallas por Equipo");
        menuItemReporteCostos = new JMenuItem("Reporte de Costos");

        menuReportes.add(menuItemReporteOrdenesEquipo);
        menuReportes.add(menuItemReporteFallasEquipo);
        menuReportes.add(menuItemReporteCostos);

        // Añadir menús a la barra
        menuBar.add(menuEquipos);
        menuBar.add(menuMantenimiento);
        menuBar.add(menuCatalogos);
        menuBar.add(menuReportes);

        // --- Inicializar Panel de Bienvenida (en lugar del panel de botones) ---
        panelBienvenida = new JPanel(new BorderLayout());
        panelBienvenida.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Margen interno
        JLabel lblBienvenida = new JLabel("Bienvenido al Sistema de Mantenimiento de Equipos", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 24)); // Fuente grande para el mensaje
        lblBienvenida.setForeground(new Color(51, 51, 51)); // Color de texto oscuro
        panelBienvenida.add(lblBienvenida, BorderLayout.CENTER);

        // Opcional: Agregar una imagen de fondo o un ícono aquí si lo deseas
        // panelBienvenida.add(new JLabel(new ImageIcon("ruta/a/tu/imagen.png")), BorderLayout.NORTH);

        // --- Inicializar Panel de Estado ---
        panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblEstado = new JLabel("Sistema listo.");
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 12)); // Fuente del estado
        panelEstado.setBorder(BorderFactory.createLoweredBevelBorder()); // Borde para distinguirlo
        panelEstado.add(lblEstado);

        // --- Añadir componentes al JFrame ---
        setJMenuBar(menuBar); // Asigna la barra de menú
        add(panelBienvenida, BorderLayout.CENTER); // Panel de bienvenida en el centro
        add(panelEstado, BorderLayout.SOUTH);   // Panel de estado en la parte inferior

        // Opcional: Decorar la ventana con un borde
        ((JComponent) getContentPane()).setBorder(BorderFactory.createRaisedBevelBorder());
    }

    private void configurarEventos() {
        // --- Eventos de Menú ---
        // En VentanaMenuPrincipal.java, dentro de configurarEventos(), en el ActionListener de menuItemVerEquipos o un nuevo menuItem
menuItemVerEquipos.addActionListener(e -> {
    // lblEstado.setText("Acción: Consulta de Mantenimiento"); // Opcional
    // JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Consulta de Mantenimiento", "Info", JOptionPane.INFORMATION_MESSAGE); // QUITAR ESTO

    // CREAR Y MOSTRAR LA NUEVA VENTANA DE CONSULTA
    VentanaConsultarEquipo ventanaConsulta = new VentanaConsultarEquipo(sistema, this); // Pasa la instancia de sistema y la ventana padre
    ventanaConsulta.setVisible(true);
});

        menuItemAgregarEquipo.addActionListener(e -> {
        // lblEstado.setText("Acción: Agregar Equipo"); // Opcional: Actualizar estado

        // CREAR Y MOSTRAR LA VENTANA DE REGISTRO
        // Se pasa la instancia de 'sistema' y 'this' (la ventana principal) a la nueva ventana
        VentanaRegistrarEquipo ventanaReg = new VentanaRegistrarEquipo(sistema, this);
        ventanaReg.setVisible(true); // Hace visible la nueva ventana
        // Opcional: ventanaReg.setLocationRelativeTo(this); // Asegura que se centre en la ventana padre si no lo hace automáticamente
    });

        menuItemVerArbolEquipo.addActionListener(e -> {
            // TODO: Abrir ventana de ver árbol
            lblEstado.setText("Acción: Ver Árbol de Equipo");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Ver Árbol de Equipo", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemProgramasMantenimiento.addActionListener(e -> {
            // TODO: Abrir ventana de programas
            lblEstado.setText("Acción: Programas de Mantenimiento");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Programas de Mantenimiento", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemGenerarOrdenPreventiva.addActionListener(e -> {
            // TODO: Abrir ventana de generar órdenes preventivas
            lblEstado.setText("Acción: Generar Órdenes Preventivas");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Generar Órdenes Preventivas", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemConsultarModificarOrdenPreventiva.addActionListener(e -> {
            // TODO: Abrir ventana de consultar/modificar órdenes preventivas
            lblEstado.setText("Acción: Consultar/Modificar Órdenes Preventivas");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Consultar/Modificar Órdenes Preventivas", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemIniciarFinalizarOrdenPreventiva.addActionListener(e -> {
            // TODO: Abrir ventana de iniciar/finalizar órdenes preventivas
            lblEstado.setText("Acción: Iniciar/Finalizar Órdenes Preventivas");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Iniciar/Finalizar Órdenes Preventivas", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemRegistrarOrdenCorrectiva.addActionListener(e -> {
            // TODO: Abrir ventana de registrar orden correctiva
            lblEstado.setText("Acción: Registrar Nueva Orden Correctiva");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Registrar Nueva Orden Correctiva", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemConsultarModificarOrdenCorrectiva.addActionListener(e -> {
            // TODO: Abrir ventana de consultar/modificar órdenes correctivas
            lblEstado.setText("Acción: Consultar/Modificar Órdenes Correctivas");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Consultar/Modificar Órdenes Correctivas", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemIniciarFinalizarOrdenCorrectiva.addActionListener(e -> {
            // TODO: Abrir ventana de iniciar/finalizar órdenes correctivas
            lblEstado.setText("Acción: Iniciar/Finalizar Órdenes Correctivas");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Iniciar/Finalizar Órdenes Correctivas", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemTareasMaestras.addActionListener(e -> {
            // TODO: Abrir ventana de tareas maestras
            lblEstado.setText("Acción: Tareas Maestras");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Tareas Maestras", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemFallasMaestras.addActionListener(e -> {
            // TODO: Abrir ventana de fallas maestras
            lblEstado.setText("Acción: Fallas Maestras");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Fallas Maestras", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemReporteOrdenesEquipo.addActionListener(e -> {
            // TODO: Abrir ventana de reporte
            lblEstado.setText("Acción: Reporte de Órdenes por Equipo");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Reporte de Órdenes por Equipo", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemReporteFallasEquipo.addActionListener(e -> {
            // TODO: Abrir ventana de reporte
            lblEstado.setText("Acción: Reporte de Fallas por Equipo");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Reporte de Fallas por Equipo", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        menuItemReporteCostos.addActionListener(e -> {
            // TODO: Abrir ventana de reporte
            lblEstado.setText("Acción: Reporte de Costos");
            JOptionPane.showMessageDialog(this, "Funcionalidad pendiente: Reporte de Costos", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public static void main(String[] args) {
        // Ejemplo de uso
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