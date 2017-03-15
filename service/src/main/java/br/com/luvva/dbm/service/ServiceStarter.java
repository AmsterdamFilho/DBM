package br.com.luvva.dbm.service;

import br.com.jwheel.cdi.WeldContext;
import br.com.jwheel.logging.JwLoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class ServiceStarter
{
    private @Inject JwLoggerFactory loggerFactory;

    @PostConstruct
    private void init ()
    {
        loggerFactory.init();
        WeldContext.getInstance().getAny(BackupService.class).start();
    }

    public static void main (String[] args)
    {
        WeldContext.getInstance().getAny(ServiceStarter.class);
    }
}
