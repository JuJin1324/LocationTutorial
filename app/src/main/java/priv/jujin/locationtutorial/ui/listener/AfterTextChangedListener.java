package priv.jujin.locationtutorial.ui.listener;

import android.text.Editable;
import android.text.TextWatcher;

public class AfterTextChangedListener implements TextWatcher {
    AfterTextChangedCallback afterTextChangedCallback;

    public AfterTextChangedListener(AfterTextChangedCallback afterTextChangedCallback) {
        this.afterTextChangedCallback = afterTextChangedCallback;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        this.afterTextChangedCallback.callback(s);
    }

    public interface AfterTextChangedCallback {
        void callback(Editable s);
    }
}