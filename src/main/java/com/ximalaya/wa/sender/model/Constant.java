package com.ximalaya.wa.sender.model;

/**
 *
 * @author Stan.She 文件名格式如下： 
 * 		   应用编码_业务类型_消息类型_消息流水号_序列号_V2.xml
 *         举例：1220007_QUERYTBTRADE_1_3301001436584505000_0001_V2.xml 
 *         
 *         说明：
 *         应用编码：用于区分不同应用，详见字典定义。 
 *         业务类型：用于区分不同的业务数据，如：注册信息、登录日志等，详见业务接口定义。
 *         消息类型：用于区分请求、应答和报送数据等，详见业务接口定义。 
 *         消息流水号：报送接口厂商自行生成，生成规则：属地行政区划 + 13位时间串（绝对秒数+微秒数）+ 自定索引码（0001开始，步长1）。
 *         	  当文件为查询响应文件布控结果文件时消息流水号做透传处理。
 *         序列号：原则上可自定义，用于实现文件名的唯一性。 
 *         V2:固定值
 *
 */
public class Constant {

	public final static String	FILE_SPLIT_SYMBOL					= "_";
	public final static String	FILE_EXT							= ".zip";

	// 应用编码
	public final static String	APPLICATION_CODING					= "1390008";						// 喜马拉雅FM

	// 上海浦东新区行政编号
	public final static String	ADMINISTRATIVE_NUMBER				= "310116";

	// 业务类型
	public final static String	SENDER_TYPE							= "1";
	public final static String	QUERY_TYPE							= "2";
	public final static String	MONITOR_TYPE						= "3";
	public final static String	MANAGEMENT_TYPE						= "4";
	
	//布控类型
	public final static String	MONITOR_ADD							= "add";							//布控
	public final static String	MONITOR_DEL							= "del";							//停控
	public final static String	MONITOR_STATUS						= "status";							//状态

	public final static String	QUERY_NODE_STATUS					= "QUERYNODESTATUS";				// 服务状态
	public final static String	COMM_CONTENT						= "COMMCONTENT";					// 协调内容

	// 业务类型－报送
	public final static String	ACCOUNT								= "ACCOUNT";						// 用户注册信息
	public final static String	RELATION_ACCOUNT_INFO				= "RELATIONACCOUNTINFO";			// 用户注册-关联账号信息
	public final static String	LOGIN								= "LOGIN";							// 登录日志
	public final static String	FRIEND_LIST							= "FRIENDLIST";						// 好友信息
	public final static String	MEDIA_BROWSE_LOG					= "MEDIABROWSELOG";					// 播放音视频/浏览图片日志信息
	public final static String	COMMENT								= "COMMENT";						// 评论信息
	public final static String	UP_DOWN_LOADLOG						= "UPDOWNLOADLOG";					// 上传文件信息(下载/缓存文件信息)
	public final static String	COLLECTION							= "COLLECTION";						// 收藏/点赞信息
	public final static String	SUBSCRIBE_INFO						= "SUBSCRIBEINFO";					// 关注/订阅信息
	public final static String	MEDIA_SHARE_FORWARDING				= "MEDIASHAREFORWARDING";			// 分享/转发信息
	public final static String	REWARD								= "REWARD";							// 赠送/打赏信息
	public final static String	CLOG								= "CLOG";							// 通联日志信息（点对点通信）
	public final static String	MASK_MESSAGE						= "MASKMESSAGE";					// 过滤/屏蔽/删除图片/音视频信息
	public final static String	MEDIA_SEARCH_INFO					= "MEDIASEARCHINFO";				// 搜索日志信息

