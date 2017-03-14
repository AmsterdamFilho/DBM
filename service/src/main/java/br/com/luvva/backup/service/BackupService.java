package br.com.luvva.backup.service;

import br.com.jwheel.cdi.WeldContext;
import br.com.luvva.backup.model.BackupAgenda;
import br.com.luvva.backup.model.DatabaseManager;
import org.slf4j.Logger;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Singleton
@Default
class BackupService
{
    private @Inject BackupAgenda       agenda;
    private @Inject Logger             logger;
    private @Inject BackupErrorHandler errorHandler;

    private Timer timer = new Timer();

    void start ()
    {
        Timer timer = new Timer("AgendaUpdateChecker", false);
        int oneMinute = 60 * 1000;
        timer.schedule(new AgendaUpdateChecker(), oneMinute, oneMinute);
        schedule();
    }

    private void schedule ()
    {
        timer.cancel();
        final String backupFolder = agenda.getBackupFolder();
        if (!(backupFolder == null || backupFolder.trim().isEmpty()))
        {
            timer = new Timer("backupTask", false);
            LocalDateTime next = agenda.next();
            logger.info("Backup scheduled to " + next.toString());
            timer.schedule(new TimerTask()
            {
                @Override
                public void run ()
                {
                    try
                    {
                        WeldContext.getInstance().getDefault(DatabaseManager.class).backup(Paths.get(backupFolder));
                    }
                    catch (Exception e)
                    {
                        logger.error("Backup exception!", e);
                        errorHandler.handleError();
                    }
                    schedule();
                }
            }, Date.from(next.atZone(ZoneId.systemDefault()).toInstant()));
        }
    }

    private class AgendaUpdateChecker extends TimerTask
    {
        public void run ()
        {
            BackupAgenda current = WeldContext.getInstance().getDefault(BackupAgenda.class);
            if (current.getVersion() > agenda.getVersion())
            {
                logger.info("Agenda version changed! Rescheduling...");
                agenda = current;
                schedule();
            }
        }
    }
}
