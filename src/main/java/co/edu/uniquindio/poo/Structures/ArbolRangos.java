package co.edu.uniquindio.poo.Structures;

// Arbol AVL de clientes ordenados por sus puntos.
// Sirve para el ranking y para saber que clientes hay en cada rango.
// Factor de equilibrio (Fe) = altura(derecho) - altura(izquierdo).
// Si un nodo queda con Fe = 2 o Fe = -2 se hace una rotacion.
public class ArbolRangos {

    private NodoAVL raiz;
    private int rotaciones;

    public void vaciar() {
        raiz = null;
    }

    private int altura(NodoAVL nodo) {
        if (nodo == null) {
            return 0;
        }
        return nodo.getAltura();
    }

    private void actualizarAltura(NodoAVL nodo) {
        nodo.setAltura(1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho())));
    }

    private int factorEquilibrio(NodoAVL nodo) {
        return altura(nodo.getDerecho()) - altura(nodo.getIzquierdo());
    }

    private NodoAVL rotarDerecha(NodoAVL nodo) {
        NodoAVL hijo = nodo.getIzquierdo();
        nodo.setIzquierdo(hijo.getDerecho());
        hijo.setDerecho(nodo);
        actualizarAltura(nodo);
        actualizarAltura(hijo);
        rotaciones++;
        return hijo;
    }

    private NodoAVL rotarIzquierda(NodoAVL nodo) {
        NodoAVL hijo = nodo.getDerecho();
        nodo.setDerecho(hijo.getIzquierdo());
        hijo.setIzquierdo(nodo);
        actualizarAltura(nodo);
        actualizarAltura(hijo);
        rotaciones++;
        return hijo;
    }

    // Inserta un cliente. Si dos clientes tienen los mismos puntos, el nuevo va a la derecha.
    public void insertar(int puntos, String nombre, String cedula) {
        raiz = insertar(raiz, puntos, nombre, cedula);
    }

    private NodoAVL insertar(NodoAVL nodo, int puntos, String nombre, String cedula) {
        if (nodo == null) {
            return new NodoAVL(puntos, nombre, cedula);
        }
        if (puntos < nodo.getPuntos()) {
            nodo.setIzquierdo(insertar(nodo.getIzquierdo(), puntos, nombre, cedula));
        } else {
            nodo.setDerecho(insertar(nodo.getDerecho(), puntos, nombre, cedula));
        }

        actualizarAltura(nodo);
        int fe = factorEquilibrio(nodo);

        // Caso izquierda-izquierda
        if (fe < -1 && factorEquilibrio(nodo.getIzquierdo()) <= 0) {
            return rotarDerecha(nodo);
        }
        // Caso izquierda-derecha (rotacion doble)
        if (fe < -1 && factorEquilibrio(nodo.getIzquierdo()) > 0) {
            nodo.setIzquierdo(rotarIzquierda(nodo.getIzquierdo()));
            return rotarDerecha(nodo);
        }
        // Caso derecha-derecha
        if (fe > 1 && factorEquilibrio(nodo.getDerecho()) >= 0) {
            return rotarIzquierda(nodo);
        }
        // Caso derecha-izquierda (rotacion doble)
        if (fe > 1 && factorEquilibrio(nodo.getDerecho()) < 0) {
            nodo.setDerecho(rotarDerecha(nodo.getDerecho()));
            return rotarIzquierda(nodo);
        }
        return nodo;
    }

    // Ranking: recorre de mayor a menor puntaje (derecha, raiz, izquierda)
    public String ranking() {
        return ranking(raiz);
    }

    private String ranking(NodoAVL nodo) {
        if (nodo == null) {
            return "";
        }
        return ranking(nodo.getDerecho())
                + "  " + nodo.getNombre() + " (" + nodo.getCedula() + "): " + nodo.getPuntos() + " pts\n"
                + ranking(nodo.getIzquierdo());
    }

    // Clientes cuyos puntos estan entre minimo y maximo
    public String clientesEntre(int minimo, int maximo) {
        return clientesEntre(raiz, minimo, maximo);
    }

    private String clientesEntre(NodoAVL nodo, int minimo, int maximo) {
        if (nodo == null) {
            return "";
        }
        String texto = "";
        if (nodo.getPuntos() > minimo) {
            texto += clientesEntre(nodo.getIzquierdo(), minimo, maximo);
        }
        if (nodo.getPuntos() >= minimo && nodo.getPuntos() <= maximo) {
            texto += nodo.getNombre() + ", ";
        }
        if (nodo.getPuntos() <= maximo) {
            texto += clientesEntre(nodo.getDerecho(), minimo, maximo);
        }
        return texto;
    }

    public int alturaArbol() {
        return altura(raiz);
    }

    public int getRotaciones() {
        return rotaciones;
    }
}
