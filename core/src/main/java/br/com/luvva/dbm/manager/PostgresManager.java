package br.com.luvva.dbm.manager;

import br.com.jwheel.cdi.WeldContext;
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
    private @Inject PostgresInfo    postgresInfo;
    private @Inject Logger          logger;
    private @Inject PathPreferences pathPreferences;

    //<editor-fold desc="Backup">

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmm");

    @Override
    public void backup (Path backupDirectory) throws Exception
    {
        Files.createDirectories(backupDirectory);
        ConnectionParameters connectionParameters = WeldContext.getInstance().getDefault(ConnectionParameters.class);
        Process backupProcess = startProcess(connectionParameters, executablePath("pg_dump"));
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

    //<editor-fold desc="Drop and restore">

    @Override
    public void dropAndRestore (Path backupPath) throws Exception
    {
        ConnectionParameters cp = WeldContext.getInstance().getDefault(ConnectionParameters.class);
        new PsqlRunner(cp, dropCommand(cp.getUser()), "drop").execute();
        new PsqlRunner(cp, restoreCommand(backupPath.toString()), "restore").execute();
    }

    private String[] dropCommand (String user)
    {
        return new String[]{"--command", "DROP OWNED BY " + user + ";"};
    }

    private String[] restoreCommand (String filePath)
    {
        return new String[]{"--file", filePath};
    }

    //</editor-fold>

    //<editor-fold desc="Init">

    @Override
    public void init (ConnectionParameters cp, String user, String password, String database) throws Exception
    {
        new PsqlRunner(cp, createUserCommand(user, password), "createUser").execute();
        new PsqlRunner(cp, createDatabaseCommand(user, database), "createDatabase").execute();
    }

    private String[] createUserCommand (String user, String password)
    {
        return new String[]{"--command", "CREATE USER " + user + " WITH PASSWORD '" + password + "';"};
    }

    private String[] createDatabaseCommand (String user, String database)
    {
        return new String[]{"--command", "CREATE DATABASE " + database + " OWNER " + user + ";"};
    }

    //</editor-fold>

    //<editor-fold desc="Utilities">

    private class PsqlRunner
    {
        private ConnectionParameters cp;
        private String[]             command;
        private String               routine;

        private PsqlRunner (ConnectionParameters cp, String[] command, String routine)
        {
            this.cp = cp;
            this.command = command;
            this.routine = routine;
        }

        private void execute () throws Exception
        {
            Process psql = startProcess(cp, executablePath("psql"), command);
            Path errorFile = errorFile(sdf.format(new Date()) + routine + "Error");
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
                    logger.warn("Could not delete temp file with the output of psql in " + routine + " routine!", e);
                }
                logger.info(routine + " routine finished successfully.");
            }
            else
            {
                throw new IOException("Error in " + routine + " routine! " +
                        "The file " + errorFile.toString() + " has the details.");
            }
        }
    }

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

    private Process startProcess (ConnectionParameters cp, String executable, String... args) throws IOException
    {
        List<String> command = new ArrayList<>();
        command.add(executable);
        command.add("--port");
        command.add(cp.getPort());
        command.add("--username");
        command.add(cp.getUser());
        command.add("--host");
        command.add("localhost");
        List<String> customCommands = Arrays.asList(args);
        if (!customCommands.isEmpty())
        {
            command.addAll(customCommands);
        }
        command.add(cp.getDatabase());
        ProcessBuilder pb = new ProcessBuilder(command);
        //noinspection SpellCheckingInspection
        pb.environment().put("PGPASSWORD", cp.getPassword());
        pb.redirectErrorStream(true);
        return pb.start();
    }

    //</editor-fold>
}
