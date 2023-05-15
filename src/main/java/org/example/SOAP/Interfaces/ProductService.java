package org.example.SOAP.Interfaces;

import org.example.Entities.Product;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface ProductService {
    @WebMethod
    List<Product> getProducentProducts (String producent);

    @WebMethod
    List<Product> getScreenTypeProducts (String screenType);

    @WebMethod
    List<Product> getAspectRatioProducts(String aspectRatio);

}
