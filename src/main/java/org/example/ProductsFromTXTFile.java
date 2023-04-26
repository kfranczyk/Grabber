package org.example;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ProductsFromTXTFile {
    final String ELEM_DISPLAY_WIDTH = "| %-30s |";
     String fileName = "katalog.txt";
    final String[] columnTitles = {
            "nazwa producenta",
            "przekątna ekranu",
            "rozdzielczość ekranu",
            "rodzaj powierzchni ekranu",
            "czy ekran jest dotykowy",
            "nazwa procesora",
            "liczba rdzeni fizycznych",
            "prędkość taktowania MHz",
            "wielkość pamięci RAM",
            "pojemność dysku",
            "rodzaj dysku",
            "układ graficzny",
            "pamięć układu graficznego",
            "system operacyjny",
            "rodzaj napędu fizycznego"
    };

    final int paramCount = columnTitles.length;
    HashMap<String,Integer> producentLaptopsCount;

    void readAndPrintProductsFromFile(String fileName){
        try {
            ArrayList<ArrayList<String>> products = new ArrayList<>(readDataFromFile(fileName));
            printDataToConsole(products);
        }catch (FileNotFoundException | NullPointerException e){
            System.out.println("Brak pliku z którego pobierano dane");
        }catch (IOException e){
            System.out.println("Wystąpił błąd w pobieraniu danych z pliku");
        }

    }
    ArrayList<ArrayList<String>> readDataFromFile (String fileName) throws FileNotFoundException,IOException,NullPointerException {
        this.fileName = fileName;

        BufferedReader reader;
        String tmpLine;
        ArrayList<ArrayList<String>> resultArr = new ArrayList<>();


                ClassLoader classLoader = getClass().getClassLoader();
                InputStream inputStream = classLoader.getResourceAsStream(fileName);
                reader = new BufferedReader(new InputStreamReader(inputStream));

                int currLine = 0;
                String line = reader.readLine();
                while(line!=null){
                    resultArr.add(getDataFromLine(line));
                    currLine++;
                    line = reader.readLine();
                }
                return resultArr;


    }


    ArrayList<String> getDataFromLine(String line){
        Scanner scanner;
        scanner = new Scanner(line);
        scanner.useDelimiter(";");
        ArrayList<String> retValArr = new ArrayList<>();
        String tmpVal="";
        for(int i=0; i<paramCount; i++) {
            tmpVal = scanner.next();
            retValArr.add(tmpVal.equals("")? "brak informacji" : tmpVal );
        }

        return retValArr;
    }

    void printDataToConsole(ArrayList<ArrayList<String>> inputData){
        System.out.println("\nIlość produktów danego producenta:\n");
        System.out.printf("| %4s |","LP");
        for(int i=0;i<paramCount; i++){
            System.out.printf(ELEM_DISPLAY_WIDTH,columnTitles[i]);
        }

        for(int i=0;i< inputData.size(); i++){
            System.out.printf("\n| %4d |", i);
            ArrayList<String> tmpList = inputData.get(i);
            for(int j=0;j< tmpList.size(); j++){
                System.out.printf(ELEM_DISPLAY_WIDTH,tmpList.get(j));
            }

        }


        calcProducentLaptopCount(inputData);

    }


    void calcProducentLaptopCount(ArrayList<ArrayList<String>> laptopsData){
        producentLaptopsCount = new HashMap<String,Integer>();


        for(int i=0; i<laptopsData.size(); i++){
            String tmpProducent = laptopsData.get(i).get(0);
            if (producentLaptopsCount.get(tmpProducent) == null ){
                producentLaptopsCount.put(laptopsData.get(i).get(0),1);
                continue;
            }
            producentLaptopsCount.put(laptopsData.get(i).get(0), producentLaptopsCount.get(laptopsData.get(i).get(0)).intValue()+1);
        }

        showProducentLaptopCount(producentLaptopsCount);
    }

    void showProducentLaptopCount(HashMap<String,Integer> producentLaptopsCounts){
        System.out.println("\n\nIlość produktów danego producenta:\n");
        System.out.printf(ELEM_DISPLAY_WIDTH, "nazwa producenta");
        System.out.printf(ELEM_DISPLAY_WIDTH+"\n", "ilość produktów");
        producentLaptopsCounts.forEach((s, integer) ->
        {
            System.out.printf(ELEM_DISPLAY_WIDTH, s);
            System.out.printf(ELEM_DISPLAY_WIDTH+"\n", integer);
        });
    }


   public static String getDataFromTableToTxt(JTable table){
        StringBuilder retVal = new StringBuilder();
        String tmpVal ="";
        for (int i=0;i< table.getRowCount();i++){
            for(int j=0; j< table.getColumnCount(); j++){
                tmpVal = table.getValueAt(i,j).toString();
                //if (!(tmpVal.length()==0 || tmpVal.equals(NO_INFO_VAL)))
                if (tmpVal.length()!=0)
                    retVal.append(tmpVal);
                retVal.append(";");
            }
            retVal.append("\n");
        }
        return retVal.toString();
    }


   public static void writeToTxtFile(String text){
        try {
            Path resourceDirectory = Paths.get("src","main","resources");
            String absolutePath = resourceDirectory.toFile().getAbsolutePath();

            System.out.println(absolutePath);
            FileWriter file = new FileWriter(absolutePath + "/katalog.txt");
            BufferedWriter writer = new BufferedWriter(file);

            writer.write(text);
            writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}

