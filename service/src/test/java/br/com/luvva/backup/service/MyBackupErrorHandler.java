package br.com.luvva.backup.service;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class MyBackupErrorHandler implements BackupErrorHandler
{
    @Override
    public void handleError ()
    {
        System.out.println("Backup error!");
    }
}
