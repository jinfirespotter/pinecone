package com.firespotter.jinwroh.pinecone.Module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jinroh on 2/2/15.
 */
public class TextExtractor {

    private String content;

    public TextExtractor(String content) {
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public String extractEmail() {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
        Matcher matcher = pattern.matcher(this.content);

        while (matcher.find()) {
            System.out.println(matcher.group());
            return matcher.group();
        }
        return "";
    }

    // TODO:
    public String extractPhoneNumber() {
        Pattern pattern = Pattern.compile("^[a-zA-Z]+([0-9]+).*");
        Matcher matcher = pattern.matcher(this.content);

        if (matcher.find()) {
            System.out.println(matcher.group(1));
            return matcher.group();
        }
        return "";
    }

    // TODO:
    public String extractName() {
        return "";
    }


}
