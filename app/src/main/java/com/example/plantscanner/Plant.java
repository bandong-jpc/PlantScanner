package com.example.plantscanner;

public class Plant {
    public String   file, localName, medicinalUse, benefits, properUsage;/*contributor, contributorUID,*/
    public boolean approved;

    public Plant(){

    }

    public Plant( String benefits, /*String contributor, String contributorUID,*/ String file, String localName, String medicinalUse, String properUsage, boolean approved) {
        this.benefits = benefits;
        /*this.contributor = contributor;
        this.contributorUID = contributorUID;*/
        this.file = file;
        this.localName = localName;
        this.medicinalUse = medicinalUse;
        this.approved = approved;
        this.properUsage = properUsage;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    /*public String getContributor() {
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
    }*/

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
