package org.example.Products;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Processor {
    String name;
    Integer physicalCores;
    Integer clockSpeed;
}
