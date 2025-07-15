package com.example.noteapp;

public class Page {
    private final String id;
    private String title;
    private String content;
    private String colorFont;
    private String colorText;


    public Page(String id, String title, String content, String colorFont, String colorText){
        this.id = id;
        this.title = title;
        this.content = content;
        this.colorFont = colorFont;
        this.colorText = colorText;
    }

    public Page(String id, String title, String content) {
        this(id, title, content, "#FF000000", "#FFFFFFFF");
    }

    public String getId(){return id;}
    public String getTitle(){return title;}
    public String getContent(){return content;}
    public String getColorFont(){return colorFont;}
    public String getColorText(){return colorText;}

    public void setTitle(String title){this.title = title;}
    public void setContent(String content){this.content = content;}
    public void setColorFont(String colorFont){this.colorFont = colorFont;}
    public void setColorText(String colorText){this.colorText = colorText;}

}
