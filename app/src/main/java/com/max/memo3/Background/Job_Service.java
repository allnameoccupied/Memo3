package com.max.memo3.Background;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.PersistableBundle;

import com.max.memo3.Util.Custom_Scheduler_Record_JobService;
import com.max.memo3.Util.util;

public class Job_Service extends JobService {
    //var
    public static int time_runned = 0;
    private static final int time_to_run = 10;

    //func
    @Override
    public boolean onStartJob(JobParameters params) {
        PersistableBundle bundle = params.getExtras();
        String action = bundle.getString(Custom_Scheduler_Record_JobService.ACTION_TODO_JOBSERVICE);
        switch (action){
            case "test" :
                util.quickLog("job service started");
                break;
//            case "from_device_test" :
//                frequently_used_stuff.quick_log("Job service started for "+Integer.toString(time_runned)+" times");
//                time_runned++;
//                if (time_runned >= time_to_run){
//                    jobFinished(params,false);
//                } else {
//                    Device_Data_Test.schedule_job_from_job_service(getApplicationContext());
//                }
//                break;
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
//        frequently_used_stuff.quick_log("Job service stopped");
        return true;
    }
}
