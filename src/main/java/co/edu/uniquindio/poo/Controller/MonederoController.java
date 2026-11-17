package co.edu.uniquindio.poo.Controller;

import co.edu.uniquindio.poo.App;
import co.edu.uniquindio.poo.Model.Cliente;
import co.edu.uniquindio.poo.Model.Transaccion;
import co.edu.uniquindio.poo.Service.MonederoService;
import co.edu.uniquindio.poo.Structures.Nodo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

// Controlador de la vista monedero.fxml.
// Cada pestaña tiene sus campos (con fx:id) y sus metodos (con onAction).
public class MonederoController {

    private MonederoService servicio = App.servicio;

    // Cedula del cliente que esta cargado en cada formulario (para no perder los monederos elegidos)
    private String cedulaCargadaOp = "";
    private String cedulaCargadaPro = "";

    // Encabezado
    @FXML private Label lblFecha;

    // Pestaña Clientes
    @FXML private TextField txtRegCedula;
    @FXML private TextField txtRegNombre;
    @FXML private TextField txtMonCedula;
    @FXML private TextField txtMonNombre;
    @FXML private TextArea txtResultadoClientes;

    // Pestaña Operaciones
    @FXML private ComboBox<String> cmbTipoOperacion;
    @FXML private TextField txtOpCedula;
    @FXML private Label lblOpCliente;
    @FXML private ComboBox<String> cmbOpMonedero;
    @FXML private TextField txtOpCedulaDestino;
    @FXML private ComboBox<String> cmbOpMonederoDestino;
    @FXML private ComboBox<String> cmbOpCategoria;
    @FXML private TextField txtOpMonto;
    @FXML private TextArea txtResultadoOperaciones;

    // Pestaña Consultas
    @FXML private TextField txtConCedula;
    @FXML private Label lblConCliente;
    @FXML private TableView<Transaccion> tblHistorial;
    @FXML private TableColumn<Transaccion, String> colId;
    @FXML private TableColumn<Transaccion, String> colFecha;
    @FXML private TableColumn<Transaccion, String> colTipo;
    @FXML private TableColumn<Transaccion, String> colMonto;
    @FXML private TableColumn<Transaccion, String> colCargo;
    @FXML private TableColumn<Transaccion, String> colPuntos;
    @FXML private TableColumn<Transaccion, String> colEstado;
    @FXML private TextArea txtResultadoConsultas;

    // Pestaña Puntos
    @FXML private TextField txtPunCedula;
    @FXML private ComboBox<String> cmbCanje;
    @FXML private TextArea txtResultadoPuntos;

    // Pestaña Programadas
    @FXML private TextField txtProCedula;
    @FXML private Label lblProCliente;
    @FXML private ComboBox<String> cmbProTipo;
    @FXML private ComboBox<String> cmbProMonedero;
    @FXML private TextField txtProCedulaDestino;
    @FXML private TextField txtProMonto;
    @FXML private TextField txtProDias;
    @FXML private ComboBox<String> cmbProFrecuencia;
    @FXML private TextArea txtResultadoProgramadas;

    // Pestaña Reportes
    @FXML private TextField txtRepCedula;
    @FXML private TextArea txtResultadoReportes;

