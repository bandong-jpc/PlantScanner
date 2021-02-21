package com.example.plantscanner;

public class Plant {
    public String  contributor, contributorUID, file, localName, medicinalUse;
    public boolean approved;

    public Plant(){

    }

    public Plant( String contributor, String contributorUID, String file, String localName, String medicinalUse, boolean approved) {
        //this.name = name;
        this.contributor = contributor;
        this.contributorUID = contributorUID;
        this.file = file;
        this.localName = localName;
        this.medicinalUse = medicinalUse;
        this.approved = approved;
    }

    /*public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
*/
    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getContributorUID() {
        return contributorUID;
    }

    public void setContributorUID(String contributorUID) {
        this.contributorUID = contributorUID;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getMedicinalUse() {
        return medicinalUse;
    }

    public void setMedicinalUse(String medicinalUse) {
        this.medicinalUse = medicinalUse;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
