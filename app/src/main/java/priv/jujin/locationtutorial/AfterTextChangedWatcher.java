package priv.jujin.locationtutorial;

import android.text.Editable;
import android.text.TextWatcher;

public class AfterTextChangedWatcher implements TextWatcher {
    AfterTextChangedCallback afterTextChangedCallback;

    public AfterTextChangedWatcher(AfterTextChangedCallback afterTextChangedCallback) {
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

    interface AfterTextChangedCallback {
        void callback(Editable s);
    }
}