package com.example.taskify;

import com.parse.ParseException;

// Contains utility variables and methods used in the app.
public class TaskifyUtilities {

    // Returns the user-friendly error message that accompanies a ParseException.
    public static String parseExceptionToErrorText(ParseException e) {
        // fullErrorMessage in the format of: "com.parse.ParseRequest$ParseRequestException: [error message]."
        // sometimes, the error may be "com.parse.ParseRequest$ParseRequestException: java.lang.IllegalArgumentException: [error message]."
        // The function extracts and capitalizes the first letter of the user-friendly part of the error message,
        // which always begins 2 characters after the last colon.
        String fullErrorMessage = e.toString();
        int indexOfColon = fullErrorMessage.lastIndexOf(":");
        String errorReasonText = fullErrorMessage.substring(indexOfColon+2, indexOfColon+3).toUpperCase()
                .concat(fullErrorMessage.substring(indexOfColon + 3));
        return errorReasonText;
    }
}
