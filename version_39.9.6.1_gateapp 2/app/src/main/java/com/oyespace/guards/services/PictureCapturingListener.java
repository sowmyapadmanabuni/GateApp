package com.oyespace.guards.services;

import java.util.TreeMap;


public interface PictureCapturingListener {

    void onCaptureDone(String pictureUrl, byte[] pictureData);
    void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken);

}
