package com.ximalaya.wa.config;

public class Dict {

	/**
	 * dataset类型
	 */
	public static final String	DS_COMMOM					= "WA_COMMON_010000";
	public static final String	DS_REPORT					= "WA_COMMON_010022";
	public static final String	DS_QUERY					= "WA_COMMON_010010";
	public static final String	DS_MONITOR_RESULT			= "WA_COMMON_010010";
	public static final String	DS_MONITOR					= "WA_COMMON_010020";
	public static final String	DS_QUERY_STATUS				= "WA_COMMON_010004";
	public static final String	DS_QUERY_RESULT				= "WA_COMMON_010012";

	/**
	 * 应用类型:apptype
	 */
	public static final String	APPTYPE						= "1390008";

	/**
	 * 业务类型:opcode
	 */
	public static final String	OP_QUERY_NODE_STATUS		= "QUERYNODESTATUS";
	public static final String	OP_COMM_CONTENT				= "COMMCONTENT";

	public static final String	OP_COMMENT					= "COMMENT";
	public static final String	OP_SHARE					= "MEDIASHAREFORWARDING";
	public static final String	OP_UPDOWNLOAD				= "UPDOWNLOADLOG";
	public static final String	OP_STAR						= "COLLECTION";
	public static final String	OP_SUBSCRIBE				= "SUBSCRIBEINFO";
	public static final String	OP_CLOG						= "CLOG";
	public static final String	OP_SEARCH					= "MEDIASEARCHINFO";
	public static final String	OP_ACCOUNT					= "ACCOUNT";
	public static final String	OP_RELATED_ACCOUNT			= "RELATIONACCOUNTINFO";
	public static final String	OP_LOGIN					= "LOGIN";
	public static final String	OP_PLAY						= "MEDIABROWSELOG";
	public static final String	OP_DONATE					= "REWARD";
	public static final String	OP_FILTER					= "MASKMESSAGE";
	public static final String	OP_PAYMENT					= "PAYMENT";
	public static final String	OP_PAYMENT_ORDER			= "PAYMENTORDER";
	public static final String	OP_RECHARGE					= "RECHARGEWITHDRAWALS";

	public static final String	OP_QUERY_ACCOUNT			= "QUERYACCOUNT";
	public static final String	OP_QUERY_LOGIN				= "QUERYLOG";
	public static final String	OP_QUERY_PLAY				= "QUERYMEDIABROWSELOG";
	public static final String	OP_QUERY_COMMENT			= "QUERYCOMMENT";
	public static final String	OP_QUERY_DONATE				= "QUERYREWARD";
	public static final String	OP_QUERY_RECHARGE			= "QUERYRECHARGEWITHDRAWALS";
	public static final String	OP_QUERY_PAYMENT			= "QUERYPAYMENT";
	public static final String	OP_QUERY_PAYMENT_ORDER		= "QUERYPAYMENTORDER";
	public static final String	OP_QUERY_RELATED_ACCOUNT	= "QUERYRELATIONACCOUNTINFO";

	public static final String	OP_MONITOR_BULLET_ADD		= "ADDMONITORBARRAGE";
	public static final String	OP_MONITOR_PLAY_ADD			= "ADDMONITORMEDIABROWSELOG";
	public static final String	OP_MONITOR_CHAT_ADD			= "ADDMONITORCHAT";
	public static final String	OP_MONITOR_PAYMENTORDER_ADD = "ADDMONITORPAYMENTORDER";
	public static final String	OP_MONITOR_PAYMENT_ADD      = "ADDMONITORPAYMENT";
	public static final String	OP_MONITOR_RECHARGE_ADD     = "ADDMONITORRECHARGEWITHDRAWALS";
	
	
	public static final String	OP_MONITOR_BULLET_DEL		= "DELMONITORBARRAGE";
	public static final String	OP_MONITOR_PLAY_DEL			= "DELMONITORMEDIABROWSELOG";
	public static final String	OP_MONITOR_CHAT_DEL			= "DELMONITORCHAT";
	public static final String	OP_MONITOR_PAYMENTORDER_DEL = "DELMONITORPAYMENTORDER";
	public static final String	OP_MONITOR_PAYMENT_DEL      = "DELMONITORPAYMENT";
	public static final String	OP_MONITOR_RECHARGE_DEL     = "DELMONITORRECHARGEWITHDRAWALS";
	
	
	public static final String	OP_MONITOR_BULLET_STATUS	= "ADDMONITORBARRAGESTATUS";
	public static final String	OP_MONITOR_PLAY_STATUS		= "ADDMONITORMEDIABROWSELOGSTATUS";
	public static final String	OP_MONITOR_CHAT_STATUS		= "ADDMONITORCHATSTATUS";
	public static final String	OP_MONITOR_PAYMENTORDER_STATUS = "ADDMONITORPAYMENTORDERSTATUS";
	public static final String	OP_MONITOR_PAYMENT_STATUS      = "ADDMONITORPAYMENTSTATUS";
	public static final String	OP_MONITOR_RECHARGE_STATUS     = "ADDMONITORRECHARGEWITHDRAWALSSTATUS";

	public static final String	OP_MANAGE_USERS				= "FREEZEUSERS";
	public static final String	OP_MANAGE_AREAS				= "FREEZEAREAS";
	public static final String	OP_MANAGE_MESSAGES			= "FREEZEMESSAGES";
	public static final String	OP_MANAGE_APPFUNCTIONS		= "FREEZEAPPFUNCTIONS";

