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

package com.webank.weid.command;

import java.math.BigInteger;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.fisco.bcos.web3j.abi.datatypes.generated.Bytes32;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.tuples.generated.Tuple2;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;

import com.beust.jcommander.JCommander;
import com.webank.weid.config.FiscoConfig;
import com.webank.weid.constant.CnsType;
import com.webank.weid.constant.WeIdConstant;
import com.webank.weid.contract.deploy.AddressProcess;
import com.webank.weid.contract.deploy.v2.RegisterAddressV2;
import com.webank.weid.contract.v2.DataBucket;
import com.webank.weid.protocol.base.WeIdPrivateKey;
import com.webank.weid.service.BaseService;
import com.webank.weid.service.fisco.WeServer;
import com.webank.weid.util.DataToolUtils;
import com.webank.weid.util.WeIdSdkUtils;

@Slf4j
public class RegisterEvidenceByGroup {

    public static void main(String[] args) {
        String goupIdStr = null;
        String cns = null;
        try {
            CommandArgs commandArgs = new CommandArgs();
            JCommander.newBuilder()
                .addObject(commandArgs)
                .build()
                .parse(args);
            // ćŁ€ćźĄčľ“ĺ…Ąçľ¤ç»„çĽ–ĺŹ·ć?Żĺ?¦ä¸şç©ş
            goupIdStr = commandArgs.getGroupId();
            if(StringUtils.isBlank(goupIdStr)) {
                System.out.println("[RegisterEvidenceByGroup] input error, the groupId is null. Abort.");
                System.exit(1);
            }
            // ćŁ€ćźĄčľ“ĺ…Ąçľ¤ç»„çĽ–ĺŹ·ć?Żĺ?¦ä¸şć•°ĺ­—
            if (!NumberUtils.isDigits(goupIdStr)) {
                System.out.println("[RegisterEvidenceByGroup] input error, the groupId does not digits. Abort.");
                System.exit(1);
            }
            // ćŁ€ćźĄčľ“ĺ…Ącns ĺś°ĺť€ć?Żĺ?¦ä¸şç©ş
            cns = commandArgs.getCns();
            if(StringUtils.isBlank(cns)) {
                System.out.println("[RegisterEvidenceByGroup] input error, the cns is null. Abort.");
                System.exit(1);
            }
            System.out.println("[RegisterEvidenceByGroup] begin register evidenceAddress by cns and groupId, cns = "+ cns + ", groupId = " + goupIdStr);
            // ćŁ€ćźĄçľ¤ç»„ć?Żĺ?¦ĺ­?ĺś¨
            int groupId = Integer.parseInt(goupIdStr);
            boolean checkGroupId = BaseService.checkGroupId(groupId);
            if (!checkGroupId) {
                System.out.println("[RegisterEvidenceByGroup] input error, the group does not exists, Abort.");
                System.exit(1);
            }
            // čŽ·ĺŹ–ĺ˝“ĺ‰Ťç§?é’Ąč´¦ć?·
            WeIdPrivateKey currentPrivateKey = WeIdSdkUtils.getCurrentPrivateKey();
            String  privatekey = currentPrivateKey.getPrivateKey();
            // ćŁ€ćźĄčľ“ĺ…Ącns ĺś°ĺť€ć?Żĺ?¦ć­Łçˇ®ĺ­?ĺś¨Evidenceĺś°ĺť€
            FiscoConfig fiscoConfig = WeIdSdkUtils.loadNewFiscoConfig();
            WeServer<?, ?, ?> weServer = WeServer.getInstance(fiscoConfig, groupId);
            Credentials credentials = GenCredential.create(new BigInteger(privatekey).toString(16));
            // ĺŠ č˝˝DataBucket
            DataBucket dataBucket = DataBucket.load(
                weServer.getBucketByCns(CnsType.DEFAULT).getAddress(), 
                (Web3j)weServer.getWeb3j(), 
                credentials, 
                new StaticGasProvider(WeIdConstant.GAS_PRICE, WeIdConstant.GAS_LIMIT)
            );
            Bytes32 keyByte32 = DataToolUtils.bytesArrayToBytes32(WeIdConstant.CNS_EVIDENCE_ADDRESS.getBytes());
            // ć ąćŤ®cns hashĺ€Ľä»ŽdataBucketä¸­čŽ·ĺŹ–evidenceAddress
            Tuple2<BigInteger, String> tuple = dataBucket.get(cns, keyByte32.getValue()).send();
            int code = tuple.getValue1().intValue();
            if (code == 102) {
                System.out.println("[RegisterEvidenceByGroup] can not find evidenceAddress by cns = " + cns + ", Abort.");
                System.exit(1);
            }
            String evidenceAddress = tuple.getValue2();
            System.out.println("[RegisterEvidenceByGroup] get the evidence address is: " + evidenceAddress);
            // ĺ°†ĺś°ĺť€ćł¨ĺ†Śĺ?°cnsä¸­
            CnsType cnsType = CnsType.SHARE;
            // ćł¨ĺ†ŚSHARE CNS é»?č®¤ä¸»çľ¤ç»„
            RegisterAddressV2.registerBucketToCns(cnsType, currentPrivateKey);
            // ć ąćŤ®çľ¤ç»„ĺ’Śevidence AddressčŽ·ĺŹ–hash
            String hash = AddressProcess.getHashForShare(groupId, evidenceAddress);
            // ĺ°†evidenceĺś°ĺť€ćł¨ĺ†Śĺ?°cnsä¸­ é»?č®¤ä¸»çľ¤ç»„
            RegisterAddressV2.registerAddress(
                cnsType, 
                hash, 
                evidenceAddress, 
                WeIdConstant.CNS_EVIDENCE_ADDRESS, 
                currentPrivateKey
            );
            // ĺ°†çľ¤ç»„çĽ–ĺŹ·ćł¨ĺ†Śĺ?°cnsä¸­ é»?č®¤ä¸»çľ¤ç»„
            RegisterAddressV2.registerAddress(
                cnsType, 
                hash, 
                String.valueOf(groupId), 
                WeIdConstant.CNS_GROUP_ID, 
                currentPrivateKey
            );
            System.out.println("[RegisterEvidenceByGroup] register address into cns by group has successfully.");
            System.exit(0);
        } catch (Exception e) {
            log.error(
                "[RegisterEvidenceByGroup] register address into cns by group has error. cns = {}, groupId = {}",
                cns, 
                goupIdStr,
                e
            );
            System.out.println("[RegisterEvidenceByGroup] register address into cns by group has error. please check the log.");
            System.exit(1);
        }
        
    }

}
