package com.itstrong.popularscience.utils;

import com.itstrong.popularscience.bean.Details;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by itstrong on 2016/6/19.
 */
public class JsonUtils {

    public void parseJsonData(String response, List<Details> itemList) {
        try {
            JSONObject root = new JSONObject(response);
            boolean flag = root.getBoolean("flag");
            if (flag) {
                JSONArray results = root.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    Details item = new Details();
                    JSONObject object = results.getJSONObject(i);
                    item.setId(object.getString("id"));
                    item.setImageUrl(object.getString("imageUrl"));
                    item.setTitle(object.getString("title"));
                    itemList.add(item);
//                    mAdapter.setCount(itemList.size());
//                    mAdapter.notifyItemInserted(i);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
