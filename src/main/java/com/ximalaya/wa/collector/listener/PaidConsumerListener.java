package com.ximalaya.wa.collector.listener;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.ximalaya.data.event.util.NumberedKeyMapTransfer;
import com.ximalaya.wa.collector.core.DataHub;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.Payment;
import com.ximalaya.wa.model.PaymentOrder;
import com.ximalaya.wa.model.Recharge;
import com.ximalaya.wa.util.MonitorUtil;

public class PaidConsumerListener extends AbstractConsumerListener {

	private static final String RECHARGE = "recharge";
	private static final String PAYMENT = "payment";
	private static final String PAYMENTORDER = "album_order_common";

	// event,key

	@Override
	public void onReceiveMessage(String key, String message) {
		try {
			NumberedKeyMapTransfer transfer = NumberedKeyMapTransfer.instance(key);// Thread safe
			Map<String, Object> result = transfer.trans(message);
			sampleLogger.log(key + ":{}", JSON.toJSONString(result));

			switch (key) {
			case PAYMENT:
				Payment payment = WaConverter.convertToModel(Payment.class, result);
				// 布控
				String paymentOpId = MonitorUtil.getPmId(payment);
				if (StringUtils.isNotBlank(paymentOpId)) {
					payment.setPmId(paymentOpId);
					DataHub.getPaymentMonitorQueue().put(payment);
				}
				break;
			case PAYMENTORDER:
				PaymentOrder paymentOrder = WaConverter.convertToModel(PaymentOrder.class, result);
				// 布控
				String paymentOrderOpId = MonitorUtil.getPmId(paymentOrder);
				if (StringUtils.isNotBlank(paymentOrderOpId)) {
					paymentOrder.setPmId(paymentOrderOpId);
					DataHub.getPaymentOrderMonitorQueue().put(paymentOrder);
				}
				break;
			case RECHARGE:
				Recharge recharge = WaConverter.convertToModel(Recharge.class, result);
				// 布控
				String rechargeOpId = MonitorUtil.getPmId(recharge);
				if (StringUtils.isNotBlank(rechargeOpId)) {
					recharge.setPmId(rechargeOpId);
					DataHub.getRechargeMonitorQueue().put(recharge);
				}
				break;

			default:
				logger.debug("result is :", result);

			}

		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(PaidConsumerListener.class.getName());
	}

}
