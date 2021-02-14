package fr.umlv.LocalKube.logs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * csv file handler class, register copy of data base in csv file while shutting down the localkube
 * restore the data base from the files while starting localkube
 */
@Component("CsvFile")
class CsvFile implements LogObserver{

    private final FileWriter fileWriter;
    private final Date fileWriterDate = new Date();
    @Autowired
    @Qualifier("DataBase")
    private DataBase dataBase;

    CsvFile() {
        try {
            var formatter = new SimpleDateFormat("yyyy-MM-dd_HH");
            this.fileWriter = new FileWriter("logs/Log-" + formatter.format(fileWriterDate) + ".csv", true);
        } catch (IOException e) {
            throw new IllegalArgumentException("Log file can not be initialized");
        }
    }

    boolean correctTimeFileWriter(){
        var actualDate = new Date();
        var diffInMillies = Math.abs(actualDate.getTime() - fileWriterDate.getTime());
        return TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS) < 1;
    }

    CsvFile createNewCsvFile(){
        this.closeFile();
        return new CsvFile();
    }

    void addToFile(LogModel log){
        try {
            fileWriter.append(Integer.toString(log.id()))
                    .append(",")
                    .append(log.timeStamp())
                    .append(",")
                    .append(log.message())
                    .append("\n");
        } catch (IOException e) {
           throw new IllegalStateException("Error while writing to csv file");
        }
    }

    @Override
    public void onStart(Log log){
        this.openFileLog();
    }

    private void openFileLog(){
        var file = new File("logs");
        var files = file.listFiles();
        if(files != null){
            for(var f : files ){
                if(!f.isDirectory()){
                    readFile(f.getName());
                }
            }
        }
    }

    private void readFile(String fileName){
        try(var bufferedReader = new BufferedReader(new FileReader("logs/" + fileName))){
            String line;
            while ((line = bufferedReader.readLine()) != null){
                String[] array = line.split(",");
                if(array.length < 2)
                    throw new IllegalStateException("not enough argument in " + fileName);
                if(array.length == 2)
                    dataBase.addDataBase(new LogModel(Integer.parseInt(array[0]), "", array[1]));
                else
                    dataBase.addDataBase(new LogModel(Integer.parseInt(array[0]), array[2], array[1]));
            }
        } catch (IOException e) {
           throw new IllegalStateException("Failed to read from file "+e.getMessage());
        }
    }

    private void closeFile(){
        try {
            fileWriter.close();
        } catch (IOException e) {
           throw new IllegalStateException("failed to close csv File");
        }
    }

    @Override
    public void onStop( int appId){
        this.closeFile();
    }

    @Override
    public void onShutDown(){
        this.closeFile();
    }
}