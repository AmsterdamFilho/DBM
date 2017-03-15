package br.com.luvva.dbm.model;

import br.com.jwheel.xml.service.PreferencesFactoryFromXml;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Singleton
public class BackupAgendaFactory extends PreferencesFactoryFromXml<BackupAgenda>
{
    private @Inject BackupAgendaDao dao;

    @Produces
    private BackupAgenda produce ()
    {
        return produce(dao);
    }

    @Override
    protected void setDefaultPreferences (BackupAgenda agenda)
    {
        List<BackupSchedule> schedules = new ArrayList<>();
        schedules.add(new BackupSchedule(DayOfWeek.MONDAY, LocalTime.NOON));
        schedules.add(new BackupSchedule(DayOfWeek.TUESDAY, LocalTime.NOON));
        schedules.add(new BackupSchedule(DayOfWeek.WEDNESDAY, LocalTime.NOON));
        schedules.add(new BackupSchedule(DayOfWeek.THURSDAY, LocalTime.NOON));
        schedules.add(new BackupSchedule(DayOfWeek.FRIDAY, LocalTime.NOON));
        schedules.add(new BackupSchedule(DayOfWeek.SATURDAY, LocalTime.NOON));
        agenda.setSchedules(schedules);
        agenda.setBackupFolder("");
    }
}
