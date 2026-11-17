package co.edu.uniquindio.poo.Structures;

import co.edu.uniquindio.poo.Model.Notificacion;

// Cola (FIFO) de notificaciones pendientes de un cliente.
// Las notificaciones se leen en el mismo orden en que llegaron.
public class ColaNotificaciones {

    private NodoNotificacion frente;
    private NodoNotificacion fin;
    private int tamano;

    public void encolar(Notificacion notificacion) {
        NodoNotificacion nuevo = new NodoNotificacion(notificacion);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        tamano++;
    }

    public Notificacion desencolar() {
        if (estaVacia()) {
            return null;
        }
        Notificacion n = frente.getNotificacion();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        tamano--;
        return n;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamano() {
        return tamano;
    }
}
