package com.zadanie.service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;




public class FileMover {

    public final static String PATH_ROOT = "C:/test/";
    public final static String PATH_HOME = PATH_ROOT + "HOME/";
    public final static String PATH_DEV = PATH_ROOT + "DEV/";
    public final static String PATH_TEST = PATH_ROOT + "TEST/";
    public final static String OUTPUT_FILE_PATH = PATH_HOME + "count.txt";
    final int RE_TRY_ATTEMPT_LIMIT = 5;
    
    Integer countOfmovedToDev, copuntOfToTestCount;
    private boolean successfullyCreatedFolders, successfullyCreatedOutputTextFile, successfullyMovedFiles;

    public FileMover(){
        this.countOfmovedToDev = 0;
        this.copuntOfToTestCount = 0;
        this.successfullyCreatedFolders = false;
        this.successfullyCreatedOutputTextFile = false;
        this.successfullyMovedFiles = false;
    }
    public Integer getMovedToDevCount(){
        return countOfmovedToDev;
    }
    public Integer getMovedToTestCount(){
        return copuntOfToTestCount;
    }

    public boolean moveFilesToDestinationFolders(String fileName){
        int faliledAttemptsCount = 0;
        do {
            if(faliledAttemptsCount > 0){
                System.err.println("Failed to move files! " + faliledAttemptsCount + " times.");
                if(faliledAttemptsCount >= RE_TRY_ATTEMPT_LIMIT){
                    System.err.println("Failed to move files! " + faliledAttemptsCount + " times. Task aborted!");
                    return false;
                }
                try{
                    TimeUnit.MICROSECONDS.sleep(100);
                } catch (InterruptedException e2){
                    System.err.println("Faied to sleep for 100ms");
                }
            }            
            if(!this.successfullyCreatedFolders)
            this.successfullyCreatedFolders = createFolders();
            if(!this.successfullyCreatedOutputTextFile)
                this.successfullyCreatedOutputTextFile = createOutputTextFile();
            if(!this.successfullyMovedFiles)
                this.successfullyMovedFiles = checkAllFilesInHomeDirectoryAndMove();
            if (!this.successfullyCreatedFolders && !successfullyCreatedOutputTextFile){
                faliledAttemptsCount++;
                continue;
            }
            if(fileName == "start")
                continue;
            this.successfullyMovedFiles = determinDestinationFolderAndMove(fileName) & this.successfullyMovedFiles;
            if (!this.successfullyMovedFiles){
                faliledAttemptsCount++;
                continue;
            }
            faliledAttemptsCount = 0;
        } while (faliledAttemptsCount > 0);
        
        return faliledAttemptsCount == 0;
    }
    public boolean moveFilesToDestinationFolders(){
        return this.moveFilesToDestinationFolders("start");
    }

    private boolean createFolders(){
        boolean foldersCreatedSuccesfuly = true;
        File fileDirectory;
        String[] pathArray = {PATH_HOME, PATH_DEV, PATH_TEST};
        for (String path : pathArray) {
            fileDirectory = new File(path);
            if (!fileDirectory.exists())
                foldersCreatedSuccesfuly = fileDirectory.mkdirs() & foldersCreatedSuccesfuly;
        }
        return foldersCreatedSuccesfuly;
    }
    private boolean createOutputTextFile(){
        File fileDirectory = new File(OUTPUT_FILE_PATH);
        try{
            fileDirectory.createNewFile();
        } catch (IOException e) {
            System.err.println("Failed to create output txt file! ");
            return false;
        }
        return true;
    }


    private boolean determinDestinationFolderAndMove(String fileName){
        File file = new File(PATH_HOME + fileName);
        if (fileName.endsWith(".xml")){
            if(!moveFileToDirectory(PATH_DEV, file))
                return false;
            countOfmovedToDev++;
            return true;
        }
        if (fileName.endsWith(".jar")){
            FileTime creationtime;
            try {
                creationtime = (FileTime) Files.getAttribute(file.toPath(), "creationTime", java.nio.file.LinkOption.NOFOLLOW_LINKS);
            } catch (IOException e) {
                System.err.println("Failed to get file creation time!");
                return false;
            }
            if(creationtime.to(TimeUnit.HOURS) % 2 == 0){
                if(!moveFileToDirectory(PATH_DEV, file))
                    return false;
                countOfmovedToDev++;
            }
            else{
                if(!moveFileToDirectory(PATH_TEST, file))
                    return false;
                copuntOfToTestCount++;
            }
        }
        return true;
    }

    private boolean checkAllFilesInHomeDirectoryAndMove(){
        boolean successfullyMovedFiles = true;
        File homeDirectory = new File(PATH_HOME);
        File[] fileList = homeDirectory.listFiles();
        for (File file : fileList) {
            successfullyMovedFiles = determinDestinationFolderAndMove(file.getName()) & successfullyMovedFiles;
        }
        return successfullyMovedFiles;
    }
    private boolean moveFileToDirectory(String directory, File file){
        return file.renameTo(new File((directory) + file.getName()));
    }

}
