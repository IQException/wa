package com.ximalaya.wa.sender.model;

public enum PayChannel {
	
	ALI_PAY(1,"06"),
	WX_PAY(2,"06"),		
	IAP_PAY(4,"06"),
	CCB_PAY(9,"04");   //建行
	
    private int id;
    private String paymentModel;

    PayChannel(int id,String paymentModel) {
        this.id = id;
        this.paymentModel = paymentModel;
    }

    public int getId() {
        return id;
    }
    
    public String getPaymentModel(){
    	return paymentModel;
    }

    public static String getPaymentModelByPayChannelId(int id){
    	
    	for (PayChannel channel : PayChannel.values()) {
			if (channel.getId() == id) {
				return channel.getPaymentModel();
			}
		}
    	
    	return "99";
    }
    

}
