package co.edu.uniquindio.poo.Structures;

import co.edu.uniquindio.poo.Model.TransaccionProgramada;

public class NodoProgramada {

    private TransaccionProgramada programada;
    private NodoProgramada siguiente;

    public NodoProgramada(TransaccionProgramada programada) {
        this.programada = programada;
        this.siguiente = null;
    }

    public TransaccionProgramada getProgramada() {
        return programada;
    }

    public NodoProgramada getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoProgramada siguiente) {
        this.siguiente = siguiente;
    }
}
