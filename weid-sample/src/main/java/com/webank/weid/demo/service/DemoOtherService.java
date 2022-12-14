/*
 *       CopyrightÂ© (2019) WeBank Co., Ltd.
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

package com.webank.weid.demo.service;

import com.webank.weid.demo.common.model.AddSignatureModel;
import com.webank.weid.demo.common.model.CreateCredentialPojoModel;
import com.webank.weid.demo.common.model.CreateEvidenceModel;
import com.webank.weid.demo.common.model.CreatePresentationModel;
import com.webank.weid.demo.common.model.CreatePresentationPolicyEModel;
import com.webank.weid.demo.common.model.CreateSelectiveCredentialModel;
import com.webank.weid.demo.common.model.CredentialPoJoAddSignature;
import com.webank.weid.demo.common.model.GetCredentialHashModel;
import com.webank.weid.demo.common.model.JsonTransportationSerializeModel;
import com.webank.weid.demo.common.model.JsonTransportationSpecifyModel;
import com.webank.weid.demo.common.model.SetHashValueModel;
import com.webank.weid.demo.common.model.VerifyCredentialModel;
import com.webank.weid.demo.common.model.VerifyCredentialPoJoModel;
import com.webank.weid.demo.common.model.VerifyEvidenceModel;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.EvidenceInfo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.response.ResponseData;

/**
 * demo interface.
 * 
 * @author v_wbgyang
 *
 */
public interface DemoOtherService {

    /**
     * get credential hash.
     *
     * @param verifyCredentialModel éŞŚčŻ?ç”µĺ­?ĺ‡­čŻ?ć¨ˇćťż
     * @return returns the create hash
     */
    ResponseData<String> getCredentialHash(VerifyCredentialModel verifyCredentialModel);

    /**
     * ĺ?›ĺ»şç”µĺ­?ĺ‡­čŻ?.
     * @param createCredentialPojoModel ĺ?›ĺ»şç”µĺ­?ĺ‡­čŻ?ć¨ˇćťż
     * @return
     */
    ResponseData<CredentialPojo> createCredentialPoJo(
        CreateCredentialPojoModel createCredentialPojoModel);


    /**
     * é€ščż‡ĺŽźĺ§‹ĺ‡­čŻ?ĺ’ŚćŠ«ćĽŹç­–ç•ĄďĽŚĺ?›ĺ»şé€‰ć‹©ć€§ćŠ«éś˛çš„Credential.
     * @param createSelectiveCredentialModel ĺ?›é€ é€‰ć‹©ć€§ćŠ«éś˛ć¨ˇćťż
     * @return
     */
    ResponseData<CredentialPojo> createSelectiveCredential(
        CreateSelectiveCredentialModel createSelectiveCredentialModel);

    /**
     * éŞŚčŻ?credential.
     * @param verifyCredentialPoJoModel éŞŚčŻ?ç”µĺ­?ĺ‡­čŻ?ć¨ˇćťż
     * @return
     */
    ResponseData<Boolean> verify(VerifyCredentialPoJoModel verifyCredentialPoJoModel);


    /**
     * ĺ?›ĺ»şPresentationPolicyEModel.
     * @param createPresentationPolicyEModel ĺ?›ĺ»şPresentationPolicyć¨ˇćťż
     * @return
     */
    ResponseData<PresentationPolicyE> createPresentationPolicyE(
        CreatePresentationPolicyEModel createPresentationPolicyEModel);


    /**
     * čŽ·ĺŹ–ç”µĺ­?ĺ‡­čŻ?hash.
     * @param getCredentialHashModel čŽ·ĺŹ–ç”µĺ­?ĺ‡­čŻ?hashć¨ˇćťż
     * @return
     */
    ResponseData<String> getCredentialPoJoHash(GetCredentialHashModel getCredentialHashModel);

    /**
     * ĺ?›ĺ»şPresentation.
     * @param createPresentationModel ĺ?›ĺ»şPresentationć¨ˇćťż
     * @return
     */
    ResponseData<PresentationE> createPresentation(
        CreatePresentationModel createPresentationModel);

    /**
     * ĺ¤šç­ľďĽŚĺś¨ĺŽźĺ‡­čŻ?ĺ?—čˇ¨çš„ĺźşçˇ€ä¸ŠďĽŚĺ?›ĺ»şĺŚ…čŁąć??ä¸€ä¸Şć–°çš„ĺ¤šç­ľĺ‡­čŻ?ďĽŚç”±äĽ ĺ…Ąçš„ç§?é’Ąć‰€ç­ľĺ?Ťă€‚ć­¤ĺ‡­čŻ?çš„CPTä¸şä¸€ä¸Şĺ›şĺ®šĺ€Ľ.
     * ĺś¨éŞŚčŻ?ä¸€ä¸Şĺ¤šç­ľĺ‡­čŻ?ć—¶ďĽŚäĽščż­ä»ŁéŞŚčŻ?ĺ…¶ĺŚ…čŁąçš„ć‰€ćś‰ĺ­?ĺ‡­čŻ?ă€‚ćś¬ćŽĄĺŹŁä¸Ťć”ŻćŚ?ĺ?›ĺ»şé€‰ć‹©ć€§ćŠ«éś˛çš„ĺ¤šç­ľĺ‡­čŻ?.
     *
     * @param credentialPoJoAddSignature ç”µĺ­?ĺ‡­čŻ?ĺŠ ç­ľć¨ˇćťż
     * @return
     */
    ResponseData<CredentialPojo> addSignatureCredentialPojo(
        CredentialPoJoAddSignature credentialPoJoAddSignature);

    /**
     * ĺ?›ĺ»şĺ­?čŻ?.
     * @param createEvidenceModel ĺ?›ĺ»şĺ­?čŻ?ć¨ˇćťż
     * @return
     */
    ResponseData<String> createEvidence(CreateEvidenceModel createEvidenceModel);


    /**
     * ć ąćŤ®äĽ ĺ…Ąçš„ĺ‡­čŻ?ĺ­?čŻ?ĺś°ĺť€ďĽŚĺś¨é“ľä¸ŠćźĄć‰ľĺ‡­čŻ?ĺ­?čŻ?äżˇć?Ż.
     * @param evidenceAddress ĺ­?čŻ?ĺś°ĺť€
     * @return
     */
    ResponseData<EvidenceInfo> getEvidence(String evidenceAddress);

    /**
     * ćŚ‡ĺ®štransportationçš„č®¤čŻ?č€…,ç”¨äşŽćť?é™?ćŽ§ĺ?¶.
     * @param jsonTransportationSpecifyModel ĺŹ‚ć•°ć¨ˇćťż
     * @return
     */
    ResponseData<String> specify(JsonTransportationSpecifyModel jsonTransportationSpecifyModel);


    /**
     * ç”¨äşŽĺşŹĺ?—ĺŚ–ĺŻąč±ˇ,č¦?ć±‚ĺŻąč±ˇĺ®žçŽ°JsonSerializerćŽĄĺŹŁ.
     * @param jsonTransportationSerializeModel ĺŹ‚ć•°ć¨ˇćťż
     * @return
     */
    ResponseData<String> serialize(
        JsonTransportationSerializeModel jsonTransportationSerializeModel);
}
