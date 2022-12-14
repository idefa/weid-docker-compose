/*
 *       CopyrightÂ© (2019-2020) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-sample.
 *
 *       weidentity-sample is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-sample is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-sample.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.demo.command;

import java.util.HashMap;
import java.util.Map;

import com.webank.weid.constant.CredentialType;
import com.webank.weid.constant.ProcessingMode;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.CredentialPojoList;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.base.PublicKeyProperty;
import com.webank.weid.protocol.base.WeIdAuthentication;
import com.webank.weid.protocol.base.WeIdDocument;
import com.webank.weid.protocol.request.CreateCredentialPojoArgs;
import com.webank.weid.protocol.response.CreateWeIdDataResult;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.rpc.CredentialPojoService;
import com.webank.weid.rpc.EvidenceService;
import com.webank.weid.rpc.WeIdService;
import com.webank.weid.service.impl.CredentialPojoServiceImpl;
import com.webank.weid.service.impl.EvidenceServiceImpl;
import com.webank.weid.service.impl.WeIdServiceImpl;
import com.webank.weid.suite.api.crypto.CryptoServiceFactory;
import com.webank.weid.suite.api.crypto.params.CryptoType;

public class MultiGroupEvidenceSample {

    static WeIdService weidService = new WeIdServiceImpl();

    static CredentialPojoService credentialPojoService = new CredentialPojoServiceImpl();

    static EvidenceService evidenceService = new EvidenceServiceImpl(ProcessingMode.IMMEDIATE, 2);

    /**
     * Demočż‡ç¨‹čŻ´ć?Ž
     * 1. ĺ?›ĺ»şweid
     * 2. ĺ?›ĺ»şlite Credential1 ĺ?›ĺ»şlite Credential2
     * 3. ĺ°†ä¸¤ä¸ŞCredentialć”ľĺ…ĄCredentialPojoListä¸­
     * 4. CredentialPojoListč˝¬json
     * 5. CredentialPojoList json ĺŠ ĺŻ†
     * 6. CredentialPojoList json č§ŁĺŻ†
     * 7. CredentialPojoList json č˝¬ CredentialPojoList
     * 8. éŞŚčŻ?Credential
     * 9. ć ąćŤ®Credentialĺ?›ĺ»şEvidence
     * 10. čŽ·ĺŹ–ĺ­?čŻ?ć•°ćŤ®
     * 11. éŞŚčŻ?ĺ­?ĺś¨ć•°ćŤ®
     * 
     * @param args ĺ…ĄĺŹ‚
     */
    public static void main(String[] args) {
        // 1. ĺ?›ĺ»şweid
        ResponseData<CreateWeIdDataResult> createWeIdRes = weidService.createWeId();
        System.out.println("ĺ?›ĺ»şweidç»“ćžśďĽš" + createWeIdRes);
        CreateWeIdDataResult createWeId = createWeIdRes.getResult();
        // çś?ç•Ąčż”ĺ›žcodeĺ?¤ć–­
       
        // 2. ĺ?›ĺ»şlite Credential
        ResponseData<WeIdDocument> weIdDocumentRes = 
            weidService.getWeIdDocument(createWeId.getWeId());
        String publicKeyId = null;
        for (PublicKeyProperty publicKey : weIdDocumentRes.getResult().getPublicKey()) {
            if (publicKey.getOwner().equals(createWeId.getWeId())) {
                publicKeyId = publicKey.getId();
                break;
            }
        }
        // ćž„é€ WeIdAuthentication
        WeIdAuthentication weIdAuthentication = buildWeIdAuthority(createWeId, publicKeyId);
        CreateCredentialPojoArgs<Map<String, Object>> createArgs = 
            buildCreateCredentialPojoArgs(weIdAuthentication); 
        ResponseData<CredentialPojo> createCredential1 =
            credentialPojoService.createCredential(createArgs);
        System.out.println("lite Credential1 ĺ?›ĺ»şç»“ćžś:" + createCredential1);
        ResponseData<CredentialPojo> createCredential2 = 
            credentialPojoService.createCredential(createArgs);
        System.out.println("lite Credential2 ĺ?›ĺ»şç»“ćžś:" + createCredential2);
        // çś?ç•Ąčż”ĺ›žcodeĺ?¤ć–­

        // 3.ĺ°†CredentialPojoć”ľĺ…ĄCredentialPojoListä¸­
        CredentialPojoList credentialPojoList = new CredentialPojoList();
        credentialPojoList.add(createCredential1.getResult());
        credentialPojoList.add(createCredential2.getResult());
        
        // 4. CredentialPojoListč˝¬json
        String credentialListJson = credentialPojoList.toJson();
        System.out.println("CredentialPojoList č˝¬Json ç»“ćžś:" + credentialListJson);
        
        // 5. ĺŠ ĺŻ†Credential Json
        String encrypt = CryptoServiceFactory.getCryptoService(CryptoType.ECIES).encrypt(
            credentialListJson, createWeId.getUserWeIdPublicKey().getPublicKey());
        System.out.println("credentialList Json ĺŠ ĺŻ†ç»“ćžś:" + encrypt);
        
        // 6. č§ŁĺŻ† Credential Json
        String decrypt = CryptoServiceFactory.getCryptoService(CryptoType.ECIES).decrypt(
            encrypt, createWeId.getUserWeIdPrivateKey().getPrivateKey());
        System.out.println("credentialList Json č§ŁĺŻ†ç»“ćžś:" + decrypt);
        
        // 7. Credential Json č˝¬Credential
        CredentialPojoList credential = CredentialPojoList.fromJson(credentialListJson);
        System.out.println("č§ŁĺŻ†ĺ?Žçš„credentialList Json č˝¬ credentialListç»“ćžśďĽš" + credential);
        
        for (CredentialPojo credentialPojo : credential) {
            // 8. éŞŚčŻ?Credential
            ResponseData<Boolean> verifyRes = 
                credentialPojoService.verify(createWeId.getWeId(), credentialPojo);
            System.out.println("Credential éŞŚčŻ?ç»“ćžśďĽš" + verifyRes);
            // çś?ç•Ąčż”ĺ›žcodeĺ?¤ć–­
            
            // 9. ĺ?›ĺ»şEvidence
            ResponseData<String> createEvidence = 
                evidenceService.createEvidence(credentialPojo, createWeId.getUserWeIdPrivateKey());
            System.out.println("ĺ­?čŻ?ĺ?›ĺ»şç»“ćžś:" + createEvidence);
            // çś?ç•Ąčż”ĺ›žcodeĺ?¤ć–­
            
            // 10. čŽ·ĺŹ–ĺ­?čŻ?
            ResponseData<EvidenceInfo> evidenceRes = 
                evidenceService.getEvidence(createEvidence.getResult());
            System.out.println("čŽ·ĺŹ–ĺ­?čŻ?ç»“ćžśďĽš" + evidenceRes);
            // çś?ç•Ąčż”ĺ›žcodeĺ?¤ć–­
            
            // 11. éŞŚčŻ?ĺ­?čŻ?
            //  ResponseData<Boolean> verifySigner =
            //      evidenceService.verifySigner(evidenceRes.getResult(), createWeId.getWeId());
            ResponseData<Boolean> verifySigner = evidenceService.verifySigner(
                credentialPojo, evidenceRes.getResult(), createWeId.getWeId());
            System.out.println("ĺ­?čŻ?éŞŚčŻ?ç»“ćžś:" + verifySigner);
            // çś?ç•Ąčż”ĺ›žcodeĺ?¤ć–­
        }
    }
    
    /**
     * ćž„ĺ»şĺ‡­čŻ?ĺ?›ĺ»şĺŹ‚ć•°.
     * @param weIdAuthentication weIdčş«ä»˝äżˇć?Ż
     * @return čż”ĺ›žĺ‡­čŻ?ĺ?›ĺ»şĺŹ‚ć•°
     */
    public static CreateCredentialPojoArgs<Map<String, Object>> buildCreateCredentialPojoArgs(
        WeIdAuthentication weIdAuthentication
    ) {
        CreateCredentialPojoArgs<Map<String, Object>> createArgs = new CreateCredentialPojoArgs<>();
        createArgs.setIssuer(weIdAuthentication.getWeId());
        createArgs.setExpirationDate(
            System.currentTimeMillis() + (1000 * 60 * 60 * 24));
        createArgs.setWeIdAuthentication(weIdAuthentication);
        Map<String, Object> claimMap = new HashMap<String, Object>();
        claimMap.put("name", "zhang san");
        claimMap.put("gender", "F");
        claimMap.put("age", 23);
        createArgs.setClaim(claimMap);
        createArgs.setCptId(1000);
        createArgs.setType(CredentialType.LITE1);
        return createArgs;
    }

    /**
     * build weId authority.
     */
    public static WeIdAuthentication buildWeIdAuthority(
        CreateWeIdDataResult createWeId, 
        String publicKeyId
    ) {
        return new WeIdAuthentication(
            createWeId.getWeId(), 
            createWeId.getUserWeIdPrivateKey().getPrivateKey(),
            publicKeyId
        );
    }
}