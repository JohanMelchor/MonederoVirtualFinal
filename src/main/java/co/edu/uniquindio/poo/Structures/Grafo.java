package co.edu.uniquindio.poo.Structures;

// Grafo dirigido y con peso.
// Los vertices son textos (nombres de clientes, categorias o monederos).
// Cada arista origen -> destino guarda el total de dinero y cuantas veces se uso.
public class Grafo {

    // Arista del grafo
    public static class Arista {
        private String origen;
        private String destino;
        private long total;
        private int veces;

        public Arista(String origen, String destino) {
            this.origen = origen;
            this.destino = destino;
        }

        public String getOrigen() {
            return origen;
        }

        public String getDestino() {
            return destino;
        }

        public long getTotal() {
            return total;
        }

        public int getVeces() {
            return veces;
        }
    }

    private ListaSimple<String> vertices = new ListaSimple<>();
    private ListaSimple<Arista> aristas = new ListaSimple<>();

    public void agregarVertice(String vertice) {
        for (int i = 0; i < vertices.getTamano(); i++) {
            if (vertices.obtenerValorNodo(i).equals(vertice)) {
                return; // ya existe
            }
        }
        vertices.agregarFinal(vertice);
    }

    // Si la arista ya existe se suma el monto; si no, se crea
    public void agregarArista(String origen, String destino, long monto) {
        agregarVertice(origen);
        agregarVertice(destino);
        Arista arista = buscarArista(origen, destino);
        if (arista == null) {
            arista = new Arista(origen, destino);
            aristas.agregarFinal(arista);
        }
        arista.total += monto;
        arista.veces++;
    }

    // Se usa al revertir una transferencia
    public void restarArista(String origen, String destino, long monto) {
        Arista arista = buscarArista(origen, destino);
        if (arista != null) {
            arista.total -= monto;
            arista.veces--;
        }
    }

    public Arista buscarArista(String origen, String destino) {
        for (int i = 0; i < aristas.getTamano(); i++) {
            Arista a = aristas.obtenerValorNodo(i);
            if (a.origen.equals(origen) && a.destino.equals(destino)) {
                return a;
            }
        }
        return null;
    }

    // Suma de lo que sale de un vertice
    public long totalSalida(String vertice) {
        long total = 0;
        for (int i = 0; i < aristas.getTamano(); i++) {
            if (aristas.obtenerValorNodo(i).origen.equals(vertice)) {
                total += aristas.obtenerValorNodo(i).total;
            }
        }
        return total;
    }

    // Suma de lo que llega a un vertice
    public long totalEntrada(String vertice) {
        long total = 0;
        for (int i = 0; i < aristas.getTamano(); i++) {
            if (aristas.obtenerValorNodo(i).destino.equals(vertice)) {
                total += aristas.obtenerValorNodo(i).total;
            }
        }
        return total;
    }

    public ListaSimple<String> getVertices() {
        return vertices;
    }

    public ListaSimple<Arista> getAristas() {
        return aristas;
    }
}
