package org.example.Products;

import lombok.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Disc {
    @XmlAttribute
    String type;
    String storage;

    @XmlTransient
    public String getType() {
        return type;
    }
}
