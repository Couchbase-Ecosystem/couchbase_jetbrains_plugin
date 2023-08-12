package com.couchbase.intellij.tree.overview;

public class NewConnectionBanner {


    public static String getBannerContent() {

        return "<html>\n" +
                "<style>\n" +
                "    .container {\n" +
                "        margin: 15px;\n" +
                "        display: flex;\n" +
                "        align-items: center;\n" +
                "        font-family: Arial;\n" +
                "    }\n" +
                "\n" +
                "    a {\n" +
                "        color: #FFFFF; /* Change this hex value to your desired color */\n" +
                "    }\n" +
                "\n" +
                "    .word {\n" +
                "        margin: 0 10px;\n" +
                "    }\n" +
                "</style>\n" +
                "<body style='width:100%;text-align:center;'>\n" +
                "\n" +
                "<div style='margin-right:10px; margin-left:10px;'>\n" +
                "    <h1 style='color: #181818;font-size:1.2em;font-family: Tahoma'>Don't have a Couchbase Cluster yet?</h1>\n" +
                "    <p style='color: #2a2a2a;font-size:0.98em;margin-bottom: 20px;font-family: Tahoma'>Sign up to a free Couchbase\n" +
                "        Capella trial or simply install Couchbase Server on your machine</p><br>\n" +
                "\n" +
                "    <table style='border-collapse: collapse;'>\n" +
                "        <tr>\n" +
                "            <td style='background:#e32623; border:1px solid red; padding:5px;color: #fff;'>\n" +
                "                <a href='https://cloud.couchbase.com/sign-up'\n" +
                "                   style='font-family: Tahoma, sans-serif; color: #fff; font-weight:normal; text-align:center; text-decoration: none; display: inline-block;'>\n" +
                "                    <span style='font-size: 12px; font-family: Arial, sans-serif;color: #FFFFFF; font-weight:normal; line-height:1.5em; text-align:center;'>Sign up to Capella >></span>\n" +
                "                </a>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "\n" +
                "    <div class='container'>\n" +
                "        <div class='word' style='font-size:0.98em;color: #2a2a2a;'>or</div>\n" +
                "    </div>\n" +
                "\n" +
                "        <table style='border-collapse: collapse;'>\n" +
                "        <tr>\n" +
                "            <td style='background:#e32623; border:1px solid red; padding:5px;color: #fff;'>\n" +
                "                <a href='https://www.couchbase.com/downloads/'\n" +
                "                   style='font-family: Tahoma, sans-serif; color: #FFFFF; font-weight:normal; text-align:center; text-decoration: none; display: inline-block;'>\n" +
                "                    <span style='color:#FFFFFF;font-size: 12px; font-family: Arial, sans-serif; font-weight:normal; line-height:1.5em; text-align:center;'>Download Couchbase >></span>\n" +
                "                </a>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }
}
