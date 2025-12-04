package poo.proyecto2.GUI;

import poo.proyecto2.EstadoEquipo;
import poo.proyecto2.sistema.SistemaPrincipal;
import poo.proyecto2.equipos.*;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class VentanaRegistrarEquipo extends JFrame {

    private SistemaPrincipal sistema;
    private VentanaMenuPrincipal ventanaPadre; // Referencia a la ventana principal para actualizarla si es necesario

    // Componentes de la interfaz
    private JLabel lblTitulo;
    // Nuevo campo: Equipo Principal
    private JLabel lblEquipoPrincipal;
    private JTextField txtEquipoPrincipal; // Campo para ingresar ID del equipo padre o 0
    private JLabel lblDescripcion;
    private JTextField txtDescripcion;
    private JLabel lblTipo;
    private JTextField txtTipo;
    private JLabel lblUbicacion;
    private JTextField txtUbicacion;
    private JLabel lblFabricante;
    private JTextField txtFabricante;
    private JLabel lblSerie;
    private JTextField txtSerie;
    private JLabel lblFechaAdquisicion;
    private JFormattedTextField txtFechaAdquisicion;
    private JLabel lblFechaPuestaEnServicio;
    private JFormattedTextField txtFechaPuestaEnServicio;
    private JLabel lblMesesVidaUtil;
    private JFormattedTextField txtMesesVidaUtil;
    private JLabel lblEstado;
    private JComboBox<EstadoEquipo> cmbEstado;
    private JLabel lblCostoInicial;
    private JFormattedTextField txtCostoInicial;
    private JLabel lblEspecTecnicas;
    private JTextArea txtEspecTecnicas;
    private JScrollPane scrollEspecTecnicas;
    private JLabel lblInfoGarantia;
    private JTextArea txtInfoGarantia;
    private JScrollPane scrollInfoGarantia;
    private JButton btnRegistrar;
    private JButton btnCancelar;

    public VentanaRegistrarEquipo(SistemaPrincipal sistema, VentanaMenuPrincipal ventanaPadre) {
        this.sistema = sistema;
        this.ventanaPadre = ventanaPadre; // Guardamos la referencia
        inicializarComponentes();
        configurarEventos();
        setTitle("Registrar Nuevo Equipo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cierra solo esta ventana
        setResizable(false); // Opcional: evitar redimensionar para mantener el layout simple
        pack(); // Ajusta el tamaño al contenido
        setLocationRelativeTo(ventanaPadre); // Centra en la ventana padre
    }

    private void inicializarComponentes() {
        // --- Configuración general del layout ---
        setLayout(new BorderLayout());

        // --- Panel superior: Título ---
        lblTitulo = new JLabel("Registrar Nuevo Equipo", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Margen
        add(lblTitulo, BorderLayout.NORTH);

        // --- Panel central: Formulario ---
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espacio entre componentes
        gbc.anchor = GridBagConstraints.WEST; // Alinear etiquetas a la izquierda

        // --- Fila 0: Equipo Principal ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        lblEquipoPrincipal = new JLabel("Equipo Principal (0 si es raíz): *");
        panelFormulario.add(lblEquipoPrincipal, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        // Usamos un JTextField normal para permitir el borrado completo
        // La validación se hará en el botón Registrar
        txtEquipoPrincipal = new JTextField("0", 10); // Campo de texto normal, valor por defecto "0"
        panelFormulario.add(txtEquipoPrincipal, gbc);

        // --- Fila 1: Descripción ---
        gbc.gridx = 0; gbc.gridy = 1;
        lblDescripcion = new JLabel("Descripción *:");
        panelFormulario.add(lblDescripcion, gbc);

        gbc.gridx = 1;
        txtDescripcion = new JTextField(20);
        panelFormulario.add(txtDescripcion, gbc);

        // --- Fila 2: Tipo ---
        gbc.gridx = 0; gbc.gridy = 2;
        lblTipo = new JLabel("Tipo *:");
        panelFormulario.add(lblTipo, gbc);

        gbc.gridx = 1;
        txtTipo = new JTextField(20);
        panelFormulario.add(txtTipo, gbc);

        // --- Fila 3: Ubicación ---
        gbc.gridx = 0; gbc.gridy = 3;
        lblUbicacion = new JLabel("Ubicación *:");
        panelFormulario.add(lblUbicacion, gbc);

        gbc.gridx = 1;
        txtUbicacion = new JTextField(20);
        panelFormulario.add(txtUbicacion, gbc);

        // --- Fila 4: Fabricante ---
        gbc.gridx = 0; gbc.gridy = 4;
        lblFabricante = new JLabel("Fabricante *:");
        panelFormulario.add(lblFabricante, gbc);

        gbc.gridx = 1;
        txtFabricante = new JTextField(20);
        panelFormulario.add(txtFabricante, gbc);

        // --- Fila 5: Serie ---
        gbc.gridx = 0; gbc.gridy = 5;
        lblSerie = new JLabel("Serie *:");
        panelFormulario.add(lblSerie, gbc);

        gbc.gridx = 1;
        txtSerie = new JTextField(20);
        panelFormulario.add(txtSerie, gbc);

        // --- Fila 6: Fecha Adquisición ---
        gbc.gridx = 0; gbc.gridy = 6;
        lblFechaAdquisicion = new JLabel("Fecha Adquisición (AAAA-MM-DD) *:");
        panelFormulario.add(lblFechaAdquisicion, gbc);

        gbc.gridx = 1;
        txtFechaAdquisicion = new JFormattedTextField(DateTimeFormatter.ISO_LOCAL_DATE);
        txtFechaAdquisicion.setValue(LocalDate.now()); // Valor por defecto
        panelFormulario.add(txtFechaAdquisicion, gbc);

        // --- Fila 7: Fecha Puesta en Servicio ---
        gbc.gridx = 0; gbc.gridy = 7;
        lblFechaPuestaEnServicio = new JLabel("Fecha Puesta en Servicio (AAAA-MM-DD) *:");
        panelFormulario.add(lblFechaPuestaEnServicio, gbc);

        gbc.gridx = 1;
        txtFechaPuestaEnServicio = new JFormattedTextField(DateTimeFormatter.ISO_LOCAL_DATE);
        txtFechaPuestaEnServicio.setValue(LocalDate.now()); // Valor por defecto
        panelFormulario.add(txtFechaPuestaEnServicio, gbc);

        // --- Fila 8: Meses Vida Útil ---
        gbc.gridx = 0; gbc.gridy = 8;
        lblMesesVidaUtil = new JLabel("Meses Vida Útil *:");
        panelFormulario.add(lblMesesVidaUtil, gbc);

        gbc.gridx = 1;
        NumberFormatter formatterMeses = new NumberFormatter();
        formatterMeses.setValueClass(Integer.class);
        formatterMeses.setMinimum(1);
        txtMesesVidaUtil = new JFormattedTextField(formatterMeses);
        txtMesesVidaUtil.setValue(12); // Valor por defecto
        panelFormulario.add(txtMesesVidaUtil, gbc);

        // --- Fila 9: Estado ---
        gbc.gridx = 0; gbc.gridy = 9;
        lblEstado = new JLabel("Estado *:");
        panelFormulario.add(lblEstado, gbc);

        gbc.gridx = 1;
        cmbEstado = new JComboBox<>(EstadoEquipo.values());
        cmbEstado.setSelectedItem(EstadoEquipo.FUNCIONANDO); // Valor por defecto
        panelFormulario.add(cmbEstado, gbc);

        // --- Fila 10: Costo Inicial ---
        gbc.gridx = 0; gbc.gridy = 10;
        lblCostoInicial = new JLabel("Costo Inicial *:");
        panelFormulario.add(lblCostoInicial, gbc);

        gbc.gridx = 1;
        NumberFormatter formatterCosto = new NumberFormatter(new java.text.DecimalFormat("#,##0.00"));
        formatterCosto.setValueClass(Double.class);
        formatterCosto.setMinimum(0.01);
        txtCostoInicial = new JFormattedTextField(formatterCosto);
        txtCostoInicial.setValue(0.0); // Valor por defecto
        panelFormulario.add(txtCostoInicial, gbc);

        // --- Fila 11: Especificaciones Técnicas (área de texto) ---
        gbc.gridx = 0; gbc.gridy = 11; gbc.fill = GridBagConstraints.BOTH;
        lblEspecTecnicas = new JLabel("Especificaciones Técnicas:");
        panelFormulario.add(lblEspecTecnicas, gbc);

        gbc.gridx = 1;
        txtEspecTecnicas = new JTextArea(3, 20);
        scrollEspecTecnicas = new JScrollPane(txtEspecTecnicas);
        panelFormulario.add(scrollEspecTecnicas, gbc);

        // --- Fila 12: Información de Garantía (área de texto) ---
        gbc.gridx = 0; gbc.gridy = 12;
        lblInfoGarantia = new JLabel("Información de Garantía:");
        panelFormulario.add(lblInfoGarantia, gbc);

        gbc.gridx = 1;
        txtInfoGarantia = new JTextArea(3, 20);
        scrollInfoGarantia = new JScrollPane(txtInfoGarantia);
        panelFormulario.add(scrollInfoGarantia, gbc);

        add(panelFormulario, BorderLayout.CENTER);

        // --- Panel inferior: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        // No hay más inicialización de ComboBox de Equipos Padre
    }

    private void configurarEventos() {
        // --- Evento del Botón Registrar ---
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarEquipo();
            }
        });

        // --- Evento del Botón Cancelar ---
        btnCancelar.addActionListener(e -> {
            // Opcional: Preguntar si está seguro si hay datos ingresados
            dispose(); // Cierra la ventana
        });
    }

        private void registrarEquipo() {
        // 1. Validar campos requeridos (marcados con *) - Omitimos fechas aquí, se validan en el parseo
        if (txtEquipoPrincipal.getText().trim().isEmpty() || // Importante validar este campo
            txtDescripcion.getText().trim().isEmpty() ||
            txtTipo.getText().trim().isEmpty() ||
            txtUbicacion.getText().trim().isEmpty() ||
            txtFabricante.getText().trim().isEmpty() ||
            txtSerie.getText().trim().isEmpty() ||
            txtMesesVidaUtil.getText().trim().isEmpty() || // Se valida como número más abajo
            txtCostoInicial.getText().trim().isEmpty()) { // Se valida como número más abajo

            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos obligatorios (marcados con *).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Obtener y validar ID del Equipo Principal (ahora es un String que convertimos)
        int idPadre;
        try {
            String equipoPrincipalStr = txtEquipoPrincipal.getText().trim();
            if (equipoPrincipalStr.isEmpty()) {
                // Opcional: Podrías considerar 0 como valor por defecto si está vacío, o lanzar error
                // Por consistencia con la validación de arriba, asumiremos que no debe estar vacío si llegó hasta aquí.
                // Pero si decides que puede estar vacío y ser 0, descomenta la línea siguiente:
                // idPadre = 0;
                // Para este caso, si pasó la validación de arriba, no debería estar vacío.
                // Asumiremos que la validación de arriba cubre este caso.
                // Si se borra y se intenta registrar, la validación de arriba lo debe atrapar.
                // Pero si se borra y se intenta registrar sin pasar por la primera validación (caso raro),
                // lo manejamos aquí.
                idPadre = 0; // O lanzar un error si se borra y se intenta registrar sin haberlo puesto antes.
            } else {
                 idPadre = Integer.parseInt(equipoPrincipalStr); // Intenta parsear el string
                 if (idPadre < 0) {
                     JOptionPane.showMessageDialog(this, "El ID del Equipo Principal no puede ser negativo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                     return;
                 }
            }
        } catch (NumberFormatException ex) {
             JOptionPane.showMessageDialog(this, "Por favor, ingrese un número entero válido para el Equipo Principal (0 o ID existente).", "Error de Validación", JOptionPane.ERROR_MESSAGE);
             return;
        }

        // 3. Obtener otros datos del formulario (igual que antes)
        String descripcion = txtDescripcion.getText().trim();
        String tipo = txtTipo.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();
        String fabricante = txtFabricante.getText().trim();
        String serie = txtSerie.getText().trim();

        LocalDate fechaAdq, fechaPuestaEnServ;
        try {
            fechaAdq = LocalDate.parse(txtFechaAdquisicion.getText().trim());
            fechaPuestaEnServ = LocalDate.parse(txtFechaPuestaEnServicio.getText().trim());
            // Opcional: Validar que la fecha de puesta en servicio sea >= fecha de adquisición
            if(fechaPuestaEnServ.isBefore(fechaAdq)) {
                JOptionPane.showMessageDialog(this, "La fecha de puesta en servicio debe ser igual o posterior a la fecha de adquisición.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese fechas válidas en formato AAAA-MM-DD.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int mesesVidaUtil;
        try {
            mesesVidaUtil = ((Number) txtMesesVidaUtil.getValue()).intValue();
            if(mesesVidaUtil <= 0) {
                JOptionPane.showMessageDialog(this, "Los meses de vida útil deben ser un número positivo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido para meses de vida útil.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EstadoEquipo estado = (EstadoEquipo) cmbEstado.getSelectedItem();

        double costoInicial;
        try {
            costoInicial = ((Number) txtCostoInicial.getValue()).doubleValue();
            if(costoInicial <= 0) {
                JOptionPane.showMessageDialog(this, "El costo inicial debe ser un número positivo.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido para el costo inicial.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String especTecnicas = txtEspecTecnicas.getText().trim();
        String infoGarantia = txtInfoGarantia.getText().trim();

        // 4. Llamar a SistemaPrincipal para crear el equipo
        try {
            NodoEquipo nuevoEquipo = sistema.crearEquipo(idPadre, descripcion, tipo, ubicacion,
                    fabricante, serie, fechaAdq, fechaPuestaEnServ, mesesVidaUtil,
                    estado, costoInicial);

            // 5. Asignar campos opcionales
            nuevoEquipo.setEspecificacionesTecnicas(especTecnicas.isEmpty() ? null : especTecnicas);
            nuevoEquipo.setInformacionGarantia(infoGarantia.isEmpty() ? null : infoGarantia);

            // 6. Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(this, "Equipo '" + descripcion + "' registrado exitosamente con ID: " + nuevoEquipo.getId(), "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Opcional: Limpiar el formulario o cerrar la ventana
            // limpiarFormulario(); // Si defines este método
            dispose(); // Cierra la ventana de registro

        } catch (IllegalArgumentException ex) {
            // Manejar errores específicos del sistema (por ejemplo, equipo padre no encontrado)
             JOptionPane.showMessageDialog(this, "Error al registrar el equipo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Manejar otros errores inesperados
             JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al registrar el equipo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             ex.printStackTrace(); // Para depuración
        }
    }

    // Opcional: Método para limpiar los campos del formulario
    /*
    private void limpiarFormulario() {
        txtEquipoPrincipal.setValue(0);
        txtDescripcion.setText("");
        txtTipo.setText("");
        txtUbicacion.setText("");
        txtFabricante.setText("");
        txtSerie.setText("");
        txtFechaAdquisicion.setValue(LocalDate.now());
        txtFechaPuestaEnServicio.setValue(LocalDate.now());
        txtMesesVidaUtil.setValue(12);
        cmbEstado.setSelectedItem(EstadoEquipo.FUNCIONANDO);
        txtCostoInicial.setValue(0.0);
        txtEspecTecnicas.setText("");
        txtInfoGarantia.setText("");
    }
    */
}