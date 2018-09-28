package edu.cofc.japanesestudytool.Pages;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import edu.cofc.japanesestudytool.Adapters.CheckBoxDropDownSpinnerAdapter;
import edu.cofc.japanesestudytool.AsyncTasks.DeleteTerm;
import edu.cofc.japanesestudytool.AsyncTasks.InsertTerms;
import edu.cofc.japanesestudytool.R;
import edu.cofc.japanesestudytool.Term;
public class EditSingleTermDialogPage extends AppCompatActivity
{
    private EditText japaneseTextBox,englishTextBox,kanjiTextBox;
    private Spinner typeDropDownBar,lessonDropDownBar;
    private CheckBox reqKanjiCheckBox;
    private Button cancelEdit,confirmEdit;
    private Term originalTerm,editedTerm;
    private final String[] typeSpecs = new String[]{"noun","u-verb","ru-verb","irr-verb","adjective","grammar","other"};
    private CheckBoxDropDownSpinnerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_single_term_dialog);

        DisplayMetrics dp = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dp);
        double factor = 0.8;
        getWindow().setLayout((dp.widthPixels),(int)(dp.heightPixels*factor));

        originalTerm = (Term) getIntent().getSerializableExtra("term");
        japaneseTextBox = findViewById(R.id.editHiraganaTextBox);
        japaneseTextBox.setText(originalTerm.getJpns());
        englishTextBox = findViewById(R.id.editEnglishTextBox);
        englishTextBox.setText(originalTerm.getEng());
        kanjiTextBox = findViewById(R.id.editKanjiTextBox);
        kanjiTextBox.setText(originalTerm.getKanji());
        typeDropDownBar = findViewById(R.id.editTypeDropDownBar);
        ArrayAdapter<String> mainAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,typeSpecs);
        typeDropDownBar.setAdapter(mainAdapter);
        typeDropDownBar.setSelection(mainAdapter.getPosition(originalTerm.getType().toString()));

        lessonDropDownBar = findViewById(R.id.editLessonDropDownBar);
        adapter = new CheckBoxDropDownSpinnerAdapter(this.getApplicationContext());
        adapter.setCheckedFromArray(originalTerm.getLessonArray());
        lessonDropDownBar.setAdapter(adapter);

        reqKanjiCheckBox = findViewById(R.id.editReqKanjiCheckBox);
        reqKanjiCheckBox.setChecked(originalTerm.isReqKanji());
        cancelEdit = findViewById(R.id.cancelEditTermButton);
        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        confirmEdit = findViewById(R.id.confirmEditTermButton);
        confirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(englishTextBox.getText().toString().length() ==0 || japaneseTextBox.getText().toString().length() ==0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(confirmEdit.getContext());
                    builder.setTitle(getResources().getString(R.string.errorTitle));
                    builder.setMessage(getResources().getString(R.string.fillReqFields));
                    builder.setPositiveButton(getResources().getString(R.string.okLabel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (englishTextBox.getText().toString().contains("\n") || japaneseTextBox.getText().toString().contains("\n")
                        || kanjiTextBox.getText().toString().contains("\n"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(englishTextBox.getContext());
                    builder.setTitle(getResources().getString(R.string.warningTitle));
                    builder.setMessage(getResources().getString(R.string.multiLineWarning));
                    builder.setNegativeButton(getResources().getString(R.string.cancelLabel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton(getResources().getString(R.string.proceedLabel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            promptThenAdd();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    promptThenAdd();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putIntArray("lessons",adapter.getLessonsArray());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        adapter.setCheckedFromArray(savedInstanceState.getIntArray("lessons"));
        //TODO:figure out how to set
    }

    private void promptThenAdd()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(confirmEdit.getContext());
        builder.setTitle(getResources().getString(R.string.warningTitle));
        builder.setMessage(getResources().getString(R.string.overwriteMessage));
        builder.setNegativeButton(getResources().getString(R.string.cancelLabel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.proceedLabel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                editedTerm = new Term();
                editedTerm.setJpns(japaneseTextBox.getText().toString());
                editedTerm.setEng(englishTextBox.getText().toString());
                editedTerm.setKanji(kanjiTextBox.getText().toString());
                editedTerm.setType(typeDropDownBar.getSelectedItem().toString());
                editedTerm.setLesson(adapter.getLessonsArray());
                editedTerm.setReqKanji(reqKanjiCheckBox.isChecked());
                ArrayList<Term> temp = new ArrayList<>();
                temp.add(editedTerm);
                DeleteTerm deleteTerm = new DeleteTerm(confirmEdit.getContext());
                deleteTerm.execute(originalTerm);
                InsertTerms insertTerms = new InsertTerms(confirmEdit.getContext());
                insertTerms.execute(temp);
                Intent intent = new Intent(confirmEdit.getContext(),EditTermsMenuPage.class);
                confirmEdit.getContext().startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
