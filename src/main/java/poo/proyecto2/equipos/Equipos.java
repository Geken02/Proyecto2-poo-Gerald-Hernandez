package poo.proyecto2.equipos;

import com.google.gson.annotations.Expose;

// Representa un árbol completo: solo contiene su raíz
public class Equipos {
    @Expose private NodoEquipo raiz;

    public Equipos() {}

    public Equipos(NodoEquipo raiz) {
        this.raiz = raiz;
    }

    public NodoEquipo getRaiz() { return raiz; }
    public void setRaiz(NodoEquipo raiz) { this.raiz = raiz; }
}