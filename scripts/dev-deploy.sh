#!/bin/bash

PROJECT_DIR="/home/ubuntu/nmnb"
NGINX_CONF_DIR="/home/ubuntu/nginx"

COMPOSE_PATH="$PROJECT_DIR/docker-compose.override.yml"
ENV_FILE_PATH="/home/ubuntu/.dev_env"

NGINX_CONTAINER="nginx"
NGINX_CONF="nmnb.dev.conf"

MVC_API_ENV='dev-nmnb'
WEBFLUX_API_ENV='dev-nmnb-webflux'

echo
echo "-----------------------------"
echo ".env 파일 복사 중..."
cp "$ENV_FILE_PATH" "$PROJECT_DIR/.env" || { echo ".env 파일 복사 실패"; exit 1; }
echo ".env 파일 복사 완료"
echo "-----------------------------"
echo

echo "기존 컨테이너 중단 중..."
sudo docker-compose -f "$COMPOSE_PATH" stop $MVC_API_ENV $WEBFLUX_API_ENV

echo "기존 컨테이너 삭제 중..."
sudo docker-compose -f "$COMPOSE_PATH" rm -f $MVC_API_ENV $WEBFLUX_API_ENV

echo
echo "-----------------------------"
echo "도커 허브에서 새로운 이미지 pull 중: $MVC_API_ENV"
sudo docker-compose -f "$COMPOSE_PATH" pull $MVC_API_ENV || { echo "이미지 pull 실패"; exit 1; }
echo "이미지 pull 완료"
echo "-----------------------------"
echo

echo
echo "-----------------------------"
echo "도커 허브에서 새로운 이미지 pull 중(webflux): $WEBFLUX_API_ENV"
sudo docker-compose -f "$COMPOSE_PATH" pull $WEBFLUX_API_ENV || { echo "이미지 pull 실패"; exit 1; }
echo "이미지 pull 완료"
echo "-----------------------------"
echo

echo "-----------------------------"
echo "컨테이너 재시작 중..."
sudo docker-compose -f "$COMPOSE_PATH" up -d --no-deps $MVC_API_ENV $WEBFLUX_API_ENV || { echo "컨테이너 재시작 실패"; exit 1; }
echo "컨테이너 재시작 완료"
echo "-----------------------------"
echo


for ENV in $MVC_API_ENV $WEBFLUX_API_ENV; do
    if ! docker ps --filter "name=$ENV" --filter "status=running" | grep -q "$ENV"; then
        echo "컨테이너($ENV)가 정상 실행되지 않음"
        exit 1
    fi
done

echo "Nginx 설정 파일 덮어쓰기 중... ($NGINX_CONF)"
docker cp "$NGINX_CONF_DIR/$NGINX_CONF" "$NGINX_CONTAINER:/etc/nginx/conf.d/$NGINX_CONF" || { echo "Nginx 설정 복사 실패"; exit 1; }

echo
echo "-----------------------------"
echo "Nginx 설정 테스트 중..."
docker exec "$NGINX_CONTAINER" nginx -t || { echo "Nginx 설정 오류"; exit 1; }
echo "Nginx 설정 문제 없음"

echo "Nginx 리로드 중..."
docker exec "$NGINX_CONTAINER" nginx -s reload || { echo "Nginx 리로드 실패"; exit 1; }
echo "Nginx 리로드 완료"
echo "-----------------------------"
echo

echo
echo "-----------------------------"
echo "불필요한 이미지 및 컨테이너 정리 중..."
docker system prune -a -f || { echo "docker system prune 실패"; exit 1; }
echo "불필요한 리소스 정리 완료"
echo "-----------------------------"

echo
echo "-----------------------------"
echo "✅ 배포가 완료되었습니다!"
echo "-----------------------------"
