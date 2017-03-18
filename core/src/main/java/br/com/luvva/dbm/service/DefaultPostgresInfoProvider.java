package br.com.luvva.dbm.service;

import br.com.jwheel.utils.SystemUtils;
import br.com.luvva.dbm.model.PostgresInfo;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class DefaultPostgresInfoProvider
{
    public PostgresInfo provide ()
    {
        if (SystemUtils.isWindows())
        {
            return new PostgresInfo("c:\\Program Files\\PostgreSQL\\9.6\\bin");
        }
        return new PostgresInfo("");
    }
}