    // Se ejecuta automaticamente al cargar el FXML
    @FXML
    private void initialize() {
        cmbTipoOperacion.setItems(FXCollections.observableArrayList(
                "Depositar", "Retirar", "Transferir", "Mover entre monederos", "Registrar gasto"));
        cmbTipoOperacion.getSelectionModel().selectFirst();
        cmbOpCategoria.setItems(FXCollections.observableArrayList(
                "ALIMENTACION", "TRANSPORTE", "SERVICIOS", "ENTRETENIMIENTO", "SALUD", "OTROS"));
        cmbCanje.setItems(FXCollections.observableArrayList(
                "1. 100 pts: 10% menos de comision en transferencias",
                "2. 500 pts: un mes sin cargo por retiros",
                "3. 1000 pts: bono de $50.000 COP"));
        cmbProTipo.setItems(FXCollections.observableArrayList("DEPOSITO", "TRANSFERENCIA"));
        cmbProTipo.getSelectionModel().selectFirst();
        cmbProFrecuencia.setItems(FXCollections.observableArrayList("UNA VEZ", "SEMANAL", "MENSUAL"));
        cmbProFrecuencia.getSelectionModel().selectFirst();

        // Cada vez que cambia el texto de la cedula se busca el cliente.
        // Asi los demas campos se activan apenas se escribe una cedula que existe (no hace falta Enter).
        txtOpCedula.textProperty().addListener((obs, antes, ahora) -> cargarClienteOperaciones());
        txtProCedula.textProperty().addListener((obs, antes, ahora) -> cargarClienteProgramadas());
        cmbTipoOperacion.setOnAction(e -> ajustarCamposOperacion());
        cmbProTipo.setOnAction(e -> ajustarCamposProgramadas());

        configurarTablaHistorial();
        // Al inicio no hay cliente, asi que los campos siguientes a la cedula quedan bloqueados
        ajustarCamposOperacion();
        ajustarCamposProgramadas();
        actualizarFecha();
    }

    // utilidades

    private void actualizarFecha() {
        lblFecha.setText("Fecha del sistema: " + servicio.getFechaActual());
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Muestra el resultado; si empieza por ERROR tambien sale una alerta
    private void mostrarResultado(TextArea area, String resultado) {
        area.setText(resultado);
        if (resultado.startsWith("ERROR")) {
            mostrarError(resultado);
        }
    }

    // Devuelve la cedula si el cliente existe; si no, muestra el error y devuelve null
    private String validarCedula(TextField campo) {
        String cedula = campo.getText().trim();
        if (cedula.isEmpty()) {
            mostrarError("Escriba la cedula del cliente");
            campo.requestFocus();
            return null;
        }
        if (servicio.buscarCliente(cedula) == null) {
            mostrarError("No existe un cliente con la cedula " + cedula);
            campo.requestFocus();
            return null;
        }
        return cedula;
    }

    // Convierte el texto a un numero mayor que cero. Devuelve -1 si no es valido.
    private long validarMonto(TextField campo, String nombre) {
        String texto = campo.getText().trim().replace(".", "");
        long valor;
        try {
            valor = Long.parseLong(texto);
        } catch (NumberFormatException e) {
            mostrarError(nombre + " debe ser un numero (sin puntos ni letras)");
            campo.requestFocus();
            return -1;
        }
        if (valor <= 0) {
            mostrarError(nombre + " debe ser mayor que cero");
            campo.requestFocus();
            return -1;
        }
        return valor;
    }

    // Llena una lista desplegable con los monederos del cliente.
    // Si el usuario ya habia elegido un monedero (mantener) se deja elegido; si no, se elige el de la posicion porDefecto.
    private void llenarMonederos(Cliente cliente, ComboBox<String> combo, String mantener, int porDefecto) {
        ObservableList<String> nombres = FXCollections.observableArrayList();
        for (int i = 0; i < cliente.getMonederos().getTamano(); i++) {
            nombres.add(cliente.getMonederos().obtenerValorNodo(i).getNombre());
        }
        combo.setItems(nombres);
        if (mantener != null && nombres.contains(mantener)) {
            combo.setValue(mantener);
        } else {
            combo.getSelectionModel().select(Math.min(porDefecto, nombres.size() - 1));
        }
    }

    // pestaña Clientes

    @FXML
    private void registrarCliente() {
        String cedula = txtRegCedula.getText().trim();
        String nombre = txtRegNombre.getText().trim();
        if (!cedula.matches("[0-9]+")) {
            mostrarError("La cedula solo debe tener numeros");
            txtRegCedula.requestFocus();
            return;
        }
        if (nombre.isEmpty()) {
            mostrarError("Escriba el nombre del cliente");
            txtRegNombre.requestFocus();
            return;
        }
        String resultado = servicio.registrarCliente(cedula, nombre);
        mostrarResultado(txtResultadoClientes, resultado);
        if (!resultado.startsWith("ERROR")) {
            txtRegCedula.clear();
            txtRegNombre.clear();
        }
    }

    @FXML
    private void crearMonedero() {
        String cedula = validarCedula(txtMonCedula);
        if (cedula == null) return;
        String nombre = txtMonNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("Escriba el nombre del nuevo monedero");
            txtMonNombre.requestFocus();
            return;
        }
        mostrarResultado(txtResultadoClientes, servicio.crearMonedero(cedula, nombre));
        txtMonNombre.clear();
    }

