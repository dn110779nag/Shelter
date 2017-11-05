package com.novax00.shelter.shelter.data;

import java.math.BigDecimal;

/**
 * Created by Samsung on 9/24/2017.
 */
@lombok.Data
public class Item {
    private long id;
    private String name;
    private String description;
    private long quantity;
    private BigDecimal price;
    private String imagePath;
}
