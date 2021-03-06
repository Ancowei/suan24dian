package ExitApp;

import java.util.LinkedList;
import java.util.List;

import com.ancowei.main.Suan24dianMain;
import com.ancowei.services.Background_music;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

public class ExitApp extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();
	private static ExitApp instance;

	private ExitApp() {
	}

	// 单例模式中获取唯一的ExitApplication实例
	public static ExitApp getInstance() {
		if (null == instance) {
			instance = new ExitApp();
		}
		return instance;
	}
	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	// 遍历所有Activity并finish
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);

	}
}