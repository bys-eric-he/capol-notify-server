package com.capol.notify.manage.domain.model;

import com.capol.notify.manage.domain.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdGenerator {
    private static volatile SnowflakeIdWorker instance;

    private IdGenerator() {
    }

    public static SnowflakeIdWorker getInstance() {
        if (instance == null) {
            synchronized (SnowflakeIdWorker.class) {
                if (instance == null) {
                    log.info(" id generate start!!!");
                    instance = new SnowflakeIdWorker();
                }
            }
        }
        return instance;
    }

    public static Long generateId() {
        SnowflakeIdWorker idWorker = getInstance();

        try {
            if (idWorker.isGenerateByLocal()) {
                return idWorker.nextId();
            }
        } catch (Exception e) {
            log.error("id from id-generator-server failure , {} ", e.getMessage());
            e.printStackTrace();
        }
        return idWorker.nextId();
    }

    /**
     * 设置机器码，增加取余操作，保证不大于15
     *
     * @param machineCode 机器码
     */
    public void setMachineCode(long machineCode) {
        // 取余，保证输入不会大于15
        instance.setMachineCode(machineCode);
    }

    public static void setGenerateByLocal(boolean generateByLocal) {
        instance.setGenerateByLocal(generateByLocal);
    }
}
