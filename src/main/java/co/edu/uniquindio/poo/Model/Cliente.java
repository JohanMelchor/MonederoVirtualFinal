package co.edu.uniquindio.poo.Model;

import co.edu.uniquindio.poo.Structures.ColaNotificaciones;
import co.edu.uniquindio.poo.Structures.Grafo;
import co.edu.uniquindio.poo.Structures.ListaCircularNotificaciones;
import co.edu.uniquindio.poo.Structures.ListaSimple;
import co.edu.uniquindio.poo.Structures.PilaTransacciones;

import java.time.LocalDate;

public class Cliente {

    private String cedula;
    private String nombre;
    private int puntos;            // puntos acumulados (definen el rango)
    private int puntosCanjeados;
    private LocalDate descuentoComisionHasta;  // beneficio de 100 puntos
    private LocalDate retiroGratisHasta;       // beneficio de 500 puntos
    private String ultimaCategoria;            // para el grafo de categorias

    private ListaSimple<Monedero> monederos = new ListaSimple<>();
    private ListaSimple<Transaccion> historial = new ListaSimple<>();
    private PilaTransacciones reversibles = new PilaTransacciones();
    private ColaNotificaciones notificacionesPendientes = new ColaNotificaciones();
    private ListaCircularNotificaciones notificacionesLeidas = new ListaCircularNotificaciones(10);
    private Grafo grafoMonederos = new Grafo();
    private Grafo grafoCategorias = new Grafo();

    public Cliente(String cedula, String nombre) {
        this.cedula = cedula;
        this.nombre = nombre;
        monederos.agregarFinal(new Monedero("Principal"));
        monederos.agregarFinal(new Monedero("Ahorros"));
        grafoMonederos.agregarVertice("Principal");
        grafoMonederos.agregarVertice("Ahorros");
    }

    public Monedero buscarMonedero(String nombreMonedero) {
        for (int i = 0; i < monederos.getTamano(); i++) {
            Monedero m = monederos.obtenerValorNodo(i);
            if (m.getNombre().equalsIgnoreCase(nombreMonedero)) {
                return m;
            }
        }
        return null;
    }

    public long getSaldoTotal() {
        long total = 0;
        for (int i = 0; i < monederos.getTamano(); i++) {
            total += monederos.obtenerValorNodo(i).getSaldo();
        }
        return total;
    }

    public int getPuntosDisponibles() {
        return puntos - puntosCanjeados;
    }

    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
    public int getPuntosCanjeados() { return puntosCanjeados; }
    public void setPuntosCanjeados(int puntosCanjeados) { this.puntosCanjeados = puntosCanjeados; }
    public LocalDate getDescuentoComisionHasta() { return descuentoComisionHasta; }
    public void setDescuentoComisionHasta(LocalDate fecha) { this.descuentoComisionHasta = fecha; }
    public LocalDate getRetiroGratisHasta() { return retiroGratisHasta; }
    public void setRetiroGratisHasta(LocalDate fecha) { this.retiroGratisHasta = fecha; }
    public String getUltimaCategoria() { return ultimaCategoria; }
    public void setUltimaCategoria(String ultimaCategoria) { this.ultimaCategoria = ultimaCategoria; }
    public ListaSimple<Monedero> getMonederos() { return monederos; }
    public ListaSimple<Transaccion> getHistorial() { return historial; }
    public PilaTransacciones getReversibles() { return reversibles; }
    public ColaNotificaciones getNotificacionesPendientes() { return notificacionesPendientes; }
    public ListaCircularNotificaciones getNotificacionesLeidas() { return notificacionesLeidas; }
    public Grafo getGrafoMonederos() { return grafoMonederos; }
    public Grafo getGrafoCategorias() { return grafoCategorias; }
}
