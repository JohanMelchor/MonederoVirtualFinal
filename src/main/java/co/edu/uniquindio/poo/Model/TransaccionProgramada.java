package co.edu.uniquindio.poo.Model;

import java.time.LocalDate;

// Transaccion que se ejecuta sola en una fecha.
// tipo: DEPOSITO o TRANSFERENCIA. frecuencia: UNA VEZ, SEMANAL o MENSUAL
public class TransaccionProgramada {

    private String tipo;
    private String cedula;
    private String monedero;
    private String cedulaDestino;
    private long monto;
    private LocalDate fecha;
    private String frecuencia;

    public TransaccionProgramada(String tipo, String cedula, String monedero, String cedulaDestino,
                                 long monto, LocalDate fecha, String frecuencia) {
        this.tipo = tipo;
        this.cedula = cedula;
        this.monedero = monedero;
        this.cedulaDestino = cedulaDestino;
        this.monto = monto;
        this.fecha = fecha;
        this.frecuencia = frecuencia;
    }

    public String getTipo() { return tipo; }
    public String getCedula() { return cedula; }
    public String getMonedero() { return monedero; }
    public String getCedulaDestino() { return cedulaDestino; }
    public long getMonto() { return monto; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getFrecuencia() { return frecuencia; }
}
