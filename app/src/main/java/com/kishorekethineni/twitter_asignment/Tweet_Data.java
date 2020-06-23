package com.kishorekethineni.twitter_asignment;

public class Tweet_Data {
    int SNo;
    String Name;
    String Tweet;
    String Date;

    public Tweet_Data(int Sno,String name,String tweet, String date) {
        SNo=Sno;
        Name=name;
        Tweet = tweet;
        Date = date;
    }

    public int SNo() {
        return SNo;
    }
    public String Name() {
        return Name;
    }
    public String getTweet() {
        return Tweet;
    }

    public String getDate() {
        return Date;
    }
}
