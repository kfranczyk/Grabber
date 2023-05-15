package org.example.SOAP;

import org.example.Entities.Product;
import org.example.ProductsFromDatabase;
import org.example.SOAP.Interfaces.ProductService;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@WebService(endpointInterface = "org.example.SOAP.Interfaces.ProductService")
public class ProductServiceImpl implements ProductService {

    private ProductsFromDatabase productsFromDatabase;

    final Map<String, Double> acceptableAspectRatios = Stream.of(new Object[][] {
            {"4:3", 1.33},
            {"5:4", 1.25},
            {"3:2", 1.5},
            {"16:10", 1.6},
            {"16:9", 1.78},
            {"17:9", 1.89},
            {"21:9", 2.37},
            {"32:9", 3.56},
            {"1:1", 1.0},
            {"4:1", 4.0}
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Double) data[1]));

    final ArrayList<String> acceptableScreenType = new ArrayList<>(Arrays.asList("matowa","błyszcząca"));
    final ArrayList<String>  acceptableManufacturer = new ArrayList<>(Arrays.asList(
        "Asus",
        "Fujitsu",
        "Huawei",
        "MSI",
        "Dell",
        "Samsung",
        "Sony"
        ));



    public ProductServiceImpl(ProductsFromDatabase productsFromDatabase) {
        this.productsFromDatabase = productsFromDatabase;
    }

    @Override
    public List<Product> getProducentProducts(String producent) {
        if(!acceptableManufacturer.contains(producent))
            return null;
        return productsFromDatabase.getProductsFromDBWithSelectedManufacturer(producent);
    }

    @Override
    public List<Product> getScreenTypeProducts(String screenType) {
        if(!acceptableScreenType.contains(screenType))
            return null;

        return productsFromDatabase.getProductsFromDBWithSelectedScreenType(screenType);
    }

    @Override
    public List<Product> getAspectRatioProducts(String aspectRatio) {
        if(!acceptableAspectRatios.containsKey(aspectRatio))
            return null;

        return productsFromDatabase.getProductsFromDBWithCalculatedAspectRatio(acceptableAspectRatios.get(aspectRatio));
    }
}



