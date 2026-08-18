FROM alpine:3.24.1
RUN apk add --no-cache wireguard-tools gettext

WORKDIR /etc/wireguard
COPY wg0.conf.template .
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
