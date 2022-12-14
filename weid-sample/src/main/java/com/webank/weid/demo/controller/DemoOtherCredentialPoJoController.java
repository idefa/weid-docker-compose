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

package com.webank.weid.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.webank.weid.demo.common.model.CreateCredentialPojoModel;
import com.webank.weid.demo.common.model.CreatePresentationModel;
import com.webank.weid.demo.common.model.CreatePresentationPolicyEModel;
import com.webank.weid.demo.common.model.CreateSelectiveCredentialModel;
import com.webank.weid.demo.common.model.CredentialPoJoAddSignature;
import com.webank.weid.demo.common.model.GetCredentialHashModel;
import com.webank.weid.demo.common.model.VerifyCredentialPoJoModel;
import com.webank.weid.demo.service.DemoOtherService;
import com.webank.weid.protocol.base.CredentialPojo;
import com.webank.weid.protocol.base.PresentationE;
import com.webank.weid.protocol.base.PresentationPolicyE;
import com.webank.weid.protocol.response.ResponseData;

/**
 * Demo Controller.
 *
 * @author darwindu
 */
@RestController
@Api(description = "ç”µĺ­?ĺ‡­čŻ?ĺ…¶ä»–ç›¸ĺ…łćŽĄĺŹŁă€‚",
    tags = {"ĺ…¶ä»–ç›¸ĺ…łćŽĄĺŹŁ-CredentialPoJo"})
public class DemoOtherCredentialPoJoController {

    @Autowired
    private DemoOtherService demoOtherService;


    @ApiOperation(value = "äĽ ĺ…ĄCredentialäżˇć?Żç”źć??Credentialć•´ä˝“çš„Hashĺ€ĽďĽŚä¸€č?¬ĺś¨ç”źć??Evidenceć—¶č°?ç”¨ă€‚")
    @PostMapping("/step1/createCredentialPoJo")
    public ResponseData<CredentialPojo> createCredentialPoJo(
        @ApiParam(name = "createCredentialModel", value = "ç”µĺ­?ĺ‡­čŻ?ć¨ˇćťż")
        @RequestBody CreateCredentialPojoModel createCredentialPojoModel) {

        return demoOtherService.createCredentialPoJo(createCredentialPojoModel);
    }

    @ApiOperation(value = "é€ščż‡ĺŽźĺ§‹ĺ‡­čŻ?ĺ’ŚćŠ«ćĽŹç­–ç•ĄďĽŚĺ?›ĺ»şé€‰ć‹©ć€§ćŠ«éś˛çš„Credentială€‚")
    @PostMapping("/step2/createSelectiveCredential")
    public ResponseData<CredentialPojo> createSelectiveCredential(
        @ApiParam(name = "createSelectiveCredentialModel", value = "é€‰ć‹©ć€§ćŠ«éś˛ç”µĺ­?ĺ‡­čŻ?ć¨ˇćťż")
        @RequestBody CreateSelectiveCredentialModel createSelectiveCredentialModel) {

        return demoOtherService.createSelectiveCredential(createSelectiveCredentialModel);
    }

    @ApiOperation(value = "éŞŚčŻ?ç”µĺ­?ĺ‡­čŻ?ă€‚")
    @PostMapping("/step3/verifyEvidence")
    public ResponseData<Boolean> verify(
        @ApiParam(name = "verifyCredentialPoJoModel", value = "éŞŚčŻ?ç”µĺ­?ĺ‡­čŻ?ć¨ˇćťż")
        @RequestBody VerifyCredentialPoJoModel verifyCredentialPoJoModel) {

        return demoOtherService.verify(verifyCredentialPoJoModel);
    }

    @ApiOperation(value = "ĺ?›ĺ»şPresentationPolicyEă€‚")
    @PostMapping("/step4/createPresentationPolicyE")
    public ResponseData<PresentationPolicyE> createPresentationPolicyE(
        @ApiParam(name = "createPresentationModel", value = "ĺ?›ĺ»şPresentationć¨ˇćťż")
        @RequestBody CreatePresentationPolicyEModel createPresentationPolicyEModel) {

        return demoOtherService.createPresentationPolicyE(createPresentationPolicyEModel);
    }

    @ApiOperation(value = "ĺ?›ĺ»şPresentationă€‚")
    @PostMapping("/step5/createPresentation")
    public ResponseData<PresentationE> createPresentation(
        @ApiParam(name = "createPresentationModel", value = "ĺ?›ĺ»şPresentationć¨ˇćťż")
        @RequestBody CreatePresentationModel createPresentationModel) {

        return demoOtherService.createPresentation(createPresentationModel);
    }

    @ApiOperation(value = "äĽ ĺ…ĄCredentialPojoäżˇć?Żç”źć??CredentialPojoć•´ä˝“çš„Hashĺ€ĽďĽŚä¸€č?¬ĺś¨ç”źć??Evidenceć—¶č°?ç”¨ă€‚")
    @PostMapping("/step6/getCredentialPoJoHash")
    public ResponseData<String> getCredentialPoJoHash(
        @ApiParam(name = "getCredentialHashModel", value = "CredentialPojoć¨ˇćťż")
        @RequestBody GetCredentialHashModel getCredentialHashModel) {

        return demoOtherService.getCredentialPoJoHash(getCredentialHashModel);
    }

    @ApiOperation(value = "ĺ¤šç­ľďĽŚĺś¨ĺŽźĺ‡­čŻ?ĺ?—čˇ¨çš„ĺźşçˇ€ä¸ŠďĽŚĺ?›ĺ»şĺŚ…čŁąć??ä¸€ä¸Şć–°çš„ĺ¤šç­ľĺ‡­čŻ?ďĽŚç”±äĽ ĺ…Ąçš„ç§?é’Ąć‰€ç­ľĺ?Ťă€‚"
        + "ć­¤ĺ‡­čŻ?çš„CPTä¸şä¸€ä¸Şĺ›şĺ®šĺ€Ľă€‚ĺś¨éŞŚčŻ?ä¸€ä¸Şĺ¤šç­ľĺ‡­čŻ?ć—¶ďĽŚäĽščż­ä»ŁéŞŚčŻ?ĺ…¶ĺŚ…čŁąçš„ć‰€ćś‰ĺ­?ĺ‡­čŻ?ă€‚ćś¬ćŽĄĺŹŁä¸Ťć”ŻćŚ?ĺ?›ĺ»şé€‰ć‹©ć€§ćŠ«éś˛çš„ĺ¤šç­ľĺ‡­čŻ?ă€‚")
    @PostMapping("/step7/addSignatureCredentialPojo")
    public ResponseData<CredentialPojo> addSignatureCredentialPojo(
        @ApiParam(name = "credentialPoJoAddSignature", value = "CredentialPojoĺŠ ç­ľć¨ˇćťż")
        @RequestBody CredentialPoJoAddSignature credentialPoJoAddSignature) {

        return demoOtherService.addSignatureCredentialPojo(credentialPoJoAddSignature);
    }
}
