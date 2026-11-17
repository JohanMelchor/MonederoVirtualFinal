package co.edu.uniquindio.poo.Model;

import java.time.LocalDate;

// Una transaccion del monedero.
// tipo puede ser: DEPOSITO, RETIRO, TRANSFERENCIA, GASTO, MOVIMIENTO o BONO
public class Transaccion {

    private int id;
    private String tipo;
    private long monto;
    private long comision;
    private LocalDate fecha;
    private String cedulaOrigen;     // quien hace la transaccion
    private String monederoOrigen;   // de donde sale el dinero (null si no sale)
    private String cedulaDestino;
    private String monederoDestino;  // a donde llega el dinero (null si no llega)
    private String categoria;        // solo para los gastos
    private int puntos;
    private boolean revertida;

    public Transaccion(int id, String tipo, long monto, long comision, LocalDate fecha,
                       String cedulaOrigen, String monederoOrigen,
                       String cedulaDestino, String monederoDestino,
                       String categoria, int puntos) {
        this.id = id;
        this.tipo = tipo;
        this.monto = monto;
        this.comision = comision;
        this.fecha = fecha;
        this.cedulaOrigen = cedulaOrigen;
        this.monederoOrigen = monederoOrigen;
        this.cedulaDestino = cedulaDestino;
        this.monederoDestino = monederoDestino;
        this.categoria = categoria;
        this.puntos = puntos;
        this.revertida = false;
    }

    // Cuanto cambia esta transaccion el saldo de un monedero de un cliente
    public long efectoSobre(String cedula, String nombreMonedero) {
        if (revertida) {
            return 0;
        }
        long efecto = 0;
        if (cedula.equals(cedulaOrigen) && nombreMonedero.equals(monederoOrigen)) {
            efecto = efecto - monto - comision;
        }
        if (cedula.equals(cedulaDestino) && nombreMonedero.equals(monederoDestino)) {
            efecto = efecto + monto;
        }
        return efecto;
    }

    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public long getMonto() { return monto; }
    public long getComision() { return comision; }
    public LocalDate getFecha() { return fecha; }
    public String getCedulaOrigen() { return cedulaOrigen; }
    public String getMonederoOrigen() { return monederoOrigen; }
    public String getCedulaDestino() { return cedulaDestino; }
    public String getMonederoDestino() { return monederoDestino; }
    public String getCategoria() { return categoria; }
    public int getPuntos() { return puntos; }
    public boolean isRevertida() { return revertida; }
    public void setRevertida(boolean revertida) { this.revertida = revertida; }
}
