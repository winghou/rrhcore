package com.util;

public class HtmlBuilder
{
    private static String htmlPrefix1 = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\" />\n"
            + "<meta name=\"viewport\" content=\"user-scalable=no, width=device-width, initial-scale=1.0, maximum-scale=1.0\"/><title>";
    
    private static String htmlPrefix2 = "</title><script>\n"
            + "                        addEventListener('load', function(){\n"
            + "                            resetImageStyle();\n" + "                         });\n"
            + "                         \n" + "                         function resetImageStyle(){\n"
            + "                             var imageList = document.getElementsByTagName('img');\n"
            + "                             for(var index = 0; index < imageList.length; index++){\n"
            + "                                 var imageObj = imageList[index];\n"
            + "                                 imageObj.style.width = '100%';\n"
            + "                                 imageObj.style.height = '';\n" + "                             }\n"
            + "                         }\n" + " \n" + "    \n" + "        </script></head><body>";
    
    private static String htmlPostfix = "</body></html>";

    public static String buildHtml(String title, String htmlBody)
    {
        StringBuilder html = new StringBuilder();
        html.append(htmlPrefix1);
        html.append(title);
        html.append(htmlPrefix2);
        html.append(htmlBody);
        html.append(htmlPostfix);
        return html.toString();
    }
}
