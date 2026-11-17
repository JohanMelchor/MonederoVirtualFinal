package co.edu.uniquindio.poo.Model;

import java.time.LocalDate;

public class Notificacion {

    private LocalDate fecha;
    private String mensaje;

    public Notificacion(LocalDate fecha, String mensaje) {
        this.fecha = fecha;
        this.mensaje = mensaje;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    @Override
    public String toString() {
        return "[" + fecha + "] " + mensaje;
    }
}
