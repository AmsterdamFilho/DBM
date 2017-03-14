package br.com.luvva.backup.service;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class ServiceTestAutomaticBackupListener implements AutomaticBackupListener
{
    @Override
    public void exceptionOccurred ()
    {
        System.out.println("Backup error!");
    }
}
