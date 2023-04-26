package com.capol.notify.manage.domain;


import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SnowflakeIdWorker {
    // ==============================Fields===========================================
    /**
     * 开始时间截 (2009-01-01)
     * 减少时间戳范围，避免超过前端能接受的最大时间戳
     * 保证id 在 2078-09-07 15:47:35 之前可用(timestamp <= 1230739200000L + 2 ^ (53 - 12))
     */
    private final long twepoch = 1230739200000L;

    /**
     * 序列在id中占的位数 = 机器码左移的位数
     */
    private final long sequenceBits = 8L;

    /**
     * 机器码 4bits  (0 - 15)
     */
    private long machineCode = 0L;

    /**
     * 时间截向左移12位(12)
     * 序列掩码8位 = 机器码左移的8位
     * 机器码4位 + 序列掩码8位 = 时间截向左移12位
     */
    private final long timestampLeftShift = 12;

    /**
     * 生成序列的掩码，这里为 1 << 8 (0b11111111)
     */
    private final long sequenceMask = 0b11111111L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;

    private static final Pattern PATTERN_HOSTNAME = Pattern.compile("^.*\\D+([0-9]+)$");

    /**
     * 秒内序列(0~65535)
     */
    private static long offset = 0;

    private static long next = 0;

    private boolean generateByLocal = true;


    //==============================Constructors=====================================

    // ==============================Methods=========================================
    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            long timestampDiff = lastTimestamp - timestamp;
            lastTimestamp = timestamp;
            throw new RuntimeException(
                    String.format("Clock moved backwards.Refusing to generate id for %d milliseconds", timestampDiff));
        }

        if (lastTimestamp != timestamp) {
            offset = 0;
            lastTimestamp = timestamp;
        }

        // 如果是同一时间生成的，则进行豪秒内序列
        offset++;
        next = offset & sequenceMask;

        if (next == 0) {
            timestamp = tilNextMillis(lastTimestamp);
        }

        // 移位并通过或运算拼到一起组成53位的ID
        // 并不是说只能生成53位的ID，而是由于前端接受Number类型数据的范围只能到53位，超过53位的id传给前端会溢出
        return ((timestamp - twepoch) << timestampLeftShift) | machineCode << sequenceBits | next;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private static long getServerIdAsLong() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            Matcher matcher = PATTERN_HOSTNAME.matcher(hostname);

            if (matcher.matches()) {
                System.out.println(matcher.group(1));
                long n = Long.parseLong(matcher.group(1));
                if (n >= 0 && n < 4) {
                    log.info("detect server id from host name {}: {}.", hostname, n);
                    return n;
                }
            }
        } catch (UnknownHostException e) {
            log.error("unable to get host name. set server id = 0.");
        }
        return 0;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 设置机器码，增加取余操作，保证不大于15
     *
     * @param machineCode 机器码
     */
    public void setMachineCode(long machineCode) {
        // 取余，保证输入不会大于15
        this.machineCode = machineCode & 0b1111;
    }

    public void setGenerateByLocal(boolean generateByLocal) {
        this.generateByLocal = generateByLocal;
    }

    public boolean isGenerateByLocal() {
        return generateByLocal;
    }

    public long getMachineCode() {
        return machineCode;
    }
}