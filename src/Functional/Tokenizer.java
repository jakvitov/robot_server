package Functional;

/**
 * Tokenizer class used to split strings to many parts by a String token
 * All messages from the client include only one token at the end, therefore this class is customised
 * only for those purposes
 */
public class Tokenizer {

    private String token;
    private String text;

    public Tokenizer (String text, String token){

        if (text == null){
            this.text = new String();
        }

        this.token = token;
        this.text = text;
    }

    public boolean hasMoreTokens (){
        if (this.text.contains("\\a\\b")){
            return true;
        }
        return false;
    }

    //A method that returns next part of the text before next token
    public String nextToken(){
        return this.text.replace("\\a\\b", "");
    }

}
