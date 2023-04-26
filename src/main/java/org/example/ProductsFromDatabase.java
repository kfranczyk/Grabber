package org.example;

import org.example.Entities.Product;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

public class ProductsFromDatabase {
    SessionFactory sessionFactory;

    List tableHeaderValues;

    public ProductsFromDatabase() {
        ProductsFromTXTFile productsFromTXTFile = new ProductsFromTXTFile();
        tableHeaderValues =  Arrays.asList(productsFromTXTFile.columnTitles);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DefaultTableModel getProductsFromDB(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Product> result = session.createQuery("from Product", Product.class).list();

        DefaultTableModel defaultTableModel = new DefaultTableModel(tableHeaderValues.toArray(), 0);

        result.forEach( product -> {

            defaultTableModel.addRow(product.toTxtString().replace("null","brak informacji").split(";"));

        });

        session.getTransaction().commit();
        session.close();

        return defaultTableModel;
    }
    public void writeProductsToDB(ArrayList<Integer> duplicateRows, JTable table, HashMap<Integer,String> editedRows){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List<Product> dbRows = session.createQuery("from Product", Product.class).list();

        ArrayList<Product> uniqueProducts = getProductsFromTable(duplicateRows, table, dbRows, editedRows);

        session.clear();


        uniqueProducts.forEach( product -> {
            System.out.println(product);
            session.saveOrUpdate(product);
        });

        session.getTransaction().commit();
        session.close();
    }

    private ArrayList<Product> getProductsFromTable(ArrayList<Integer> duplicateRows, JTable table, List<Product> dbRows, HashMap<Integer,String> editedRows){
        Integer idFromDB;
        boolean rowExist;
        Product tmpProduct;
        ArrayList<Product> tmpProducts = new ArrayList<>();

        ArrayList<Integer> dbRowIds = new ArrayList<>();
        dbRows.forEach( row -> dbRowIds.add(row.getId()));

        for(Integer i=0; i< table.getRowCount();i++) {
            if (duplicateRows.contains(i))
                continue;

            tmpProduct = createProductFromTable(table, i);

            rowExist=isProductInDB(dbRows,tmpProduct);

            if(!rowExist) {
                if(editedRows.containsKey(i)){
                    Product tmpPrd = getIdOfEditedRow(getPrdFromTxt(editedRows.get(i)));
                    idFromDB = tmpPrd != null ? tmpPrd.getId() : null;
                    tmpProduct.setId(idFromDB);
                    tmpProducts.add(tmpProduct);
                }
                else {
                    tmpProducts.add(tmpProduct);
                }
            }
        }
        return tmpProducts;
    }


    private Product createProductFromTable(JTable table, int i){
        return new Product(
                null,
                table.getValueAt(i,0).toString(),
                table.getValueAt(i,1).toString(),
                table.getValueAt(i,2).toString(),
                table.getValueAt(i,3).toString(),
                table.getValueAt(i,4).toString(),
                table.getValueAt(i,5).toString(),

                table.getValueAt(i,6).toString().equals("brak informacji") ?
                        null :
                        Integer.valueOf(table.getValueAt(i,6).toString()),

                table.getValueAt(i,7).toString().equals("brak informacji") ?
                        null :
                        Integer.valueOf(table.getValueAt(i,7).toString()),

                table.getValueAt(i,8).toString(),
                table.getValueAt(i,9).toString(),
                table.getValueAt(i,10).toString(),
                table.getValueAt(i,11).toString(),
                table.getValueAt(i,12).toString(),
                table.getValueAt(i,13).toString(),
                table.getValueAt(i,14).toString()
        );
    }

    private boolean isProductInDB(List<Product> dbRows, Product tmpProduct){
        for(Product dbProduct : dbRows){
            dbProduct.setId(null);

            if(dbProduct.equals(tmpProduct))
                return true;
        }
        return false;
    }
    public Product getIdOfEditedRow( Product product){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Example example = Example.create(product);
        Criteria criteria = session.createCriteria(Product.class).add(example);
        System.out.println(criteria.list());
        if(criteria.list().size() == 0)
            return null;
        return (Product) criteria.list().get(0) ;
    }
    private  Product getPrdFromTxt(String txt){
        String[] attr = txt.split(";");
        for(int i=0;i<attr.length;i++){
            if (attr[i].equals("brak informacji"))
                attr[i]=null;
        }

        return new Product(
                null,
                attr[0],
                attr[1],
                attr[2],
                attr[3],
                attr[4],
                attr[5],
                attr[6] == (null) ? null : Integer.valueOf(attr[6]) ,
                attr[7] == (null) ? null : Integer.valueOf(attr[7]) ,
                attr[8],
                attr[9],
                attr[10],
                attr[11],
                attr[12],
                attr[13],
                attr[14]
        );
    }
}
