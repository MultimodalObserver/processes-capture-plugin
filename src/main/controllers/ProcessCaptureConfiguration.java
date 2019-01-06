package main.controllers;

import mo.capture.RecordableConfiguration;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.communication.streaming.capture.PluginCaptureSender;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

import java.io.File;

public class ProcessCaptureConfiguration implements RecordableConfiguration, PluginCaptureSender {

    private ProcessRecorder processRecorder;

    @Override
    public void setupRecording(File file, ProjectOrganization projectOrganization, Participant participant) {
        this.processRecorder = new ProcessRecorder(file, projectOrganization, participant,this);
    }

    @Override
    public void startRecording() {
        processRecorder.start();
    }

    @Override
    public void cancelRecording() {
        processRecorder.stop();
    }

    @Override
    public void pauseRecording() {

    }

    @Override
    public void resumeRecording() {

    }

    @Override
    public void stopRecording() {

    }

    @Override
    public void subscribeListener(PluginCaptureListener pluginCaptureListener) {
        this.processRecorder.subscribeListener(pluginCaptureListener);
    }

    @Override
    public void unsubscribeListener(PluginCaptureListener pluginCaptureListener) {
        this.processRecorder.unsubscribeListener(pluginCaptureListener);
    }

    @Override
    public String getCreator() {
        return ProcessCaptureConfiguration.class.getName();
    }

    @Override
    public void send25percent() {

    }

    @Override
    public void send50percent() {

    }

    @Override
    public void send75percent() {

    }

    @Override
    public void send100percent() {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public File toFile(File file) {
        return null;
    }

    @Override
    public Configuration fromFile(File file) {
        return null;
    }
}
