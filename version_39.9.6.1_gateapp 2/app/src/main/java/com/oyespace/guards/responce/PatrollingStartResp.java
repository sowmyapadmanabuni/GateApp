package com.oyespace.guards.responce;

public class PatrollingStartResp {
    public String apiVersion;

    public PatrollingData data;

    public String success;

    public class PatrollingData
    {
        public Patrolling patrolling;
    }
    public class Patrolling
    {
        public int ptPtrlID;

    }

}
