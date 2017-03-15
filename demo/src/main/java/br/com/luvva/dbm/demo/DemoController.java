package br.com.luvva.dbm.demo;

import br.com.luvva.dbm.manager.DatabaseManager;
import br.com.luvva.dbm.service.AutomaticBackupListener;
import br.com.luvva.dbm.test.MyPathPreferences;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Singleton
public class DemoController implements AutomaticBackupListener
{
    private @Inject DatabaseManager   databaseManager;
    private @Inject MyPathPreferences pathPreferences;
    private @Inject Logger            logger;

    public void restore ()
    {
        try
        {
            databaseManager.restore(pathPreferences.getAppDataDirectory().resolve("backup.sql"));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success Dialog");
            alert.setHeaderText("Nice...");
            alert.setContentText("Database has been restored!");
            alert.showAndWait();
        }
        catch (Exception e)
        {
            showErrorAlert("Database restore was not successful!");
            logger.error("Error in restoring routine!", e);
        }
    }

    @Override
    public void exceptionOccurred ()
    {
        Platform.runLater(() -> showErrorAlert("Error during automatic backup!"));
    }

    private void showErrorAlert (String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Oops...");
        alert.setContentText(message);

        alert.showAndWait();
    }
}
