package com.oyespace.guards.responce;

public class StaffRegistrationRespJv {
    public String apiVersion;

    public WorkerData data;

    public String success;

    public class WorkerData
    {
        public Worker worker;
    }
    public class Worker
    {
        public int wkWorkID;

    }
}

