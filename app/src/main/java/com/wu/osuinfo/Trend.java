package com.wu.osuinfo;

import android.graphics.drawable.Drawable;
import android.widget.Button;

/**
 * Created by wu on 2017/7/22.
 */

public class Trend {
    private String username;
    private String userpp;
    private String userprepp;
    private String userrank;
    private String userprerank;
    private String usertotalscore;
    private String userpretotalscore;
    private String userplaycount;
    private String userpreplaycount;
    private Drawable useravatar;
    private Integer avatarcolor;
    private Button userdetail;
    // private Button unsub;
    private Drawable userblurredavatar;

    public Trend() {}

    public Trend(String username, String userpp, String userprepp, String userrank, String userprerank, String usertotalscore, String userpretotalscore, String userplaycount, String userpreplaycount, Drawable useravatar, Drawable userblurredavatar){
        this.username = username;
        this.userpp = userpp;
        this.userprepp = userprepp;
        this.userrank = userrank;
        this.userprerank = userprerank;
        this.usertotalscore = usertotalscore;
        this.userpretotalscore = userpretotalscore;
        this.userplaycount = userplaycount;
        this.userpreplaycount = userpreplaycount;
        this.useravatar = useravatar;
        this.userblurredavatar = userblurredavatar;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String n){
        this.username = n;
    }

    public String getUserpp(){
        return this.userpp;
    }

    public void setUserpp(String n){
        this.userpp = n;
    }

    public String getUserprepp(){
        return this.userprepp;
    }

    public void setUserprepp(String n){
        this.userprepp = n;
    }

    public String getUserrank(){
        return this.userrank;
    }

    public void setUserrank(String n){
        this.userrank = n;
    }

    public String getUserprerank(){
        return this.userprerank;
    }

    public void setUserprerank(String n){
        this.userprerank = n;
    }

    public String getUsertotalscore(){
        return this.usertotalscore;
    }

    public void setUsertotalscore(String n){
        this.usertotalscore = n;
    }

    public String getUserpretotalscore(){
        return this.userpretotalscore;
    }

    public void setUserpretotalscore(String n){
        this.userpretotalscore = n;
    }

    public String getUserplaycount(){
        return this.userplaycount;
    }

    public void setUserplaycount(String n){
        this.userplaycount = n;
    }

    public String getUserpreplaycount(){
        return this.userpreplaycount;
    }

    public void setUserpreplaycount(String n){
        this.userpreplaycount = n;
    }

    public Drawable getUseravatar(){
        return this.useravatar;
    }

    public void setUseravatar(Drawable n){
        this.useravatar = n;
    }

    public Button getUserdetail(){
        return this.userdetail;
    }

    public void setUserdetail(Button n){
        this.userdetail = n;
    }

    /*public Button getUnsub(){
        return this.unsub;
    }
    */

    /*public void setUnsub(Button n){
        this.unsub = n;
    }
    */

    public Integer getAvatarcolor(){
        return this.avatarcolor;
    }

    public Drawable getUserblurredavatar(){
        return this.userblurredavatar;
    }


}
