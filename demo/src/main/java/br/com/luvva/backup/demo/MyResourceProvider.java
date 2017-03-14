package br.com.luvva.backup.demo;

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
        return "backup-test";
    }

    URL getMainSceneFxml ()
    {
        return getFxml("mainScene");
    }
}
