package com.zadanie.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class TxtPrinter {
    public final static void printCountToTextFile(Integer movedToDEV, Integer movedToTest){
        final int RE_TRY_ATTEMPT_LIMIT = 5;
        PrintWriter txtWriter = null;
        int faliledAttemptsCount = 0;
        do {
            try{
                txtWriter = new PrintWriter(FileMover.OUTPUT_FILE_PATH);
                txtWriter.print("Moved to DEV Folder:\t" + movedToDEV + 
                "\nMoved to TEST Folder:\t" + movedToTest + 
                "\nMoved total:\t" + (movedToDEV + movedToTest) + "\n");
                faliledAttemptsCount = 0;
            }catch (IOException e){
                if(++faliledAttemptsCount >= RE_TRY_ATTEMPT_LIMIT){
                    System.err.println("Failed to save result to text file " + faliledAttemptsCount + "times. saving aborted!");
                    return;
                }
                System.err.println("Failed to save result to text file " + faliledAttemptsCount + " times!");
                try{
                    TimeUnit.MICROSECONDS.sleep(100);
                } catch (InterruptedException e2){
                    System.err.println("Faied to sleep for 100ms");
                }
            }finally{
                if (txtWriter != null)
                    txtWriter.close();
            }
        } while (faliledAttemptsCount > 0);
        
    } 
}
