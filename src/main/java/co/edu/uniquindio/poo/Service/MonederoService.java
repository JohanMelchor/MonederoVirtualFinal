package co.edu.uniquindio.poo.Service;

import co.edu.uniquindio.poo.Model.Cliente;
import co.edu.uniquindio.poo.Model.Monedero;
import co.edu.uniquindio.poo.Model.Notificacion;
import co.edu.uniquindio.poo.Model.Transaccion;
import co.edu.uniquindio.poo.Model.TransaccionProgramada;
import co.edu.uniquindio.poo.Structures.ArbolPuntos;
import co.edu.uniquindio.poo.Structures.ArbolRangos;
import co.edu.uniquindio.poo.Structures.ColaProgramadas;
import co.edu.uniquindio.poo.Structures.Grafo;
import co.edu.uniquindio.poo.Structures.ListaSimple;
import co.edu.uniquindio.poo.Structures.Nodo;
import co.edu.uniquindio.poo.Structures.NodoProgramada;
import co.edu.uniquindio.poo.Structures.TablaHashPuntos;

import java.time.LocalDate;

// Clase con toda la logica del monedero virtual.
// Todos los metodos devuelven un texto que la ventana muestra al usuario.
// Si algo sale mal, el texto empieza por "ERROR".
public class MonederoService {

    // Reglas del negocio en pesos colombianos
    public static final long PESOS_POR_BLOQUE = 10000;    // los puntos se dan por cada $10.000 COP
    public static final long CARGO_RETIRO = 2000;         // cargo fijo por retiro
    public static final long BONO_CANJE = 50000;          // bono por canjear 1000 puntos
    public static final long SALDO_MINIMO_ALERTA = 20000; // alerta de saldo bajo

    private ListaSimple<Cliente> clientes = new ListaSimple<>();
    private ArbolPuntos arbolPuntos = new ArbolPuntos();
    private TablaHashPuntos tablaPuntos = new TablaHashPuntos();
    private ArbolRangos arbolRangos = new ArbolRangos();
    private ColaProgramadas programadas = new ColaProgramadas();
    private Grafo grafoTransferencias = new Grafo();
    private LocalDate fechaActual = LocalDate.now();
    private int contadorTransacciones = 0;

    //  utilidades

    // Da formato de pesos: 1500000 -> $1.500.000 COP
    public static String pesos(long valor) {
        return "$" + String.format("%,d", valor).replace(',', '.') + " COP";
    }

    public Cliente buscarCliente(String cedula) {
        for (int i = 0; i < clientes.getTamano(); i++) {
            Cliente c = clientes.obtenerValorNodo(i);
            if (c.getCedula().equals(cedula)) {
                return c;
            }
        }
        return null;
    }

    public int calcularPuntos(String tipo, long monto) {
        int factor = 0;
        if (tipo.equals("DEPOSITO")) {
            factor = 1;
        } else if (tipo.equals("RETIRO") || tipo.equals("GASTO")) {
            factor = 2;
        } else if (tipo.equals("TRANSFERENCIA")) {
            factor = 3;
        }
        return (int) (monto / PESOS_POR_BLOQUE) * factor;
    }

    public String calcularRango(int puntos) {
        if (puntos > 5000) {
            return "Platino";
        } else if (puntos > 1000) {
            return "Oro";
        } else if (puntos > 500) {
            return "Plata";
        } else {
            return "Bronce";
        }
    }

    // Comision de transferencia segun el rango (en decimas de porcentaje)
    public long calcularComision(Cliente cliente, long monto) {
        String rango = calcularRango(cliente.getPuntos());
        long decimas = 10; // Bronce 1%
        if (rango.equals("Plata")) {
            decimas = 8;   // 0,8%
        } else if (rango.equals("Oro")) {
            decimas = 5;   // 0,5%
        } else if (rango.equals("Platino")) {
            decimas = 0;   // sin comision
        }
        long comision = monto * decimas / 1000;
        // Beneficio de canje: 10% menos de comision
        if (cliente.getDescuentoComisionHasta() != null && !fechaActual.isAfter(cliente.getDescuentoComisionHasta())) {
            comision = comision * 90 / 100;
        }
        return comision;
    }

    private void notificar(Cliente cliente, String mensaje) {
        cliente.getNotificacionesPendientes().encolar(new Notificacion(fechaActual, mensaje));
    }

    private void revisarSaldoBajo(Cliente cliente, Monedero monedero) {
        if (monedero.getSaldo() < SALDO_MINIMO_ALERTA) {
            notificar(cliente, "ALERTA: el monedero " + monedero.getNombre() + " quedo con saldo bajo ("
                    + pesos(monedero.getSaldo()) + ")");
        }
    }

