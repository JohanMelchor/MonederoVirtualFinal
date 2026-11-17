package co.edu.uniquindio.poo.Structures;

// Tabla hash para consultar rapido los puntos de un cliente por su cedula.
// Es un arreglo de 31 posiciones; si dos cedulas caen en la misma posicion
// (colision) se encadenan en una lista.
public class TablaHashPuntos {

    private static final int TAMANO = 31;

    // Nodo interno de la tabla
    private static class NodoHash {
        String cedula;
        int puntos;
        NodoHash siguiente;

        NodoHash(String cedula, int puntos) {
            this.cedula = cedula;
            this.puntos = puntos;
        }
    }

    private NodoHash[] tabla = new NodoHash[TAMANO];
    private int cantidad;

    // Funcion hash: suma los codigos de los caracteres y saca el modulo
    private int funcionHash(String cedula) {
        int suma = 0;
        for (int i = 0; i < cedula.length(); i++) {
            suma += cedula.charAt(i) * (i + 1);
        }
        return suma % TAMANO;
    }

    public void poner(String cedula, int puntos) {
        int posicion = funcionHash(cedula);
        NodoHash aux = tabla[posicion];
        while (aux != null) {
            if (aux.cedula.equals(cedula)) {
                aux.puntos = puntos; // ya existia, se actualiza
                return;
            }
            aux = aux.siguiente;
        }
        NodoHash nuevo = new NodoHash(cedula, puntos);
        nuevo.siguiente = tabla[posicion];
        tabla[posicion] = nuevo;
        cantidad++;
    }

    // Devuelve los puntos o -1 si la cedula no esta
    public int obtener(String cedula) {
        NodoHash aux = tabla[funcionHash(cedula)];
        while (aux != null) {
            if (aux.cedula.equals(cedula)) {
                return aux.puntos;
            }
            aux = aux.siguiente;
        }
        return -1;
    }

    public int getCantidad() {
        return cantidad;
    }
}
