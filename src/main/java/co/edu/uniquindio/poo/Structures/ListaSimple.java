package co.edu.uniquindio.poo.Structures;

// Lista simplemente enlazada generica.
// Se usa para el historial de cada cliente, sus monederos y la lista de clientes.
public class ListaSimple<T> {

    private Nodo<T> nodoPrimero;
    private int tamano;

    public ListaSimple() {
        nodoPrimero = null;
        tamano = 0;
    }

    public boolean estaVacia() {
        return nodoPrimero == null;
    }

    // Agrega un elemento al final de la lista
    public void agregarFinal(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        if (estaVacia()) {
            nodoPrimero = nuevo;
        } else {
            Nodo<T> aux = nodoPrimero;
            while (aux.getSiguienteNodo() != null) {
                aux = aux.getSiguienteNodo();
            }
            aux.setSiguienteNodo(nuevo);
        }
        tamano++;
    }

    // Devuelve el valor que esta en la posicion indicada
    public T obtenerValorNodo(int indice) {
        if (indice < 0 || indice >= tamano) {
            return null;
        }
        Nodo<T> aux = nodoPrimero;
        for (int i = 0; i < indice; i++) {
            aux = aux.getSiguienteNodo();
        }
        return aux.getValorNodo();
    }

    public Nodo<T> getNodoPrimero() {
        return nodoPrimero;
    }

    public int getTamano() {
        return tamano;
    }
}
