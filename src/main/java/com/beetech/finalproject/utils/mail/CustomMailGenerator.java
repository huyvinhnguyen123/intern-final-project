package com.beetech.finalproject.utils.mail;


import com.beetech.finalproject.web.dtos.email.ProductOrigin;

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

    public static final String productWhistListMessage(ProductOrigin productOrigin, ProductOrigin productAfterUpdate) {
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
                "    <p>We have important news about the product you liked. There have been changes in the product details:</p>\n" +
                "    <div class=\"change-details\">\n" +
                "        <p><strong>Old sku:</strong> "+productOrigin.getSku()+" <Strong>Change to</Strong> <strong>New sku:</strong> "+productAfterUpdate.getSku()+"</p>\n" +
                "        <p><strong>Old name:</strong> "+productOrigin.getProductName()+" <Strong>Change to</Strong> <strong>New name:</strong> "+productAfterUpdate.getProductName()+"</p>\n" +
                "        <p><strong>Old price:</strong> "+productOrigin.getPrice()+" <Strong>Change to</Strong> <strong>New price:</strong> "+productAfterUpdate.getPrice()+"</p>\n" +
                "        <p><strong>Old detail:</strong> "+productOrigin.getDetailInfo()+" <Strong>Change to</Strong> <strong>New detail:</strong> "+productAfterUpdate.getDetailInfo()+"</p>\n" +
                "        <p><strong>Old category:</strong> "+productOrigin.getCategoryName()+" <Strong>Change to</Strong> <strong>New category:</strong> "+productAfterUpdate.getCategoryName()+"</p>\n" +
                "        <p><strong>Old thumbnail:</strong> <img src="+productOrigin.getThumbnailImage()+" width=500 height=600> <Strong>Change to</Strong> <strong>New thumbnail:</strong> <img src="+productAfterUpdate.getThumbnailImage()+" width=500 height=600></p>\n" +
                "        <p><strong>Old detail images:</strong> "+productOrigin.getDetailImages()+" <Strong>Change to</Strong> <strong>New price:</strong> "+productAfterUpdate.getDetailImages()+"</p>\n" +
                "    </div>\n" +
                "    <p>If you have any questions, please don't hesitate to contact us at: <a href=\"mailto:atifarlunar.official@gmail.com\">atifarlunar.official@gmail.com</a></p>\n" +
                "    <p>Thank you!</p>\n" +
                "    <p class=\"footer\">Best Regards, <br><strong>The BlindSight Team \uD83D\uDE0A</strong></p>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }
}
