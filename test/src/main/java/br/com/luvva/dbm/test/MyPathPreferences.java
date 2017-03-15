package br.com.luvva.dbm.test;

import br.com.jwheel.xml.model.PathPreferences;

import javax.enterprise.inject.Specializes;
import javax.inject.Singleton;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
@Specializes
@Singleton
public class MyPathPreferences extends PathPreferences
{
    @Override
    public String getRootFolderName ()
    {
        return "dbm-test";
    }
}
