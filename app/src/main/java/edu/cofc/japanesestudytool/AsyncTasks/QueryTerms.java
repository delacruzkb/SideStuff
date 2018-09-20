package edu.cofc.japanesestudytool.AsyncTasks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.cofc.japanesestudytool.Pages.FlashCardPage;
import edu.cofc.japanesestudytool.Pages.HomePage;
import edu.cofc.japanesestudytool.Pages.KanjiWritingPage;
import edu.cofc.japanesestudytool.Pages.StoryPage;
import edu.cofc.japanesestudytool.R;
import edu.cofc.japanesestudytool.Term;
import edu.cofc.japanesestudytool.TermDatabase;
import edu.cofc.japanesestudytool.TermMenuMetrics;

public class QueryTerms extends AsyncTask<Void,Void,Void>
{
    TermDatabase termDatabase;
    TermMenuMetrics metrics;
    Context mContext;
    ArrayList<Term> nounList, verbList, adjectiveList, grammarList, otherList,termList;
    public QueryTerms(TermMenuMetrics termMenuMetrics, Context context)
    {
        mContext = context;
        metrics = termMenuMetrics;
        termDatabase = Room.databaseBuilder(mContext,TermDatabase.class,"terms").build();
    }

    @Override
    protected void onPostExecute(Void voids)
    {
        if(termList.size()==0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getResources().getString(R.string.errorTitle));
            builder.setMessage(mContext.getResources().getString(R.string.noTermsFound));
            builder.setPositiveButton(mContext.getResources().getString(R.string.okLabel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
        }
        else
        {
            Intent intent = new Intent(mContext, HomePage.class);
            if(metrics.getMode().equalsIgnoreCase("story"))
            {
                intent = new Intent(mContext, StoryPage.class);
            }
            if(metrics.getMode().equalsIgnoreCase("flashcard"))
            {
                intent = new Intent(mContext, FlashCardPage.class);
            }
            if(metrics.getMode().equalsIgnoreCase("kanjiwriting"))
            {
                intent= new Intent(mContext, KanjiWritingPage.class);
            }
            intent.putExtra("metrics",metrics);
            intent.putExtra("nounList",nounList);
            intent.putExtra("verbList",verbList);
            intent.putExtra("adjectiveList",adjectiveList);
            intent.putExtra("grammarList",grammarList);
            intent.putExtra("otherList",otherList);
            Collections.shuffle(termList);
            intent.putExtra("termList",termList);
            mContext.startActivity(intent);
        }

    }

    @Override
    protected Void doInBackground(Void... voids)
    {

        if(metrics.isAllTerms())
        {
            nounList = loadTerms("noun", metrics.getNounCount());
            verbList = loadTerms("verb", metrics.getVerbCount());
            adjectiveList = loadTerms("adjective", metrics.getAdjectiveCount());
            grammarList = loadTerms("grammar", metrics.getGrammarCount());
            otherList = loadTerms("other", metrics.getOtherCount());
            termList= new ArrayList<>();
            if(nounList !=null)
            {
                termList.addAll(nounList);
            }
            if(verbList !=null)
            {
                termList.addAll(verbList);
            }
            if(adjectiveList !=null)
            {
                termList.addAll(adjectiveList);
            }
            if(grammarList !=null)
            {
                termList.addAll(grammarList);
            }
            if(otherList !=null)
            {
                termList.addAll(otherList);
            }
        }
        else
        {
            int[] lessons = metrics.getLessons();
            nounList=   loadFromLessons("noun",lessons,metrics.getNounCount());
            verbList=   loadFromLessons("verb",lessons,metrics.getVerbCount());
            adjectiveList=  loadFromLessons("adjective",lessons,metrics.getAdjectiveCount());
            grammarList=    loadFromLessons("grammar",lessons,metrics.getGrammarCount());
            otherList=  loadFromLessons("other",lessons,metrics.getOtherCount());
            termList= new ArrayList<>();
            if(nounList !=null)
            {
                termList.addAll(nounList);
            }
            if(verbList !=null)
            {
                termList.addAll(verbList);
            }
            if(adjectiveList !=null)
            {
                termList.addAll(adjectiveList);
            }
            if(grammarList !=null)
            {
                termList.addAll(grammarList);
            }
            if(otherList !=null)
            {
                termList.addAll(otherList);
            }
        }
        return null;
    }

    private ArrayList<Term> loadTerms(String type, int count)
    {
        ArrayList<Term> rtnval = new ArrayList<>();
        if(metrics.useKanjiOnly())
        {
            if(metrics.useLessonKanjiOnly())
            {
                rtnval = (ArrayList<Term>) termDatabase.termDAO().getLessonKanjiOnly(type,count);
            }
            else
            {

                rtnval = (ArrayList<Term>) termDatabase.termDAO().getKanjiOnly(type,count);

            }
        }
        else
        {
            rtnval= (ArrayList<Term>) termDatabase.termDAO().getAllTypes(type,count);
        }

        return rtnval;
    }

    private ArrayList<Term> loadFromLessons(String type, int[] lessons, int count)
    {
        Set<Term> termHashTable = new HashSet<>();
        ArrayList<Term> rtnval=new ArrayList<>();
        ArrayList<Term> temp=new ArrayList<>();
        if(metrics.useKanjiOnly())
        {
            if(metrics.useLessonKanjiOnly())
            {
                for(int i=0;i<lessons.length;i++ )
                {
                    temp = (ArrayList<Term>) termDatabase.termDAO().getLessonKanjiOnlyFromLessons(type,Term.getLessonChar(lessons[i]),count);
                    termHashTable.addAll(temp);
                }
            }
            else
            {
                for(int i=0;i<lessons.length;i++ )
                {
                    temp = (ArrayList<Term>) termDatabase.termDAO().getKanjiOnlyFromLessons(type,Term.getLessonChar(lessons[i]),count);
                    termHashTable.addAll(temp);
                }
            }
        }
        else
        {
            for(int i=0;i<lessons.length;i++ )
            {
                temp = (ArrayList<Term>) termDatabase.termDAO().getAllTypeFromLessons(type,Term.getLessonChar(lessons[i]),count);
                termHashTable.addAll(temp);
            }
        }
        //Add all terms in the set to the array list
        rtnval.addAll(termHashTable);
        //Randomize order and truncate to fit use
        Collections.shuffle(rtnval);
        if(rtnval.size()>count)
        {
            temp = new ArrayList<>();
            for(int i =0; i<count;i++)
            {
                temp.add(rtnval.get(i));
            }
            rtnval=temp;
        }

        return rtnval;
    }
}
