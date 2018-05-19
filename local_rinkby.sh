#!/bin/bash
# run a rinkby light node locally with rpc over http
# run as a detached process with sh ./rinkby_local.sh > ~/local_rinkby.log 2>&1 &

geth --networkid=4 --datadir=$HOME/.rinkeby --syncmode=light --ethstats='yournode:Respect my authoritah!@stats.rinkeby.io' --bootnodes=enode://a24ac7c5484ef4ed0c5eb2d36620ba4e4aa13b8c84684e1b4aab0cebea2ae45cb4d375b77eab56516d34bfbd3c1a833fc51296ff084b770b94fb9028c4d25ccf@52.169.42.101:30303 --rpcapi eth,web3 --rpc
