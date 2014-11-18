package com.ihgoo.allinone.qiniu.storage;

import java.io.File;

import com.ihgoo.allinone.qiniu.common.Config;
import com.ihgoo.allinone.qiniu.http.HttpManager;
import com.ihgoo.allinone.qiniu.http.ResponseInfo;
import com.ihgoo.allinone.util.AsyncRun;

public final class UploadManager {
    private final Recorder recorder;
    private final HttpManager httpManager;
    private final KeyGenerator keyGen;

    public UploadManager() {
        this(null, null);
    }

    public UploadManager(Recorder recorder, KeyGenerator keyGen) {
        this.recorder = recorder;
        this.httpManager = new HttpManager();
        this.keyGen = keyGen;
    }

    public UploadManager(Recorder recorder) {
        this(recorder, null);
    }

    private static boolean areInvalidArg(final String key, byte[] data, File f, String token, final UpCompletionHandler completionHandler) {
        if (completionHandler == null) {
            throw new IllegalArgumentException("no UpCompletionHandler");
        }
        String message = null;
        if (f == null && data == null) {
            message = "no input data";
        } else if (token == null || token.equals("")) {
            message = "no token";
        }
        if (message != null) {
            final ResponseInfo info = ResponseInfo.invalidArgument(message);
            AsyncRun.run(new Runnable() {
                @Override
                public void run() {
                    completionHandler.complete(key, info, null);
                }
            });
            return true;
        }
        return false;
    }

    public void put(final byte[] data, final String key, final String token, final UpCompletionHandler completionHandler,
                    final UploadOptions options) {
        if (areInvalidArg(key, data, null, token, completionHandler)) {
            return;
        }
        AsyncRun.run(new Runnable() {
            @Override
            public void run() {
                FormUploader.upload(httpManager, data, key, token, completionHandler, options);
            }
        });
    }

    public void put(String filePath, String key, String token, UpCompletionHandler completionHandler,
                    final UploadOptions options) {
        put(new File(filePath), key, token, completionHandler, options);
    }

    public void put(File file, String key, String token, UpCompletionHandler completionHandler,
                    final UploadOptions options) {
        if (areInvalidArg(key, null, file, token, completionHandler)) {
            return;
        }
        long size = file.length();
        if (size <= Config.PUT_THRESHOLD) {
            FormUploader.upload(httpManager, file, key, token, completionHandler, options);
            return;
        }
        String recorderKey = key;
        if (keyGen != null) {
            recorderKey = keyGen.gen(key, file);
        }
        ResumeUploader uploader = new ResumeUploader(httpManager, recorder, file, key, token, completionHandler,
                options, recorderKey);

        AsyncRun.run(uploader);
    }
}