	/**
	 * 消息类型：msgtype
	 */
	public static final String	MSG_REQUSET					= "1";
	public static final String	MSG_RESPONSE				= "2";
	public static final String	MSG_RESULT					= "3";

	/**
	 * areacode
	 */
	public static final String	AREA_SH						= "310116";

	/**
	 * fileType
	 */
	public static final String	FILETYPE_PIC				= "01";
	public static final String	FILETYPE_TRACK				= "02";
	public static final String	FILETYPE_VIDEO				= "03";
	public static final String	FILETYPE_OTHER				= "99";

	/**
	 * actionType 上传下载
	 */
	public static final String	ACTION_UD_UPLOAD			= "1";
	public static final String	ACTION_UD_DOWNLOAD			= "2";
	public static final String	ACTION_UD_STAR				= "3";
	public static final String	ACTION_UD_UPDATE			= "4";
	public static final String	ACTION_UD_DELETE			= "5";
	public static final String	ACTION_UD_CACHE				= "6";
	public static final String	ACTION_UD_UNKNOWN			= "99";

	/**
	 * actionType 注册
	 */
	public static final String	ACTION_REG_ADD				= "1";
	public static final String	ACTION_REG_DEL				= "2";
	public static final String	ACTION_REG_MOD				= "3";

	/**
	 * actionType 收藏
	 */
	public static final String	ACTION_STAR_STAR			= "01";
	public static final String	ACTION_STAR_LIKE			= "02";
	public static final String	ACTION_STAR_OTHER			= "99";
	/**
	 * actionType 订阅
	 */
	public static final String	ACTION_SUBSCRIBE_SUB		= "01";
	public static final String	ACTION_SUBSCRIBE_CANCEL		= "02";
	public static final String	ACTION_SUBSCRIBE_FOLLOW		= "03";
	public static final String	ACTION_SUBSCRIBE_OTHER		= "99";

	/**
	 * relatedAccountType
	 */
	public static final String	RELATED_ACCOUNT_QQ			= "1030001";
	public static final String	RELATED_ACCOUNT_WEIXIN		= "1030036";
	public static final String	RELATED_ACCOUNT_WEIBO		= "1330001";
	public static final String	RELATED_ACCOUNT_TWEIBO		= "1330002";
	public static final String	RELATED_ACCOUNT_RMWEIBO		= "1330003";
	public static final String	RELATED_ACCOUNT_FHWEIBO		= "1330004";
	public static final String	RELATED_ACCOUNT_ALIPAY		= "1290007";
	public static final String	RELATED_ACCOUNT_OTHER		= "1299999";

	/**
	 * userType
	 */
	public static final String	USERTYPE_USER				= "02";
	public static final String	USERTYPE_VISITOR			= "07";
	public static final String	USERTYPE_OTHER				= "99";

	/**
	 * login-termType
	 */
	public static final String	LOGIN_TERM_TYPE_PC			= "01";
	public static final String	LOGIN_TERM_TYPE_TEL			= "02";
	public static final String	LOGIN_TERM_TYPE_PAD			= "03";
	public static final String	LOGIN_TERM_TYPE_BROSWER		= "04";
	public static final String	LOGIN_TERM_TYPE_OTHER		= "99";

	/**
	 * triggerType
	 */
	public static final String	TRIGGER_WORD				= "01";
	public static final String	TRIGGER_PIC					= "02";
	public static final String	TRIGGER_MD5					= "03";
	public static final String	TRIGGER_OTHER				= "99";

	/**
	 * actionType filter
	 */
	public static final String	ACTION_FILTER				= "01";
	public static final String	ACTION_DELETE				= "02";
	public static final String	ACTION_SHIELD				= "03";

	/**
	 * violationType
	 */
	public static final String	VIOLATION_POLITICS			= "01";
	public static final String	VIOLATION_TERRORISM			= "02";
	public static final String	VILOATION_EROTICISM			= "03";
	public static final String	VIOLATION_OTHER				= "99";

	/**
	 * serach target
	 */
	public static final String	SEARCH_USER					= "01";
	public static final String	SEARCH_POST					= "02";
	public static final String	SEARCH_BA					= "03";
	public static final String	SEARCH_MODULE				= "04";
	public static final String	SEARCH_OTHER				= "99";

	/**
	 * mediaType/messageType
	 */
	public static final String	MEDIA_MESSAGE_TEXT			= "01";
	public static final String	MEDIA_MESSAGE_LINK			= "05";
	public static final String	MEDIA_MESSAGE_REDBAG		= "06";
	public static final String	MEDIA_MESSAGE_CARD			= "07";
	public static final String	MEDIA_MESSAGE_PAY			= "08";
	public static final String	MEDIA_MESSAGE_FILE			= "09";
	public static final String	MEDIA_MESSAGE_AUDIO			= "20";
	public static final String	MEDIA_MESSAGE_VIDEO			= "21";
	public static final String	MEDIA_MESSAGE_PIC			= "98";
	public static final String	MEDIA_MESSAGE_OTHER			= "99";

	/**
	 * actionType donate
	 */
	public static final String	ACTION_DONATE				= "0";
	public static final String	ACTION_GIFT					= "1";

}
