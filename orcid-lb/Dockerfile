FROM ubuntu:noble AS base

ARG HAPROXY_REPO=ha32

RUN apt-get update \
 && apt-get install -y --no-install-recommends ca-certificates \
 && rm -rf /var/lib/apt/lists/*

ADD --chmod=644 https://pks.haproxy.com/linux/community/RPM-GPG-KEY-HAProxy /usr/share/keyrings/haproxy-com.asc

RUN printf 'Types: deb\nURIs: https://www.haproxy.com/download/haproxy/performance/ubuntu/%s\nSuites: noble\nComponents: main\nSigned-By: /usr/share/keyrings/haproxy-com.asc\n' "$HAPROXY_REPO" \
      > /etc/apt/sources.list.d/haproxy-com.sources \
 && apt-get update \
 && apt-get install -y --no-install-recommends haproxy-awslc \
 && rm -rf /var/lib/apt/lists/*

COPY certs/* /etc/ssl/private/

COPY orcid-lb/haproxy.cfg /etc/haproxy/haproxy.cfg

STOPSIGNAL SIGUSR1
CMD ["haproxy", "-W", "-db", "-f", "/etc/haproxy/haproxy.cfg"]

FROM base AS dev

RUN apt-get update \
 && apt-get install -y --no-install-recommends curl dnsutils iputils-ping less procps socat vim-tiny \
 && rm -rf /var/lib/apt/lists/*

FROM base AS release
