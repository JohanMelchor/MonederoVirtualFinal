package co.edu.uniquindio.poo.Structures;

import co.edu.uniquindio.poo.Model.Transaccion;

// Pila de transacciones reversibles (LIFO).
// La ultima transaccion que se hizo es la primera que se puede revertir.
public class PilaTransacciones {

    private NodoPila cima;
    private int tamano;

    public void apilar(Transaccion transaccion) {
        NodoPila nuevo = new NodoPila(transaccion);
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamano++;
    }

    public Transaccion desapilar() {
        if (estaVacia()) {
            return null;
        }
        Transaccion t = cima.getTransaccion();
        cima = cima.getSiguiente();
        tamano--;
        return t;
    }

    public Transaccion verCima() {
        if (estaVacia()) {
            return null;
        }
        return cima.getTransaccion();
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int getTamano() {
        return tamano;
    }
}
