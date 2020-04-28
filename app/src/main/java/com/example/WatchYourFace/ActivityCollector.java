package com.example.WatchYourFace;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    public static List<Activity> activities=new ArrayList<>();
    public static void addActivity(Activity activity)
    {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity)
    {
        activities.remove(activity);
    }

    public static void finishAll()
    {
        for(Activity activity : activities)
        {
            if(!activity.isFinishing())
            {
                activity.finish();
            }
        }
        activities.clear();
    }

    public static List getListOfActivity(){return activities;}

    public static Activity getActivity(int index)
    {
        return activities.get(index);
    }

    public static void finishByIndex(int index)
    {
        Activity activity=activities.get(index);
        activity.finish();
    }
}
