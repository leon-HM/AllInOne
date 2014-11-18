package com.ihgoo.allinone.qiniu.storage;


public interface UpProgressHandler {
    void progress(String key, double percent);
}
