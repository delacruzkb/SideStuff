package edu.cofc.japanesestudytool;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;


@Entity
public class Term implements Serializable
{
    @PrimaryKey @NonNull
    String id;

    private String jpns;
    private String eng;
    private String kanji;
    private String type;
    private String lesson;
    private boolean reqKanji;
    private boolean checked;

    public Term()
    {
        jpns ="a";
        eng= "ア";
        kanji = "a";
        type = "u-verb";
        lesson="";
        reqKanji=false;
        checked = false;
        setId(jpns+eng);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.toLowerCase();
    }

    public String getJpns() {
        return jpns;
    }

    public void setJpns(String jpns) {
        this.jpns = jpns;
        setId(jpns+eng);
    }

    public String getEng() {
        return eng;
    }

    public void setEng(String eng) {
        this.eng = eng;
        setId(jpns+eng);
    }

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji.toLowerCase();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.toLowerCase();
    }


    public String getLesson() {
        return lesson;
    }


    public void setLesson(String lesson)
    {
        this.lesson = lesson;
    }
    public void setLesson(int position, int value)
    {
        int[] convertedLesson = fromStringToArray(lesson);
        convertedLesson[position] = value;
        setLesson(fromArrayToString(convertedLesson));
        System.out.println("woop");
    }
    public String getLessonString()
    {
        int[] lessons = fromStringToArray(lesson);
        String rtnval="";
        for(int i=0; i<lessons.length; i++)
        {
            if(lessons[i]>=0)
            {
                if(i==0)
                {
                    rtnval= rtnval+"extra,";
                }
                else {
                    rtnval = rtnval + lessons[i] + ",";
                }
            }
        }
        //remove last ","
        rtnval = rtnval.substring(0,rtnval.length()-1);
        return rtnval;
    }

    public boolean isReqKanji() {
        return reqKanji;
    }

    public void setReqKanji(boolean reqKanji) {
        this.reqKanji = reqKanji;
    }

    public void setReqKanji(String flag)
        {
        if( flag.equalsIgnoreCase("kanji"))
        {
            reqKanji = true;
        }
        else
        {
            reqKanji = false;
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public boolean equals(Object term)
    {
        boolean rtnval = false;
        Term otherTerm = (Term)term;
        if(this.getId().equals(otherTerm.getId()))
        {
            rtnval = true;
        }

        return rtnval;

    }

    public static int[] fromStringToArray(String value)
    {
        int[] lessons = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        for(int i=0;i<lessons.length;i++)
        {
            String letter = getLessonChar(i);
            if(value.contains(letter))
            {
                lessons[i]=i;
            }
        }
        return lessons;
    }

    public static String fromArrayToString(int[] value)
    {
        String lessonString="";

        for (int i=0; i< value.length;i++)
        {
            if(value[i]>=0)
            {
                lessonString= lessonString + getLessonChar(value[i]);
            }
        }

        return lessonString;
    }

    public static String getLessonChar(int lesson)
    {
        String rtnval="";
        int letterOffset= 'a';
        rtnval=String.valueOf(Character.toChars(letterOffset+lesson));
        return rtnval;
    }

}
