package co.edu.uniquindio.poo.Structures;

// Arbol binario de busqueda con los puntos acumulados de cada cliente.
// La clave es la cedula: las menores van a la izquierda y las mayores a la derecha.
public class ArbolPuntos {

    private NodoArbolPuntos raiz;
    private int peso;

    // Inserta la cedula con sus puntos. Si ya existe, actualiza los puntos.
    public void insertar(String cedula, int puntos) {
        raiz = insertar(raiz, cedula, puntos);
    }

    private NodoArbolPuntos insertar(NodoArbolPuntos nodo, String cedula, int puntos) {
        if (nodo == null) {
            peso++;
            return new NodoArbolPuntos(cedula, puntos);
        }
        int comparacion = cedula.compareTo(nodo.getCedula());
        if (comparacion < 0) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), cedula, puntos));
        } else if (comparacion > 0) {
            nodo.setDerecho(insertar(nodo.getDerecho(), cedula, puntos));
        } else {
            nodo.setPuntos(puntos); // ya existia
        }
        return nodo;
    }

    // Busca los puntos de una cedula. Devuelve -1 si no esta.
    public int buscar(String cedula) {
        return buscar(raiz, cedula);
    }

    private int buscar(NodoArbolPuntos nodo, String cedula) {
        if (nodo == null) {
            return -1;
        }
        int comparacion = cedula.compareTo(nodo.getCedula());
        if (comparacion == 0) {
            return nodo.getPuntos();
        } else if (comparacion < 0) {
            return buscar(nodo.getIzquierdo(), cedula);
        } else {
            return buscar(nodo.getDerecho(), cedula);
        }
    }

    // Recorrido inorden: muestra las cedulas ordenadas
    public String inorden() {
        return inorden(raiz);
    }

    private String inorden(NodoArbolPuntos nodo) {
        if (nodo == null) {
            return "";
        }
        return inorden(nodo.getIzquierdo())
                + "  " + nodo.getCedula() + " -> " + nodo.getPuntos() + " pts\n"
                + inorden(nodo.getDerecho());
    }

    public int altura() {
        return altura(raiz);
    }

    private int altura(NodoArbolPuntos nodo) {
        if (nodo == null) {
            return 0;
        }
        return 1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho()));
    }

    public int getPeso() {
        return peso;
    }
}
