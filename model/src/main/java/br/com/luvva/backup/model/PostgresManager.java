package br.com.luvva.backup.model;

import br.com.jwheel.jpa.ConnectionParameters;
import br.com.jwheel.utils.StringUtils;
import br.com.jwheel.utils.SystemUtils;
import br.com.jwheel.xml.model.PathPreferences;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lima Filho, A. L. - amsterdam@luvva.com.br
 */
public class PostgresManager implements DatabaseManager
{
    private @Inject PostgresInfo         postgresInfo;
    private @Inject ConnectionParameters connectionParameters;
    private @Inject Logger               logger;
    private @Inject PathPreferences      pathPreferences;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");

    @Override
    public void backup (Path backupPath) throws Exception
    {
        Files.createDirectories(backupPath);
        ProcessBuilder pb = new ProcessBuilder(buildBackupCommand());
        //noinspection SpellCheckingInspection
        pb.environment().put("PGPASSWORD", connectionParameters.getPassword());
        pb.redirectErrorStream(true);
        Process backupProcess = pb.start();
        MyInputStreamReader reader = new MyInputStreamReader(backupProcess.getInputStream());
        reader.start();
        int result = backupProcess.waitFor();
        if (result != 0)
        {
            try (PrintWriter out = new PrintWriter(backupErrorFile()))
            {
                out.write(reader.getOutput());
            }
            throw new IOException("Backup process returned an error: " + result);
        }
        else
        {
            try (PrintWriter out = new PrintWriter(backupSqlFile(backupPath), "utf-8"))
            {
                out.write(reader.getOutput());
            }
            logger.info("Backup " + backupSqlFile(backupPath).getName() + " created successfully.");
        }
    }

    private List<String> buildBackupCommand () throws IOException
    {
        List<String> response = new ArrayList<>();
        response.add(pg_dumpPath().toString());
        response.add("--port");
        response.add(connectionParameters.getPort());
        response.add("--username");
        response.add(connectionParameters.getUser());
        response.add("--host");
        response.add("localhost");
        response.add(connectionParameters.getDatabase());
        return response;
    }

    private Path pg_dumpPath () throws IOException
    {
        String binFolderPathString = postgresInfo.getBinFolderPath();
        if (StringUtils.isNullOrEmpty(binFolderPathString))
        {
            throw new IOException("Postgres info bin folder path string is empty!");
        }
        Path binFolder = Paths.get(binFolderPathString);
        Path pg_dumpPath;
        if (SystemUtils.isWindows())
        {
            pg_dumpPath = binFolder.resolve("pg_dump.exe");
        }
        else
        {
            pg_dumpPath = binFolder.resolve("pg_dump");
        }
        if (Files.notExists(pg_dumpPath))
        {
            throw new IOException("pg_dump not found in bin directory!");
        }
        return pg_dumpPath;
    }

    private File backupSqlFile (Path backupPath)
    {
        return backupPath.resolve(sdf.format(new Date()) + ".sql").toFile();
    }

    private File backupErrorFile ()
    {
        return pathPreferences.getAppDataDirectory().resolve("backupError.txt").toFile();
    }

    private class MyInputStreamReader extends Thread
    {
        private StringBuilder sb = new StringBuilder();
        private InputStream inputStream;

        private MyInputStreamReader (InputStream inputStream)
        {
            this.inputStream = inputStream;
        }

        private String getOutput ()
        {
            return sb.toString();
        }

        @Override
        public void run ()
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try
            {
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                }
                reader.close();
            }
            catch (IOException e)
            {
                logger.error("Could not read process input stream", e);
            }
        }
    }

    @Override
    public void restore (Path backupPath)
    {

    }
}
