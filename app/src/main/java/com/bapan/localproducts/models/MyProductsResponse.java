package com.bapan.localproducts.models;

import java.util.List;

public class MyProductsResponse {
    private List<Product> myproducts = null;

    public List<Product> getMyproducts() {
        return myproducts;
    }

    public void setMyproducts(List<Product> myproducts) {
        this.myproducts = myproducts;
    }
}
