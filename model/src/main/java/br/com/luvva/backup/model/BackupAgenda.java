package br.com.luvva.backup.model;

import br.com.jwheel.cdi.Custom;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Custom
public class BackupAgenda
{
    private List<BackupSchedule> schedules = new ArrayList<>();

    private int    version;
    private String backupFolder;

    public List<BackupSchedule> getSchedules ()
    {
        return schedules;
    }

    public int getVersion ()
    {
        return version;
    }

    public void updateVersion ()
    {
        version++;
    }

    public String getBackupFolder ()
    {
        return backupFolder;
    }

    public void setBackupFolder (String backupFolder)
    {
        this.backupFolder = backupFolder;
    }

    public LocalDateTime next ()
    {
        if (schedules.isEmpty())
        {
            return null;
        }
        LocalDateTime current = schedules.get(0).next();
        for (int i = 1; i < schedules.size(); i++)
        {
            LocalDateTime challenger = schedules.get(i).next();
            if (challenger.isBefore(current))
            {
                current = challenger;
            }
        }
        return current;
    }
}
