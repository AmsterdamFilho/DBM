package br.com.luvva.dbm.model;

import br.com.jwheel.xml.dao.GenericXStreamDao;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.IOException;
import java.time.LocalTime;

import static br.com.jwheel.utils.StringUtils.leftPadIntWithZeros;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class BackupAgendaDao extends GenericXStreamDao<BackupAgenda>
{
    @Override
    protected XStream createXStream ()
    {
        XStream xStream = super.createXStream();
        xStream.setMode(XStream.NO_REFERENCES);

        xStream.registerConverter(new BackupTimeConverter());

        xStream.alias("schedule", BackupSchedule.class);
        xStream.alias("agenda", BackupAgenda.class);
        xStream.addImplicitCollection(BackupAgenda.class, "schedules");
        return xStream;
    }

    @Override
    public void merge (BackupAgenda agenda) throws XStreamException, IOException
    {
        agenda.updateVersion();
        super.merge(agenda);
    }

    private class BackupTimeConverter implements Converter
    {
        @Override
        public void marshal (Object o, HierarchicalStreamWriter writer, MarshallingContext context)
        {
            LocalTime backupTime = (LocalTime) o;
            writer.setValue(leftPadIntWithZeros(backupTime.getHour(), 2) + ":" + leftPadIntWithZeros(backupTime
                    .getMinute(), 2));
        }

        @Override
        public Object unmarshal (HierarchicalStreamReader reader, UnmarshallingContext context)
        {
            String time = reader.getValue();
            if (time == null || !time.matches("[0-9]{2}:[0-9]{2}"))
            {
                throw new ConversionException("Could not convert backupTime: " + time);
            }
            return LocalTime.of(Integer.valueOf(time.substring(0, time.indexOf(":"))),
                    Integer.valueOf(time.substring(time.indexOf(":") + 1)));
        }

        @Override
        public boolean canConvert (Class aClass)
        {
            return aClass.equals(LocalTime.class);
        }
    }
}
