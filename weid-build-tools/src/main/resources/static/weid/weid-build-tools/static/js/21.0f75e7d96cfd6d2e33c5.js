webpackJsonp([21],{Ffpd:function(t,e,i){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var s=i("/p82"),a={data:function(){return{roleType:localStorage.getItem("roleType"),createType:1,accountForm:{account:""},dialog:{dialogVisible:!1,dialogUserListVisible:!1,fileForm:{privateKeyFile:"",privateKeyFileName:""},userListPage:{userList:[],pageIndex:1,pageSize:5,total:0,selectedRow:null}},isExistsAdmin:!1,title:"创建管理员WeID",useWeBase:!1}},methods:{active:function(t){this.createType=t},createAdmin:function(){var t=this,e=new FormData;if(2===this.createType){if(""===this.dialog.fileForm.privateKeyFile)return void this.$alert("请选择私钥文件!","温馨提示",{}).catch(function(){});e.append("ecdsa",this.dialog.fileForm.privateKeyFile)}s.a.doPostAndUploadFile("createAdmin",e).then(function(e){0===e.data.errorCode?(t.$alert("账户创建成功!","温馨提示",{}).catch(function(){}),t.init()):t.$alert("账户创建失败!","温馨提示",{}).catch(function(){})})},createAdminByWeBase:function(){var t=this,e={};e.userId=this.dialog.userListPage.selectedRow.userId,s.a.doPost("webase/createAdmin",e).then(function(e){0===e.data.errorCode?(t.$alert("账户启用成功!","温馨提示",{}).catch(function(){}),t.dialog.dialogUserListVisible=!1,t.init()):t.$alert("账户启用失败!","温馨提示",{}).catch(function(){})})},setGuideStatus:function(){var t=this;s.a.doPost("setGuideStatus",{step:"5"}).then(function(e){localStorage.setItem("step",""),t.$router.push({name:"deployWeId"})})},next:function(){var t=this;this.isExistsAdmin?"2"===this.roleType?this.setGuideStatus():(localStorage.setItem("step",5),this.$router.push({name:"step5"})):3===this.createType?this.queryUserList():2===this.createType?this.dialog.dialogVisible=!0:this.$confirm("请确认，系统将自动为管理员的 WeID 创建公私钥?","温馨提示",{closeOnClickModal:!1,cancelButtonClass:"el-button--primary",dangerouslyUseHTMLString:!0}).then(function(e){t.createAdmin()}).catch(function(){})},prev:function(){localStorage.setItem("step",3),this.$router.push({name:"step3"})},chooseFile:function(t){var e=this,i=document.getElementById(t);i.value="",i.click(),i.onchange=function(t){t.target.files[0]&&(e.dialog.fileForm.privateKeyFileName=t.target.files[0].name,e.dialog.fileForm.privateKeyFile=t.target.files[0])}},chooseUser:function(){var t=this;if(null!==this.dialog.userListPage.selectedRow){var e=this.dialog.userListPage.selectedRow.address;this.$confirm("确定使用["+e+"]作为WeID的账户吗?","温馨提示",{closeOnClickModal:!1,cancelButtonClass:"el-button--primary",dangerouslyUseHTMLString:!0}).then(function(e){t.createAdminByWeBase()}).catch(function(){})}else this.$alert("请选择用户!","温馨提示",{}).catch(function(){})},init:function(){var t=this;s.a.doGet("checkAdmin").then(function(e){""!==e.data.result?(t.isExistsAdmin=!0,t.title="当前管理员的 WeID 已经存在（目前不支持修改）",t.accountForm.account=e.data.result):(t.isExistsAdmin=!1,t.title="创建管理员WeID",t.loadConfig())}),this.dialog.dialogVisible=!1},loadConfig:function(){var t=this;s.a.doGet("loadConfig").then(function(e){0===e.data.errorCode&&(t.useWeBase=JSON.parse(e.data.result.useWeBase))})},indexChange:function(t){this.dialog.userListPage.pageIndex=t,this.queryUserList()},queryUserList:function(){var t=this,e={};e.pageIndex=this.dialog.userListPage.pageIndex,e.pageSize=this.dialog.userListPage.pageSize,s.a.doGet("webase/queryUserList",e).then(function(e){0===e.data.errorCode&&(t.dialog.userListPage.userList=e.data.result.dataList,t.dialog.userListPage.total=e.data.result.allCount,t.dialog.dialogUserListVisible=!0),t.dialog.userListPage.selectedRow=null})}},mounted:function(){this.checkStep(),this.init()}},l={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"app_view_content"},[s("div",{staticClass:"app_view_register"},[s("section",{staticClass:"content"},[s("div",{staticClass:"guide_step_part box"},[s("div",{staticClass:"bottom_line"}),t._v(" "),t._m(0),t._v(" "),t._m(1),t._v(" "),t._m(2),t._v(" "),t._m(3),t._v(" "),"1"==t.roleType?s("div",{staticClass:"guide_step_item"},[s("span",[t._v("5")]),t._v(" "),s("img",{attrs:{src:i("LyBL"),alt:""}}),t._v(" "),s("p",{staticStyle:{width:"200px",left:"-89px"}},[t._v("部署WeIdentity智能合约")])]):t._e()]),t._v(" "),s("div",{staticClass:"container-fluid"},[s("div",{staticClass:"box"},[s("div",{staticClass:"card card-primary warning_box",attrs:{id:"AccountDiv"}},[s("div",{staticClass:"card-header"},[s("h3",[t._v(t._s(t.title))])]),t._v(" "),t._m(4),t._v(" "),s("div",{staticClass:"card-body",staticStyle:{"margin-top":"15px"}},[s("el-form",{ref:"accountForm",attrs:{model:t.accountForm}},[!1===t.isExistsAdmin?s("div",{staticClass:"form-group",attrs:{id:"createDiv"}},[s("div",{class:{"key_item active_key":1===t.createType,key_item:1!==t.createType},attrs:{type:"1"},on:{click:function(e){return t.active(1)}}},[s("span",{staticClass:"item_out_role"},[s("span")]),t._v(" "),s("p",[t._v("系统自动创建公私钥")])]),t._v(" "),s("div",{class:{"key_item active_key":2===t.createType,key_item:2!==t.createType},attrs:{type:"1"},on:{click:function(e){return t.active(2)}}},[s("span",{staticClass:"item_out_role"},[s("span")]),t._v(" "),s("p",[t._v("自行上传私钥")])]),t._v(" "),t.useWeBase?s("div",{class:{"key_item active_key":3===t.createType,key_item:3!==t.createType},attrs:{type:"3"},on:{click:function(e){return t.active(3)}}},[s("span",{staticClass:"item_out_role"},[s("span")]),t._v(" "),s("p",[t._v("WeBASE同步账户")])]):t._e()]):t._e(),t._v(" "),!0===t.isExistsAdmin?s("div",{staticClass:"form-group",attrs:{id:"accountDiv"}},[s("el-form-item",{attrs:{prop:"account"}},[s("el-input",{staticStyle:{width:"100%"},attrs:{readOnly:""},model:{value:t.accountForm.account,callback:function(e){t.$set(t.accountForm,"account",e)},expression:"accountForm.account"}})],1)],1):t._e(),t._v(" "),s("div",{staticClass:"bt-part",attrs:{id:"nextDiv"}},[s("el-button",{staticClass:"btn btn_150",attrs:{type:"primary"},on:{click:t.prev}},[t._v("上一步")]),t._v(" "),s("el-button",{staticClass:"btn btn_150",attrs:{type:"primary"},on:{click:t.next}},[t._v("下一步")])],1)]),t._v(" "),t._m(5)],1)])])]),t._v(" "),s("el-dialog",{staticClass:"dialog-view",attrs:{title:"自行上传私钥",width:"30%",visible:t.dialog.dialogVisible,"close-on-click-modal":!1},on:{"update:visible":function(e){return t.$set(t.dialog,"dialogVisible",e)}}},[s("div",{staticClass:"dialog-body"},[s("el-form",{attrs:{model:t.dialog.fileForm}},[s("div",{staticClass:"file_part",staticStyle:{"margin-top":"15px"}},[s("el-form-item",[s("div",{staticClass:"mark-text"},[s("span",[t._v("备注：支持椭圆曲线的公私钥，并且为十进制私钥数据。")]),t._v(" "),s("a",{attrs:{href:"https://weidentity.readthedocs.io/zh_CN/latest/docs/weidentity-quick-tools-web.html?highlight=创建密钥#weid",target:"blank_"}},[s("img",{staticClass:"icon_question",attrs:{src:i("JaJV"),alt:""}}),t._v(" "),s("span",{staticClass:"icon_question",staticStyle:{color:"#017CFF","font-size":"12px",display:"inline-block"}},[t._v("如何生成私钥？")])])]),t._v(" "),s("div",{staticClass:"input_item",staticStyle:{"margin-bottom":"-8px"}},[s("el-input",{staticStyle:{width:"75%"},attrs:{placeholder:"请选择私钥文件",maxlength:"30",readOnly:""},model:{value:t.dialog.fileForm.privateKeyFileName,callback:function(e){t.$set(t.dialog.fileForm,"privateKeyFileName",e)},expression:"dialog.fileForm.privateKeyFileName"}}),t._v(" "),s("button",{staticClass:"upload-btn btn btn-block btn-primary btn-flat",attrs:{type:"button"},on:{click:function(e){return t.chooseFile("privateKeyFile")}}},[t._v("选择文件")])],1)]),t._v(" "),s("input",{staticStyle:{display:"none"},attrs:{type:"file",id:"privateKeyFile"}})],1)])],1),t._v(" "),s("div",{staticClass:"dialog-footer width_100",attrs:{slot:"footer"},slot:"footer"},[s("el-button",{staticClass:"width_100",attrs:{type:"primary"},on:{click:function(e){return t.createAdmin()}}},[t._v("确 定")])],1)])],1),t._v(" "),s("el-dialog",{staticClass:"dialog-view",attrs:{title:"WeBASE账户列表",width:"40%",visible:t.dialog.dialogUserListVisible,"close-on-click-modal":!1},on:{"update:visible":function(e){return t.$set(t.dialog,"dialogUserListVisible",e)}}},[s("el-table",{attrs:{data:t.dialog.userListPage.userList,border:"true",cellpadding:"0",cellspacing:"0"}},[s("el-table-column",{attrs:{label:"选择",width:"55"},scopedSlots:t._u([{key:"default",fn:function(e){return[s("el-radio",{attrs:{label:e.row},model:{value:t.dialog.userListPage.selectedRow,callback:function(e){t.$set(t.dialog.userListPage,"selectedRow",e)},expression:"dialog.userListPage.selectedRow"}},[s("i")])]}}])}),t._v(" "),s("el-table-column",{attrs:{label:"用户名"},scopedSlots:t._u([{key:"default",fn:function(e){return[s("span",{staticClass:"long_words",attrs:{title:e.row.userName}},[t._v(t._s(e.row.userName))])]}}])}),t._v(" "),s("el-table-column",{attrs:{property:"address",label:"用户账户",width:"350"}}),t._v(" "),s("el-table-column",{attrs:{property:"createTime",label:"创建时间",width:"170"}})],1),t._v(" "),s("el-pagination",{attrs:{"current-page":t.dialog.userListPage.pageIndex,"page-size":t.dialog.userListPage.pageSize,layout:"total, prev, pager, next, jumper",total:t.dialog.userListPage.total},on:{"current-change":t.indexChange}}),t._v(" "),s("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[s("el-button",{staticClass:"width_100",attrs:{type:"primary"},on:{click:t.chooseUser}},[t._v("确定")])],1)],1)],1)])},staticRenderFns:[function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"guide_step_item guide_step_complated"},[e("span",[this._v("1")]),this._v(" "),e("img",{attrs:{src:i("LyBL"),alt:""}}),this._v(" "),e("p",[this._v("区块链节点配置")])])},function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"guide_step_item guide_step_complated"},[e("span",[this._v("2")]),this._v(" "),e("img",{attrs:{src:i("LyBL"),alt:""}}),this._v(" "),e("p",[this._v("设置主群组")])])},function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"guide_step_item guide_step_complated"},[e("span",[this._v("3")]),this._v(" "),e("img",{attrs:{src:i("LyBL"),alt:""}}),this._v(" "),e("p",[this._v("数据库配置(可选)")])])},function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"guide_step_item guide_step_active"},[e("span",[this._v("4")]),this._v(" "),e("img",{attrs:{src:i("LyBL"),alt:""}}),this._v(" "),e("p",[this._v("创建管理员WeID")])])},function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"card-mark"},[e("a",{staticStyle:{display:"block"},attrs:{href:"https://weidentity.readthedocs.io/zh_CN/latest/docs/deploy-via-web.html#weid",target:"blank_"}},[e("img",{staticClass:"icon_question",attrs:{src:i("JaJV"),alt:""}}),this._v(" "),e("span",{staticClass:"icon_question",staticStyle:{color:"#017CFF","font-size":"12px",display:"inline-block"}},[this._v("什么是管理员？")])])])},function(){var t=this.$createElement,e=this._self._c||t;return e("div",{staticClass:"sql_warning",staticStyle:{right:"-160px"}},[e("a",{staticStyle:{display:"block"},attrs:{href:"https://weidentity.readthedocs.io/zh_CN/latest/docs/deploy-via-web.html#weid",target:"blank_"}},[e("img",{staticClass:"icon_question",attrs:{src:i("JaJV"),alt:""}}),this._v(" "),e("span",{staticClass:"icon_question",staticStyle:{color:"#017CFF","font-size":"12px",display:"inline-block"}},[this._v("点击查看配置配置教程")])])])}]},o=i("C7Lr")(a,l,!1,null,null,null);e.default=o.exports}});