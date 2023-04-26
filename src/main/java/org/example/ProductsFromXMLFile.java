package org.example;

import org.example.Products.*;

import javax.management.InvalidAttributeValueException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProductsFromXMLFile {

    List tableHeaderValues;

    public ProductsFromXMLFile() {
        ProductsFromTXTFile productsFromTXTFile = new ProductsFromTXTFile();
        tableHeaderValues =  Arrays.asList(productsFromTXTFile.columnTitles);
    }

    private final String NO_INFO_VAL = "brak informacji";
    JTable table; 

    private HashMap<String,Integer[]> getIndexesOfPeripherals(){
        HashMap<String, Integer[]> tmpMap = new HashMap<>();
        tmpMap.put("disc",
                new Integer[]{
                        tableHeaderValues.indexOf("czy ekran jest dotykowy"),
                        tableHeaderValues.indexOf("rodzaj powierzchni ekranu")
                });
        tmpMap.put("graphicsCard",
                new Integer[]{
                        tableHeaderValues.indexOf("układ graficzny"),
                        tableHeaderValues.indexOf("pamięć układu graficznego")
                });
        tmpMap.put("processor",
                new Integer[]{
                        tableHeaderValues.indexOf("nazwa procesora"),
                        tableHeaderValues.indexOf("liczba rdzeni fizycznych"),
                        tableHeaderValues.indexOf("prędkość taktowania MHz")
                });
        tmpMap.put("screen",
                new Integer[]{
                        tableHeaderValues.indexOf("czy ekran jest dotykowy"),
                        tableHeaderValues.indexOf("przekątna ekranu"),
                        tableHeaderValues.indexOf("rozdzielczość ekranu"),
                        tableHeaderValues.indexOf("rodzaj powierzchni ekranu")
                });
        tmpMap.put("laptop",
                new Integer[]{
                        tableHeaderValues.indexOf("nazwa producenta"),
                        tableHeaderValues.indexOf("wielkość pamięci RAM"),
                        tableHeaderValues.indexOf("system operacyjny"),
                        tableHeaderValues.indexOf("rodzaj napędu fizycznego")
                });

        return tmpMap;
    }

    public void exportToXMLFile(JTable tableCopy){
        LocalDateTime createTime =  LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd 'T' HH:mm");
        String formattedDate = createTime.format(myFormatObj);

        table = tableCopy;
        
        Laptops laptopsObj = new Laptops(formattedDate,parseDataToLaptops());
        JAXBContext context = null;

        try {
            context = JAXBContext.newInstance(Laptops.class);
            Marshaller mar= context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(laptopsObj, new File("./laptops.xml"));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public DefaultTableModel importFromXMLFile() throws JAXBException, FileNotFoundException {
        List<Laptop> laptops = getDataFromXMLFile().getLaptop();

        DefaultTableModel defaultTableModel = new DefaultTableModel(tableHeaderValues.toArray(), 0);
        ArrayList<String> product;

        for (Laptop laptop: laptops) {
            product = new ArrayList<>();

            product.add(laptop.getManufacturer());
            product.add(laptop.getScreen().getSize());
            product.add(laptop.getScreen().getResolution());
            product.add(laptop.getScreen().getType());
            product.add(laptop.getScreen().getTouch());
            product.add(laptop.getProcessor().getName());

            Integer cores = laptop.getProcessor().getPhysicalCores();
            product.add( Objects.isNull(cores) ? NO_INFO_VAL : cores.toString());
            Integer cpuspeed = laptop.getProcessor().getClockSpeed();
            product.add(Objects.isNull(cpuspeed) ? NO_INFO_VAL : cpuspeed.toString());

            product.add(laptop.getRam());
            product.add(laptop.getDisc().getStorage());
            product.add(laptop.getDisc().getType());
            product.add(laptop.getGraphicCard().getName());
            product.add(laptop.getGraphicCard().getMemory());
            product.add(laptop.getOperatingSystem());
            product.add(laptop.getDiscReader());


            for (int i=0;i< product.size();i++){
                if( Objects.isNull(product.get(i)))
                    product.set(i, NO_INFO_VAL);
            }

            defaultTableModel.addRow(product.toArray(new String[0]));

            product.clear();
        }

        return defaultTableModel;
    }

    private Laptops getDataFromXMLFile() throws JAXBException, FileNotFoundException {

            JAXBContext context = JAXBContext.newInstance(Laptops.class);
            return (Laptops) context.createUnmarshaller()
                    .unmarshal(new FileReader("./laptops.xml"));

    }

    private List<Laptop> parseDataToLaptops(){

        try{
            HashMap<String, Integer[]> pcPartsGroupColumnIndexes = getIndexesOfPeripherals();

            ArrayList<Laptop> tmpLaptops = new ArrayList<Laptop>();
            Laptop tmpLaptop;

            for(int i=0; i< table.getRowCount(); i++){
                tmpLaptop = getLaptopPerifFromTable(i,pcPartsGroupColumnIndexes.get("laptop"));

                tmpLaptop.setDisc(getDiscFromTable(i,pcPartsGroupColumnIndexes.get("disc")));
                tmpLaptop.setProcessor(getProcessorFromTable(i,pcPartsGroupColumnIndexes.get("processor")));
                tmpLaptop.setScreen(getScreenFromTable(i,pcPartsGroupColumnIndexes.get("screen")));
                tmpLaptop.setGraphicCard(getGraphicCardFromTable(i,pcPartsGroupColumnIndexes.get("graphicsCard")));

                tmpLaptops.add(tmpLaptop);
            }
            return tmpLaptops;
        }catch (InvalidAttributeValueException e){
            e.printStackTrace();
            return null;
        }


    }

    private String getTableVal(int x, int y) throws InvalidAttributeValueException{
        if(x < 0 || x>table.getRowCount())
            throw new InvalidAttributeValueException();

        Object tmpVal = table.getValueAt(x,y);
        if (Objects.isNull(tmpVal) || tmpVal.equals(NO_INFO_VAL))
            return null;
        return tmpVal.toString();
    }

    private Laptop getLaptopPerifFromTable(int x,Integer[] columns) throws InvalidAttributeValueException{
        String manufacturer = getTableVal(x,columns[0]);
        String ram = getTableVal(x,columns[1]);
        String operatingSystem = getTableVal(x,columns[2]);
        String discReader = getTableVal(x,columns[3]);

        return new Laptop(++x,manufacturer,ram,operatingSystem,discReader);
    }

    private GraphicCard getGraphicCardFromTable(int x, Integer[] columns) throws InvalidAttributeValueException{
        String name = getTableVal(x,columns[0]);
        String memory = getTableVal(x,columns[1]);
        return new GraphicCard(name,memory);
    }
    private Disc getDiscFromTable(int x, Integer[] columns) throws InvalidAttributeValueException{
        String type = getTableVal(x,columns[0]);
        String touch = getTableVal(x,columns[1]);
        return new Disc(type,touch);
    }

    private Processor getProcessorFromTable(int x, Integer[] columns) throws InvalidAttributeValueException{

        String name = getTableVal(x,columns[0]);
        String cores = getTableVal(x,columns[1]);
        String clockSpeed = getTableVal(x,columns[2]);
        Integer coreNum = !Objects.isNull(cores)  ? Integer.valueOf(cores) : null;
        Integer clockSpeedNum = !Objects.isNull(clockSpeed) ? Integer.valueOf(clockSpeed) : null;

        return new Processor(name, coreNum, clockSpeedNum);
    }

    private Screen getScreenFromTable(int x,Integer[] columns) throws InvalidAttributeValueException{

        String touch = getTableVal(x,columns[0]);
        String size =  getTableVal(x,columns[1]);
        String resolution = getTableVal(x,columns[2]);
        String type = getTableVal(x,columns[3]);

        return new Screen(touch,size,resolution,type);
    }
}
