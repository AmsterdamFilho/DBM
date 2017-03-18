package br.com.luvva.dbm.service;

import br.com.jwheel.weld.WeldContext;
import br.com.jwheel.xml.model.FromXmlPreferences;
import br.com.luvva.dbm.manager.DatabaseManager;
import br.com.luvva.dbm.model.BackupAgenda;
import org.slf4j.Logger;

import javax.enterprise.util.AnnotationLiteral;
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
public class BackupService
{
    private @Inject Logger                  logger;
    private @Inject AutomaticBackupListener errorHandler;
    private @Inject DatabaseManager         databaseManager;

    private Timer backupTimer         = new Timer();
    private Timer agendaUpdateChecker = new Timer("AgendaUpdateChecker");

    private BackupAgenda agenda;

    public void start ()
    {
        agenda = getCurrentAgenda();
        schedule();

        int oneMinute = 60 * 1000;
        agendaUpdateChecker.schedule(new AgendaUpdateChecker(), oneMinute, oneMinute);
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
                        databaseManager.backup(Paths.get(backupFolder));
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

    private BackupAgenda getCurrentAgenda ()
    {
        return WeldContext.getInstance().getWithQualifiers(BackupAgenda.class, new
                AnnotationLiteral<FromXmlPreferences>() {});
    }

    private class AgendaUpdateChecker extends TimerTask
    {
        @Override
        public void run ()
        {
            BackupAgenda current = getCurrentAgenda();
            if (current.getVersion() > agenda.getVersion())
            {
                logger.info("Agenda version changed! Rescheduling...");
                agenda = current;
                schedule();
            }
        }
    }
}
