package br.com.luvva.dbm.demo;

import br.com.jwheel.utils.ResourceProvider;

import javax.inject.Singleton;
import java.net.URL;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Singleton
public class MyResourceProvider extends ResourceProvider
{
    @Override
    protected String root ()
    {
        return "dbm-test";
    }

    URL getMainSceneFxml ()
    {
        return getFxml("mainScene");
    }
}
