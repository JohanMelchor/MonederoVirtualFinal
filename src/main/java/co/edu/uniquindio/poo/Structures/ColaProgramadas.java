package co.edu.uniquindio.poo.Structures;

import co.edu.uniquindio.poo.Model.TransaccionProgramada;

// Cola de prioridad de transacciones programadas.
// La prioridad es la fecha: al insertar se busca su lugar para que
// el frente siempre sea la transaccion con la fecha mas cercana.
public class ColaProgramadas {

    private NodoProgramada frente;
    private int tamano;

    public void insertar(TransaccionProgramada programada) {
        NodoProgramada nuevo = new NodoProgramada(programada);

        // Si la cola esta vacia o la nueva va antes que el frente
        if (frente == null || programada.getFecha().isBefore(frente.getProgramada().getFecha())) {
            nuevo.setSiguiente(frente);
            frente = nuevo;
        } else {
            // Se avanza hasta encontrar una fecha mayor
            NodoProgramada aux = frente;
            while (aux.getSiguiente() != null
                    && !aux.getSiguiente().getProgramada().getFecha().isAfter(programada.getFecha())) {
                aux = aux.getSiguiente();
            }
            nuevo.setSiguiente(aux.getSiguiente());
            aux.setSiguiente(nuevo);
        }
        tamano++;
    }

    // Saca la transaccion del frente (la de fecha mas cercana)
    public TransaccionProgramada sacar() {
        if (estaVacia()) {
            return null;
        }
        TransaccionProgramada p = frente.getProgramada();
        frente = frente.getSiguiente();
        tamano--;
        return p;
    }

    public TransaccionProgramada verPrimero() {
        if (estaVacia()) {
            return null;
        }
        return frente.getProgramada();
    }

    public NodoProgramada getFrente() {
        return frente;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getTamano() {
        return tamano;
    }
}
