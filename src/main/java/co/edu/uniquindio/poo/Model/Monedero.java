package co.edu.uniquindio.poo.Model;

public class Monedero {

    private String nombre;
    private long saldo;

    public Monedero(String nombre) {
        this.nombre = nombre;
        this.saldo = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public long getSaldo() {
        return saldo;
    }

    public void setSaldo(long saldo) {
        this.saldo = saldo;
    }
}
