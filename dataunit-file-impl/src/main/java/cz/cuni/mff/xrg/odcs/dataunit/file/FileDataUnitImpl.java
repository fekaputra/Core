package cz.cuni.mff.xrg.odcs.dataunit.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.DirectoryHandler;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.DirectoryHandlerImpl;
import cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd;

/**
 * Implementation of {@link FileDataUnit}.
 * 
 * @author Petyr
 */
class FileDataUnitImpl implements ManageableFileDataUnit {

    private static final Logger LOG = LoggerFactory.getLogger(FileDataUnitImpl.class);

    /**
     * Name for directory with data.
     */
    private static final String DATA_DIR_NAME = "data";

    /**
     * Name of data unit given by DPU programmer.
     */
    private final String name;

    /**
     * Handler for root directory.
     */
    private DirectoryHandlerImpl rootDirHandler;

    FileDataUnitImpl(final String name, final File directory) {
        this.name = name;
        // create main directory for data and handler to it
        final File rootDir = new File(directory, DATA_DIR_NAME);
        rootDir.mkdirs();
        this.rootDirHandler = new DirectoryHandlerImpl(rootDir);
    }

    @Override
    public DataUnitType getType() {
        return DataUnitType.FILE;
    }

    @Override
    public boolean isType(DataUnitType dataUnitType) {
        return this.getType().equals(dataUnitType);
    }

    @Override
    public String getDataUnitName() {
        return this.name;
    }

    @Override
    public void merge(DataUnit unit) throws IllegalArgumentException {
        if (unit instanceof FileDataUnitImpl) {
            // ok we can merge
        } else {
            throw new IllegalArgumentException("Wrong DataUnit type.");
        }
        FileDataUnitImpl toMerge = (FileDataUnitImpl) unit;
        // add as links do not overwrite
        OptionsAdd opt = new OptionsAdd(true, false);
        // add all -> merge
        this.rootDirHandler.addAll(toMerge.rootDirHandler, opt);
    }

    @Override
    public void release() {
        final File dataDir = this.rootDirHandler.asFile();
        try {
            FileUtils.forceDelete(dataDir);
        } catch (IOException ex) {
            LOG.error("Failed to delete root directory.", ex);
        }
    }

    @Override
    public void clear() {
        final File dataDir = this.rootDirHandler.asFile();
        // recreate the root directory handler
        this.rootDirHandler = new DirectoryHandlerImpl(dataDir);
        // and delete it's content
        if (dataDir.exists()) {
            try {
                FileUtils.cleanDirectory(dataDir);
            } catch (IOException ex) {
                LOG.error("Failed to clean directory.", ex);
            }
        }
    }

    @Override
    public void save(File directory) throws RuntimeException {
        // use XStream serialization ?
        final File fileName = new File(directory, "metadata.dat");
        try (OutputStream os = new FileOutputStream(fileName)) {
            // write into the file
            XStream xstream = new XStream();
            xstream.toXML(this.rootDirHandler, os);
        } catch (FileNotFoundException ex) {
            LOG.error("Failed to save DataUnit.", ex);
        } catch (IOException ex) {
            LOG.error("Failed to write information into DataUnit save file.", ex);
        }

    }

    @Override
    public void load(File directory)
            throws RuntimeException {
        // use XStream serialization ?
        final File file = new File(directory, "metadata.dat");
        if (!file.exists()) {
            throw new RuntimeException("File does not exist " + file.toString());
        }
        XStream xstream = new XStream();
        this.rootDirHandler = (DirectoryHandlerImpl) xstream.fromXML(file);
    }

    @Override
    public DirectoryHandler getRootDir() {
        return this.rootDirHandler;
    }

    @Override
    public void isReleaseReady() {
        // TODO Auto-generated method stub

    }

    @Override
    public void addAll(DataUnit unit) {
        // TODO Auto-generated method stub

    }

}
