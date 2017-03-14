package br.com.luvva.backup.model;

import br.com.jwheel.xml.dao.GenericXStreamDao;
import com.thoughtworks.xstream.XStream;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class PostgresInfoDao extends GenericXStreamDao<PostgresInfo>
{
    @Override
    protected XStream createXStream ()
    {
        XStream xStream = super.createXStream();
        xStream.alias("postgresInfo", PostgresInfo.class);
        return xStream;
    }
}
