#!/usr/bin/env bash
set -euo pipefail

echo "========================================="
echo "  AI SSH Agent v4.0 - 一键部署"
echo "========================================="

for cmd in docker openssl curl; do
  command -v $cmd &>/dev/null || { echo "缺少 $cmd"; exit 1; }
done
echo "依赖检查通过"

if [ ! -f .env ]; then
  cp .env.example .env
  sed -i "s/your_db_password_here/$(openssl rand -hex 16)/" .env
  sed -i "s/your_redis_password_here/$(openssl rand -hex 16)/" .env
  sed -i "s/your_jwt_secret_here/$(openssl rand -hex 32)/" .env
  echo "已生成 .env，请编辑填入API Key后重新运行"
  exit 0
fi

if [ ! -f nginx/ssl/fullchain.pem ]; then
  mkdir -p nginx/ssl
  openssl req -x509 -nodes -days 365 -newkey rsa:2048     -keyout nginx/ssl/privkey.pem -out nginx/ssl/fullchain.pem     -subj "/CN=ai-ssh-agent" 2>/dev/null
  echo "SSL证书已生成"
fi

docker compose build --parallel
docker compose up -d

echo "等待后端启动..."
for i in $(seq 1 30); do
  curl -sf http://localhost:8000/health >/dev/null 2>&1 && break
  sleep 2
done

echo "初始化管理员..."
docker compose exec -T backend python -c "
import asyncio
from models import init_db, User, async_session_factory
from api.auth import hash_password
from sqlalchemy import select

async def setup():
    await init_db()
    async with async_session_factory() as s:
        exists = (await s.execute(select(User).where(User.username == 'admin'))).scalar_one_or_none()
        if not exists:
            s.add(User(username='admin', email='admin@local', hashed_password=hash_password('changeme'), role='admin'))
            await s.commit()
            print('管理员已创建: admin / changeme')
        else:
            print('管理员已存在')
asyncio.run(setup())
"

echo ""
echo "部署完成！"
echo "API: https://your-server/api/"
echo "管理后台: https://your-server/admin/"
echo "管理员: admin / changeme"
