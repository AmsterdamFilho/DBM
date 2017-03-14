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

    //<editor-fold desc="Backup">

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");

    @Override
    public void backup (Path backupDirectory) throws Exception
    {
        Files.createDirectories(backupDirectory);
        Process backupProcess = createProcess(executablePath("pg_dump"));
        Path backupFilePath = backupSqlFile(backupDirectory);
        InputStreamExporter exporter = new InputStreamExporter(backupProcess.getInputStream(), backupFilePath);
        exporter.start();
        int result = backupProcess.waitFor();
        if (result == 0)
        {
            logger.info("Backup " + backupFilePath.toString() + " created successfully.");
        }
        else
        {
            Path errorFile = errorFile(sdf.format(new Date()) + "-backupError");
            Files.move(backupFilePath, errorFile);
            throw new IOException("Error executing backup! The file " + errorFile.toString() + " has the details.");
        }
    }

    private Path backupSqlFile (Path backupPath)
    {
        return backupPath.resolve(sdf.format(new Date()) + ".sql");
    }

    //</editor-fold>

    //<editor-fold desc="Drop">

    @Override
    public void drop () throws Exception
    {

    }

    //</editor-fold>

    //<editor-fold desc="Restore">

    @Override
    public void restore (Path backupPath)
    {

    }

    //</editor-fold>

    //<editor-fold desc="Utilities">

    private Path errorFile (String name)
    {
        return pathPreferences.getAppDataDirectory().resolve(name + ".txt");
    }

    private String executablePath (String executable) throws IOException
    {
        String binFolderPathString = postgresInfo.getBinFolderPath();
        if (StringUtils.isNullOrEmpty(binFolderPathString))
        {
            throw new IOException("Postgres info bin folder path string is empty!");
        }
        Path binFolder = Paths.get(binFolderPathString);
        Path response;
        if (SystemUtils.isWindows())
        {
            response = binFolder.resolve(executable + ".exe");
        }
        else
        {
            response = binFolder.resolve(executable);
        }
        if (Files.notExists(response))
        {
            throw new IOException(executable + " not found in bin directory!");
        }
        return response.toString();
    }

    private class InputStreamExporter extends Thread
    {
        private InputStream inputStream;
        private Path        filePath;

        private InputStreamExporter (InputStream inputStream, Path filePath)
        {
            this.inputStream = inputStream;
            this.filePath = filePath;
        }

        @Override
        public void run ()
        {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                 BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.toFile())))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    bw.write(line + "\n");
                }
            }
            catch (IOException e)
            {
                logger.error("Could not write input stream to file.", e);
            }
        }
    }

    private Process createProcess (String executable) throws IOException
    {
        List<String> command = new ArrayList<>();
        command.add(executable);
        command.add("--port");
        command.add(connectionParameters.getPort());
        command.add("--username");
        command.add(connectionParameters.getUser());
        command.add("--host");
        command.add("localhost");
        command.add(connectionParameters.getDatabase());
        ProcessBuilder pb = new ProcessBuilder(command);
        //noinspection SpellCheckingInspection
        pb.environment().put("PGPASSWORD", connectionParameters.getPassword());
        pb.redirectErrorStream(true);
        return pb.start();
    }

    //</editor-fold>
}
