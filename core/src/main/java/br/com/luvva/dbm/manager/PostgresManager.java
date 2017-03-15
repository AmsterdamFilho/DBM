package br.com.luvva.dbm.manager;

import br.com.jwheel.jpa.ConnectionParameters;
import br.com.jwheel.utils.StringUtils;
import br.com.jwheel.utils.SystemUtils;
import br.com.jwheel.xml.model.PathPreferences;
import br.com.luvva.dbm.model.PostgresInfo;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        Process backupProcess = startProcess(executablePath("pg_dump"));
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
        Process psql = startProcess(executablePath("psql"), dropCommand());
        Path errorFile = errorFile(sdf.format(new Date()) + "-dropError");
        InputStreamExporter exporter = new InputStreamExporter(psql.getInputStream(), errorFile);
        exporter.start();
        int result = psql.waitFor();
        if (result == 0)
        {
            try
            {
                Files.delete(errorFile);
            }
            catch (IOException e)
            {
                logger.warn("Could not delete temp file with the output of psql in drop routine!", e);
            }
            logger.info("Drop routine finished successfully.");
        }
        else
        {
            throw new IOException("Error in dropping routine! The file " + errorFile.toString() + " has the details.");
        }
    }

    private String[] dropCommand ()
    {
        return new String[]{"--command", "DROP OWNED BY " + connectionParameters.getUser() + ";"};
    }

    //</editor-fold>

    //<editor-fold desc="Restore">

    @Override
    public void restore (Path backupPath) throws Exception
    {
        Process psql = startProcess(executablePath("psql"), restoreCommand(backupPath.toString()));
        Path errorFile = errorFile(sdf.format(new Date()) + "-restoreError");
        InputStreamExporter exporter = new InputStreamExporter(psql.getInputStream(), errorFile);
        exporter.start();
        int result = psql.waitFor();
        if (result == 0)
        {
            try
            {
                Files.delete(errorFile);
            }
            catch (IOException e)
            {
                logger.warn("Could not delete temp file with the output of psql in restore routine!", e);
            }
            logger.info("Restore routine finished successfully.");
        }
        else
        {
            throw new IOException("Error in restoring routine! The file " + errorFile.toString() + " has the details.");
        }
    }

    private String[] restoreCommand (String filePath)
    {
        return new String[]{"--file", filePath};
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
        String executableExtension;
        if (SystemUtils.isWindows())
        {
            executableExtension = ".exe";
        }
        else
        {
            executableExtension = "";
        }
        Path response = Paths.get(binFolderPathString).resolve(executable + executableExtension);
        if (Files.notExists(response))
        {
            throw new IOException(executable + " not found in bin directory!");
        }
        return response.toString();
    }

    private class InputStreamExporter extends Thread
    {
        private InputStream inputStream;
        private File        file;

        private InputStreamExporter (InputStream inputStream, Path filePath)
        {
            this.inputStream = inputStream;
            this.file = filePath.toFile();
        }

        @Override
        public void run ()
        {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), UTF_8)))
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

    private Process startProcess (String executable, String... args) throws IOException
    {
        List<String> command = new ArrayList<>();
        command.add(executable);
        command.add("--port");
        command.add(connectionParameters.getPort());
        command.add("--username");
        command.add(connectionParameters.getUser());
        command.add("--host");
        command.add("localhost");
        List<String> customCommands = Arrays.asList(args);
        if (!customCommands.isEmpty())
        {
            command.addAll(customCommands);
        }
        command.add(connectionParameters.getDatabase());
        ProcessBuilder pb = new ProcessBuilder(command);
        //noinspection SpellCheckingInspection
        pb.environment().put("PGPASSWORD", connectionParameters.getPassword());
        pb.redirectErrorStream(true);
        return pb.start();
    }

    //</editor-fold>
}