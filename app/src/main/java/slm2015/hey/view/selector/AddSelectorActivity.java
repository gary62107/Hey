package slm2015.hey.view.selector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import slm2015.hey.R;
import slm2015.hey.view.util.UiUtility;

public class AddSelectorActivity extends Activity {
    private ImageButton confirm;
    private EditText add_selector_edittext;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_selector_activity);

        initialConfirmButton();
        initialAddSelectorEditText();
        initialBackButton();
    }

    private void initialConfirmButton() {
        this.confirm = (ImageButton) findViewById(R.id.confirm);
        this.confirm.setEnabled(false);
        this.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean confirm = false;
                backToMainActivity(confirm);
            }
        });
    }

    private void initialAddSelectorEditText() {
        this.add_selector_edittext = (EditText) findViewById(R.id.add_selector_edittext);
        this.add_selector_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = add_selector_edittext.getText().toString();
                if (input == null || input.equals((String) ""))
                    confirm.setEnabled(false);
                else
                    confirm.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initialBackButton() {
        this.back = (ImageButton) findViewById(R.id.back);
        this.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtility.closeKeyBoard(AddSelectorActivity.this);
                SystemClock.sleep(100);
                onBackPressed();
            }
        });
    }

    private void backToMainActivity(boolean backPressed) {
        Intent intent = new Intent();
        String addSelector = this.add_selector_edittext.getText().toString();
        if (backPressed || addSelector.isEmpty())
            setResult(Activity.RESULT_CANCELED);
        else {
            intent.putExtra("selector", addSelector);
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        boolean backPressed = true;
        backToMainActivity(backPressed);
    }

    @Override
    protected void onPause() {
        super.onPause();
        backToMainActivity(true);
    }
}
