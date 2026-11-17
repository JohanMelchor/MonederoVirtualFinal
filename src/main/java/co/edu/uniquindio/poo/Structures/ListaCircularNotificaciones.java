package co.edu.uniquindio.poo.Structures;

import co.edu.uniquindio.poo.Model.Notificacion;

// Lista circular con las ultimas notificaciones leidas.
// Tiene una capacidad maxima: cuando se llena, la nueva notificacion
// reemplaza a la mas antigua.
public class ListaCircularNotificaciones {

    private NodoNotificacion ultimo; // ultimo.siguiente es el primero (el mas antiguo)
    private int tamano;
    private int capacidad;

    public ListaCircularNotificaciones(int capacidad) {
        this.capacidad = capacidad;
    }

    public void agregar(Notificacion notificacion) {
        if (tamano == capacidad) {
            // La lista esta llena: se reemplaza la mas antigua
            NodoNotificacion masAntiguo = ultimo.getSiguiente();
            masAntiguo.setNotificacion(notificacion);
            ultimo = masAntiguo;
            return;
        }
        NodoNotificacion nuevo = new NodoNotificacion(notificacion);
        if (ultimo == null) {
            nuevo.setSiguiente(nuevo); // apunta a si mismo
        } else {
            nuevo.setSiguiente(ultimo.getSiguiente());
            ultimo.setSiguiente(nuevo);
        }
        ultimo = nuevo;
        tamano++;
    }

    // Recorre la lista una vuelta completa, desde la mas antigua
    public String mostrar() {
        if (ultimo == null) {
            return "  (no hay notificaciones leidas)\n";
        }
        String texto = "";
        NodoNotificacion aux = ultimo.getSiguiente();
        for (int i = 0; i < tamano; i++) {
            texto += "  " + aux.getNotificacion().toString() + "\n";
            aux = aux.getSiguiente();
        }
        return texto;
    }

    public int getTamano() {
        return tamano;
    }

    public int getCapacidad() {
        return capacidad;
    }
}
