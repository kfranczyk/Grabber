package org.example;


import org.example.SOAP.ProductServiceImpl;

import javax.swing.*;
import javax.swing.table.*;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static org.example.ProductsFromTXTFile.getDataFromTableToTxt;

public class GuiScrolled {


    JFrame frame;
    JTable table;
    List tableHeaderValues;
    private final String NO_INFO_VAL = "brak informacji";
    String labelMessage="";
    private JPanel topNavPanel;

    ProductsFromDatabase productsFromDatabase;

    HashMap<Integer,String> editedRows = new HashMap<>();

    GuiScrolled() {

        frame = new JFrame("Integracja systemów - aplikacja serwerowa Karol Franczyk");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        topNavPanel = createTopPanel();
        //top panel
        frame.add(topNavPanel,BorderLayout.NORTH);

        //connect to DB
        productsFromDatabase = new ProductsFromDatabase();


        javax.xml.ws.Endpoint.publish("http://localhost:8090/productservice",
                new ProductServiceImpl(productsFromDatabase));

        frame.setSize(1000, 500);
        frame.setMinimumSize(new Dimension(400,400));
        frame.setVisible(true);
    }
    JScrollPane createMainPanel() throws FileNotFoundException, IOException, NullPointerException{

        DefaultTableModel defaultTableModel = importDataToModel();


        table = new JTable( defaultTableModel);
        table.setModel(defaultTableModel);

        table = setCellStyle(table);
        table = setResizingOption(table);

        addValidators(table);
        addChangedValListener();

        return new JScrollPane(table);
    }


