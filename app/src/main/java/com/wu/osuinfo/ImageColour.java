package com.wu.osuinfo;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by wu on 2017/7/22.
 */

public class ImageColour {
    String colour = "";


    public ImageColour(Bitmap image) throws Exception {

        int height = image.getHeight();
        int width = image.getWidth();

        Map m = new HashMap();

        for(int i=0; i < width ; i++){

            for(int j=0; j < height ; j++){

                int rgb = image.getPixel(i, j);
                int[] rgbArr = getRGBArr(rgb);

                if (!isGray(rgbArr)) {

                    Integer counter = (Integer) m.get(rgb);
                    if (counter == null)
                        counter = 0;
                    counter++;
                    m.put(rgb, counter);

                }
            }
        }

        String colourHex = getMostCommonColour(m);
    }



    public static String getMostCommonColour(Map map) {

        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {

                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());

            }

        });

        Map.Entry me = (Map.Entry )list.get(list.size()-1);
        int[] rgb= getRGBArr((Integer)me.getKey());

        return Integer.toHexString(rgb[0])+" "+Integer.toHexString(rgb[1])+" "+Integer.toHexString(rgb[2]);
    }


    public static int[] getRGBArr(int pixel) {

        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        return new int[]{red,green,blue};

    }

    public static boolean isGray(int[] rgbArr) {

        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];

        int tolerance = 10;

        if (rgDiff > tolerance || rgDiff < -tolerance)
            if (rbDiff > tolerance || rbDiff < -tolerance) {

                return false;

            }

        return true;
    }


    public String returnColour() {

        if (colour.length() == 6) {
            return colour.replaceAll("\\s", "");
        } else {
            return "ffffff";
        }
    }
}
