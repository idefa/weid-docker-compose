docker-compose down
rm -rf ./fisco/nodes/172.25.0.2/node0/data
rm -rf ./fisco/nodes/172.25.0.2/node0/log
rm -rf ./fisco/nodes/172.25.0.3/node0/data
rm -rf ./fisco/nodes/172.25.0.3/node0/log
rm -rf ./fisco/nodes/172.25.0.4/node0/data
rm -rf ./fisco/nodes/172.25.0.4/node0/log
rm -rf ./weid-build-tools/logs
rm -rf ./weid-build-tools/output
rm -rf ./weid-sample/logs
rm -rf ./webase/webase-front
rm -rf ./webase/webase-node-mgr
rm -rf ./webase/webase-sign
rm -rf ./webase/webase-web
rm -rf ./webase/mysql
cd ./webase
unzip mysql.zip