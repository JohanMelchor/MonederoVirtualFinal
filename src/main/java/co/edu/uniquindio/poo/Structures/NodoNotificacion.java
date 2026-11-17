package co.edu.uniquindio.poo.Structures;

import co.edu.uniquindio.poo.Model.Notificacion;

public class NodoNotificacion {

    private Notificacion notificacion;
    private NodoNotificacion siguiente;

    public NodoNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
        this.siguiente = null;
    }

    public Notificacion getNotificacion() {
        return notificacion;
    }

    public void setNotificacion(Notificacion notificacion) {
        this.notificacion = notificacion;
    }

    public NodoNotificacion getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoNotificacion siguiente) {
        this.siguiente = siguiente;
    }
}
