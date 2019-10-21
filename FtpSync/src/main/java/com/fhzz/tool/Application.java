package com.fhzz.tool;

import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 	全局存储正在同步的任务
 * @author Engine
 *
 */
public class Application {
	//当前任务调度
	static ConcurrentHashMap<String, Timer> timerMap = new ConcurrentHashMap<>();
	//当前ftp信息
	static ConcurrentHashMap<String, ThreadSyncFile> threadMap = new ConcurrentHashMap<>();

//	public static ConcurrentHashMap<String, Timer> getAllTimerMap() {
//		return timerMap;
//	}
//	
//	public static ConcurrentHashMap<String, ThreadSyncFile> getAllThreadSyncFileMap() {
//		return threadMap;
//	}
//
//	public static void setTimerMap(ConcurrentHashMap<String, Timer> timer) {
//		Application.timerMap = timer;
//	}
//	
//	
//	public static void setThreadSyncFileMap(ConcurrentHashMap<String, ThreadSyncFile> thread) {
//		Application.threadMap = thread;
//	}
	
	//获取key 的任务调度
	public static Timer getTimerMap(String key) {
		return timerMap.get(key);
	}
	
	//获取key 的ftp
	public static ThreadSyncFile getThreadSyncFileMap(String key) {
		return threadMap.get(key);
	}
	
	//存储当前任务调度
	public static void setTimerMap(String key,Timer timer) {
		timerMap.put(key, timer);
	}
	
	//存储当前ftp
	public static void setThreadSyncFileMap(String key,ThreadSyncFile thread) {
		threadMap.put(key, thread);
	}
	//移除当前任务调度
	public static void removeTimerMap(String key) {
		timerMap.remove(key);
	}
	
	//移除ftp
	public static void removeThreadSyncFileMap(String key) {
		threadMap.remove(key);
	}
}
