package org.example.Products;

import lombok.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Screen {
    @XmlAttribute
    String touch;
    String size;
    String resolution;
    String type;

    @XmlTransient
    public String getTouch() {
        return touch;
    }
}
