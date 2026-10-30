FROM ubuntu:noble AS base

RUN apt-get update \
 && apt-get install -y --no-install-recommends nginx \
 && rm -rf /var/lib/apt/lists/* \
 && rm -f /etc/nginx/sites-enabled/default \
 && ln -sf /dev/stdout /var/log/nginx/access.log \
 && ln -sf /dev/stderr /var/log/nginx/error.log \
 && mkdir -p /etc/nginx/certs \
 && touch /run/nginx.pid \
 && chown -R www-data:www-data /var/lib/nginx /var/log/nginx /run/nginx.pid

COPY orcid-web-proxy/nginx/nginx.conf /etc/nginx/nginx.conf

COPY orcid-web-proxy/nginx/snippets/* /etc/nginx/snippets/

COPY orcid-web-proxy/nginx/conf.d/* /etc/nginx/conf.d/

USER www-data

EXPOSE 80 443
STOPSIGNAL SIGQUIT
CMD ["nginx", "-g", "daemon off;"]

FROM base AS dev

USER root
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      bsdmainutils curl dnsutils iproute2 iputils-ping jq less net-tools procps unzip vim-tiny \
 && rm -rf /var/lib/apt/lists/*
USER www-data

FROM base AS release
