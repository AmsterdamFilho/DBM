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
public class BackupService
{
    private @Inject BackupAgenda            agenda;
    private @Inject Logger                  logger;
    private @Inject AutomaticBackupListener errorHandler;

    private Timer backupTimer         = new Timer();
    private Timer agendaUpdateChecker = new Timer("AgendaUpdateChecker");

    public void start ()
    {
        int oneMinute = 60 * 1000;
        agendaUpdateChecker.schedule(new AgendaUpdateChecker(), oneMinute, oneMinute);
        schedule();
    }

    private void schedule ()
    {
        backupTimer.cancel();
        final String backupFolder = agenda.getBackupFolder();
        LocalDateTime nextSchedule = agenda.next();
        if (!(backupFolder == null || backupFolder.trim().isEmpty() || nextSchedule == null))
        {
            backupTimer = new Timer("backupTimer");
            logger.info("Backup scheduled to " + nextSchedule.toString());
            backupTimer.schedule(new TimerTask()
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
                        errorHandler.exceptionOccurred();
                    }
                    schedule();
                }
            }, Date.from(nextSchedule.atZone(ZoneId.systemDefault()).toInstant()));
        }
        else
        {
            logger.info("Agenda is empty or backup directory is not set. Awaiting agenda update...");
        }
    }

    public void exit ()
    {
        agendaUpdateChecker.cancel();
        backupTimer.cancel();
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
