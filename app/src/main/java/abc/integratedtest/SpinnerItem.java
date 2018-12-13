package abc.integratedtest;

/**
 * Created by HAN on 2017-10-08.
 */

public class SpinnerItem {
    private int id;
    private String txt;

    public SpinnerItem(int id, String txt) {
        this.id = id;
        this.txt = txt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
}
