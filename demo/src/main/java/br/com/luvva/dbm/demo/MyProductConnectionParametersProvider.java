package br.com.luvva.dbm.demo;

import br.com.jwheel.jpa.model.ConnectionParameters;
import br.com.jwheel.jpa.service.ProductDatabaseParametersProvider;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class MyProductConnectionParametersProvider implements ProductDatabaseParametersProvider
{
    @Override
    public ConnectionParameters provide ()
    {
        ConnectionParameters connectionParameters = new ConnectionParameters();
        connectionParameters.setDriver("org.postgresql.Driver");
        connectionParameters.setPassword("dbm");
        connectionParameters.setUrl("jdbc:postgresql://localhost:5432/dbm");
        connectionParameters.setUser("dbm");
        return connectionParameters;
    }
}
