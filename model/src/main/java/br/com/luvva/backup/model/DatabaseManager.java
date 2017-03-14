package br.com.luvva.backup.model;

import java.nio.file.Path;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public interface DatabaseManager
{
    void backup (Path backupDirectory) throws Exception;

    void drop () throws Exception;

    void restore (Path backupPath);
}
