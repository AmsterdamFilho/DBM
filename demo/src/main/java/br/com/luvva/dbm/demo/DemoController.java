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

    public void dropAndRestore ()
    {
        try
        {
            databaseManager.dropAndRestore(pathPreferences.getAppDataDirectory().resolve("backup.sql"));
            showSuccessAlert("Database has been restored!");
        }
        catch (Exception e)
        {
            showErrorAlert("Database drop and restore was not successful!");
            logger.error("Error in restoring routine!", e);
        }
    }

    public void init ()
    {
        try
        {
            databaseManager.init();
            showSuccessAlert("Database init successful!");
        }
        catch (Exception e)
        {
            showErrorAlert("Database init was not successful!");
            logger.error("Error in init routine!", e);
        }
    }

    @Override
    public void exceptionOccurred ()
    {
        Platform.runLater(() -> showErrorAlert("Error during automatic backup!"));
    }

    private void showSuccessAlert (String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success Dialog");
        alert.setHeaderText("Nice...");
        alert.setContentText(message);
        alert.showAndWait();
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
