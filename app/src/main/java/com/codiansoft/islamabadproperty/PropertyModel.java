package com.codiansoft.islamabadproperty;

/**
 * Created by CodianSoft on 17/01/2018.
 */

public class PropertyModel {
    private String date;
    private String contactNumber;
    private String description;
    private String imageLink;


    public PropertyModel( String date,String contactNumber,String description,String imageLink)
    {
        this.date=date;
        this.contactNumber=contactNumber;
        this.description=description;
        this.imageLink=imageLink;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