    // pestaña Operaciones

    // Busca el cliente de la cedula escrita y carga sus monederos.
    // Si es el mismo cliente de antes, se respetan los monederos que ya se habian elegido.
    private void cargarClienteOperaciones() {
        String cedula = txtOpCedula.getText().trim();
        Cliente cliente = servicio.buscarCliente(cedula);
        if (cliente == null) {
            lblOpCliente.setText(cedula.isEmpty() ? "" : "No existe ese cliente");
            lblOpCliente.getStyleClass().setAll("mensaje-error");
            cmbOpMonedero.getItems().clear();
            cmbOpMonederoDestino.getItems().clear();
            cedulaCargadaOp = "";
            ajustarCamposOperacion();
            return;
        }
        boolean mismoCliente = cedula.equals(cedulaCargadaOp);
        lblOpCliente.setText(cliente.getNombre());
        lblOpCliente.getStyleClass().setAll("mensaje-ok");
        // origen: el primer monedero; destino: el segundo, para que no salgan iguales
        llenarMonederos(cliente, cmbOpMonedero, mismoCliente ? cmbOpMonedero.getValue() : null, 0);
        llenarMonederos(cliente, cmbOpMonederoDestino, mismoCliente ? cmbOpMonederoDestino.getValue() : null, 1);
        cedulaCargadaOp = cedula;
        ajustarCamposOperacion();
    }

    // Si no hay un cliente valido, todos los campos despues de la cedula quedan bloqueados.
    // Si lo hay, solo se activan los campos que necesita el tipo de operacion elegido.
    private void ajustarCamposOperacion() {
        String tipo = cmbTipoOperacion.getValue();
        boolean hayCliente = !cmbOpMonedero.getItems().isEmpty();
        cmbOpMonedero.setDisable(!hayCliente);
        txtOpMonto.setDisable(!hayCliente);
        txtOpCedulaDestino.setDisable(!hayCliente || !tipo.equals("Transferir"));
        cmbOpMonederoDestino.setDisable(!hayCliente || !tipo.equals("Mover entre monederos"));
        cmbOpCategoria.setDisable(!hayCliente || !tipo.equals("Registrar gasto"));
    }

    @FXML
    private void ejecutarOperacion() {
        // Se valida en orden y se para en el primer dato que no sirve
        String cedula = validarCedula(txtOpCedula);
        if (cedula == null) return;
        cargarClienteOperaciones();
        String monedero = cmbOpMonedero.getValue();
        if (monedero == null) {
            mostrarError("Seleccione el monedero");
            return;
        }
        String tipo = cmbTipoOperacion.getValue();
        String destino = null;
        if (tipo.equals("Transferir")) {
            destino = validarCedula(txtOpCedulaDestino);
            if (destino == null) return;
            if (destino.equals(cedula)) {
                mostrarError("No puede transferirse a si mismo (use Mover entre monederos)");
                return;
            }
        }
        if (tipo.equals("Mover entre monederos") && monedero.equals(cmbOpMonederoDestino.getValue())) {
            mostrarError("El monedero de origen y el de destino deben ser distintos");
            return;
        }
        if (tipo.equals("Registrar gasto") && cmbOpCategoria.getValue() == null) {
            mostrarError("Seleccione la categoria del gasto");
            return;
        }
        long monto = validarMonto(txtOpMonto, "El monto");
        if (monto < 0) return;

        String resultado;
        if (tipo.equals("Depositar")) {
            resultado = servicio.depositar(cedula, monedero, monto);
        } else if (tipo.equals("Retirar")) {
            resultado = servicio.retirar(cedula, monedero, monto);
        } else if (tipo.equals("Transferir")) {
            resultado = servicio.transferir(cedula, monedero, destino, monto);
        } else if (tipo.equals("Mover entre monederos")) {
            resultado = servicio.moverEntreMonederos(cedula, monedero, cmbOpMonederoDestino.getValue(), monto);
        } else {
            resultado = servicio.registrarGasto(cedula, monedero, monto, cmbOpCategoria.getValue());
        }
        mostrarResultado(txtResultadoOperaciones, resultado);
        if (!resultado.startsWith("ERROR")) {
            txtOpMonto.clear();
        }
    }

