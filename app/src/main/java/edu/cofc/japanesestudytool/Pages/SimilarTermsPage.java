package edu.cofc.japanesestudytool.Pages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.cofc.japanesestudytool.Adapters.AddTermListAdapter;
import edu.cofc.japanesestudytool.AsyncTasks.InsertTerm;
import edu.cofc.japanesestudytool.R;
import edu.cofc.japanesestudytool.Term;

public class SimilarTermsPage extends AppCompatActivity
{
    private ListView similarTermsList;
    private Button toJpns,toEng,toKanji,cancel,addNewTerm;
    private ArrayList<Term> similarTerms;
    private Term newTerm;
    private EditText writingStyleTextBox;
    private TextView writingStyleLabel,newLessonTextBox,newTypeTextBox,newReqKanjiTextBox;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_terms_page);

        Intent intent = getIntent();
        newTerm = (Term) intent.getSerializableExtra("newTerm");
        similarTerms = (ArrayList<Term>)intent.getSerializableExtra("similarTerms");
        instantiateViews();
        loadNewTermOntoDisplay(newTerm);

    }

    private void instantiateViews()
    {
        writingStyleLabel = findViewById(R.id.writingStyleLabel);
        writingStyleTextBox = findViewById(R.id.writingStyleTextBox);
        writingStyleTextBox.setEnabled(false);
        newLessonTextBox = findViewById(R.id.newTermLessonTextBox);
        newTypeTextBox = findViewById(R.id.newTermTypeTextBox);
        newReqKanjiTextBox = findViewById(R.id.newTermReqKanjiTextBox);
        similarTermsList = findViewById(R.id.similarTermListView);
        AddTermListAdapter addTermListAdapter = new AddTermListAdapter(similarTerms,this);
        similarTermsList.setAdapter(addTermListAdapter);

        toJpns = findViewById(R.id.toJpnsNewTerm);
        toJpns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writingStyleLabel.setText(getResources().getString(R.string.toJapaneseText));
                writingStyleTextBox.setText(newTerm.getJpns());
            }
        });
        toEng = findViewById(R.id.toEngNewTerm);
        toEng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writingStyleLabel.setText(getResources().getString(R.string.toEnglishText));
                writingStyleTextBox.setText(newTerm.getEng());
            }
        });
        toKanji = findViewById(R.id.toKanjiNewTerm);
        toKanji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writingStyleLabel.setText(getResources().getString(R.string.toKanjiText));
                writingStyleTextBox.setText(newTerm.getKanji());
            }
        });

        cancel = findViewById(R.id.cancelNewTermButton);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder warning = new AlertDialog.Builder(cancel.getContext());
                warning.setTitle(getResources().getString(R.string.warningTitle));
                warning.setMessage(getResources().getString(R.string.onBackPressedMessage));
                warning.setPositiveButton(getResources().getString(R.string.proceedLabel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
                warning.setNegativeButton(getResources().getString(R.string.cancelLabel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = warning.create();
                dialog.show();
            }
        });

        addNewTerm = findViewById(R.id.proceedNewTermButton);
        addNewTerm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder warning = new AlertDialog.Builder(cancel.getContext());
                warning.setTitle(getResources().getString(R.string.warningTitle));
                warning.setMessage(getResources().getString(R.string.addNewTermMessage));
                warning.setNegativeButton(getResources().getString(R.string.cancelLabel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                warning.setPositiveButton(getResources().getString(R.string.addNewTerm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        InsertTerm insertTerm = new InsertTerm(addNewTerm.getContext());
                        ArrayList<Term> temp = new ArrayList<>();
                        temp.add(newTerm);
                        insertTerm.execute(temp);
                        onBackPressed();
                    }
                });
                AlertDialog dialog = warning.create();
                dialog.show();
            }
        });

    }
    private void loadNewTermOntoDisplay(Term term)
    {
        writingStyleLabel.setText(getResources().getString(R.string.toJapaneseText));
        writingStyleTextBox.setText(term.getJpns());
        newTypeTextBox.setText(term.getType());
        newLessonTextBox.setText(term.getNumberedLessonString());
        if(!term.isReqKanji())
        {
            newReqKanjiTextBox.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("styleLabel",writingStyleLabel.getText().toString());
        savedInstanceState.putString("styleTextBox",writingStyleTextBox.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        writingStyleLabel.setText(savedInstanceState.getString("styleLabel"));
        writingStyleTextBox.setText(savedInstanceState.getString("styleTextBox"));
    }
}
