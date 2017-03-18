package br.com.luvva.dbm.demo;

import br.com.jwheel.javafx.JWheelFxmlLoader;
import br.com.jwheel.javafx.JavaFxApplication;
import br.com.luvva.dbm.service.BackupService;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;

import javax.inject.Inject;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class DemoApplication implements JavaFxApplication
{
    private @Inject BackupService      backupService;
    private @Inject Logger             logger;
    private @Inject MyResourceProvider resourceProvider;

    @Override
    public void init (Stage primaryStage)
    {
        try
        {
            backupService.start();
            Stage newPrimaryStage = new Stage();
            newPrimaryStage.setTitle("Backup manager test (postgres)");
            newPrimaryStage.setScene(new Scene(JWheelFxmlLoader.loadWithCdi(resourceProvider.getMainSceneFxml())));
            newPrimaryStage.centerOnScreen();

            primaryStage.close();
            newPrimaryStage.setOnCloseRequest(event -> closingEvent());
            newPrimaryStage.show();
        }
        catch (Exception ex)
        {
            logger.error("Can't load Backup Manager Test!", ex);
            primaryStage.close();
        }
    }

    private void closingEvent ()
    {
        backupService.exit();
    }
}
