/*
 *       CopyrightÂ© (2018-2020) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-build-tools.
 *
 *       weidentity-build-tools is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-build-tools is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-build-tools.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.service;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.crypto.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.BuildToolsConstant;
import com.webank.weid.constant.CnsType;
import com.webank.weid.constant.DataFrom;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.FileOperator;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.deploy.DeployEvidence;
import com.webank.weid.contract.deploy.v2.DeployContractV2;
import com.webank.weid.contract.v2.WeIdContract;
import com.webank.weid.dto.CnsInfo;
import com.webank.weid.dto.DeployInfo;
import com.webank.weid.dto.ShareInfo;
import com.webank.weid.protocol.base.AuthorityIssuer;
import com.webank.weid.protocol.base.CptBaseInfo;
import com.webank.weid.protocol.base.HashContract;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.protocol.request.CptStringArgs;
import com.webank.weid.protocol.request.CreateWeIdArgs;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.service.impl.CptServiceImpl;
import com.webank.weid.service.impl.WeIdServiceImpl;
import com.webank.weid.service.impl.engine.DataBucketServiceEngine;
import com.webank.weid.util.ClassUtils;
import com.webank.weid.util.ConfigUtils;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.FileUtils;
import com.webank.weid.util.WeIdSdkUtils;
import com.webank.weid.util.WeIdUtils;

@Service
@Slf4j
public class ContractService {

    private static String preMainHash;

    private ConfigService configService = new ConfigService();

    private WeIdSdkService weIdSdkService = new WeIdSdkService();
    
    private WeBaseService weBaseService = new WeBaseService();
    
    private static final String CONTRACT_JAR_PEFIX = "weid-contract-java-";

    static {
        FileUtils.mkdirs(BuildToolsConstant.ADMIN_PATH);
        FileUtils.mkdirs(BuildToolsConstant.DEPLOY_PATH);
    }

    public ResponseData<String> deploy(String chainId, String applyName) {
        try {
            FiscoConfig fiscoConfig = WeIdSdkUtils.loadNewFiscoConfig();
            fiscoConfig.setChainId(chainId);
            String hash = deployContract(fiscoConfig, DataFrom.WEB);
            //configService.updateChainId(chainId);
            log.info("[deploy] the hash: {}", hash);
            //ĺ°†ĺş”ç”¨ĺ?Ťĺ†™ĺ…Ąé…Ťç˝®ä¸­
            applyName = StringEscapeUtils.unescapeHtml(applyName);
            WeIdPrivateKey currentPrivateKey = WeIdSdkUtils.getWeIdPrivateKey(hash);
            ResponseData<Boolean> response = WeIdSdkUtils.getDataBucket(CnsType.DEFAULT)
                    .put(hash, BuildToolsConstant.APPLY_NAME, applyName, currentPrivateKey);
            log.info("[deploy] put applyName: {}", response);
            return new ResponseData<>(hash, ErrorCode.SUCCESS);
        } catch (Exception e) {
            log.error("[deploy] the contract depoly error.", e);
            return new ResponseData<>(StringUtils.EMPTY, ErrorCode.UNKNOW_ERROR);
        } finally {
            FileUtils.clearDeployFile();
        }
    }

    // ć‰§čˇŚé?¨ç˝˛weIdĺ??çş¦
    public String deployContract(FiscoConfig fiscoConfig, DataFrom from) {
        log.info("begin deploy contract...");
        File targetDir = new File(BuildToolsConstant.ADMIN_PATH, BuildToolsConstant.ECDSA_KEY);
        String privateKey = null;
        if (targetDir.exists()) {
            privateKey = FileUtils.readFile(targetDir.getAbsolutePath());
        }
        DeployContractV2.deployContract(privateKey, fiscoConfig, false);
        log.info("the contract deploy finish.");
        //ĺĽ€ĺ§‹äżťĺ­?ć–‡ä»¶
        //ĺ°†ç§?é’Ąç§»ĺŠ¨ĺ?°/output/adminä¸­
        copyEcdsa();
        return saveDeployInfo(fiscoConfig, from);
    }

    private void copyEcdsa() {
        log.info("[copyEcdsa] begin copy the ecdsa to admin...");
        File ecdsaFile = new File(BuildToolsConstant.ECDSA_KEY);
        File targetDir = new File(BuildToolsConstant.ADMIN_PATH);
        FileUtils.copy(ecdsaFile, new File(targetDir.getAbsoluteFile(), BuildToolsConstant.ECDSA_KEY));
        
        File ecdsaPubFile = new File(BuildToolsConstant.ECDSA_PUB_KEY);
        FileUtils.copy(ecdsaPubFile, new File(targetDir.getAbsoluteFile(), BuildToolsConstant.ECDSA_PUB_KEY));
        log.info("[copyEcdsa] the ecdsa copy successfully.");
    }

    private String saveDeployInfo(FiscoConfig fiscoConfig, DataFrom from) {
        log.info("[saveDeployInfo] begin to save deploy info...");
        //ĺ?›ĺ»şé?¨ç˝˛ç›®ĺ˝•
        String hash = FileUtils.readFile(BuildToolsConstant.HASH);
        DeployInfo deployInfo = buildInfo(fiscoConfig, hash, from);
        saveDeployInfo(deployInfo);
        log.info("[saveDeployInfo] save the deploy info successfully.");
        saveContractToWeBase(deployInfo);
        return hash;
    }

    private void saveContractToWeBase(DeployInfo deployInfo) {
        Integer groupId = configService.getMasterGroupId();
        String version = getContractVersion();
        String hash = deployInfo.getHash();
        weBaseService.contractSave(groupId, "WeIdContract", deployInfo.getWeIdAddress(),version, hash);
        weBaseService.contractSave(groupId, "CptController", deployInfo.getCptAddress(), version, hash);
        weBaseService.contractSave(groupId, "AuthorityIssuerController", deployInfo.getAuthorityAddress(), version, hash);
        weBaseService.contractSave(groupId, "SpecificIssuerController", deployInfo.getEvidenceAddress(), version, hash);
        weBaseService.contractSave(groupId, "EvidenceContract", deployInfo.getSpecificAddress(), version, hash);
    }

    public String getContractVersion() {
        return ClassUtils.getVersionByClass(WeIdContract.class, CONTRACT_JAR_PEFIX);
    }

    private  void saveDeployInfo(DeployInfo info) {
        File deployDir = new File(BuildToolsConstant.DEPLOY_PATH);
        File deployFile = new File(deployDir.getAbsoluteFile(), info.getHash());
        String jsonData = DataToolUtils.serialize(info);
        FileUtils.writeToFile(jsonData, deployFile.getAbsolutePath(), FileOperator.OVERWRITE);
    }

    private DeployInfo buildInfo(FiscoConfig fiscoConfig, String hash, DataFrom from) {
        DeployInfo info = new DeployInfo();
        info.setHash(hash);
        long time = System.currentTimeMillis();
        info.setTime(time);
        info.setEcdsaKey(FileUtils.readFile(BuildToolsConstant.ECDSA_KEY));
        BigInteger privateKey = new BigInteger(info.getEcdsaKey());
        info.setEcdsaPublicKey(DataToolUtils.publicKeyFromPrivate(privateKey).toString());
        try {
            info.setNodeVerion(BaseService.getVersion());
        } catch (Exception e) {
            info.setNodeVerion(fiscoConfig.getVersion()); 
        }
        info.setNodeAddress(fiscoConfig.getNodes());
        info.setAuthorityAddress(FileUtils.readFile(BuildToolsConstant.AUTH_ADDRESS_FILE_NAME));
        info.setCptAddress(FileUtils.readFile(BuildToolsConstant.CPT_ADDRESS_FILE_NAME));
        info.setWeIdAddress(FileUtils.readFile(BuildToolsConstant.WEID_ADDRESS_FILE_NAME));
        info.setEvidenceAddress(FileUtils.readFile(BuildToolsConstant.EVID_ADDRESS_FILE_NAME));
        info.setSpecificAddress(FileUtils.readFile(BuildToolsConstant.SPECIFIC_ADDRESS_FILE_NAME));
        info.setChainId(fiscoConfig.getChainId());
        info.setContractVersion(ClassUtils.getJarNameByClass(WeIdContract.class));
        info.setWeIdSdkVersion(ClassUtils.getJarNameByClass(WeIdServiceImpl.class));
        info.setFrom(from.name());
        return info;
    }

    public ResponseData<LinkedList<CnsInfo>> getDeployList() {
        LinkedList<CnsInfo> dataList = new LinkedList<>();
        //ĺ¦‚ćžść˛ˇćś‰é?¨ç˝˛databuketĺ?™ç›´ćŽĄčż”ĺ›ž
        org.fisco.bcos.web3j.precompile.cns.CnsInfo cnsInfo = BaseService.getBucketByCns(CnsType.DEFAULT);
        if (cnsInfo == null) {
            return new ResponseData<>(dataList, ErrorCode.BASE_ERROR);
        }
        //ĺ¦‚ćžść˛ˇćś‰é?¨ç˝˛databuketĺ?™ç›´ćŽĄčż”ĺ›ž
        cnsInfo = BaseService.getBucketByCns(CnsType.ORG_CONFING);
        if (cnsInfo == null) {
            return new ResponseData<>(dataList, ErrorCode.BASE_ERROR);
        }
        String currentHash = WeIdSdkUtils.getMainHash();
        List<HashContract> result = WeIdSdkUtils.getDataBucket(CnsType.DEFAULT).getAllBucket().getResult();
        String roleType = configService.getRoleType().getResult();
        for (HashContract hashContract : result) {
            CnsInfo cns = new CnsInfo();
            cns.setHash(hashContract.getHash());
            cns.setTime(hashContract.getTime());
            cns.setWeId(hashContract.getOwner());
            cns.setEnable(hashContract.getHash().equals(currentHash));
            cns.setRoleType(roleType);
            //ĺ¦‚ćžśĺ˝“ĺ‰Ťč§’č‰˛ä¸şéťžç®ˇç?†ĺ‘?, ĺ?™ĺŹŞć?ľç¤şĺ·˛ĺ?Żç”¨ć•°ćŤ®ďĽŚćśŞĺ?Żç”¨ć•°ćŤ®ç›´ćŽĄč·łčż‡
            if ("2".equals(roleType) && !cns.isEnable()) {
                continue;
            }
            
            DeployInfo deployInfo = WeIdSdkUtils.getDepolyInfoByHash(cns.getHash());
            if (deployInfo != null && !deployInfo.isDeploySystemCpt()) {
                cns.setNeedDeployCpt(true);
            }
            if (cns.isEnable() && cns.isNeedDeployCpt()) { //ĺ¦‚ćžść?Żĺ?Żç”¨çŠ¶ć€?
                cns.setShowDeployCptBtn(true);
            }
            // ćźĄčŻ˘ć­¤é?¨ç˝˛č´¦ć?·çš„ćť?ĺ¨?ćśşćž„ĺ?Ť
            if (cns.isEnable()) {
                AuthorityIssuer issuer = weIdSdkService.getIssuerByWeId(cns.getWeId());
                cns.setIssuer(issuer);
            }
            String applyName = WeIdSdkUtils.getDataBucket(CnsType.DEFAULT).get(cns.getHash(), BuildToolsConstant.APPLY_NAME).getResult();
            cns.setApplyName(applyName);
            dataList.add(cns);
        }
        Collections.sort(dataList);
        dataList.forEach(cns -> {
            cns.setGroupId("group-" + WeIdSdkUtils.loadNewFiscoConfig().getGroupId());
            if (cns.isEnable()) { // ĺ¦‚ćžść?Żĺ?Żç”¨çŠ¶ć€?
                //ĺ¦‚ćžśä¸Šä¸€ä¸Şĺś°ĺť€ä¸Ťä¸şç©şďĽŚĺą¶ä¸”ć–°hashĺś°ĺť€č·źä¸Šä¸€ä¸Şĺś°ĺť€ä¸Ťç›¸ĺ?Śĺ?™reloadAddress
                if (StringUtils.isNotBlank(preMainHash) && !preMainHash.equals(cns.getHash())) {
                    configService.reloadAddress();
                }
                //ĺ˝“ĺ‰Ťč´¦ć?·ĺ?›ĺ»şweIdďĽŚďĽ?ĺ†…é?¨ćś‰ĺ?¤ć–­ĺ¦‚ćžśĺ·˛ĺ?›ĺ»şĺ?™ä¸ŤäĽšč°?ç”¨ĺŚşĺť—é“ľĺ?›ĺ»şďĽ‰
                createWeIdForCurrentUser(DataFrom.WEB);
                preMainHash = cns.getHash();
            }
        });
        return new ResponseData<>(dataList, ErrorCode.SUCCESS);
    }

    /**
     * ć ąćŤ®hashä»Žé“ľä¸ŠčŽ·ĺŹ–ĺś°ĺť€äżˇć?Ż.
     * @param hash čŽ·ĺŹ–é?¨ç˝˛ć•°ćŤ®çš„hashĺ€Ľ
     * @return čż”ĺ›žĺ˝“ĺ‰Ťhashçš„é?¨ç˝˛äżˇć?Ż
     */
    public DeployInfo getDeployInfoByHashFromChain(String hash) {
        //ĺ?¤ć–­ćś¬ĺś°ć?Żĺ?¦ćś‰ć¬ˇhashč®°ĺ˝•
        DeployInfo deploy = WeIdSdkUtils.getDepolyInfoByHash(hash);
        if (deploy != null) {
            deploy.setLocal(true);//ćś¬ĺś°ćś‰
        } else {
            deploy = new DeployInfo();
            String weIdAddr = getValueFromCns(CnsType.DEFAULT, hash, WeIdConstant.CNS_WEID_ADDRESS);
            String authAddr = getValueFromCns(CnsType.DEFAULT, hash, WeIdConstant.CNS_AUTH_ADDRESS);
            String cptAddr = getValueFromCns(CnsType.DEFAULT, hash, WeIdConstant.CNS_CPT_ADDRESS);
            String specificAddr = getValueFromCns(CnsType.DEFAULT, hash, WeIdConstant.CNS_SPECIFIC_ADDRESS);
            String evidenceAddr = getValueFromCns(CnsType.DEFAULT, hash, WeIdConstant.CNS_EVIDENCE_ADDRESS);
            String chainId = getValueFromCns(CnsType.DEFAULT, hash, WeIdConstant.CNS_CHAIN_ID);
            deploy.setChainId(chainId);
            deploy.setHash(hash);
            deploy.setWeIdAddress(weIdAddr);
            deploy.setAuthorityAddress(authAddr);
            deploy.setCptAddress(cptAddr);
            deploy.setSpecificAddress(specificAddr);
            deploy.setEvidenceAddress(evidenceAddr);
            deploy.setFrom("ĺ…¶ä»–ćśşćž„");
        }
        return deploy;
    }

    private String getValueFromCns(CnsType cnsType, String hash, String key) {
        return WeIdSdkUtils.getDataBucket(cnsType).get(hash, key).getResult();
    }

    public ResponseData<Boolean> deploySystemCpt(String hash, DataFrom from) {
        try {
            DeployInfo deployInfo = WeIdSdkUtils.getDepolyInfoByHash(hash);
            if (deployInfo == null) {
                log.error("[deploySystemCpt] can not found the admin ECDSA.");
                return new ResponseData<>(Boolean.FALSE, ErrorCode.BASE_ERROR.getCode(), "can not found the admin ECDSA.");
            }
            // ćł¨ĺ†Śweid
            createWeId(deployInfo, from, true);

            log.info("[deploySystemCpt] begin register systemCpt...");
            //é?¨ç˝˛çł»ç»źCPT, 
            if (!registerSystemCpt(deployInfo)) {
                log.error("[deploySystemCpt] systemCpt is register fail.");
                return new ResponseData<>(Boolean.FALSE, ErrorCode.BASE_ERROR.getCode(), "systemCpt is register fail.");
            }

            log.info("[deploySystemCpt] systemCpt is registed and to save.");
            //ć›´ć–°é?¨ç˝˛äżˇć?Ż,čˇ¨ç¤şĺ·˛é?¨ç˝˛çł»ç»źCPT
            deployInfo.setDeploySystemCpt(true);
            saveDeployInfo(deployInfo);
        } catch (Exception e) {
            log.error("[deploySystemCpt] the System Cpt depoly error.", e);
            return new ResponseData<>(Boolean.FALSE, ErrorCode.BASE_ERROR);
        }

        return new ResponseData<>(Boolean.TRUE, ErrorCode.SUCCESS);
    }

    private boolean registerSystemCpt(DeployInfo deployInfo) {
        CptStringArgs cptStringArgs = new CptStringArgs();
        WeIdAuthentication weIdAuthentication = new WeIdAuthentication();
        BigInteger privateKey = new BigInteger(deployInfo.getEcdsaKey());
        String weId = WeIdUtils.convertPublicKeyToWeId(deployInfo.getEcdsaPublicKey());
        WeIdPrivateKey weIdPrivateKey = new WeIdPrivateKey();
        weIdPrivateKey.setPrivateKey(privateKey.toString());
        weIdAuthentication.setWeIdPrivateKey(weIdPrivateKey);
        weIdAuthentication.setWeId(weId);
        cptStringArgs.setWeIdAuthentication(weIdAuthentication);

        CptServiceImpl cptService = new CptServiceImpl();
        for (Integer cptId : BuildToolsConstant.CPTID_LIST) {
            String cptJsonSchema = DataToolUtils.generateDefaultCptJsonSchema(cptId);
            if (cptJsonSchema.isEmpty()) {
                log.info("[registerSystemCpt] Cannot generate CPT json schema with ID: " + cptId);
                return false;
            }
            cptStringArgs.setCptJsonSchema(cptJsonSchema);
            ResponseData<CptBaseInfo> responseData = cptService.registerCpt(cptStringArgs, cptId);
            if (responseData.getResult() == null) {
                log.info("[registerSystemCpt] Register System CPT failed with ID: " + cptId);
                return false;
            }
        }
        return true;
    }

    private void createWeId(DeployInfo deployInfo, DataFrom from, boolean isAdmin) {
        log.info("[createWeId] begin createWeid for admin");
        CreateWeIdArgs arg = new CreateWeIdArgs();
        arg.setPublicKey(deployInfo.getEcdsaPublicKey());
        WeIdPrivateKey pkey = new WeIdPrivateKey();
        pkey.setPrivateKey(deployInfo.getEcdsaKey());
        arg.setWeIdPrivateKey(pkey);
        String weId = WeIdUtils.convertPublicKeyToWeId(arg.getPublicKey());
        boolean checkWeId = weIdSdkService.checkWeId(weId);
        if (!checkWeId) {
            String result = weIdSdkService.createWeId(arg, from, isAdmin).getResult();
            log.info("[createWeId]  createWeId for admin result = {}", result);
            System.out.println("createWeId for admin result = " + result);
        } else {
            log.info("[createWeId] the weId is exist."); 
        }
        // é»?č®¤ĺ°†ĺ˝“ĺ‰Ťweidćł¨ĺ†Ść??ä¸şćť?ĺ¨?ćśşćž„ďĽŚĺą¶č®¤čŻ?
        String orgId = ConfigUtils.getCurrentOrgId();
        // ćł¨ĺ†Śćť?ĺ¨?ćśşćž„
        weIdSdkService.registerIssuer(weId, orgId, null);
        // č®¤čŻ?ćť?ĺ¨?ćśşćž„
        weIdSdkService.recognizeAuthorityIssuer(weId);
    }

    /**
     * ç»™ĺ˝“ĺ‰Ťč´¦ć?·ĺ?›ĺ»şWeId.
     * @param from ĺ?›ĺ»şćťĄćş?
     * @return čż”ĺ›žĺ?›ĺ»şçš„weId
     */
    public String createWeIdForCurrentUser(DataFrom from) {
        //ĺ?¤ć–­ĺ˝“ĺ‰Ťç§?é’Ąč´¦ć?·ĺŻąĺş”çš„weidć?Żĺ?¦ĺ­?ĺś¨ďĽŚĺ¦‚ćžśä¸Ťĺ­?ĺś¨ĺ?™ĺ?›ĺ»şweId
        CreateWeIdArgs arg = new CreateWeIdArgs();
        arg.setWeIdPrivateKey(WeIdSdkUtils.getCurrentPrivateKey());
        arg.setPublicKey(DataToolUtils.publicKeyFromPrivate(new BigInteger(arg.getWeIdPrivateKey().getPrivateKey())).toString());
        String weId = WeIdUtils.convertPublicKeyToWeId(arg.getPublicKey());
        log.info("[createWeIdForCurrentUser] the current weId is = {}", weId);
        boolean checkWeId = weIdSdkService.checkWeId(weId);
        if (!checkWeId) {
            log.info("[createWeIdForCurrentUser] the current weId is not exist and begin create.");
            String result = weIdSdkService.createWeId(arg, from, true).getResult();
            log.info("[createWeIdForCurrentUser] create weid for current account result = {}", result);
            return result;
        } else {
            log.info("[createWeIdForCurrentUser] the current weId is exist.");
            return weId;
        }
    }

    public void enableHash(CnsType cnsType, String hash, String oldHash) {
        log.info("[enableHash] begin enable the hash: {}", hash);
        //ĺ?Żç”¨ć–°hash
        WeIdPrivateKey privateKey = WeIdSdkUtils.getWeIdPrivateKey(hash);
        ResponseData<Boolean> enableHash = WeIdSdkUtils.getDataBucket(cnsType).enable(hash, privateKey);
        log.info("[enableHash] enable the hash {} --> result: {}", hash, enableHash);
        //ĺ¦‚ćžśĺŽźhashä¸Ťä¸şç©şďĽŚĺ?™ĺ?śç”¨ĺŽźhash
        if (StringUtils.isNotBlank(oldHash)) {
            ResponseData<Boolean> disableHash = WeIdSdkUtils.getDataBucket(cnsType).disable(oldHash, privateKey);
            log.info("[enableHash] disable the old hash {} --> result: {}", oldHash, disableHash);
        } else {
            log.info("[enableHash] no old hash to disable");
        }
    }

    public ResponseData<Boolean> removeHash(CnsType cnsType, String hash) {
        log.info("[removeHash] begin remove the hash: {}", hash);
        WeIdPrivateKey privateKey = WeIdSdkUtils.getWeIdPrivateKey(hash);
        return WeIdSdkUtils.getDataBucket(cnsType).removeDataBucketItem(hash, false, privateKey);
    }

    /**
     * čŽ·ĺŹ–ć‰€ćś‰çš„hashĺ?—čˇ¨
     * @return čż”ĺ›žshareInfoĺ?—čˇ¨ć•°ćŤ®
     */
    public ResponseData<List<ShareInfo>> getShareList() {
        List<ShareInfo> result = new ArrayList<>();
        // ĺ¦‚ćžść˛ˇćś‰é?¨ç˝˛databuketĺ?™ç›´ćŽĄčż”ĺ›ž
        org.fisco.bcos.web3j.precompile.cns.CnsInfo cnsInfo = BaseService.getBucketByCns(CnsType.SHARE);
        if (cnsInfo == null) {
            log.warn("[getShareList] the cnsType does not regist, please deploy the evidence.");
            return new ResponseData<>(result, ErrorCode.BASE_ERROR);
        }
        String orgId = ConfigUtils.getCurrentOrgId();
        DataBucketServiceEngine dataBucket = WeIdSdkUtils.getDataBucket(CnsType.SHARE);
        List<HashContract> list = dataBucket.getAllBucket().getResult();
        // ĺ?¤ć–­ćśşćž„é…Ťç˝®ç§?é’Ąć?Żĺ?¦ĺŚąé…Ťć‰€ćś‰č€…ďĽŚĺ¦‚ćžśä¸ŤĺŚąé…Ťéˇµéť˘ĺŹŻä»Ąä¸Ťç”¨ć?ľç¤şćŚ‰é’®
        boolean isMatch = isMatchThePrivateKey();
        Map<String, AuthorityIssuer> cache = new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> allGroup = weIdSdkService.getAllGroup(true);
            for (HashContract hashContract : list) {
                ShareInfo share = new ShareInfo();
                share.setTime(hashContract.getTime());
                share.setOwner(hashContract.getOwner());
                share.setHash(hashContract.getHash());
                share.setShowBtn(isMatch);
                // čŽ·ĺŹ–hashçš„çľ¤ç»„
                String groupId = dataBucket.get(hashContract.getHash(), WeIdConstant.CNS_GROUP_ID).getResult();
                if(StringUtils.isNotBlank(groupId) && allGroup.contains(groupId)) {
                    share.setGroupId(Integer.parseInt(groupId));
                    //ĺ?¤ć–­ć?Żĺ?¦ĺ?Żç”¨ć­¤hash
                    String enableHash = WeIdSdkUtils.getDataBucket(CnsType.ORG_CONFING).get(orgId, WeIdConstant.CNS_EVIDENCE_HASH + groupId).getResult();
                    share.setEnable(hashContract.getHash().equals(enableHash));
                    //ćźĄčŻ˘ć­¤é?¨ç˝˛č´¦ć?·çš„ćť?ĺ¨?ćśşćž„ĺ?Ť
                    if(cache.containsKey(share.getOwner())) {
                        share.setIssuer(cache.get(share.getOwner()));
                    } else {
                        AuthorityIssuer issuer = weIdSdkService.getIssuerByWeId(share.getOwner());
                        share.setIssuer(issuer);
                        cache.put(share.getOwner(), issuer);
                    }
                    String evidenceName = dataBucket.get(share.getHash(), BuildToolsConstant.EVIDENCE_NAME).getResult();
                    share.setEvidenceName(evidenceName);
                    result.add(share);
                }
            }
        }
        Collections.sort(result);
        return new ResponseData<>(result, ErrorCode.SUCCESS);
    }

    /**
     * ć ąćŤ®çľ¤ç»„é?¨ç˝˛Evidenceĺ??çş¦.
     * @param fiscoConfig ĺ˝“ĺ‰Ťé…Ťç˝®äżˇć?Ż
     * @param groupId çľ¤ç»„çĽ–ĺŹ·
     * @param from é?¨ç˝˛ćťĄćş?
     * @return čż”ĺ›žć?Żĺ?¦é?¨ç˝˛ć??ĺŠź
     */
    public String deployEvidence(FiscoConfig fiscoConfig, Integer groupId, DataFrom from) {
        log.info("[deployEvidence] begin deploy the evidence, groupId = {}.", groupId);
        try {
            //  čŽ·ĺŹ–ç§?é’Ą
            WeIdPrivateKey currentPrivateKey = WeIdSdkUtils.getCurrentPrivateKey();
            String hash = DeployEvidence.deployContract(
                currentPrivateKey.getPrivateKey(), 
                groupId, 
                false
            );
            if (StringUtils.isBlank(hash)) {
                log.error("[deployEvidence] deploy the evidence fail, please check the log.");
                return StringUtils.EMPTY;
            }
            // ĺ†™é?¨ç˝˛ć–‡ä»¶
            ShareInfo share = buildShareInfo(fiscoConfig, hash, groupId, currentPrivateKey, from);
            saveShareInfo(share);
            // ĺŻĽĺ…Ąĺ??çş¦ĺ?°WeBaseä¸­
            String version =  this.getContractVersion();
            weBaseService.contractSave(groupId, "EvidenceContract", share.getEvidenceAddress(), version, hash);
            log.info("[deployEvidence] the evidence deploy successfully.");
            return hash;
        } catch (Exception e) {
            log.error("[deployEvidence] deploy the evidence has error.", e);
            return StringUtils.EMPTY;
        }
    }

    private  void saveShareInfo(ShareInfo info) {
        File deployDir = new File(BuildToolsConstant.SHARE_PATH);
        File deployFile = new File(deployDir.getAbsoluteFile(), info.getHash());
        String jsonData = DataToolUtils.serialize(info);
        FileUtils.writeToFile(jsonData, deployFile.getAbsolutePath(), FileOperator.OVERWRITE);
    }

    private ShareInfo buildShareInfo(
        FiscoConfig fiscoConfig, 
        String hash,
        Integer groupId,
        WeIdPrivateKey currentPrivateKey,
        DataFrom from
    ) {
        ShareInfo info = new ShareInfo();
        info.setHash(hash);
        info.setTime(System.currentTimeMillis());
        info.setEcdsaKey(currentPrivateKey.getPrivateKey());
        info.setEcdsaPublicKey(
            DataToolUtils.publicKeyFromPrivate(new BigInteger(info.getEcdsaKey())).toString());
        try {
            info.setNodeVerion(BaseService.getVersion());
        } catch (Exception e) {
            info.setNodeVerion(fiscoConfig.getVersion()); 
        }
        info.setNodeAddress(fiscoConfig.getNodes());
        String evidenceAddress = WeIdSdkUtils.getDataBucket(CnsType.SHARE).get(hash, WeIdConstant.CNS_EVIDENCE_ADDRESS).getResult();
        info.setEvidenceAddress(evidenceAddress);
        info.setContractVersion(ClassUtils.getJarNameByClass(WeIdContract.class));
        info.setWeIdSdkVersion(ClassUtils.getJarNameByClass(WeIdServiceImpl.class));
        info.setGroupId(groupId);
        info.setFrom(from.name());
        return info;
    }

    public ResponseData<ShareInfo> getShareInfo(String hash) {
        ShareInfo shareInfo = getShareInfoByHash(hash);
        if (shareInfo != null) {
            shareInfo.setLocal(true);
            return new ResponseData<>(shareInfo, ErrorCode.SUCCESS);
        }

        shareInfo = new ShareInfo();
        shareInfo.setHash(hash);
        String groupId = WeIdSdkUtils.getDataBucket(CnsType.SHARE).get(hash, WeIdConstant.CNS_GROUP_ID).getResult();
        if(StringUtils.isNotBlank(groupId)) {
            shareInfo.setGroupId(Integer.parseInt(groupId));
        }
        String evidenceAddress = WeIdSdkUtils.getDataBucket(CnsType.SHARE).get(hash, WeIdConstant.CNS_EVIDENCE_ADDRESS).getResult();
        if(StringUtils.isNotBlank(evidenceAddress)) {
            shareInfo.setEvidenceAddress(evidenceAddress);
        }
        return new ResponseData<>(shareInfo, ErrorCode.SUCCESS);
    }

    private static File getShareFileByHash(String hash) {
        hash = FileUtils.getSecurityFileName(hash);
        return new File(BuildToolsConstant.SHARE_PATH, hash);
    }

    private static ShareInfo getShareInfoByHash(String hash) {
        File shareFile = getShareFileByHash(hash);
        if (shareFile.exists()) {
            String jsonData = FileUtils.readFile(shareFile.getAbsolutePath());
            return DataToolUtils.deserialize(jsonData, ShareInfo.class);
        } else {
            return null;
        }
    }

    public ResponseData<Boolean> enableShareCns(String hash) {
        log.info("[enableShareCns] begin enable new hash...");
        try {
            List<String> allGroup = weIdSdkService.getAllGroup(true);
            // ćźĄčŻ˘hashĺŻąĺş”çš„çľ¤ç»„
            String groupId = WeIdSdkUtils.getDataBucket(CnsType.SHARE).get(hash, WeIdConstant.CNS_GROUP_ID).getResult();
            String evidenceAddress = WeIdSdkUtils.getDataBucket(CnsType.SHARE).get(hash, WeIdConstant.CNS_EVIDENCE_ADDRESS).getResult();
            if (!allGroup.contains(groupId)) {
                log.error("[enableShareCns] the groupId of hash is not in your groupList. groupId = {}" , groupId);
                return new ResponseData<>(Boolean.FALSE, ErrorCode.BASE_ERROR.getCode(),
                        "the groupId of hash is not in your groupList");
            }
            // čŽ·ĺŹ–ĺŽźhash
            String shareHashOld = getEvidenceHash(groupId);
            // ć›´ć–°é…Ťç˝®ĺ?°é“ľä¸Šćśşćž„é…Ťç˝®ä¸­ evidenceAddress.<groupId> 
            String orgId = ConfigUtils.getCurrentOrgId();
            WeIdPrivateKey privateKey = WeIdSdkUtils.getWeIdPrivateKey(hash);
            ResponseData<Boolean> result = WeIdSdkUtils.getDataBucket(CnsType.ORG_CONFING).
                    put(orgId, WeIdConstant.CNS_EVIDENCE_HASH + groupId, hash, privateKey);
            if (result.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(Boolean.FALSE, result.getErrorCode(), result.getErrorMessage());
            }
            result = WeIdSdkUtils.getDataBucket(CnsType.ORG_CONFING).put(orgId, WeIdConstant.CNS_EVIDENCE_ADDRESS + groupId, evidenceAddress, privateKey);
            if (result.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
                return new ResponseData<>(Boolean.FALSE, result.getErrorCode(), result.getErrorMessage());
            }
            // ĺ?Żç”¨ć–°hashĺą¶ĺ?śç”¨ĺŽźhash
            this.enableHash(CnsType.SHARE, hash, shareHashOld);
            log.info("[enableShareCns] enable the hash {} successFully.", hash);
        } catch (Exception e) {
            log.error("[enableShareCns] enable the hash error.", e);
            return new ResponseData<>(Boolean.FALSE, ErrorCode.BASE_ERROR.getCode(), "enable the hash error");
        }

        return new ResponseData<>(Boolean.TRUE, ErrorCode.SUCCESS);
    }

    // ĺ?¤ć–­ĺ˝“ĺ‰Ťćśşćž„é…Ťç˝®č·źĺ˝“ĺ‰Ťç§?é’Ąć?Żĺ?¦ĺŚąé…Ť
    private boolean isMatchThePrivateKey() {
        HashContract hashFromOrgIdCns = getHashFromOrgCns(ConfigUtils.getCurrentOrgId());
        // ĺ¦‚ćžśä¸Ťĺ­?ĺś¨ćśşćž„é…Ťç˝®ďĽŚĺ?™ĺŹŻä»ĄĺŚąé…Ť
        if (hashFromOrgIdCns == null) {
            log.info("[isMatchThePrivateKey] the orgId does not exist in orgConfig cns, default match.");
            return true;//ä¸Ťĺ­?ĺś¨
        }
        WeIdPrivateKey currentPrivateKey = WeIdSdkUtils.getCurrentPrivateKey();
        String publicKey = DataToolUtils.publicKeyFromPrivate(
                new BigInteger(currentPrivateKey.getPrivateKey())).toString();
        String address = "0x" + Keys.getAddress(new BigInteger(publicKey));
        if (address.equals(hashFromOrgIdCns.getOwner())) {
            log.info("[isMatchThePrivateKey] the orgId is exist in orgConfig cns, match the private key.");
            return true;//ĺ­?ĺś¨orgIdĺą¶ä¸”ä¸şĺ˝“ĺ‰Ťćśşćž„ć‰€ćś‰
        }
        log.info("[isMatchThePrivateKey] the orgId is exist, but misMatch the private key.");
        return false; //ĺ­?ĺś¨ćśşćž„idä¸Ťä¸şĺ˝“ĺ‰Ťćśşćž„ć‰€ćś‰ďĽŚç§?é’Ąä¸ŤĺŚąé…Ť
    }

    /**
     * ć ąćŤ®orgIdčŽ·ĺŹ–org_configé‡Śéť˘çš„hashć•°ćŤ®.
     * @param orgId ćśşćž„çĽ–ç ?
     * @return čż”ĺ›žhashĺŻąč±ˇäżˇć?Ż
     */
    private HashContract getHashFromOrgCns(String orgId) {
        org.fisco.bcos.web3j.precompile.cns.CnsInfo cnsInfo = BaseService.getBucketByCns(CnsType.ORG_CONFING);
        if (cnsInfo == null) {
            return null;
        }
        List<HashContract> allHash = WeIdSdkUtils.getDataBucket(CnsType.ORG_CONFING).getAllBucket().getResult();
        for (HashContract hashContract : allHash) {
            if (hashContract.getHash().equals(orgId)) {
                return hashContract;
            }
        }
        return null;
    }

    public String getEvidenceHash(String groupId) {
        org.fisco.bcos.web3j.precompile.cns.CnsInfo cnsInfo = BaseService.getBucketByCns(CnsType.ORG_CONFING);
        if (cnsInfo == null) {
            return StringUtils.EMPTY;
        }
        String orgId = ConfigUtils.getCurrentOrgId();
        return WeIdSdkUtils.getDataBucket(CnsType.ORG_CONFING).get(
                orgId, WeIdConstant.CNS_EVIDENCE_HASH + groupId).getResult();
    }

    public ResponseData<List<AuthorityIssuer>> getUserListByHash(String hash) {
        List<AuthorityIssuer> rList = new ArrayList<>();
        ResponseData<List<String>> responseData = WeIdSdkUtils.getDataBucket(CnsType.SHARE).getActivatedUserList(hash);
        if (responseData.getErrorCode() != ErrorCode.SUCCESS.getCode()) {
            return new ResponseData<>(rList, responseData.getErrorCode(), responseData.getErrorMessage());
        }
        for (String weIdAddress : responseData.getResult()) {
            AuthorityIssuer issuer = weIdSdkService.getIssuerByWeId(weIdAddress);
            if (issuer == null) {
                issuer = new AuthorityIssuer();
                issuer.setWeId(WeIdUtils.convertAddressToWeId(weIdAddress));
            }
            rList.add(issuer);
        }
        return new ResponseData<>(rList, ErrorCode.SUCCESS);
    }
}
