package com.oyespace.guards.network;

public interface ResponseHandler {
    public void onSuccess(String response, Object data, int urlId, int position);

    public void onFailure(Exception e, int urlId);
}
