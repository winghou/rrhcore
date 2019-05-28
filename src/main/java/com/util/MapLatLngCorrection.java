package com.util;


public class MapLatLngCorrection
{
    //
    // /**
    // * GPS到地图
    // *
    // * @param detail
    // * @param longitude
    // * @param latitude
    // * @param mapType
    // */
    // public static void correct(JSONObject detail, Double longitude, Double latitude, String mapType)
    // {
    // // 校验经纬度为空的情况
    // if (longitude == null || latitude == null)
    // {
    // detail.put("posLongitude", 0);
    // detail.put("posLatitude", 0);
    // return;
    // }
    //
    // if ("google".equalsIgnoreCase(mapType) || "baidu".equalsIgnoreCase(mapType))
    // {
    // MapPos ori = new MapPos(longitude, latitude);
    // MapPos offres = MapOffsetAPI.getMapPosWithOffset(ori, Algorithm.Interpolation, mapType.toLowerCase());
    // detail.put("posLongitude", Utils.rglrTude(offres.getLongitude()));
    // detail.put("posLatitude", Utils.rglrTude(offres.getLatitude()));
    // }
    // else
    // {
    // detail.put("posLongitude", Utils.rglrTude(longitude));
    // detail.put("posLatitude", Utils.rglrTude(latitude));
    // }
    // }
    //
    // /**
    // * 地图到GPS
    // *
    // * @param longitude
    // * @param latitude
    // * @param mapType
    // * @return
    // */
    // public static JSONObject incorrect(Double longitude, Double latitude, String mapType)
    // {
    // JSONObject detail = new JSONObject();
    // detail.put("posLongitude", longitude);
    // detail.put("posLatitude", latitude);
    //
    // if ("google".equalsIgnoreCase(mapType) || "baidu".equalsIgnoreCase(mapType))
    // {
    // MapPos ori = new MapPos(longitude, latitude);
    // MapPos offres = MapOffsetAPI.getMapPosOfOrigin(ori, Algorithm.Interpolation, mapType.toLowerCase());
    //
    // detail.put("posLongitude", Utils.rglrTude(offres.getLongitude()));
    // detail.put("posLatitude", Utils.rglrTude(offres.getLatitude()));
    // }
    // else
    // {
    // detail.put("posLongitude", Utils.rglrTude(longitude));
    // detail.put("posLatitude", Utils.rglrTude(latitude));
    // }
    // return detail;
    // }
    //
    // public static void main(String[] args)
    // {
    // double x =118.781244;
    // double y =32.064828;
    // String mapType = "baidu";
    // JSONObject detail = new JSONObject();
    // correct(detail, x, y, mapType);
    // System.out.println(detail.get("posLongitude"));
    // System.out.println(detail.get("posLatitude"));
    // }
}
