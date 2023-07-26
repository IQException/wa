package com.ximalaya.wa.collector.core;

import com.ximalaya.wa.model.*;

import java.util.concurrent.LinkedBlockingQueue;

public class DataHub {

	public static final int DEFAULT_SIZE = 5120;
	public static final LinkedBlockingQueue<Comment> commentQueue = new LinkedBlockingQueue<Comment>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Share> shareQueue = new LinkedBlockingQueue<Share>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<UpDownload> upDownloadQueue = new LinkedBlockingQueue<UpDownload>(
			DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Search> searchQueue = new LinkedBlockingQueue<Search>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Star> starQueue = new LinkedBlockingQueue<Star>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Subscribe> subscribeQueue = new LinkedBlockingQueue<Subscribe>(
			DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Play> playQueue = new LinkedBlockingQueue<Play>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Login> loginQueue = new LinkedBlockingQueue<Login>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Donate> donateQueue = new LinkedBlockingQueue<Donate>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Account> accountQueue = new LinkedBlockingQueue<Account>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Filter> filterQueue = new LinkedBlockingQueue<Filter>(DEFAULT_SIZE);

	public static final LinkedBlockingQueue<Play> playMonitorQueue = new LinkedBlockingQueue<Play>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Bullet> bulletMonitorQueue = new LinkedBlockingQueue<Bullet>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Recharge> rechargeMonitorQueue = new LinkedBlockingQueue<Recharge>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<Payment> paymentMonitorQueue = new LinkedBlockingQueue<Payment>(DEFAULT_SIZE);
	public static final LinkedBlockingQueue<PaymentOrder> paymentOrderMonitorQueue = new LinkedBlockingQueue<PaymentOrder>(DEFAULT_SIZE);

	public static final LinkedBlockingQueue<RelationAccount> relationAccountQueue = new LinkedBlockingQueue<RelationAccount>(DEFAULT_SIZE);

	
	public static LinkedBlockingQueue<RelationAccount> getRelationAccountQueue() {
		return relationAccountQueue;
	}

	public static LinkedBlockingQueue<Bullet> getBulletMonitorQueue() {
		return bulletMonitorQueue;
	}

	public static LinkedBlockingQueue<Play> getPlayMonitorQueue() {
		return playMonitorQueue;
	}
	
	public static LinkedBlockingQueue<Recharge> getRechargeMonitorQueue() {
		return rechargeMonitorQueue;
	}
	
	public static LinkedBlockingQueue<Payment> getPaymentMonitorQueue() {
		return paymentMonitorQueue;
	}
	
	public static LinkedBlockingQueue<PaymentOrder> getPaymentOrderMonitorQueue() {
		return paymentOrderMonitorQueue;
	}

	public static LinkedBlockingQueue<Filter> getFilterQueue() {
		return filterQueue;
	}

	public static LinkedBlockingQueue<Account> getAccountQueue() {
		return accountQueue;
	}

	public static LinkedBlockingQueue<Donate> getDonateQueue() {
		return donateQueue;
	}

	public static LinkedBlockingQueue<Login> getLoginQueue() {
		return loginQueue;
	}

	public static LinkedBlockingQueue<Play> getPlayQueue() {
		return playQueue;
	}

	public static LinkedBlockingQueue<Subscribe> getSubscribeQueue() {
		return subscribeQueue;
	}

	public static LinkedBlockingQueue<Star> getStarQueue() {
		return starQueue;
	}

	public static LinkedBlockingQueue<Comment> getCommentQueue() {
		return commentQueue;
	}

	public static LinkedBlockingQueue<Share> getShareQueue() {
		return shareQueue;
	}

	public static LinkedBlockingQueue<UpDownload> getUpDownloadQueue() {
		return upDownloadQueue;
	}

	public static LinkedBlockingQueue<Search> getSearchQueue() {
		return searchQueue;
	}


	
	
}
