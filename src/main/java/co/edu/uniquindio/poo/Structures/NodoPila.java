package co.edu.uniquindio.poo.Structures;

import co.edu.uniquindio.poo.Model.Transaccion;

public class NodoPila {

    private Transaccion transaccion;
    private NodoPila siguiente;

    public NodoPila(Transaccion transaccion) {
        this.transaccion = transaccion;
        this.siguiente = null;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public NodoPila getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoPila siguiente) {
        this.siguiente = siguiente;
    }
}
