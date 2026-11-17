# Monedero Virtual

Proyecto final de Estructuras de Datos - Universidad del Quindio 2026-2.
Autores:
Maria Jose Ocampo Tovar
Johan Esteban Melchor Morales

Simulador de un monedero virtual con sistema de puntos. Los montos estan en pesos colombianos (COP).
La interfaz esta hecha con JavaFX (FXML + controlador + hoja de estilos).

## Como ejecutarlo

Se necesita Java 17 o superior y JavaFX 13.

- Con los scripts (usan JavaFX 13 del repositorio local de Maven):
  1. `compilar.bat` genera `MonederoVirtual.jar`.
  2. `ejecutar.bat` abre el programa.
- Con Maven: `mvn javafx:run` (descarga JavaFX si no esta).

## Organizacion

- `Main.java`: clase de arranque.
- `App.java`: aplicacion JavaFX, carga `monedero.fxml` y `estilos.css`.
- `DatosEjemplo.java`: 4 clientes de ejemplo.
- `Controller/MonederoController.java`: controlador de la ventana (metodos con @FXML).
- `Model`: Cliente, Monedero, Transaccion, TransaccionProgramada, Notificacion.
- `Service`: MonederoService, con toda la logica.
- `Structures`: las estructuras de datos hechas por nosotros (lista, pila, colas, lista circular, ABB, AVL, tabla hash y grafo).
- `src/main/resources/co/edu/uniquindio/poo`: `monedero.fxml` (la vista) y `estilos.css`.

## Pestañas

Clientes · Operaciones · Consultas · Puntos y rangos · Programadas · Reportes.

## Reglas de puntos

- Deposito: 1 punto por cada $10.000 COP.
- Retiro y gasto: 2 puntos por cada $10.000 COP.
- Transferencia: 3 puntos por cada $10.000 COP.
- Rangos: Bronce 0-500, Plata 501-1000, Oro 1001-5000, Platino mas de 5000.
