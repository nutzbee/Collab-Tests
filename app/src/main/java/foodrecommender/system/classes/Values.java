package foodrecommender.system.classes;

public class Values {

    private String valuesTitle, valuesValue;

    public Values(String valuesTitle, String valuesValue) {
        this.valuesTitle = valuesTitle;
        this.valuesValue = valuesValue;
    }

    public String getValuesTitle() {
        return valuesTitle;
    }

    public String getValuesValue(){
        return valuesValue;
    }

    public void setValuesValue(String valuesValue){
        this.valuesValue = valuesValue;
    }
}
