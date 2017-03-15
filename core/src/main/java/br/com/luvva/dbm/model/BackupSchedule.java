package br.com.luvva.dbm.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class BackupSchedule
{
    private DayOfWeek backupDay;
    private LocalTime backupTime;

    public BackupSchedule ()
    {
    }

    public BackupSchedule (DayOfWeek backupDay, LocalTime backupTime)
    {
        this.backupDay = backupDay;
        this.backupTime = backupTime;
    }

    public DayOfWeek getBackupDay ()
    {
        return backupDay;
    }

    public void setBackupDay (DayOfWeek backupDay)
    {
        this.backupDay = backupDay;
    }

    public LocalTime getBackupTime ()
    {
        return backupTime;
    }

    public void setBackupTime (LocalTime backupTime)
    {
        this.backupTime = backupTime;
    }

    public LocalDateTime next ()
    {
        if (backupDay == null || backupTime == null)
        {
            return null;
        }
        LocalDateTime response = LocalDateTime.of(LocalDate.now(), backupTime);
        while (response.getDayOfWeek() != backupDay || response.isBefore(LocalDateTime.now()))
        {
            response = response.plusDays(1);
        }
        return response;
    }
}
