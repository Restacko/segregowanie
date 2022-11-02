package com.zadanie;

import java.io.File;

import com.zadanie.service.FileCreationListener;
import com.zadanie.service.FileMover;
import com.zadanie.service.TxtPrinter;

public class App 
{
    public static void main( String[] args ) {
        FileMover fileMover = new FileMover();
        FileCreationListener fileCreationListener = new FileCreationListener();
        fileMover.moveFilesToDestinationFolders();
        TxtPrinter.printCountToTextFile(fileMover.getMovedToDevCount(), fileMover.getMovedToTestCount());
        fileCreationListener.listenForFileCreation(new File(FileMover.PATH_HOME).toPath(),fileMover);
    }
}
