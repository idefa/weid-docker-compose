/*
 *       CopyrightÂ© (2019) WeBank Co., Ltd.
 *
 *       This file is part of weid-java-sdk.
 *
 *       weid-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weid-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weid-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.demo.command;

import java.util.concurrent.ConcurrentHashMap;

import com.webank.weid.suite.endpoint.EndpointDataUtil;
import com.webank.weid.suite.endpoint.EndpointFunctor;
import com.webank.weid.suite.endpoint.RpcServer;

/**
 * A sample class showing how to register a EndpointFunctor and register a handler with it.
 * Developer can also register his/her own service methods with a similar approach: firstly
 * implement the execute(), and then implement getDescription() by writing your own descriptions.
 * Finally, register it via EndpointHandler.
 *
 * @author chaoxinhu 2019.8
 */
public class EndpointSample {

    private static class DuplicateFunctor implements EndpointFunctor {

        @Override
        public String callback(String arg) {
            return arg + arg + arg;
        }

        @Override
        public String getDescription() {
            return "A sample method to duplicate the input";
        }
    }

    private static class DataAuthorizationFunctor implements EndpointFunctor {

        // The pass-in argument is a resource ID in authToken Credential. This functor must provide
        // a search mechanism with key being the resouece ID. Here, we use ConcurrentHashMap for
        // ease. It's recommended to use a DB / config / props based method for online-update.
        ConcurrentHashMap<String, String> resourceMap = new ConcurrentHashMap<>();

        @Override
        public String callback(String resourceId) {
            if (!resourceMap.containsKey(resourceId)) {
                return "Cannot find this resource: " + resourceId;
            }
            return resourceMap.get(resourceId);
        }

        @Override
        public String getDescription() {
            return "A sample method to fetch authorized data";
        }
    }

