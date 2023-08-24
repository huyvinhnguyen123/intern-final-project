package com.beetech.finalproject.utils.mail;


public class CustomMailGenerator {
    private static final String EMAIL = "mailto:atifarlunar.official@gmail.com";
    private static final String TARGET="_blank";

    public static final String resetPasswordMessage(String otp) {
        return  "<html><body>"
                + "<p><strong>Dear User</strong></p>"
                + "<p>A request has been received to change the password for your BlindSight account. </p>"
                + "<p>This is your OTP code: <strong>"+ otp +"</strong></p>"
                + "<p><strong>Don't share your OTP code with anyone.</strong></p>"
                + "<p>If you did not initiate this request, please contact us at: <a href="+EMAIL+" target="+TARGET+">atifarlunar.official@gmail.com</a></p>"
                + "<p>Thank you!</p>"
                + "<P>Best Regard,</p>"
                + "<p><strong>The BlindSight Team :D</strong></p>"
                + "</body></html>";
    }

    public static final String productWhistListMessage(String sku, String productName, Double price) {
        return  "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "            border: 1px solid #e6e6e6;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        h2 {\n" +
                "            color: #333;\n" +
                "        }\n" +
                "\n" +
                "        strong {\n" +
                "            color: #ff6600;\n" +
                "        }\n" +
                "\n" +
                "        .change-details {\n" +
                "            background-color: #f9f9f9;\n" +
                "            padding: 10px;\n" +
                "            border: 1px solid #e6e6e6;\n" +
                "            margin-top: 10px;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            margin-top: 20px;\n" +
                "            color: #777;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"container\">\n" +
                "    <h2>Dear User,</h2>\n" +
                "    <p>We have important news about the product you liked. There have been changes in the product:</p>\n" +
                "    <div class=\"change-details\">\n" +
                "        <p><strong>New sku:</strong> "+sku+"</p>\n" +
                "        <p><strong>New name:</strong> "+productName+"</p>\n" +
                "        <p><strong>New price:</strong> "+price+"</p>\n" +
                "    </div>\n" +
                "    <p>If you have any questions, please don't hesitate to contact us at: <a href=\"mailto:atifarlunar.official@gmail.com\">atifarlunar.official@gmail.com</a></p>\n" +
                "    <p>Thank you!</p>\n" +
                "    <p class=\"footer\">Best Regards, <br><strong>The BlindSight Team \uD83D\uDE0A</strong></p>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }
}
