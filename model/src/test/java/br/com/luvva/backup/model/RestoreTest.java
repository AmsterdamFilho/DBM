package br.com.luvva.backup.model;

import br.com.jwheel.cdi.WeldContext;
import br.com.jwheel.logging.JwLoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class RestoreTest
{
    private @Inject JwLoggerFactory loggerFactory;

    @PostConstruct
    private void init ()
    {
        loggerFactory.init();
        PostgresManager postgresManager = WeldContext.getInstance().getAny(PostgresManager.class);
        try
        {
            postgresManager.drop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main (String[] args)
    {
        WeldContext.getInstance().getAny(RestoreTest.class);
    }
}
