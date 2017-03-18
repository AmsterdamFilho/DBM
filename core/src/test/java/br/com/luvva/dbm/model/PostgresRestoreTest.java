package br.com.luvva.dbm.model;

import br.com.jwheel.weld.WeldContext;
import br.com.luvva.dbm.manager.PostgresManager;
import br.com.luvva.dbm.test.MyPathPreferences;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class PostgresRestoreTest
{
    private @Inject MyPathPreferences pathPreferences;

    @PostConstruct
    private void init ()
    {
        PostgresManager postgresManager = WeldContext.getInstance().getAny(PostgresManager.class);
        try
        {
            postgresManager.dropAndRestore(pathPreferences.getAppDataDirectory().resolve("backup.sql"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main (String[] args)
    {
        WeldContext.getInstance().getAny(PostgresRestoreTest.class);
    }
}
