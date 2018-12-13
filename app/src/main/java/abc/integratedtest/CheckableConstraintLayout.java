package abc.integratedtest;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;

/**
 * Created by HAN on 2017-10-11.
 */

// Custom ListView Item Layout 전용 Custom ConstraintLayout(체크 관련 기능 추가)
public class CheckableConstraintLayout extends ConstraintLayout implements Checkable {

    public CheckableConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean b) {
        CheckBox cb = findViewById(R.id.cb_isDeleteItem);

        cb.setChecked(b);
    }

    @Override
    public boolean isChecked() {
        CheckBox cb = findViewById(R.id.cb_isDeleteItem);

        return cb.isChecked();
    }

    @Override
    public void toggle() {
        CheckBox cb = findViewById(R.id.cb_isDeleteItem);

        setChecked(!cb.isChecked());
    }
}
