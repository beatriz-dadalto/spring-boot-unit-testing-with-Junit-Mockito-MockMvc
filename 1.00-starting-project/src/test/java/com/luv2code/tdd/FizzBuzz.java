package com.luv2code.tdd;

/*
 * if number is divisible by 3, print Fizz
 * if number is divisible by 5, print Buzz
 * if number is divisible by 3 and 5, print FizzBuzz
 * if number is NOT divisible by 3 or 5, then print the current number
 */

public class FizzBuzz {

     public static String compute(int number) {
         if ((number % 3 == 0) && (number % 5 == 0)) {
             return "FizzBuzz";
         } else if(number % 3 == 0) {
             return "Fizz";
         } else if (number % 5 == 0) {
             return "Buzz";
         } else {
             return String.valueOf(number);
         }
     }

}
