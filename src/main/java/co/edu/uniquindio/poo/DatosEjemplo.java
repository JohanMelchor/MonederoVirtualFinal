package co.edu.uniquindio.poo;

import co.edu.uniquindio.poo.Service.MonederoService;

// Datos para probar el programa sin tener que escribir todo
public class DatosEjemplo {

    public static void cargar(MonederoService servicio) {
        servicio.registrarCliente("41923456", "Carlos Ramirez");
        servicio.registrarCliente("1094887766", "Laura Gomez");
        servicio.registrarCliente("1097654321", "Andres Torres");
        servicio.registrarCliente("1005123987", "Sofia Castano");

        // Carlos
        servicio.depositar("41923456", "Principal", 5000000);
        servicio.transferir("41923456", "Principal", "1094887766", 2000000);
        servicio.transferir("41923456", "Principal", "1097654321", 300000);

        // Laura
        servicio.depositar("1094887766", "Principal", 2500000);
        servicio.registrarGasto("1094887766", "Principal", 120000, "ALIMENTACION");
        servicio.registrarGasto("1094887766", "Principal", 45000, "TRANSPORTE");
        servicio.registrarGasto("1094887766", "Principal", 80000, "ALIMENTACION");
        servicio.registrarGasto("1094887766", "Principal", 150000, "ENTRETENIMIENTO");
        servicio.registrarGasto("1094887766", "Principal", 30000, "TRANSPORTE");
        servicio.moverEntreMonederos("1094887766", "Principal", "Ahorros", 1000000);
        servicio.transferir("1094887766", "Principal", "1005123987", 1200000);

        // Andres
        servicio.depositar("1097654321", "Principal", 800000);
        servicio.retirar("1097654321", "Principal", 100000);
        servicio.registrarGasto("1097654321", "Principal", 180000, "SERVICIOS");

        // Sofia
        servicio.depositar("1005123987", "Principal", 600000);
        servicio.transferir("1005123987", "Principal", "41923456", 400000);

        // Programadas
        servicio.programar("1094887766", "DEPOSITO", "Principal", "", 2500000, 30, "MENSUAL");
        servicio.programar("41923456", "TRANSFERENCIA", "Principal", "1097654321", 100000, 2, "SEMANAL");
    }
}
