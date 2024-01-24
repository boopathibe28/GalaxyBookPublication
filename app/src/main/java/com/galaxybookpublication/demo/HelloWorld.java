package com.galaxybookpublication.demo;

public class HelloWorld {
    public static void main(String[] args) {
        String givenWord = "Kalai";
        char[] charArray= givenWord.toLowerCase().toCharArray();
        for(int i=0; i<givenWord.length(); i++)
        {
            Character repetedLetter;
            for(int j=i; j<charArray.length-1; j++)
            {
                if(charArray[i] == charArray[j]){
                    repetedLetter = charArray[i];
                 //   charArray.toString().replace();
                }
               // System.out.println(repetedLetter);
            }
        }
    }
}
