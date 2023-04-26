package org.example.Products;

import lombok.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Laptop {
    @XmlAttribute
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String manufacturer;
    String ram;
    String operatingSystem;
    String discReader;

    Screen screen;
    Disc disc;
    GraphicCard graphicCard;
    Processor processor;

    public Laptop(Integer id, String manufacturer, String ram, String operatingSystem, String discReader) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.ram = ram;
        this.operatingSystem = operatingSystem;
        this.discReader = discReader;
    }

    @XmlTransient
    public Integer getId() {
        return id;
    }


}
