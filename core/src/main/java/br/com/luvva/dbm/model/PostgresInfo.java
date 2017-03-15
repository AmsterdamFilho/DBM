package br.com.luvva.dbm.model;

import br.com.jwheel.cdi.Custom;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Custom
public class PostgresInfo
{
    private String binFolderPath;

    public String getBinFolderPath ()
    {
        return binFolderPath;
    }

    public void setBinFolderPath (String binFolderPath)
    {
        this.binFolderPath = binFolderPath;
    }
}