    /**
     * This piece of code is a simple sample showing how to register and boot-up the RPC server.
     *
     * @param args input args (omitted)
     * @throws Exception interrupted exception
     */
    public static void main(String[] args) throws Exception {
        // Firstly, clear the existing endpoints
        EndpointDataUtil.clearProps();

        // Register a duplicate endpoint
        EndpointFunctor functor1 = new DuplicateFunctor();
        String requestName1 = "duplicate-input";
        RpcServer.registerEndpoint(requestName1, functor1, null);

        // Register a data-authorization fetch endpoint
        EndpointFunctor functor2 = new DataAuthorizationFunctor();

        /*
         As an issuer to authorize resource, firstly you will need to register an authorization
         token from a CPT101 credential authToken. Firstly, you will need to fill-in your exposed
         endpoint into the credential claim's serviceUrl segment in the first placeďĽŚ and issue
         the CPT101 authToken credential.
         Then, you need to establish the endpoint service to enable the incoming traffic to your
         service URL. Here, the registered endpoint name MUST be the PATH segment of the serviceUrl
         and the HOST:PORT segment of the serviceUrl must be registered beforehand - this must be
         strictly the same as in the REST-service side's application.properties.

         See an example:
         Suppose the serviceUrl is http://127.0.0.1:6010/fetch-data, then endpoint name here must
         be registered as "fetch-data", and "127.0.0.1:6010" must be put in REST-service.
         Therefore, the whole workflow is like:
          0) SDK side fills-in logic to handle the resourceId lookup and reply in callback functor
          1) SDK side calls registerEndpoint() to register the functor with the endpoint name as
             "fetch-data", and then puts 6010 in weidentity.properties
          2) endpoint REST side puts 127.0.0.1:6010 in application.properties
          3) Boot up both REST service and SDK's RPCServer.main()
          4) endpoint REST side fetches endpoints (including "fetch-data") from SDK
             side (127.0.0.1:6010)
          5) endpoint REST side receives an authToken credential with claimed serviceUrl as
             http://127.0.0.1:6010/fetch-data
          6) endpoint REST side will extract HOST:PORT (127.0.0.1), and search the term (i.e.
             127.0.0.1:6010) in local endpoints info. Note: if a different port than 6010 is
             used, serviceUrl must clearly specify it e.g. http://127.0.0.1:6011/fetch-data
          7) endpoint REST side will also search the requestName with key "fetch-data" in its local
             endpoints info.
          8) endpoint REST side finds the endpoint, so an RPC call is sent to 127.0.0.1:6010,
             piggybacking the resourceId.
          9) the SDK side receives the RPC call and invoke the execute() to return authorized data.

          ä¸­ć–‡ç‰?ďĽš
          ĺ?‡č®ľć‚¨ć?ŻIssuerć?łč¦?ćŽ?ćť?ĺ?«äşşč®żé—®ć‚¨çš„čµ„ćş?ďĽŚć‚¨é¦–ĺ…?éś€č¦?č°?ç”¨createDataAuthToken()ćŽĄĺŹŁďĽŚĺ?›ĺ»şä¸€ä¸ŞCPT101
          ćŽ?ćť?ĺ‡­čŻ?ă€‚ĺś¨čż™ä¸ŞćŽ?ćť?ĺ‡­čŻ?çš„Claiméˇąé‡ŚďĽŚčŻ·č®˛ć‚¨éś€č¦?ćš´éś˛çš„ćśŤĺŠˇç«Żç‚ąäżˇć?Żĺˇ«ĺ…Ąĺ‡­čŻ?çš„Claiméˇąçš„serviceUrlĺ†…ďĽŚ
          ç„¶ĺ?Žä˝żç”¨Issuerçš„ç§?é’Ąĺ?›ĺ»şčż™ä¸Şĺ‡­čŻ?ă€‚
          ćŽĄä¸‹ćťĄďĽŚä¸şäş†č®©ĺ?«äşşč?˝ĺ¤źč®żé—®ć‚¨çš„ćśŤĺŠˇç«Żç‚ąďĽŚć‚¨éś€č¦?ć?­ĺ»şć‚¨çš„endpointćśŤĺŠˇäľ§ă€‚ĺ°†ç«Żç‚ąĺ’Śĺ…ĄĺŹŁĺś°ĺť€çš„čŻ¦ć?…ĺˇ«ĺ…Ąă€‚
          éś€č¦?ćł¨ĺ†Śçš„ç«Żç‚ąĺ?ŤďĽŚĺż…éˇ»ć?Żć‚¨ĺ¸Śćś›ćš´éś˛çš„Service URLçš„PATHé?¨ĺ?†ďĽ›č€ŚServiceURLçš„HOST:PORTé?¨ĺ?†ĺż…éˇ»č¦?ćŹ?ĺ‰Ť
          ä»Ąĺ…ĄĺŹŁä¸»ćśşĺ’Śç«ŻĺŹŁçš„ć–ąĺĽŹćł¨ĺ†ŚĺĄ˝ďĽŚä¸”éś€č¦?ĺ’Śĺś¨application.propertiesé‡ŚĺŁ°ć?Žçš„ĺ®Śĺ…¨ä¸€č‡´ă€‚
          ä»Ąä¸‹ć?Żä¸€ä¸Şäľ‹ĺ­?ďĽš
          ĺ?‡č®ľďĽŚć‚¨ĺ¸Śćś›č®©ĺ¤–é?¨äşşĺ‘?č®żé—®çš„Service URLä¸şhttp://127.0.0.1:6010/fetch-dataă€‚ć ąćŤ®HTTP URLçš„č®żé—®
          č§„ĺ?™ďĽŚć‚¨çš„ç«Żç‚ąĺż…éˇ»ĺ’ŚPATHä¸€č‡´ďĽŚćł¨ĺ†Śä¸şâ€śfetch-dataâ€ťďĽ›ç„¶ĺ?ŽďĽŚHOST:PORTé?¨ĺ?†ĺż…éˇ»č¦?ĺś¨REST-serviceçš„é…Ťç˝®
          application.propertiesé‡ŚćŹ?ĺ‰Ťćł¨ĺ†Śă€‚čż™ć ·ďĽŚć•´ä˝“ä¸šĺŠˇćµ?ĺ¦‚ä¸‹ďĽš
          0) SDKäľ§éś€č¦?ĺ°†resourceIdçš„ĺ¤„ç?†ć–ąĺĽŹĺś¨functoré‡Śćł¨ĺ†Śĺ®Ść??
          1) SDKäľ§č°?ç”¨registerEndpoint()ć–ąćł•ćł¨ĺ†ŚfunctorďĽŚç«Żç‚ąĺ?Ťä¸şâ€śfetch-dataâ€ťďĽŚç„¶ĺ?Žĺ°†ĺĽ€ć”ľçš„ç«ŻĺŹŁ6010ĺ†™ĺ…Ą
             weidentity.properties
          2) RESTćśŤĺŠˇç«Żĺ°†127.0.0.1:6010ĺˇ«ĺ…Ąapplication.properteis
          3) ĺ?ŻĺŠ¨RESTćśŤĺŠˇç«Żĺ’ŚSDKäľ§çš„RPCServer.main()
          4) RESTäľ§ĺ?ŽĺŹ°č‡ŞĺŠ¨čŻ»ĺŹ–application.propertiesďĽŚć‹‰ĺŹ–ä˝ŤäşŽ127.0.0.1:6010çš„ć‰€ćś‰ç«Żç‚ą
          5) RESTäľ§ć”¶ĺ?°äş†éŞŚčŻ?ćŽ?ćť?ĺ‡­čŻ?çš„POSTčŻ·ć±‚ä¸”Claimé‡Śçš„ServiceUrlä¸şhttp://127.0.0.1/fetch-data
          6) RESTäľ§ĺ?ŽĺŹ°ĺ°†ServiceUrlçš„HOST:PORTéˇą(127.0.0.1:6010)ćŠ˝ĺŹ–ĺ‡şćťĄďĽŚç„¶ĺ?Žĺś¨ćś¬ĺś°çš„ç«Żç‚ąäżˇć?Żé‡ŚćźĄčŻ˘
          7) RESTäľ§ĺ?ŽĺŹ°äĽšĺ?Ść ·ć?śç´˘â€śfetch-dataâ€ťčż™ä¸Şç«Żç‚ąć?Żĺ?¦äąźĺ·˛č˘«ćł¨ĺ†Śĺ®Ść??
          8) RESTäľ§ĺ?ŽĺŹ°ć‰ľĺ?°äş†čż™ä¸Şç«Żç‚ąă€‚ĺ®?äĽšĺ?‘127.0.0.1:6010ďĽ?SDKäľ§ďĽ‰ĺŹ‘é€?ä¸€ä¸ŞRPCčŻ·ć±‚ďĽŚĺą¶ĺ°†ĺ‡­čŻ?ä¸­Claimçš„
             resourceIdäąźćŤŽĺ¸¦ĺ?°SDKäľ§čż‡ĺŽ»ă€‚
          9) SDKäľ§äĽšć”¶ĺ?°čż™RPCčŻ·ć±‚ďĽŚĺą¶č§¦ĺŹ‘0)ć­Ąä¸­ćł¨ĺ†Śçš„functorçš„execute()ďĽŚč§Łćž?resourceIdĺą¶čż”ĺ›žćŽ?ćť?ć•°ćŤ®ă€‚
        */
        RpcServer.registerEndpoint("fetch-data", functor2, null);

        // Start the RPC server instance
        RpcServer.run();
        // You can also try to add endpoint here too after it is booted and running.
        //RpcServer.registerEndpoint("fetch-data-backup", functor2, null);
    }
}
