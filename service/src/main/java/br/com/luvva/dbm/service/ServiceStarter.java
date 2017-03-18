package br.com.luvva.dbm.service;

import br.com.jwheel.weld.WeldContext;

import javax.annotation.PostConstruct;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class ServiceStarter
{
    @PostConstruct
    private void init ()
    {
        WeldContext.getInstance().getAny(BackupService.class).start();
    }

    public static void main (String[] args)
    {
        WeldContext.getInstance().getAny(ServiceStarter.class);
    }
}
