package com.whisper.agent;

import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.*;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 热更监听
 * @author little whisper
 * @date 2021/1/5 12:30
 */
public class HotLoadWatch {

    /**
     * 启动热更监听
     * @param jarPath 代理jar绝对路径
     * @param hotDirect 热更class文件目录
     */
    public static void start(String jarPath, String hotDirect) {
        Path hotPath = Paths.get(hotDirect);
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            hotPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            // 每隔5s检查一次目录
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                WatchKey watchKey = watchService.poll();
                // 每个文件只处理一次就行了
                HashSet<String> fixed = new HashSet<>();
                while (watchKey != null) {
                    for (WatchEvent event : watchKey.pollEvents()) {
                        // 文件修改和添加操作
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY || event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            File[] files = hotPath.toFile().listFiles((dir, name) -> name.endsWith(".class"));
                            if (files != null) {
                                for (File file : files) {
                                    if (fixed.contains(file.getName())) {
                                        continue;
                                    }
                                    updateClass(jarPath, file.getAbsolutePath());
                                    fixed.add(file.getName());
                                }
                            }
                        }
                    }
                    boolean reset = watchKey.reset();
                    if (!reset) {
                        // log
                    }
                    watchKey = watchService.poll();
                }
            }, 0, 5, TimeUnit.SECONDS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新class
     * @param jarPath 热更jar绝对路径
     * @param newClassPath class文件绝对路径
     */
    public static void updateClass(String jarPath, String newClassPath) {
        String s = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        VirtualMachine vm = null;
        try {
             vm = VirtualMachine.attach(s);
             vm.loadAgent(jarPath, newClassPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (vm != null) {
                try {
                    vm.detach();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
