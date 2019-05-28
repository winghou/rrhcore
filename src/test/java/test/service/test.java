package test.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;

public class test {
    public static void main(String[] args) {
        User user1 = new User(1, "嘻嘻", "123");
        User user2 = new User(1, "哈哈", "456");
        User user3 = new User(1, "呵呵", "789");

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(user1);
        arrayList.add(user2);
        arrayList.add(user3);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(user1);
        jsonArray.add(user2);
        jsonArray.add(user3);
        System.out.println(arrayList);
        System.out.println(JSON.toJSONString(arrayList));
        System.out.println("-----------------");
        System.out.println(jsonArray);
        System.out.println(jsonArray.get(0));
        System.out.println("-----------------");
        System.out.println(JSON.toJSONString(jsonArray));
        Object toJSON = JSON.toJSON(jsonArray);
        System.out.println(toJSON);
        System.out.println(jsonArray.get(0));

    }
}
