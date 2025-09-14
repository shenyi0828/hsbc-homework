#!/bin/bash

# 混合负载测试脚本 - 80% 读 + 20% 写
# 随机生成交易数据进行测试

BASE_URL="http://localhost:8080/api/transactions"
READ_REQUESTS=800
WRITE_REQUESTS=200
CONCURRENCY=20

echo "开始混合负载测试 (80% 读 + 20% 写)"
echo "读请求: $READ_REQUESTS, 写请求: $WRITE_REQUESTS, 并发数: $CONCURRENCY"
echo ""

# 检查后端服务是否运行
echo "检查后端服务状态..."
if ! curl -s "$BASE_URL?page=0&size=1" > /dev/null; then
    echo "错误: 后端服务未运行，请先启动后端服务"
    exit 1
fi
echo "后端服务正常运行"
echo ""

# 创建结果目录
mkdir -p results

# 生成随机交易数据的函数
generate_random_transaction() {
    local amount=$(echo "scale=2; $RANDOM / 100" | bc)
    local types=("DEPOSIT" "WITHDRAWAL" "TRANSFER")
    local type=${types[$RANDOM % ${#types[@]}]}
    local description="Test transaction $(date +%s)$RANDOM"
    
    cat << EOF
{
  "amount": $amount,
  "transactionType": "$type",
  "description": "$description"
}
EOF
}

echo "开始执行测试..."
start_time=$(date +%s)

# 后台执行读操作测试
echo "启动读操作测试 (80%)..."
ab -n $READ_REQUESTS -c $CONCURRENCY "$BASE_URL?page=0&size=10" > results/read_test_result.txt 2>&1 &
read_pid=$!

# 等待一秒后开始写操作
sleep 1

# 执行写操作测试 (20%)
echo "启动写操作测试 (20%)..."
for i in $(seq 1 $WRITE_REQUESTS); do
    # 生成随机交易数据
    transaction_data=$(generate_random_transaction)
    
    # 每10个请求并发执行
    if [ $((i % 10)) -eq 0 ]; then
        wait # 等待前一批完成
    fi
    
    # 后台执行POST请求
    {
        echo "$transaction_data" | curl -s -X POST \
            -H "Content-Type: application/json" \
            -d @- \
            "$BASE_URL" > /dev/null
    } &
done

# 等待所有写操作完成
wait

# 等待读操作完成
echo "等待读操作测试完成..."
if kill -0 $read_pid 2>/dev/null; then
    wait $read_pid 2>/dev/null || true
fi

end_time=$(date +%s)
total_time=$((end_time - start_time))

echo ""
echo "测试完成！总耗时: ${total_time}秒"
echo ""
echo "=== 读操作测试结果 ==="
grep -E "Requests per second|Time per request|Transfer rate" results/read_test_result.txt

echo ""
echo "=== 写操作统计 ==="
echo "总写请求数: $WRITE_REQUESTS"
echo "平均写入速率: $(echo "scale=2; $WRITE_REQUESTS / $total_time" | bc) 请求/秒"

echo ""
echo "详细结果保存在 results/ 目录中"