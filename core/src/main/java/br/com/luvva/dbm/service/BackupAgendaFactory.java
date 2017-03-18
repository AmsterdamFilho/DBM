package br.com.luvva.dbm.service;

import br.com.jwheel.xml.model.FromXmlPreferences;
import br.com.jwheel.xml.service.PreferencesFactoryFromXml;
import br.com.luvva.dbm.dao.BackupAgendaDao;
import br.com.luvva.dbm.model.BackupAgenda;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Singleton
public final class BackupAgendaFactory implements PreferencesFactoryFromXml<BackupAgenda>
{
    private @Inject BackupAgendaDao             dao;
    private @Inject DefaultBackupAgendaProvider defaultBackupAgendaProvider;

    @Produces
    @FromXmlPreferences
    private BackupAgenda produce ()
    {
        return produce(dao);
    }

    @Override
    public BackupAgenda produceDefault ()
    {
        return defaultBackupAgendaProvider.provide();
    }
}
