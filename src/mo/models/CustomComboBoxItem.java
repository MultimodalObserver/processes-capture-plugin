package mo.models;

/* Clase POJO utilizada para asignar de manera custom los elementos de un comboBox,
en especifico para permitir hacer el mape de un elemento del comboBox en multiples idiomas:

id --> valor en español, valor en ingles, ...

La clave es sobreescribir el metodo toString, ya que es el que se utilizada para hacer el render
de los elementos custom en el comboBox
 */
public class CustomComboBoxItem {
    private int id;
    private String value;

    public CustomComboBoxItem(int id, String value){
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return this.value;
    }
}
