package poo.proyecto2.modelo.equipos;

import com.google.gson.annotations.Expose;

/**
 * Representa una estructura de árbol jerárquico de equipos técnicos o de mantenimiento.
 * Esta clase actúa como contenedor del árbol completo, almacenando únicamente una referencia
 * a su nodo raíz. Permite gestionar la jerarquía de equipos de forma recursiva mediante
 * la clase {@link NodoEquipo}.
 * 
 * <p>Está diseñada para ser serializable a JSON mediante la biblioteca Gson, gracias al uso
 * de la anotación {@code @Expose} en el campo raíz.</p>
 */
// Representa un árbol completo: solo contiene su raíz
public class Equipos {
    @Expose private NodoEquipo raiz;

    /**
     * Constructor por defecto que inicializa un árbol vacío (sin raíz).
     */
    public Equipos() {}

    /**
     * Constructor que permite inicializar el árbol con un nodo raíz específico.
     *
     * @param raiz el nodo que actuará como raíz del árbol de equipos.
     */
    public Equipos(NodoEquipo raiz) {
        this.raiz = raiz;
    }

    /**
     * Obtiene el nodo raíz del árbol de equipos.
     *
     * @return el nodo raíz del árbol, o {@code null} si el árbol está vacío.
     */
    public NodoEquipo getRaiz() { return raiz; }

    /**
     * Establece un nuevo nodo como raíz del árbol de equipos.
     *
     * @param raiz el nuevo nodo raíz que reemplazará al actual.
     */
    public void setRaiz(NodoEquipo raiz) { this.raiz = raiz; }
}