    // pestaña Consultas

    private void configurarTablaHistorial() {
        colId.setCellValueFactory(c -> new SimpleStringProperty("#" + c.getValue().getId()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().toString()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(describirTipo(c.getValue())));
        colMonto.setCellValueFactory(c -> new SimpleStringProperty(MonederoService.pesos(c.getValue().getMonto())));
        colCargo.setCellValueFactory(c -> new SimpleStringProperty(
                esPropia(c.getValue()) && c.getValue().getComision() > 0 ? MonederoService.pesos(c.getValue().getComision()) : "-"));
        colPuntos.setCellValueFactory(c -> new SimpleStringProperty(
                esPropia(c.getValue()) ? "+" + c.getValue().getPuntos() : "-"));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isRevertida() ? "REVERTIDA" : "Vigente"));
    }

    private boolean esPropia(Transaccion t) {
        return t.getCedulaOrigen().equals(txtConCedula.getText().trim());
    }

    private String describirTipo(Transaccion t) {
        if (t.getTipo().equals("TRANSFERENCIA")) {
            if (esPropia(t)) {
                return "Transferencia a " + servicio.buscarCliente(t.getCedulaDestino()).getNombre();
            }
            return "Recibida de " + servicio.buscarCliente(t.getCedulaOrigen()).getNombre();
        }
        if (t.getTipo().equals("GASTO")) {
            return "Gasto " + t.getCategoria();
        }
        if (t.getTipo().equals("MOVIMIENTO")) {
            return "Movimiento " + t.getMonederoOrigen() + " -> " + t.getMonederoDestino();
        }
        return t.getTipo().charAt(0) + t.getTipo().substring(1).toLowerCase();
    }

    // Llena la tabla recorriendo la lista enlazada del historial
    private void llenarTabla(Cliente cliente) {
        ObservableList<Transaccion> filas = FXCollections.observableArrayList();
        Nodo<Transaccion> aux = cliente.getHistorial().getNodoPrimero();
        while (aux != null) {
            filas.add(aux.getValorNodo());
            aux = aux.getSiguienteNodo();
        }
        tblHistorial.setItems(filas);
        lblConCliente.setText(cliente.getNombre() + " · " + filas.size() + " transacciones");
        lblConCliente.getStyleClass().setAll("mensaje-ok");
    }

    @FXML
    private void consultarSaldo() {
        String cedula = validarCedula(txtConCedula);
        if (cedula == null) return;
        llenarTabla(servicio.buscarCliente(cedula));
        mostrarResultado(txtResultadoConsultas, servicio.consultarSaldo(cedula));
    }

    @FXML
    private void verHistorial() {
        String cedula = validarCedula(txtConCedula);
        if (cedula == null) return;
        llenarTabla(servicio.buscarCliente(cedula));
        Cliente cliente = servicio.buscarCliente(cedula);
        Transaccion cima = cliente.getReversibles().verCima();
        txtResultadoConsultas.setText("Pila de reversibles: " + cliente.getReversibles().getTamano() + " transacciones.\n"
                + (cima == null ? "La pila esta vacia." : "En la cima esta la #" + cima.getId() + " (" + cima.getTipo() + ")"));
    }

    @FXML
    private void verNotificaciones() {
        String cedula = validarCedula(txtConCedula);
        if (cedula == null) return;
        mostrarResultado(txtResultadoConsultas, servicio.verNotificaciones(cedula));
    }

    @FXML
    private void revertirUltima() {
        String cedula = validarCedula(txtConCedula);
        if (cedula == null) return;
        mostrarResultado(txtResultadoConsultas, servicio.revertirUltima(cedula));
        llenarTabla(servicio.buscarCliente(cedula));
    }

    // pestaña Puntos

    @FXML
    private void verPuntos() {
        String cedula = validarCedula(txtPunCedula);
        if (cedula == null) return;
        mostrarResultado(txtResultadoPuntos, servicio.verPuntos(cedula));
    }

    @FXML
    private void canjearPuntos() {
        String cedula = validarCedula(txtPunCedula);
        if (cedula == null) return;
        if (cmbCanje.getValue() == null) {
            mostrarError("Seleccione el beneficio que desea canjear");
            return;
        }
        int opcion = cmbCanje.getSelectionModel().getSelectedIndex() + 1;
        mostrarResultado(txtResultadoPuntos, servicio.canjearPuntos(cedula, opcion));
    }

    @FXML
    private void verRanking() {
        txtResultadoPuntos.setText(servicio.verRanking());
    }

    // pestaña Programadas

    private void cargarClienteProgramadas() {
        String cedula = txtProCedula.getText().trim();
        Cliente cliente = servicio.buscarCliente(cedula);
        if (cliente == null) {
            lblProCliente.setText(cedula.isEmpty() ? "" : "No existe ese cliente");
            lblProCliente.getStyleClass().setAll("mensaje-error");
            cmbProMonedero.getItems().clear();
            cedulaCargadaPro = "";
            ajustarCamposProgramadas();
            return;
        }
        boolean mismoCliente = cedula.equals(cedulaCargadaPro);
        lblProCliente.setText(cliente.getNombre());
        lblProCliente.getStyleClass().setAll("mensaje-ok");
        llenarMonederos(cliente, cmbProMonedero, mismoCliente ? cmbProMonedero.getValue() : null, 0);
        cedulaCargadaPro = cedula;
        ajustarCamposProgramadas();
    }

    // Igual que en Operaciones: sin cliente valido no se pueden llenar los demas campos
    private void ajustarCamposProgramadas() {
        boolean hayCliente = !cmbProMonedero.getItems().isEmpty();
        cmbProTipo.setDisable(!hayCliente);
        cmbProMonedero.setDisable(!hayCliente);
        txtProMonto.setDisable(!hayCliente);
        txtProDias.setDisable(!hayCliente);
        cmbProFrecuencia.setDisable(!hayCliente);
        txtProCedulaDestino.setDisable(!hayCliente || !cmbProTipo.getValue().equals("TRANSFERENCIA"));
    }

    @FXML
    private void programar() {
        String cedula = validarCedula(txtProCedula);
        if (cedula == null) return;
        cargarClienteProgramadas();
        String tipo = cmbProTipo.getValue();
        String destino = "";
        if (tipo.equals("TRANSFERENCIA")) {
            destino = validarCedula(txtProCedulaDestino);
            if (destino == null) return;
            if (destino.equals(cedula)) {
                mostrarError("No puede transferirse a si mismo");
                return;
            }
        }
        long monto = validarMonto(txtProMonto, "El monto");
        if (monto < 0) return;
        long dias = validarMonto(txtProDias, "Los dias");
        if (dias < 0) return;
        mostrarResultado(txtResultadoProgramadas, servicio.programar(cedula, tipo, cmbProMonedero.getValue(),
                destino, monto, (int) dias, cmbProFrecuencia.getValue()));
    }

    @FXML
    private void verProgramadas() {
        txtResultadoProgramadas.setText(servicio.verProgramadas());
    }

    @FXML
    private void avanzarDia() {
        txtResultadoProgramadas.setText(servicio.avanzarDia());
        actualizarFecha();
    }

    // pestaña Reportes

    @FXML
    private void reporteGastos() {
        String cedula = validarCedula(txtRepCedula);
        if (cedula == null) return;
        mostrarResultado(txtResultadoReportes, servicio.reporteGastos(cedula));
    }

    @FXML
    private void reporteTransferencias() {
        txtResultadoReportes.setText(servicio.reporteTransferencias());
    }

    @FXML
    private void verificarIntegridad() {
        txtResultadoReportes.setText(servicio.verificarIntegridad());
    }
}
