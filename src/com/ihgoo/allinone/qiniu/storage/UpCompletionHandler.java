package com.ihgoo.allinone.qiniu.storage;

import org.json.JSONObject;

import com.ihgoo.allinone.qiniu.http.ResponseInfo;

public interface UpCompletionHandler {
    void complete(String key, ResponseInfo info, JSONObject response);
}
