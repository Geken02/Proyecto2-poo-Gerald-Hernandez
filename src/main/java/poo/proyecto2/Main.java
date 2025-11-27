package poo.proyecto2;

import poo.proyecto2.equipos.*;
import poo.proyecto2.sistema.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        // 1. Crear sistema (cargará ME_Equipos.json si existe)
        SistemaPrincipal sistema = new SistemaPrincipal();

        // 2. Si el archivo no existía, creamos datos de prueba
        if (sistema.getBosque().isEmpty()) {
            System.out.println("No se encontró ME_Equipos.json. Creando datos de prueba...");

            // Árbol 1: Servidor con subcomponentes
            NodoEquipo servidor = sistema.crearRaiz(
                "Servidor Principal", "Computadora", "Sala de Servidores",
                "Dell", "SVR-2025-001", LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 2, 1), 60, 
                poo.proyecto2.EstadoEquipo.FUNCIONANDO, 4500.0
            );
            servidor.setEspecificacionesTecnicas("Intel Xeon, 64GB RAM, 2TB SSD");
            servidor.setInformacionGarantia("3 años, soporte 24/7");

            NodoEquipo disco = sistema.crearHijo(
                servidor.getId(), "Disco SSD Principal", "Almacenamiento", "Bahía 1 - Servidor 1",
                "Samsung", "SSD-870-001", LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 2, 1), 36,
                poo.proyecto2.EstadoEquipo.FUNCIONANDO, 200.0
            );
            disco.setEspecificacionesTecnicas("2TB NVMe");
            disco.setInformacionGarantia("5 años");

            sistema.crearHijo(
                servidor.getId(), "Fuente de Poder Redundante", "Fuente de Poder", "Servidor 1",
                "Corsair", "CS-PSU-001", LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 2, 1), 48,
                poo.proyecto2.EstadoEquipo.FUNCIONANDO, 300.0
            );

            // Árbol 2: Impresora (raíz sin hijos)
            NodoEquipo impresora = sistema.crearRaiz(
                "Impresora Multifuncional", "Impresora", "Oficina Gerencia",
                "HP", "HP-MFP-402", LocalDate.of(2023, 11, 10),
                LocalDate.of(2023, 11, 15), 24,
                poo.proyecto2.EstadoEquipo.EN_MANTENIMIENTO_PREVENTIVO, 800.0
            );
            impresora.setEspecificacionesTecnicas("Color, Wi-Fi, Dúplex");
            impresora.setInformacionGarantia("1 año");

            // Guardar
            try {
                sistema.guardar();
                System.out.println("Datos de prueba guardados en datos/ME_Equipos.json");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Datos cargados desde ME_Equipos.json:");
            System.out.println("- Número de árboles: " + sistema.getBosque().size());
            int totalNodos = 0;
            for (var arbol : sistema.getBosque()) {
                totalNodos += contarNodos(arbol.getRaiz());
            }
            System.out.println("- Total de equipos: " + totalNodos);
        }


         for (var arbol : sistema.getBosque()) {
        imprimirArbol(arbol.getRaiz(), 0);
        }
    }

        private static int contarNodos(poo.proyecto2.equipos.NodoEquipo nodo) {
            if (nodo == null) return 0;
            return 1 + contarNodos(nodo.getPrimerHijo()) + contarNodos(nodo.getSiguienteHermano());
        }

       

    static void imprimirArbol(NodoEquipo nodo, int nivel) {
        if (nodo == null) return;
        System.out.println("  ".repeat(nivel) + "- ID: " + nodo.getId() + ", Desc: " + nodo.getDescripcion());
        imprimirArbol(nodo.getPrimerHijo(), nivel + 1);
        imprimirArbol(nodo.getSiguienteHermano(), nivel);
    }
}
