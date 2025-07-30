package cz.upol.logicgo;

import cz.upol.logicgo.misc.appStart.LockFileSemaphore;

/**
 * spustí aplikaci
 * je použito při vytváření spustitelného souboru
 */
public class AppStart {

    public static void main(String[] args) {
        var semaphore = new LockFileSemaphore();
        if (semaphore.acquire()) {
            StartOfApp.main(args);
            Runtime.getRuntime().addShutdownHook(new Thread(semaphore::release));
        }
    }
}