package co.edu.uniquindio.poo;

import co.edu.uniquindio.poo.Service.MonederoService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Aplicacion JavaFX: carga la vista monedero.fxml y su hoja de estilos.
public class App extends Application {

    // Un solo servicio para toda la aplicacion
    public static MonederoService servicio = new MonederoService();

    @Override
    public void start(Stage stage) throws Exception {
        DatosEjemplo.cargar(servicio);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/co/edu/uniquindio/poo/monedero.fxml"));
        Parent raiz = loader.load();
        Scene escena = new Scene(raiz, 1150, 720);
        escena.getStylesheets().add(App.class.getResource("/co/edu/uniquindio/poo/estilos.css").toExternalForm());

        stage.setTitle("Monedero Virtual - Sistema de Puntos");
        stage.setScene(escena);
        stage.setMinWidth(980);
        stage.setMinHeight(640);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
