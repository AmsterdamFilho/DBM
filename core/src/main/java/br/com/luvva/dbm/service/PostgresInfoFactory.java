package br.com.luvva.dbm.service;

import br.com.jwheel.xml.model.FromXmlPreferences;
import br.com.jwheel.xml.service.PreferencesFactoryFromXml;
import br.com.luvva.dbm.dao.PostgresInfoDao;
import br.com.luvva.dbm.model.PostgresInfo;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Singleton
public final class PostgresInfoFactory implements PreferencesFactoryFromXml<PostgresInfo>
{
    private @Inject PostgresInfoDao             dao;
    private @Inject DefaultPostgresInfoProvider defaultPostgresInfoProvider;

    @Produces
    @FromXmlPreferences
    private PostgresInfo produce ()
    {
        return produce(dao);
    }

    @Override
    public PostgresInfo produceDefault ()
    {
        return defaultPostgresInfoProvider.provide();
    }
}
