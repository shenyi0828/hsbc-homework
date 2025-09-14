# Apache Bench 性能测试指南

## 前置条件

1. 确保后端服务运行在 `http://localhost:8080`
2. 安装 Apache Bench 工具：`brew install apache2` (macOS)
3. 数据库中有一些测试数据

## 测试场景

### 场景1: 100% 读操作

```bash
# 分页查询测试 - 1000请求，50并发
ab -n 1000 -c 50 "http://localhost:8080/api/transactions?page=0&size=10"

# 单个交易查询 - 需要替换为实际的交易ID
ab -n 500 -c 25 "http://localhost:8080/api/transactions/TXN202501140001"
```

### 场景2: 80% 读 + 20% 写操作

```bash
# 运行混合负载测试脚本
./mixed_load_test.sh
```

该脚本会：
- 自动生成随机交易数据
- 并发执行 800 个读请求和 200 个写请求
- 提供详细的性能统计报告
- 将结果保存到 results/ 目录

以下是我测的结果：
```
This is ApacheBench, Version 2.3 <$Revision: 1913912 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 100 requests
Completed 200 requests
Completed 300 requests
Completed 400 requests
Completed 500 requests
Completed 600 requests
Completed 700 requests
Completed 800 requests
Finished 800 requests


Server Software:        
Server Hostname:        localhost
Server Port:            8080

Document Path:          /api/transactions?page=0&size=10
Document Length:        2925 bytes

Concurrency Level:      20
Time taken for tests:   0.390 seconds
Complete requests:      800
Failed requests:        77
   (Connect: 0, Receive: 0, Length: 77, Exceptions: 0)
Total transferred:      2495111 bytes
HTML transferred:       2339911 bytes
Requests per second:    2053.64 [#/sec] (mean)
Time per request:       9.739 [ms] (mean)
Time per request:       0.487 [ms] (mean, across all concurrent requests)
Transfer rate:          6254.96 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.3      0       4
Processing:     1    9   8.4      7      58
Waiting:        1    9   8.1      6      57
Total:          2   10   8.5      7      58

Percentage of the requests served within a certain time (ms)
  50%      7
  66%      8
  75%      9
  80%     10
  90%     16
  95%     32
  98%     43
  99%     48
 100%     58 (longest request)

```