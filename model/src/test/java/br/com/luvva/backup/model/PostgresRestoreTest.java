package br.com.luvva.backup.model;

import br.com.jwheel.cdi.WeldContext;
import br.com.jwheel.logging.JwLoggerFactory;
import br.com.luvva.backup.test.MyPathPreferences;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class PostgresRestoreTest
{
    private @Inject JwLoggerFactory   loggerFactory;
    private @Inject MyPathPreferences pathPreferences;

    @PostConstruct
    private void init ()
    {
        loggerFactory.init();
        PostgresManager postgresManager = WeldContext.getInstance().getAny(PostgresManager.class);
        try
        {
            postgresManager.drop();
            postgresManager.restore(pathPreferences.getAppDataDirectory().resolve("backup.sql"));
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
