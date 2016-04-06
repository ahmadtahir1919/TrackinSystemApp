package com.example.muhammad.try1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit.mime.TypedInput;

/**
 * Created by muhammad on 6/4/2016.
 */
public class JsonTypedInput implements TypedInput {

    private final byte[] mStringBytes;

    JsonTypedInput(byte[] stringBytes) {
        this.mStringBytes = stringBytes;
    }


    @Override
    public String mimeType() {
        return "application/json; charset=UTF-8";
    }

    @Override
    public long length() {
        return mStringBytes.length;
    }

    @Override
    public InputStream in() throws IOException {
        return new ByteArrayInputStream(mStringBytes);
    }

}