FROM ubuntu:noble AS base

RUN apt-get update \
 && apt-get install -y --no-install-recommends nginx \
 && rm -rf /var/lib/apt/lists/* \
 && rm -f /etc/nginx/sites-enabled/default \
 && ln -sf /dev/stdout /var/log/nginx/access.log \
 && ln -sf /dev/stderr /var/log/nginx/error.log \
 && mkdir -p /etc/nginx/certs

COPY orcid-pub-web-proxy/nginx/nginx.conf /etc/nginx/nginx.conf

COPY orcid-pub-web-proxy/nginx/conf.d/* /etc/nginx/conf.d/

EXPOSE 80 443
STOPSIGNAL SIGQUIT
CMD ["nginx", "-g", "daemon off;"]

FROM base AS dev

RUN apt-get update \
 && apt-get install -y --no-install-recommends curl dnsutils iputils-ping less procps vim-tiny \
 && rm -rf /var/lib/apt/lists/*

FROM base AS release
