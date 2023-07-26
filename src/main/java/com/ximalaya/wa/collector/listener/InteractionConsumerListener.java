package com.ximalaya.wa.collector.listener;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.ximalaya.data.event.util.NumberedKeyMapTransfer;
import com.ximalaya.wa.collector.core.DataHub;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.converter.WaConverter;
import com.ximalaya.wa.model.Bullet;
import com.ximalaya.wa.model.Comment;
import com.ximalaya.wa.model.Share;
import com.ximalaya.wa.model.Star;
import com.ximalaya.wa.model.Subscribe;
import com.ximalaya.wa.util.MonitorUtil;

public class InteractionConsumerListener extends AbstractConsumerListener {
    private static final String COMMENT = "comment-track";
    private static final String SHARE = "share";
    private static final String STAR = "favorite";
    private static final String FOLLOW = "follow";
    private static final String SUBSCRIBE = "subscribe";

    private String transSubActionType(String event, boolean isDo) {
        if ("follow".equals(event)) {
            return isDo ? Dict.ACTION_SUBSCRIBE_FOLLOW : Dict.ACTION_SUBSCRIBE_CANCEL;
        } else {
            return isDo ? Dict.ACTION_SUBSCRIBE_SUB : Dict.ACTION_SUBSCRIBE_CANCEL;
        }
    }

    @Override
    public void onReceiveMessage(String key, String message) {

        try {

            NumberedKeyMapTransfer transfer = NumberedKeyMapTransfer.instance(key);// Thread safe
            Map<String, Object> result = transfer.trans(message);
            if (result == null) {
                return;
            }
            if (FOLLOW.equals(key) || SUBSCRIBE.equals(key)) {
                System.err.println(result);
            }
            sampleLogger.log(key+":{}",JSON.toJSONString(result));
            switch (key) {
                case COMMENT:
                    Comment comment = WaConverter.convertToModel(Comment.class, result);
                    // 布控
                     String pmId = MonitorUtil.getPmId(comment);
                     if(StringUtils.isNotBlank(pmId)){
                    	 Bullet bullet = WaConverter.convertToModel(Bullet.class, result);
                    	 bullet.setPmId(pmId);
                    	 DataHub.getBulletMonitorQueue().put(bullet);
                     }

                    DataHub.getCommentQueue().put(comment);
                    break;

                case SHARE:
                    Share share = WaConverter.convertToModel(Share.class, result);
                    DataHub.getShareQueue().put(share);
                    break;

                case STAR:
                    Star star = WaConverter.convertToModel(Star.class, result);
                    DataHub.getStarQueue().put(star);
                    break;

                case FOLLOW:
                    Subscribe follow = new Subscribe();
                    follow.setActionType(transSubActionType(key, Boolean.valueOf(follow.getActionType())));
                    WaConverter.convertToModel(follow, result);
                    DataHub.getSubscribeQueue().put(follow);
                    break;

                case SUBSCRIBE:
                    Subscribe subscribe = new Subscribe();
                    subscribe.setActionType(transSubActionType(key, Boolean.valueOf(subscribe.getActionType())));
                    WaConverter.convertToModel(subscribe, result);
                    DataHub.getSubscribeQueue().put(subscribe);
                    break;

                default:
                    logger.debug("result is :", result);

            }

        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }

    }

}
