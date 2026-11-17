package co.edu.uniquindio.poo.Structures;

public class NodoArbolPuntos {

    private String cedula;
    private int puntos;
    private NodoArbolPuntos izquierdo;
    private NodoArbolPuntos derecho;

    public NodoArbolPuntos(String cedula, int puntos) {
        this.cedula = cedula;
        this.puntos = puntos;
    }

    public String getCedula() {
        return cedula;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public NodoArbolPuntos getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoArbolPuntos izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoArbolPuntos getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoArbolPuntos derecho) {
        this.derecho = derecho;
    }
}