    // Guarda la transaccion en el historial, la apila y suma los puntos
    private void guardarTransaccion(Cliente cliente, Transaccion t) {
        cliente.getHistorial().agregarFinal(t);
        if (!t.getTipo().equals("BONO")) {
            cliente.getReversibles().apilar(t);
        }
        if (t.getPuntos() > 0) {
            cambiarPuntos(cliente, cliente.getPuntos() + t.getPuntos());
        }
    }

    // Actualiza los puntos en el cliente, el ABB, la tabla hash y el AVL
    private void cambiarPuntos(Cliente cliente, int nuevosPuntos) {
        String rangoAntes = calcularRango(cliente.getPuntos());
        cliente.setPuntos(nuevosPuntos);
        arbolPuntos.insertar(cliente.getCedula(), nuevosPuntos);
        tablaPuntos.poner(cliente.getCedula(), nuevosPuntos);
        actualizarArbolRangos();
        String rangoDespues = calcularRango(nuevosPuntos);
        if (!rangoAntes.equals(rangoDespues)) {
            notificar(cliente, "Cambiaste de rango: " + rangoAntes + " -> " + rangoDespues);
        }
    }

    // El AVL se vuelve a armar con todos los clientes para que siempre este al dia
    private void actualizarArbolRangos() {
        arbolRangos.vaciar();
        for (int i = 0; i < clientes.getTamano(); i++) {
            Cliente c = clientes.obtenerValorNodo(i);
            arbolRangos.insertar(c.getPuntos(), c.getNombre(), c.getCedula());
        }
    }

    // clientes y monederos

    public String registrarCliente(String cedula, String nombre) {
        if (cedula == null || cedula.isEmpty() || nombre == null || nombre.isEmpty()) {
            return "ERROR: la cedula y el nombre son obligatorios";
        }
        if (buscarCliente(cedula) != null) {
            return "ERROR: ya existe un cliente con la cedula " + cedula;
        }
        Cliente cliente = new Cliente(cedula, nombre);
        clientes.agregarFinal(cliente);
        arbolPuntos.insertar(cedula, 0);
        tablaPuntos.poner(cedula, 0);
        actualizarArbolRangos();
        grafoTransferencias.agregarVertice(nombre);
        notificar(cliente, "Bienvenido(a) " + nombre + ". Empiezas en rango Bronce.");
        return "Cliente registrado: " + nombre + " (" + cedula + ")\nSe crearon los monederos Principal y Ahorros.";
    }

