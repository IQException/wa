package com.ximalaya.wa.model;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ximalaya.service.track.thrift.SecurityPlayResult;
import com.ximalaya.stat.count.client.thrift.ThriftSimpleCountCollecterClient;
import com.ximalaya.wa.annotation.Mapped;
import com.ximalaya.wa.assembler.DataAssembler;
import com.ximalaya.wa.config.Dict;
import com.ximalaya.wa.helper.BeansHolder;
import com.ximalaya.wa.model.xml.Item;

public class Account extends BaseModel {

    private static final Logger LOG = LoggerFactory.getLogger(BaseModel.class);
    private static final String[] names = new String[] { "user.followings.count", "user.followers.count" };
    @Mapped(wa = "USER_TYPE", xm = "")
    private String userType;
    @Mapped(wa = "NICKNAME", xm = "data.nickName", enc = true)
    private String nickName;
    @Mapped(wa = "USERPHOTO", xm = "data.logoPic")
    private String avatarPath;
    @Mapped(wa = "SUMMARY", xm = "data.personalSignatrue", enc = true)
    private String personalDesc;
    @Mapped(wa = "REG_MOBILEPHONE", xm = "data.registerMobile")
    private String regTel;
    @Mapped(wa = "REG_EMAIL", xm = "data.registerEmail")
    private String regMail;
    @Mapped(wa = "IDEN_TYPE", xm = "")
    private String idenType;
    @Mapped(wa = "IDENTIFICATION_TYPE", xm = "")
    private String identificationType;
    @Mapped(wa = "IDENTIFICATION_ID", xm = "")
    private String identificationId;
    @Mapped(wa = "USERID_PHOTO", xm = "")
    private String identificationPhoto;
    @Mapped(wa = "MOBILEPHONE", xm = "data.currentMobile")
    private String bindTel;
    @Mapped(wa = "EMAIL", xm = "data.currentEmail")
    private String bindMail;
    @Mapped(wa = "FOLLOWER_NUM", xm = "")
    private String followerNum;
    @Mapped(wa = "FOLLOW_NUM", xm = "")
    private String followNum;
    @Mapped(wa = "HOME_PAGE", xm = "data.url", enc = true)
    private String homePage;
    @Mapped(wa = "LABEL", xm = "")
    private String signature;
    @Mapped(wa = "ACTION_TYPE", xm = "")
    private String actionType;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getPersonalDesc() {
        return personalDesc;
    }

    public void setPersonalDesc(String personalDesc) {
        this.personalDesc = personalDesc;
    }

    public String getRegTel() {
        return regTel;
    }

    public void setRegTel(String regTel) {
        this.regTel = regTel;
        if ("15650768828".equals(regTel) || "17610687806".equals(regTel)) {
            LOG.info("register success! tel:{}", regTel);
        }
    }

    public String getRegMail() {
        return regMail;
    }

    public void setRegMail(String regMail) {
        this.regMail = regMail;
    }

    public String getIdenType() {
        return idenType;
    }

    public void setIdenType(String idenType) {
        this.idenType = idenType;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentificationId() {
        return identificationId;
    }

    public void setIdentificationId(String identificationId) {
        this.identificationId = identificationId;
    }

    public String getIdentificationPhoto() {
        return identificationPhoto;
    }

    public void setIdentificationPhoto(String identificationPhoto) {
        this.identificationPhoto = identificationPhoto;
    }

    public String getBindTel() {
        return bindTel;
    }

    public void setBindTel(String bindTel) {
        this.bindTel = bindTel;
        if ("15650768828".equals(bindTel) || "17610687806".equals(bindTel)) {
            LOG.info("register success! tel:{}", bindTel);
        }
    }

    public String getBindMail() {
        return bindMail;
    }

    public void setBindMail(String bindMail) {
        this.bindMail = bindMail;
    }

    public String getFollowerNum() {
        return followerNum;
    }

    public void setFollowerNum(String followerNum) {
        this.followerNum = followerNum;
    }

    public String getFollowNum() {
        return followNum;
    }

    public void setFollowNum(String followNum) {
        this.followNum = followNum;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public static Item getOpCode() {
        return DataAssembler.getOpCode(Dict.OP_ACCOUNT);
    }

    public static Item getQueryOpCode() {
        return DataAssembler.getOpCode(Dict.OP_QUERY_ACCOUNT);
    }

    @Override
    public void fixInfo() {
        super.fixInfo();
        setUserType(StringUtils.isBlank(this.getUid()) ? Dict.USERTYPE_VISITOR : Dict.USERTYPE_USER);
        setHomePage(!StringUtils.isBlank(this.getUid()) ? new String(Base64.encodeBase64(("www.ximalaya.com/zhubo/"+this.getUid()).getBytes())) : null);
        ThriftSimpleCountCollecterClient statCount = BeansHolder.getBean(ThriftSimpleCountCollecterClient.class);
        if (StringUtils.isNotBlank(this.getUid())) {
            long[] counts = statCount.getByNames(names, this.getUid());
            if (counts != null && counts.length >= 2)
                this.setFollowNum(String.valueOf(counts[0]));
            this.setFollowerNum(String.valueOf(counts[1]));
        }
        

    }

}
