package org.eclipse.lemminx.customservice.syntaxmodel;

public class SyntaxTextNode {

    public int start;
    public int end;
    public String data;
    public String normalizedData;
    public boolean isWhitespace;

    public SyntaxTextNode(int start, int end, String data, String normalizedData, boolean isWhitespace) {

        this.start = start;
        this.end = end;
        this.data = data;
        this.normalizedData = normalizedData;
        this.isWhitespace = isWhitespace;
    }
}
