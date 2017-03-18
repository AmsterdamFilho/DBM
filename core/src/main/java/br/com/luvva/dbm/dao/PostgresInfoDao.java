package br.com.luvva.dbm.dao;

import br.com.jwheel.xml.dao.GenericXStreamDao;
import br.com.luvva.dbm.model.PostgresInfo;
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