	// 业务类型－查询
	public final static String	QUERY_ACCOUNT						= "QUERYACCOUNT";					// 用户注册信息
	public final static String	QUERY_RELATION_ACCOUNT_INFO			= "QUERYRELATIONACCOUNTINFO";		// 注册-关联账号信息
	public final static String	QUERY_UCFORUM_LOG					= "QUERYLOG";				        // 登录日志信息
	public final static String	QUERY_MEDIA_BROWSE_LOG				= "QUERYMEDIABROWSELOG";			// 播放音视频/浏览图片日志信息
	public final static String	QUERY_COMMENT						= "QUERYCOMMENT";					// 评论信息
	public final static String	QUERY_QUERYBARRAGE				    = "QUERYBARRAGE";					// 弹幕信息
	public final static String	QUERY_RECHARGE_WITH_DRAWALS			= "QUERYRECHARGEWITHDRAWALS";		// 充值信息
	public final static String	QUERY_REWARD						= "QUERYREWARD";					// 赠送/打赏信息
	public final static String	QUERY_PAYMENT						= "QUERYPAYMENT";					// 支付信息
	public final static String	QUERY_PAYMENT_ORDER					= "QUERYPAYMENTORDER";				// 下单信息

	// 业务类型－布控和停控和状态查询
	public final static String	ADD_MONITOR_MEDIA_BROWSE_LOG		= "ADDMONITORMEDIABROWSELOG";		// 播放音视频/浏览图片日志信息布控
	public final static String	DEL_MONITOR_MEDIA_BROWSE_LOG		= "DELMONITORMEDIABROWSELOG";		// 播放音视频/浏览图片日志信息停控
	public final static String	ADD_MONITOR_MEDIA_BROWSE_LOG_STATUS	= "ADDMONITORMEDIABROWSELOGSTATUS";	// 播放音视频/浏览图片日志信息布控状态
	public final static String	ADD_MONITOR_CHAT					= "ADDMONITORCHAT";					// 私聊布控
	public final static String	DEL_MONITOR_CHAT					= "DELMONITORCHAT";					// 私聊停控
	public final static String	ADD_MONITOR_CHAT_STATUS				= "ADDMONITORCHATSTATUS";			// 私聊布控状态
	public final static String	ADD_MONITOR_BARRAGE					= "ADDMONITORBARRAGE";				// 弹幕信息布控
	public final static String	DEL_MONITOR_PHONE_GPS_INFO			= "DELMONITORBARRAGE";			    // 弹幕信息停控
	public final static String	ADD_MONITOR_BARRAGE_STATUS			= "ADDMONITORBARRAGESTATUS";		//弹幕信息布控状态

	// 业务类型－管控
	public final static String	FREEZE_ACCOUNT						= "FREEZEACCOUNT";					// 账号冻结
	public final static String	FREEZE_IP_ZONE						= "FREEZEIPZONE";					// IP区域限制

	// 业务类型－统计
	public final static String	FILE_STAT							= "FILESTAT";						// 文件统计信息
	public final static String	DATA_STAT							= "DATASTAT";						// 数据量统计信息

	// 消息类型
	public final static String	WA_REQUEST							= "1";								// 请求
	public final static String	WA_RESPONSE							= "2";								// 响应
	public final static String	WA_RESULT							= "3";								// 结果

	// 查询条件
	public final static String	DATA_USER_ACCOUNT					= "USER_ACCOUNT";					// 账号
	public final static String	DATA_USER_INTENRALID				= "USER_INTENRALID";				// 用户内部ID
	public final static String	DATA_IP								= "IP";								// IP地址
	public final static String	DATA_MAC							= "MAC";							// MAC地址
	public final static String	DATA_ACCOUNTNAME					= "ACCOUNTNAME";					// 账号
	public final static String	DATA_USERID							= "USERID";							// 用户内部ID
	public final static String	DATA_MEDIA_ID						= "MEDIA_ID";						// 音视频/图片ID
	public final static String	DATA_CONTENT						= "CONTENT";						// 评论内容
	public final static String	DATA_BEGINTIME						= "BEGINTIME";						// 开始时间，绝对秒
	public final static String	DATA_ENDTIME						= "ENDTIME";						// 结束时间，绝对秒
	public final static String	DATA_REWARD_USERID					= "REWARD_USERID";					// 赠送/打赏用户ID
	public final static String	DATA_RECEIVING_USERID				= "RECEIVING_USERID";				// 接收用户ID
	public final static String	DATA_GOODS_NAME						= "GOODS_NAME";						// 物品名称
	public final static String	DATA_ORDERNUM						= "ORDERNUM";						// 订单号

}
