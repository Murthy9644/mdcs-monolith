package com.mdcs.shared.archive.postals;

import com.mdcs.shared.archive.postals.Report.Job;

public class State extends Job{

    public enum AuthState{
        SUCCESS,
        RETRY,
        RECOVER,
        TERMINATE
    }

    private AuthState state = AuthState.SUCCESS;

    public void set(AuthState to){
        // Can only be downgraded (SUCCESS -> RETRY / RECOVER -> TERMINATE)

        if (this.state == AuthState.SUCCESS) this.state = to;

        if (this.state == AuthState.TERMINATE) return;

        if (
            (
                this.state == AuthState.RETRY 
                || 
                this.state == AuthState.RECOVER
            ) && 
            to != AuthState.SUCCESS
        ) this.state = to;

        this.state = to;
    }

    public AuthState get(){ return this.state; }
}
