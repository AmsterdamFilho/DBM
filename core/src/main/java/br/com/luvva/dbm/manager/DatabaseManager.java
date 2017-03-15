package br.com.luvva.dbm.manager;

import br.com.jwheel.jpa.ConnectionParameters;

import java.nio.file.Path;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public interface DatabaseManager
{
    void backup (Path backupDirectory) throws Exception;

    void dropAndRestore (Path backupPath) throws Exception;

    void init (ConnectionParameters cp, String newUser, String newUserPassword, String newDatabase) throws Exception;
}