    public String crearMonedero(String cedula, String nombreMonedero) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        if (nombreMonedero == null || nombreMonedero.isEmpty()) {
            return "ERROR: el nombre del monedero es obligatorio";
        }
        if (cliente.buscarMonedero(nombreMonedero) != null) {
            return "ERROR: el cliente ya tiene un monedero llamado " + nombreMonedero;
        }
        cliente.getMonederos().agregarFinal(new Monedero(nombreMonedero));
        cliente.getGrafoMonederos().agregarVertice(nombreMonedero);
        return "Monedero " + nombreMonedero + " creado para " + cliente.getNombre();
    }

    //  transacciones basicas

    public String depositar(String cedula, String nombreMonedero, long monto) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        Monedero monedero = cliente.buscarMonedero(nombreMonedero);
        if (monedero == null) {
            return "ERROR: el cliente no tiene el monedero " + nombreMonedero;
        }
        if (monto <= 0) {
            return "ERROR: el monto debe ser mayor que cero";
        }
        int puntos = calcularPuntos("DEPOSITO", monto);
        Transaccion t = new Transaccion(++contadorTransacciones, "DEPOSITO", monto, 0, fechaActual,
                cedula, null, cedula, monedero.getNombre(), null, puntos);
        monedero.setSaldo(monedero.getSaldo() + monto);
        guardarTransaccion(cliente, t);
        return "Deposito realizado en " + monedero.getNombre() + " por " + pesos(monto)
                + "\nNuevo saldo del monedero: " + pesos(monedero.getSaldo())
                + "\nPuntos ganados: " + puntos;
    }

    public String retirar(String cedula, String nombreMonedero, long monto) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        Monedero monedero = cliente.buscarMonedero(nombreMonedero);
        if (monedero == null) {
            return "ERROR: el cliente no tiene el monedero " + nombreMonedero;
        }
        if (monto <= 0) {
            return "ERROR: el monto debe ser mayor que cero";
        }
        long cargo = CARGO_RETIRO;
        if (cliente.getRetiroGratisHasta() != null && !fechaActual.isAfter(cliente.getRetiroGratisHasta())) {
            cargo = 0; // beneficio de canje
        }
        if (monedero.getSaldo() < monto + cargo) {
            return "ERROR: saldo insuficiente. Se necesitan " + pesos(monto + cargo)
                    + " (incluye cargo de " + pesos(cargo) + ") y hay " + pesos(monedero.getSaldo());
        }
        int puntos = calcularPuntos("RETIRO", monto);
        Transaccion t = new Transaccion(++contadorTransacciones, "RETIRO", monto, cargo, fechaActual,
                cedula, monedero.getNombre(), null, null, null, puntos);
        monedero.setSaldo(monedero.getSaldo() - monto - cargo);
        guardarTransaccion(cliente, t);
        revisarSaldoBajo(cliente, monedero);
        return "Retiro realizado de " + monedero.getNombre() + " por " + pesos(monto)
                + "\nCargo por retiro: " + pesos(cargo)
                + "\nNuevo saldo del monedero: " + pesos(monedero.getSaldo())
                + "\nPuntos ganados: " + puntos;
    }

    public String transferir(String cedulaOrigen, String nombreMonedero, String cedulaDestino, long monto) {
        Cliente origen = buscarCliente(cedulaOrigen);
        Cliente destino = buscarCliente(cedulaDestino);
        if (origen == null || destino == null) {
            return "ERROR: el cliente de origen o de destino no existe";
        }
        if (origen == destino) {
            return "ERROR: no se puede transferir al mismo cliente (use Mover entre monederos)";
        }
        Monedero monedero = origen.buscarMonedero(nombreMonedero);
        if (monedero == null) {
            return "ERROR: el cliente no tiene el monedero " + nombreMonedero;
        }
        if (monto <= 0) {
            return "ERROR: el monto debe ser mayor que cero";
        }
        long comision = calcularComision(origen, monto);
        if (monedero.getSaldo() < monto + comision) {
            return "ERROR: saldo insuficiente. Se necesitan " + pesos(monto + comision)
                    + " (incluye comision de " + pesos(comision) + ")";
        }
        int puntos = calcularPuntos("TRANSFERENCIA", monto);
        Monedero principalDestino = destino.buscarMonedero("Principal");
        Transaccion t = new Transaccion(++contadorTransacciones, "TRANSFERENCIA", monto, comision, fechaActual,
                cedulaOrigen, monedero.getNombre(), cedulaDestino, "Principal", null, puntos);
        monedero.setSaldo(monedero.getSaldo() - monto - comision);
        principalDestino.setSaldo(principalDestino.getSaldo() + monto);
        guardarTransaccion(origen, t);
        destino.getHistorial().agregarFinal(t); // tambien queda en el historial del que recibe
        grafoTransferencias.agregarArista(origen.getNombre(), destino.getNombre(), monto);
        notificar(destino, "Recibiste " + pesos(monto) + " de " + origen.getNombre());
        revisarSaldoBajo(origen, monedero);
        return "Transferencia de " + pesos(monto) + " a " + destino.getNombre()
                + "\nComision (" + calcularRango(origen.getPuntos()) + "): " + pesos(comision)
                + "\nNuevo saldo de " + monedero.getNombre() + ": " + pesos(monedero.getSaldo())
                + "\nPuntos ganados: " + puntos;
    }

    public String moverEntreMonederos(String cedula, String nombreOrigen, String nombreDestino, long monto) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        Monedero origen = cliente.buscarMonedero(nombreOrigen);
        Monedero destino = cliente.buscarMonedero(nombreDestino);
        if (origen == null || destino == null) {
            return "ERROR: alguno de los monederos no existe";
        }
        if (origen == destino) {
            return "ERROR: los monederos deben ser distintos";
        }
        if (monto <= 0 || origen.getSaldo() < monto) {
            return "ERROR: monto invalido o saldo insuficiente en " + origen.getNombre();
        }
        Transaccion t = new Transaccion(++contadorTransacciones, "MOVIMIENTO", monto, 0, fechaActual,
                cedula, origen.getNombre(), cedula, destino.getNombre(), null, 0);
        origen.setSaldo(origen.getSaldo() - monto);
        destino.setSaldo(destino.getSaldo() + monto);
        guardarTransaccion(cliente, t);
        cliente.getGrafoMonederos().agregarArista(origen.getNombre(), destino.getNombre(), monto);
        revisarSaldoBajo(cliente, origen);
        return "Se movieron " + pesos(monto) + " de " + origen.getNombre() + " a " + destino.getNombre()
                + "\n(los movimientos entre monederos no tienen comision ni dan puntos)";
    }

    public String registrarGasto(String cedula, String nombreMonedero, long monto, String categoria) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        Monedero monedero = cliente.buscarMonedero(nombreMonedero);
        if (monedero == null) {
            return "ERROR: el cliente no tiene el monedero " + nombreMonedero;
        }
        if (categoria == null || categoria.isEmpty()) {
            return "ERROR: la categoria es obligatoria";
        }
        if (monto <= 0 || monedero.getSaldo() < monto) {
            return "ERROR: monto invalido o saldo insuficiente";
        }
        categoria = categoria.toUpperCase();
        int puntos = calcularPuntos("GASTO", monto);
        Transaccion t = new Transaccion(++contadorTransacciones, "GASTO", monto, 0, fechaActual,
                cedula, monedero.getNombre(), null, null, categoria, puntos);
        monedero.setSaldo(monedero.getSaldo() - monto);
        guardarTransaccion(cliente, t);

        // Grafo de categorias: categoria anterior -> categoria actual
        if (cliente.getUltimaCategoria() == null) {
            cliente.getGrafoCategorias().agregarVertice(categoria);
        } else {
            cliente.getGrafoCategorias().agregarArista(cliente.getUltimaCategoria(), categoria, monto);
        }
        cliente.setUltimaCategoria(categoria);
        revisarSaldoBajo(cliente, monedero);
        return "Gasto registrado en " + categoria + " por " + pesos(monto)
                + "\nNuevo saldo de " + monedero.getNombre() + ": " + pesos(monedero.getSaldo())
                + "\nPuntos ganados: " + puntos;
    }

    //  consultas

    public String consultarSaldo(String cedula) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        String texto = "SALDO DE " + cliente.getNombre() + "\n\n";
        for (int i = 0; i < cliente.getMonederos().getTamano(); i++) {
            Monedero m = cliente.getMonederos().obtenerValorNodo(i);
            texto += "  " + m.getNombre() + ": " + pesos(m.getSaldo()) + "\n";
        }
        texto += "\n  TOTAL: " + pesos(cliente.getSaldoTotal());
        return texto;
    }

    public String verHistorial(String cedula) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        String texto = "HISTORIAL DE " + cliente.getNombre() + " (" + cliente.getHistorial().getTamano()
                + " transacciones)\n\n";
        Nodo<Transaccion> aux = cliente.getHistorial().getNodoPrimero();
        while (aux != null) {
            Transaccion t = aux.getValorNodo();
            String tipo = t.getTipo();
            if (tipo.equals("TRANSFERENCIA") && !t.getCedulaOrigen().equals(cedula)) {
                tipo = "TRANSF. RECIBIDA de " + buscarCliente(t.getCedulaOrigen()).getNombre();
            } else if (tipo.equals("TRANSFERENCIA")) {
                tipo = "TRANSFERENCIA a " + buscarCliente(t.getCedulaDestino()).getNombre();
            } else if (tipo.equals("GASTO")) {
                tipo = "GASTO " + t.getCategoria();
            }
            texto += "#" + t.getId() + "  " + t.getFecha() + "  " + tipo + "  " + pesos(t.getMonto());
            if (t.getComision() > 0 && t.getCedulaOrigen().equals(cedula)) {
                texto += "  (cargo " + pesos(t.getComision()) + ")";
            }
            if (t.getCedulaOrigen().equals(cedula)) {
                texto += "  +" + t.getPuntos() + " pts";
            }
            if (t.isRevertida()) {
                texto += "  [REVERTIDA]";
            }
            texto += "\n";
            aux = aux.getSiguienteNodo();
        }
        Transaccion cima = cliente.getReversibles().verCima();
        texto += "\nPila de reversibles: " + cliente.getReversibles().getTamano() + " transacciones. ";
        texto += cima == null ? "Esta vacia." : "En la cima esta la #" + cima.getId() + " (" + cima.getTipo() + ")";
        return texto;
    }

    //  revertir

    public String revertirUltima(String cedula) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        Transaccion t = cliente.getReversibles().verCima();
        if (t == null) {
            return "ERROR: no hay transacciones para revertir";
        }
        // Si el dinero llego a algun monedero, ese monedero debe tenerlo todavia
        Monedero monederoDestino = null;
        if (t.getMonederoDestino() != null) {
            monederoDestino = buscarCliente(t.getCedulaDestino()).buscarMonedero(t.getMonederoDestino());
            if (monederoDestino.getSaldo() < t.getMonto()) {
                return "ERROR: no se puede revertir, el monedero destino ya no tiene " + pesos(t.getMonto());
            }
        }
        cliente.getReversibles().desapilar();
        if (t.getMonederoOrigen() != null) {
            Monedero monederoOrigen = cliente.buscarMonedero(t.getMonederoOrigen());
            monederoOrigen.setSaldo(monederoOrigen.getSaldo() + t.getMonto() + t.getComision());
        }
        if (monederoDestino != null) {
            monederoDestino.setSaldo(monederoDestino.getSaldo() - t.getMonto());
        }
        t.setRevertida(true);

        if (t.getTipo().equals("TRANSFERENCIA")) {
            Cliente destino = buscarCliente(t.getCedulaDestino());
            grafoTransferencias.restarArista(cliente.getNombre(), destino.getNombre(), t.getMonto());
            notificar(destino, "Se revirtio la transferencia de " + pesos(t.getMonto()) + " de " + cliente.getNombre());
        } else if (t.getTipo().equals("MOVIMIENTO")) {
            cliente.getGrafoMonederos().restarArista(t.getMonederoOrigen(), t.getMonederoDestino(), t.getMonto());
        }

        // Los puntos se vuelven a calcular recorriendo el historial (recursivo)
        int puntosAntes = cliente.getPuntos();
        int puntosNuevos = puntosRecursivos(cliente.getHistorial().getNodoPrimero(), cedula);
        cambiarPuntos(cliente, puntosNuevos);
        notificar(cliente, "Se revirtio la transaccion #" + t.getId());
        return "Se revirtio la transaccion #" + t.getId() + " (" + t.getTipo() + " por " + pesos(t.getMonto()) + ")"
                + "\nPuntos: " + puntosAntes + " -> " + puntosNuevos;
    }

    //  puntos, rangos y canje

    public String verPuntos(String cedula) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        int puntos = tablaPuntos.obtener(cedula); // consulta rapida en la tabla hash
        String rango = calcularRango(puntos);
        String texto = "PUNTOS DE " + cliente.getNombre() + "\n\n";
        texto += "  Puntos acumulados: " + puntos + "\n";
        texto += "  Puntos canjeados: " + cliente.getPuntosCanjeados() + "\n";
        texto += "  Puntos disponibles: " + cliente.getPuntosDisponibles() + "\n";
        texto += "  Rango: " + rango + "\n";
        if (rango.equals("Bronce")) {
            texto += "  Faltan " + (501 - puntos) + " puntos para Plata\n";
        } else if (rango.equals("Plata")) {
            texto += "  Faltan " + (1001 - puntos) + " puntos para Oro\n";
        } else if (rango.equals("Oro")) {
            texto += "  Faltan " + (5001 - puntos) + " puntos para Platino\n";
        }
        texto += "\nBeneficios activos:\n";
        if (cliente.getDescuentoComisionHasta() != null && !fechaActual.isAfter(cliente.getDescuentoComisionHasta())) {
            texto += "  - 10% menos de comision hasta " + cliente.getDescuentoComisionHasta() + "\n";
        }
        if (cliente.getRetiroGratisHasta() != null && !fechaActual.isAfter(cliente.getRetiroGratisHasta())) {
            texto += "  - Retiros sin cargo hasta " + cliente.getRetiroGratisHasta() + "\n";
        }
        texto += "\n(Puntos en el arbol ABB: " + arbolPuntos.buscar(cedula) + ")";
        return texto;
    }

    // opcion 1: 100 pts, opcion 2: 500 pts, opcion 3: 1000 pts
    public String canjearPuntos(String cedula, int opcion) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        int costo;
        if (opcion == 1) {
            costo = 100;
        } else if (opcion == 2) {
            costo = 500;
        } else if (opcion == 3) {
            costo = 1000;
        } else {
            return "ERROR: opcion de canje invalida";
        }
        if (cliente.getPuntosDisponibles() < costo) {
            return "ERROR: puntos insuficientes. Necesita " + costo + " y tiene " + cliente.getPuntosDisponibles();
        }
        cliente.setPuntosCanjeados(cliente.getPuntosCanjeados() + costo);
        String mensaje;
        if (opcion == 1) {
            cliente.setDescuentoComisionHasta(fechaActual.plusDays(30));
            mensaje = "10% menos de comision en transferencias por 30 dias";
        } else if (opcion == 2) {
            cliente.setRetiroGratisHasta(fechaActual.plusMonths(1));
            mensaje = "un mes sin cargo por retiros";
        } else {
            Monedero principal = cliente.buscarMonedero("Principal");
            principal.setSaldo(principal.getSaldo() + BONO_CANJE);
            Transaccion bono = new Transaccion(++contadorTransacciones, "BONO", BONO_CANJE, 0, fechaActual,
                    cedula, null, cedula, "Principal", null, 0);
            guardarTransaccion(cliente, bono);
            mensaje = "bono de " + pesos(BONO_CANJE) + " en el monedero Principal";
        }
        notificar(cliente, "Canjeaste " + costo + " puntos: " + mensaje);
        return "Canje realizado: " + mensaje + "\nPuntos disponibles: " + cliente.getPuntosDisponibles();
    }

    public String verRanking() {
        String texto = "RANKING DE CLIENTES (arbol AVL, de mayor a menor)\n\n";
        texto += arbolRangos.ranking();
        texto += "\nCLIENTES POR RANGO\n";
        texto += "  Bronce (0 - 500): " + arbolRangos.clientesEntre(0, 500) + "\n";
        texto += "  Plata (501 - 1000): " + arbolRangos.clientesEntre(501, 1000) + "\n";
        texto += "  Oro (1001 - 5000): " + arbolRangos.clientesEntre(1001, 5000) + "\n";
        texto += "  Platino (mas de 5000): " + arbolRangos.clientesEntre(5001, Integer.MAX_VALUE) + "\n";
        texto += "\nAltura del AVL: " + arbolRangos.alturaArbol() + "   Rotaciones hechas: " + arbolRangos.getRotaciones();
        texto += "\n\nPUNTOS POR CEDULA (inorden del ABB)\n" + arbolPuntos.inorden();
        texto += "Altura del ABB: " + arbolPuntos.altura() + "   Clientes: " + arbolPuntos.getPeso();
        return texto;
    }

    //  programadas

    public String programar(String cedula, String tipo, String nombreMonedero, String cedulaDestino,
                            long monto, int dias, String frecuencia) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        if (cliente.buscarMonedero(nombreMonedero) == null) {
            return "ERROR: el cliente no tiene el monedero " + nombreMonedero;
        }
        if (!tipo.equals("DEPOSITO") && !tipo.equals("TRANSFERENCIA")) {
            return "ERROR: solo se pueden programar DEPOSITO o TRANSFERENCIA";
        }
        if (tipo.equals("TRANSFERENCIA") && buscarCliente(cedulaDestino) == null) {
            return "ERROR: no existe el cliente destino " + cedulaDestino;
        }
        if (!frecuencia.equals("UNA VEZ") && !frecuencia.equals("SEMANAL") && !frecuencia.equals("MENSUAL")) {
            return "ERROR: la frecuencia debe ser UNA VEZ, SEMANAL o MENSUAL";
        }
        if (monto <= 0 || dias < 1) {
            return "ERROR: el monto debe ser positivo y los dias al menos 1";
        }
        LocalDate fecha = fechaActual.plusDays(dias);
        programadas.insertar(new TransaccionProgramada(tipo, cedula, nombreMonedero, cedulaDestino, monto,
                fecha, frecuencia));
        notificar(cliente, "Programaste " + tipo + " de " + pesos(monto) + " para el " + fecha + " (" + frecuencia + ")");
        return "Transaccion programada para el " + fecha + " (" + frecuencia + ")";
    }

    public String verProgramadas() {
        String texto = "TRANSACCIONES PROGRAMADAS (cola de prioridad por fecha)\nHoy es " + fechaActual + "\n\n";
        NodoProgramada aux = programadas.getFrente();
        int posicion = 1;
        while (aux != null) {
            TransaccionProgramada p = aux.getProgramada();
            texto += posicion + ". " + p.getFecha() + "  " + p.getTipo() + "  " + pesos(p.getMonto())
                    + "  cliente " + buscarCliente(p.getCedula()).getNombre();
            if (p.getTipo().equals("TRANSFERENCIA")) {
                texto += " -> " + buscarCliente(p.getCedulaDestino()).getNombre();
            }
            texto += "  (" + p.getFrecuencia() + ")\n";
            aux = aux.getSiguiente();
            posicion++;
        }
        if (programadas.estaVacia()) {
            texto += "No hay transacciones programadas.";
        }
        return texto;
    }

    // Avanza la fecha un dia y ejecuta las programadas que ya tocan
    public String avanzarDia() {
        fechaActual = fechaActual.plusDays(1);
        String texto = "Nueva fecha: " + fechaActual + "\n\n";
        int ejecutadas = 0;
        while (!programadas.estaVacia() && !programadas.verPrimero().getFecha().isAfter(fechaActual)) {
            TransaccionProgramada p = programadas.sacar();
            Cliente cliente = buscarCliente(p.getCedula());
            String resultado;
            if (p.getTipo().equals("DEPOSITO")) {
                resultado = depositar(p.getCedula(), p.getMonedero(), p.getMonto());
            } else {
                resultado = transferir(p.getCedula(), p.getMonedero(), p.getCedulaDestino(), p.getMonto());
            }
            if (resultado.startsWith("ERROR")) {
                notificar(cliente, "No se pudo ejecutar la programada: " + resultado);
            } else {
                notificar(cliente, "Se ejecuto la programada: " + p.getTipo() + " de " + pesos(p.getMonto()));
                ejecutadas++;
            }
            texto += "- " + p.getTipo() + " de " + cliente.getNombre() + ": " + resultado.split("\n")[0] + "\n";

            // Si se repite, se vuelve a meter en la cola con la nueva fecha
            if (p.getFrecuencia().equals("SEMANAL")) {
                p.setFecha(p.getFecha().plusWeeks(1));
                programadas.insertar(p);
            } else if (p.getFrecuencia().equals("MENSUAL")) {
                p.setFecha(p.getFecha().plusMonths(1));
                programadas.insertar(p);
            }
        }
        // Recordatorios de las que se ejecutan manana
        NodoProgramada aux = programadas.getFrente();
        while (aux != null) {
            TransaccionProgramada p = aux.getProgramada();
            if (p.getFecha().equals(fechaActual.plusDays(1))) {
                notificar(buscarCliente(p.getCedula()), "Recordatorio: manana se ejecuta " + p.getTipo()
                        + " de " + pesos(p.getMonto()));
            }
            aux = aux.getSiguiente();
        }
        texto += "\nProgramadas ejecutadas hoy: " + ejecutadas;
        return texto;
    }

    //  notificaciones

    public String verNotificaciones(String cedula) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        String texto = "NOTIFICACIONES NUEVAS DE " + cliente.getNombre() + " (cola)\n\n";
        if (cliente.getNotificacionesPendientes().estaVacia()) {
            texto += "  No hay notificaciones nuevas\n";
        }
        while (!cliente.getNotificacionesPendientes().estaVacia()) {
            Notificacion n = cliente.getNotificacionesPendientes().desencolar();
            texto += "  " + n.toString() + "\n";
            cliente.getNotificacionesLeidas().agregar(n); // pasa a la lista circular
        }
        texto += "\nULTIMAS " + cliente.getNotificacionesLeidas().getCapacidad() + " LEIDAS (lista circular)\n";
        texto += cliente.getNotificacionesLeidas().mostrar();
        return texto;
    }

    //  reportes

    public String reporteGastos(String cedula) {
        Cliente cliente = buscarCliente(cedula);
        if (cliente == null) {
            return "ERROR: no existe el cliente " + cedula;
        }
        String texto = "REPORTE DE GASTOS DE " + cliente.getNombre() + "\n\n";
        ListaSimple<String> categorias = cliente.getGrafoCategorias().getVertices();
        long totalGastos = 0;
        Nodo<Transaccion> aux = cliente.getHistorial().getNodoPrimero();
        while (aux != null) {
            if (aux.getValorNodo().getTipo().equals("GASTO") && !aux.getValorNodo().isRevertida()) {
                totalGastos += aux.getValorNodo().getMonto();
            }
            aux = aux.getSiguienteNodo();
        }
        texto += "Gastos por categoria:\n";
        for (int i = 0; i < categorias.getTamano(); i++) {
            String categoria = categorias.obtenerValorNodo(i);
            long total = 0;
            int cantidad = 0;
            aux = cliente.getHistorial().getNodoPrimero();
            while (aux != null) {
                Transaccion t = aux.getValorNodo();
                if (t.getTipo().equals("GASTO") && !t.isRevertida() && t.getCategoria().equals(categoria)) {
                    total += t.getMonto();
                    cantidad++;
                }
                aux = aux.getSiguienteNodo();
            }
            long porcentaje = totalGastos == 0 ? 0 : total * 100 / totalGastos;
            texto += "  " + categoria + ": " + pesos(total) + " en " + cantidad + " gastos (" + porcentaje + "%)\n";
        }
        texto += "  TOTAL: " + pesos(totalGastos) + "\n";

        texto += "\nQue gasta despues de que (grafo de categorias):\n";
        ListaSimple<Grafo.Arista> aristas = cliente.getGrafoCategorias().getAristas();
        for (int i = 0; i < aristas.getTamano(); i++) {
            Grafo.Arista a = aristas.obtenerValorNodo(i);
            texto += "  " + a.getOrigen() + " -> " + a.getDestino() + ": " + a.getVeces() + " veces\n";
        }
        if (aristas.getTamano() == 0) {
            texto += "  (se necesitan al menos 2 gastos)\n";
        }

        texto += "\nMovimientos entre monederos (grafo de monederos):\n";
        ListaSimple<Grafo.Arista> movimientos = cliente.getGrafoMonederos().getAristas();
        for (int i = 0; i < movimientos.getTamano(); i++) {
            Grafo.Arista a = movimientos.obtenerValorNodo(i);
            texto += "  " + a.getOrigen() + " -> " + a.getDestino() + ": " + pesos(a.getTotal())
                    + " en " + a.getVeces() + " movimientos\n";
        }
        if (movimientos.getTamano() == 0) {
            texto += "  (no hay movimientos)\n";
        }
        return texto;
    }

    public String reporteTransferencias() {
        String texto = "REPORTE DE TRANSFERENCIAS ENTRE CLIENTES (grafo dirigido)\n\n";
        ListaSimple<Grafo.Arista> aristas = grafoTransferencias.getAristas();
        for (int i = 0; i < aristas.getTamano(); i++) {
            Grafo.Arista a = aristas.obtenerValorNodo(i);
            if (a.getVeces() > 0) {
                texto += "  " + a.getOrigen() + " -> " + a.getDestino() + ": " + pesos(a.getTotal())
                        + " en " + a.getVeces() + " transferencias\n";
            }
        }
        texto += "\nTotal enviado y recibido por cliente:\n";
        ListaSimple<String> vertices = grafoTransferencias.getVertices();
        String mayorEmisor = "";
        long mayorEnvio = 0;
        for (int i = 0; i < vertices.getTamano(); i++) {
            String v = vertices.obtenerValorNodo(i);
            long enviado = grafoTransferencias.totalSalida(v);
            texto += "  " + v + ": envio " + pesos(enviado) + ", recibio " + pesos(grafoTransferencias.totalEntrada(v)) + "\n";
            if (enviado > mayorEnvio) {
                mayorEnvio = enviado;
                mayorEmisor = v;
            }
        }
        if (mayorEnvio > 0) {
            texto += "\nEl cliente que mas transfiere es " + mayorEmisor + " con " + pesos(mayorEnvio);
        }
        return texto;
    }

    //  integridad (recursivo)

    // Suma recursivamente lo que cada transaccion le hizo al saldo de un monedero
    public long saldoRecursivo(Nodo<Transaccion> nodo, String cedula, String nombreMonedero) {
        if (nodo == null) {
            return 0; // caso base: se acabo la lista
        }
        return nodo.getValorNodo().efectoSobre(cedula, nombreMonedero)
                + saldoRecursivo(nodo.getSiguienteNodo(), cedula, nombreMonedero);
    }

    // Suma recursivamente los puntos de las transacciones no revertidas del cliente
    public int puntosRecursivos(Nodo<Transaccion> nodo, String cedula) {
        if (nodo == null) {
            return 0; // caso base
        }
        Transaccion t = nodo.getValorNodo();
        int puntos = 0;
        if (!t.isRevertida() && t.getCedulaOrigen().equals(cedula)) {
            puntos = t.getPuntos();
        }
        return puntos + puntosRecursivos(nodo.getSiguienteNodo(), cedula);
    }

    public String verificarIntegridad() {
        String texto = "VERIFICACION DE INTEGRIDAD (metodos recursivos)\n\n";
        int errores = 0;
        for (int i = 0; i < clientes.getTamano(); i++) {
            Cliente c = clientes.obtenerValorNodo(i);
            texto += c.getNombre() + ":\n";
            for (int j = 0; j < c.getMonederos().getTamano(); j++) {
                Monedero m = c.getMonederos().obtenerValorNodo(j);
                long calculado = saldoRecursivo(c.getHistorial().getNodoPrimero(), c.getCedula(), m.getNombre());
                if (calculado == m.getSaldo()) {
                    texto += "  OK saldo " + m.getNombre() + " = " + pesos(m.getSaldo()) + "\n";
                } else {
                    texto += "  ERROR saldo " + m.getNombre() + ": guardado " + pesos(m.getSaldo())
                            + " pero el historial da " + pesos(calculado) + "\n";
                    errores++;
                }
            }
            int puntos = puntosRecursivos(c.getHistorial().getNodoPrimero(), c.getCedula());
            boolean puntosBien = puntos == c.getPuntos()
                    && tablaPuntos.obtener(c.getCedula()) == puntos
                    && arbolPuntos.buscar(c.getCedula()) == puntos;
            if (puntosBien) {
                texto += "  OK puntos = " + puntos + " (cliente, tabla hash y ABB coinciden)\n";
            } else {
                texto += "  ERROR puntos: el historial da " + puntos + " pero el cliente tiene " + c.getPuntos() + "\n";
                errores++;
            }
        }
        texto += "\n" + (errores == 0 ? "Todo esta correcto." : "Se encontraron " + errores + " errores.");
        return texto;
    }

    public LocalDate getFechaActual() {
        return fechaActual;
    }

    public ListaSimple<Cliente> getClientes() {
        return clientes;
    }
}
