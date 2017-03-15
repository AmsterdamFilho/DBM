package br.com.luvva.dbm.model;

import br.com.jwheel.xml.service.PreferencesFactoryFromXml;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Singleton
public class PostgresInfoFactory extends PreferencesFactoryFromXml<PostgresInfo>
{
    private @Inject PostgresInfoDao dao;

    @Produces
    private PostgresInfo produce ()
    {
        return produce(dao);
    }

    @Override
    protected void setDefaultPreferences (PostgresInfo info)
    {
        info.setBinFolderPath("");
    }
}
