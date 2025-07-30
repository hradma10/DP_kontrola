package cz.upol.logicgo.misc.appStart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * třída, která se stará o vytvoření souboru, který slouží jako semafor pro zajištění jedné instance aplikace
 */
public class LockFileSemaphore extends FileLocker {
    public LockFileSemaphore() {
        super(".lock");
    }

    /**
     * vytvoří soubor
     *
     * @return true pokud aplikaci lze spustit, false pokud ne
     */
    public boolean acquire() {
        try {
            var semaphoreFile = this.getSemaphoreFile();
            File appFolder = new File(this.getAppFolderPath());
            if (!(appFolder.exists() || appFolder.mkdirs())) {
                return false;
            }
            if (!(semaphoreFile.exists() || semaphoreFile.createNewFile())) {
                return false;
            }
            var fileChannel = new FileOutputStream(semaphoreFile).getChannel();
            setFileChannel(fileChannel);
            var fileLock = fileChannel.tryLock();
            setFileLock(fileLock);
            return fileLock != null;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * uvolní a smaže soubor
     */
    public void release() {
        try {
            var fileLock = getFileLock();
            if (fileLock != null) {
                fileLock.release();
            }
            var fileChannel = getFileChannel();
            if (fileChannel != null) {
                fileChannel.close();
            }
            this.getSemaphoreFile().delete();

        } catch (IOException ignored) {
        }
    }
}
