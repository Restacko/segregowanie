package com.zadanie.service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

public class FileCreationListener {
    final int RE_TRY_ATTEMPT_LIMIT = 5;
    public void listenForFileCreation(Path path, FileMover fileMover) {
        WatchService watchService = null;
        int failedAttemptsCount = 0;
        do {
            try{
                watchService = FileSystems.getDefault().newWatchService();
            }catch (IOException e){
                if(++failedAttemptsCount >= RE_TRY_ATTEMPT_LIMIT){
                    System.err.println("Failed to create watchService! " + failedAttemptsCount + " times. Task aborted!");
                    return;
                }
                System.err.println("Failed to create watchService! " + failedAttemptsCount + " times");
                try{
                    TimeUnit.MICROSECONDS.sleep(100);
                } catch (InterruptedException e2){
                    System.err.println("Faied to sleep for 100ms");
                }
                return;
            }
        } while (failedAttemptsCount > 0);
        failedAttemptsCount = 0;
        do {
            try {
                path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            } catch (IOException e) {
                if(++failedAttemptsCount >= RE_TRY_ATTEMPT_LIMIT){
                    System.err.println("Failed to start watchService! " + failedAttemptsCount + " times. Task aborted!");
                    return;
                }
                System.err.println("Failed to start watchService! " + failedAttemptsCount + " times");
                
            }
        } while (failedAttemptsCount > 0);
        failedAttemptsCount = 0;
        WatchKey key;
        do  {
            try{
                key = watchService.take();
            } catch (InterruptedException e){
                if(++failedAttemptsCount >= RE_TRY_ATTEMPT_LIMIT){
                    System.err.println("Failed to get watch key! " + failedAttemptsCount + " times. Task aborted!");
                    break;
                }
                System.err.println("Failed to get watch key! " + failedAttemptsCount + " times");
                try{
                    TimeUnit.MICROSECONDS.sleep(100);
                } catch (InterruptedException e2){
                    System.err.println("Faied to sleep for 100ms");
                }
                continue;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                if(event.kind()==StandardWatchEventKinds.ENTRY_CREATE){
                    fileMover.moveFilesToDestinationFolders(event.context().toString());
                    TxtPrinter.printCountToTextFile(fileMover.getMovedToDevCount(), fileMover.getMovedToTestCount());
                }
            } 
            key.reset();
        } while (true);
        try{
            watchService.close();
        } catch (IOException e){
            System.err.println("Failed to close watch service!");
        }
        
    }
}
