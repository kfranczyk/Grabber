package org.example.Products;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Laptops {
    @XmlAttribute
    private String moddate;

    private List<Laptop> laptop;

    @XmlTransient
    public String getModdate() {
        return moddate;
    }
}
