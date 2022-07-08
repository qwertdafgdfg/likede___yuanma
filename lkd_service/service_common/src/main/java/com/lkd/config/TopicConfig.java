package com.lkd.config;

/**
 * 消息队列中的主题配置
 */
public class TopicConfig {
    /**
     * 售货机出货通知队列主题标识(下发消息主题)
     */
    public final static String VENDOUT_TOPIC = "vendoutReq";

    /**
     * 补货工单主题
     */
    public final static String SUPPLY_TOPIC = "server/task/supply";

    public static String getVendoutTopic(String innerCode){
        return "vm/"+innerCode+"/"+VENDOUT_TOPIC;
    }

    /**
     * 完成工单主题
     */
    public final static String COMPLETED_TASK_TOPIC = "server/task/completed";

    /**
     * 下发消息到售货机的主题  vm/tovm/售货机编号
     */
    public final static String TO_VM_TOPIC = "vm/tovm/";

    /**
     *  设备状态消息
     */
    public final static String VM_STATUS_TOPIC = "server/status";
}
