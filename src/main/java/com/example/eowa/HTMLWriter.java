package com.example.eowa;

public class HTMLWriter {

    public static String h1(String content){
        return evenHtmlElement(content,"h1");
    }

    public static String h2(String content){
        return evenHtmlElement(content,"h2");
    }

    public static String p(String content){
        return evenHtmlElement(content,"p");
    }

    public static String table(String content){
        return evenHtmlElement(content,"table");
    }

    public static String tr(String content){
        return evenHtmlElement(content,"tr");
    }
    public static String th(String content){
        return evenHtmlElement(content,"th");
    }
    public static String td(String content){
        return evenHtmlElement(content,"td");
    }

    private static String evenHtmlElement(String content, String elementName){
        return "<"+elementName+">"+content+"</"+elementName+">";
    }

    public static String htmlEscape(String text) {
        return text
                .replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#39;");
    }

}
