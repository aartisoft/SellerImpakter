package com.impakter.seller.object;

public class BrandObj {
    private int id;
    private String typeBrand;
    private int idBrand;
    private String brandName;
    private String description;


    public BrandObj(int id, String typeBrand, int idBrand, String brandName, String description) {
        this.id = id;
        this.typeBrand = typeBrand;
        this.idBrand = idBrand;
        this.brandName = brandName;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeBrand() {
        return typeBrand;
    }

    public void setTypeBrand(String typeBrand) {
        this.typeBrand = typeBrand;
    }

    public int getIdBrand() {
        return idBrand;
    }

    public void setIdBrand(int idBrand) {
        this.idBrand = idBrand;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
