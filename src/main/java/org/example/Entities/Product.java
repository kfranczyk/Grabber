package org.example.Entities;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String manufacturer;
    String size;
    String resolution;
    String screenType;
    String touch;

    String processorName;
    Integer physicalCores;
    Integer clockSpeed;

    String ram;

    String storage;
    String discType;

    String graphicCardName;
    String graphicCardMemory;

    String operatingSystem;
    String discReader;











    public String toLaptopString() {
        return "Laptop(" +
                "id=" + id +
                ", manufacturer=" + manufacturer +
                ", ram=" + ram +
                ", operatingSystem=" + operatingSystem +
                ", discReader=" + discReader +
                ", screen=Screen(touch=" + touch +
                ", size=" + size +
                ", resolution=" + resolution +
                ", type=" + screenType +  ')'+
                ", disc=Disc(type=" + discType +
                ", storage=" + storage +  ')'+
                ", graphicCard=GraphicCard(name=" + graphicCardName +
                ", memory=" + graphicCardMemory +  ')' +
                ", processor=Processor(name=" + processorName +
                ", physicalCores=" + physicalCores +
                ", clockSpeed=" + clockSpeed + ")"+
                ')';
    }
    public String toTxtString(){
        return  manufacturer + ';' +
                size + ';' +
                resolution + ';' +
                screenType + ';' +
                touch + ';'+
                processorName + ';' +
                physicalCores + ';' +
                clockSpeed + ';'+
                ram + ';' +
                storage + ';' +
                discType + ';' +
                graphicCardName + ';' +
                graphicCardMemory + ';' +
                operatingSystem + ';'+
                discReader + ';'
                ;
    }
}
