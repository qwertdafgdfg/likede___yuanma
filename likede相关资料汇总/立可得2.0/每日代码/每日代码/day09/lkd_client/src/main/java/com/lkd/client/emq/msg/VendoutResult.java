package com.lkd.client.emq.msg;


import com.lkd.client.config.MsgType;
import com.lkd.client.pojo.VendoutOrder;
import lombok.Data;

@Data
public class VendoutResult extends BaseData{
    public VendoutResult() {
        this.setMsgType(MsgType.vendoutResp.getType());
    }
    private VendoutOrder vendoutResult;
}
