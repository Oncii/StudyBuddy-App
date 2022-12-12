package com.thesis.myapplication.listeners;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;

public interface ChatbotReply {

    void callback(DetectIntentResponse returnResponse);
}