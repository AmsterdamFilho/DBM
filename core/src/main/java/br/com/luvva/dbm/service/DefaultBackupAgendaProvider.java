package br.com.luvva.dbm.service;

import br.com.luvva.dbm.model.BackupAgenda;
import br.com.luvva.dbm.model.BackupSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class DefaultBackupAgendaProvider
{
    public BackupAgenda provide ()
    {
        BackupAgenda agenda = new BackupAgenda();
        List<BackupSchedule> schedules = new ArrayList<>();
        schedules.add(new BackupSchedule(DayOfWeek.MONDAY, LocalTime.NOON));
        schedules.add(new BackupSchedule(DayOfWeek.TUESDAY, LocalTime.NOON));
        schedules.add(new BackupSchedule(DayOfWeek.WEDNESDAY, LocalTime.NOON));
        schedules.add(new BackupSchedule(DayOfWeek.THURSDAY, LocalTime.NOON));
        schedules.add(new BackupSchedule(DayOfWeek.FRIDAY, LocalTime.NOON));
        schedules.add(new BackupSchedule(DayOfWeek.SATURDAY, LocalTime.NOON));
        agenda.setSchedules(schedules);
        agenda.setBackupFolder("");
        return agenda;
    }
}
