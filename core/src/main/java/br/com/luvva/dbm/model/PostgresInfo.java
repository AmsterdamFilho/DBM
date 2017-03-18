package br.com.luvva.dbm.model;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class PostgresInfo
{
    private String binFolderPath;

    public PostgresInfo ()
    {
    }

    public PostgresInfo (String binFolderPath)
    {
        this.binFolderPath = binFolderPath;
    }

    public String getBinFolderPath ()
    {
        return binFolderPath;
    }

    public void setBinFolderPath (String binFolderPath)
    {
        this.binFolderPath = binFolderPath;
    }
}