    JPanel createTopPanel(){

        GridBagLayout gridBagLayout = new GridBagLayout();
        JPanel topPanel = new JPanel();
        topPanel.setLayout(gridBagLayout);

        JButton buttonReadTXT = new JButton("Wczytaj dane z pliku TXT");
        JButton buttonWriteTXT = new JButton("Zapisz dane do pliku TXT");
        JButton buttonReadXML = new JButton("Wczytaj dane z pliku XML");
        JButton buttonWriteXML = new JButton("Zapisz dane do pliku XML");
        JButton buttonReadDB = new JButton("Wczytaj dane z bazy danych");
        JButton buttonWriteDB = new JButton("Zapisz dane do bazy danych");

        buttonReadTXT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                labelMessage = "Pobrano dane z pliku txt";
                frame.getContentPane().remove(topNavPanel);

                drawTableOnTxtRead();

                editedRows.clear();
                repaintTopUI();
            }
        });

        buttonWriteTXT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table==null){
                    labelMessage = "Brak tabeli do zapisu danych";
                    frame.getContentPane().remove(topNavPanel);
                    repaintTopUI();
                    return;
                }

                ProductsFromTXTFile.writeToTxtFile( getDataFromTableToTxt(table));

                labelMessage = "Zapisano dane do pliku txt";
                frame.getContentPane().remove(topNavPanel);
                repaintTopUI();
            }
        });

        buttonReadXML.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                labelMessage = "Pobrano dane z pliku XML";
                frame.getContentPane().remove(topNavPanel);

                drawTableOnXMLRead();

                editedRows.clear();
                repaintTopUI();
            }
        });


        buttonWriteXML.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table==null){
                    labelMessage = "Brak tabeli do zapisu danych";
                    frame.getContentPane().remove(topNavPanel);
                    repaintTopUI();
                    return;
                }
                ProductsFromXMLFile productsFromXmlFile = new ProductsFromXMLFile();
                productsFromXmlFile.exportToXMLFile(table);
            }
        });

        buttonReadDB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {


                labelMessage = "Pobrano dane z pliku XML";
                frame.getContentPane().remove(topNavPanel);

                drawTableOnDBRead();

                editedRows.clear();
                repaintTopUI();
            }
        });

        buttonWriteDB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(table==null){
                    labelMessage = "Brak tabeli do zapisu danych";
                    frame.getContentPane().remove(topNavPanel);
                    repaintTopUI();
                    return;
                }

                DefaultTableModel defaultTableModel = productsFromDatabase.productsFromDBToModel(productsFromDatabase.getProductsFromDB());
                JTable newTable = new JTable( defaultTableModel);

                ArrayList<Integer> duplicatedRowsIndx = getDuplicatesIndx(table,newTable);

                productsFromDatabase.writeProductsToDB(duplicatedRowsIndx, table,editedRows);

                int uniqueRows = table.getRowCount()-duplicatedRowsIndx.size();
                labelMessage = "Zapisano w bazie " +  uniqueRows + " zmienionych rekordów";
                frame.getContentPane().remove(topNavPanel);
                repaintTopUI();


                //productsFromDatabase.getProductsFromDBWithScreenType("xd").forEach(System.out::println);
            }
        });


        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.RELATIVE;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(5,0,5,0);

        topPanel.add(buttonReadTXT,constraints);
        topPanel.add(buttonReadXML,constraints);
        topPanel.add(buttonReadDB,constraints);

        constraints.gridy = 2;
        topPanel.add(buttonWriteTXT,constraints);
        topPanel.add(buttonWriteXML,constraints);
        topPanel.add(buttonWriteDB,constraints);

        constraints.gridy = 3;
        constraints.gridwidth = 3;
        topPanel.add(new JLabel(labelMessage),constraints);


        return topPanel;
    }

    private void drawTableOnTxtRead(){
        try {

            if(table==null) {
                frame.add(createMainPanel(), BorderLayout.CENTER);
                labelMessage += ": znalazłem nowe " + table.getRowCount() + " rekordy";

            }else {
                frame.getContentPane().remove(table);
                DefaultTableModel defaultTableModel = importDataToModel();
                JTable newTable = new JTable( defaultTableModel);

                validateDuplicatesOnRead(table,newTable);

                table.setModel(newTable.getModel());
                table.setModel(defaultTableModel);

                table = setResizingOption(table);

                addValidators(table);
            }

        }catch ( FileNotFoundException | NullPointerException e){
            labelMessage = "Brak odpowiedniego pliku z danymi";
            e.printStackTrace();
        } catch (IOException e){
            labelMessage = "Błąd w odczycie pliku";
            e.printStackTrace();
        }

    }
    private void drawTableOnXMLRead(){

        try {
            ProductsFromXMLFile productsFromXmlFile = new ProductsFromXMLFile();
            DefaultTableModel defaultTableModel = productsFromXmlFile.importFromXMLFile();

            JTable newTable = new JTable( defaultTableModel);
            if (table==null){
                labelMessage += ": znalazłem nowe " + defaultTableModel.getRowCount() + " rekordy";

                table = newTable;
                table = setCellStyle(table);
                table = setResizingOption(table);
                addValidators(table);

                frame.add(new JScrollPane(table), BorderLayout.CENTER);

            }else {
                frame.getContentPane().remove(table);

                validateDuplicatesOnRead(table,newTable);

                table.setModel(newTable.getModel());
                table.setModel(defaultTableModel);
                table = setResizingOption(table);
                addValidators(table);
            }
        }catch (JAXBException | FileNotFoundException e){
            labelMessage = "Brak pliku z którego można wczytać dane";
        }

    }
    private void drawTableOnDBRead(){
        DefaultTableModel defaultTableModel = productsFromDatabase.productsFromDBToModel(productsFromDatabase.getProductsFromDB());
        JTable newTable = new JTable( defaultTableModel);
        if (table==null){
            labelMessage += ": znalazłem nowe " + defaultTableModel.getRowCount() + " rekordy";

            table = newTable;
            table = setCellStyle(table);
            table = setResizingOption(table);
            addValidators(table);

            frame.add(new JScrollPane(table), BorderLayout.CENTER);

        }else {
            frame.getContentPane().remove(table);

            validateDuplicatesOnRead(table,newTable);

            table.setModel(newTable.getModel());
            table.setModel(defaultTableModel);
            table = setResizingOption(table);
            addValidators(table);
        }
    }

    DefaultTableModel importDataToModel() throws FileNotFoundException, IOException,NullPointerException {
        ProductsFromTXTFile productsFromTXTFile = new ProductsFromTXTFile();
        ArrayList<ArrayList<String>> products = new ArrayList<>(productsFromTXTFile.readDataFromFile("katalog.txt"));

        tableHeaderValues =  Arrays.asList(productsFromTXTFile.columnTitles);

        DefaultTableModel defaultTableModel = new DefaultTableModel(productsFromTXTFile.columnTitles, 0);
        for(ArrayList<String> element : products ){
            defaultTableModel.addRow(element.toArray(new String[0]));
        }

        return defaultTableModel;
    }

    JTable setCellStyle(JTable table){
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);


        table.setDefaultRenderer(Object.class, centerRenderer);
        table.setRowHeight(30);

        for( int i=0;i<table.getColumnCount();i++){
            table.getColumnModel().getColumn(i).setMinWidth(150);
            table.getColumnModel().getColumn(i).setPreferredWidth(180);

            table.getColumnModel().getColumn(i).setMaxWidth(300);
        }

        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
        table.getTableHeader().setPreferredSize(
                new Dimension(table.getColumnModel().getTotalColumnWidth(), 52));


        return table;
    }
    JTable setResizingOption(JTable table){
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        return table;
    }

    void addValidators(JTable table){

        addNullValidator(table);
        addScreenTouchValidator(table);
        addScreenTypeValidator(table);
        addDiskTypeValidator(table);
        addRamSizeValidator(table);

    }

    void addRamSizeValidator(JTable table){
        TableColumn column = table.getColumnModel().getColumn(8);

        column.setCellEditor(new CellEditor(new RamVerifier()));
    }

    void addNullValidator(JTable table){
        for (int i=0; i<15;i++){
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setCellEditor(new CellEditor(new cellNullVerifier()));
        }
    }

    private class CellEditor extends DefaultCellEditor {

        InputVerifier verifier = null;

        public CellEditor(InputVerifier verifier) {
            super(new JTextField());
            this.verifier = verifier;
        }

        @Override
        public boolean stopCellEditing() {
            return verifier.verify(editorComponent) && super.stopCellEditing();
        }
    }
    class RamVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
            boolean verified = false;
            String text = ((JTextField) input).getText();
            try {
                if(text.equals(NO_INFO_VAL))
                    return true;
                int size = Integer.valueOf(text.substring(0,text.length()-2));
                if (size>0 && size<=512 && text.matches("^\\d+(GB|gb|MB|mb|Mb|Gb)$")) {
                    input.setBackground(Color.WHITE);
                    verified = true;
                } else {
                    input.setBackground(Color.RED);
                }
            }catch (StringIndexOutOfBoundsException | NumberFormatException e){
                input.setBackground(Color.RED);
            }
            return verified;
        }
    }

    class cellNullVerifier extends InputVerifier{
        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField) input).getText();
            if(text==null || text.length()==0 ){
                input.setBackground(Color.RED);
                return false;
            }
            return true;
        }
    }

    void addScreenTouchValidator(JTable table){
        TableColumn column = table.getColumnModel().getColumn(4);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem(NO_INFO_VAL);
        comboBox.addItem("tak");
        comboBox.addItem("nie");
        column.setCellEditor(new DefaultCellEditor(comboBox));
    }
    void addScreenTypeValidator(JTable table){
        TableColumn column = table.getColumnModel().getColumn(3);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem(NO_INFO_VAL);
        comboBox.addItem("matowa");
        comboBox.addItem("blyszczaca");
        column.setCellEditor(new DefaultCellEditor(comboBox));
    }
    void addDiskTypeValidator(JTable table){
        TableColumn column = table.getColumnModel().getColumn(10);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem(NO_INFO_VAL);
        comboBox.addItem("SSD");
        comboBox.addItem("HDD");
        column.setCellEditor(new DefaultCellEditor(comboBox));
    }


    private ArrayList<Integer> getDuplicatesIndx(JTable origTable, JTable newTable){
        List<String> originalTableRows = Arrays.asList(getDataFromTableToTxt(origTable).split("\n"));
        List<String> newTableRows = Arrays.asList(getDataFromTableToTxt(newTable).split("\n"));
        ArrayList<Integer> duplicIndx = new ArrayList<>();


        for( String row : newTableRows){
            for(int i=0;i< originalTableRows.size();i++){
                if(!duplicIndx.contains(i))
                    if(originalTableRows.get(i).equals(row))
                        duplicIndx.add(i);
            }

        }

        return duplicIndx;
    }

    private void validateDuplicatesOnRead(JTable origTable, JTable newTable ){

        ArrayList<Integer> duplicatedRowsIndx = getDuplicatesIndx(origTable,newTable);

        showChangedRowsCommunicate(newTable.getRowCount(),duplicatedRowsIndx.size());

        table.setDefaultRenderer(Object.class, new FilterRenderer(duplicatedRowsIndx));

    }

    private void showChangedRowsCommunicate(int allRowsCount, int duplicatesCount){
        int newRows = allRowsCount - duplicatesCount;
        labelMessage += ": znalazłem nowe " + newRows + " rekordy a pozostałe "+ duplicatesCount + " to duplikaty";

    }


    private void repaintTopUI(){
        topNavPanel = createTopPanel();
        frame.add(topNavPanel,BorderLayout.NORTH);

        frame.setVisible(true);
        frame.validate();
        frame.repaint();
    }


    void addChangedValListener(){

        Action action = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                TableCellListener tcl = (TableCellListener)e.getSource();

                if(!tcl.getOldValue().equals(tcl.getNewValue())) {

                    int row = tcl.getRow();
                    int column = tcl.getColumn();
                    StringBuilder oldRow = new StringBuilder();

                    for(int i=0; i< tableHeaderValues.size();i++){
                        if(i == column) {
                            oldRow.append(tcl.getOldValue().toString()).append(";");
                            continue;
                        }
                        oldRow.append(table.getValueAt(row, i)).append(";");
                    }
                    editedRows.put(tcl.getRow(), oldRow.toString());

                }
            }
        };

        TableCellListener tcl = new TableCellListener(table, action);
    }



}
